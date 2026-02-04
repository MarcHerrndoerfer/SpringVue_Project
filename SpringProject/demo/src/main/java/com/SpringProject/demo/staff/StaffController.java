package com.SpringProject.demo.staff;

import com.SpringProject.demo.staff.dto.AvailabilityResponse;
import com.SpringProject.demo.staff.dto.DepartmentResponse;
import com.SpringProject.demo.staff.dto.MyShiftResponse;
import com.SpringProject.demo.staff.dto.ShiftResponse;
import com.SpringProject.demo.staff.dto.SickLeaveResponse;
import com.SpringProject.demo.staff.dto.StaffProfileRequest;
import com.SpringProject.demo.staff.dto.StaffProfileResponse;
import com.SpringProject.demo.user.User;
import com.SpringProject.demo.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

  private static final Set<String> STAFF_ROLES = Set.of("DOCTOR", "NURSE", "ADMIN");

  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final ShiftRepository shiftRepository;
  private final ShiftAssignmentRepository shiftAssignmentRepository;
  private final StaffProfileRepository staffProfileRepository;
  private final StaffAvailabilityRepository staffAvailabilityRepository;
  private final SickLeaveRepository sickLeaveRepository;

  public StaffController(
    UserRepository userRepository,
    DepartmentRepository departmentRepository,
    ShiftRepository shiftRepository,
    ShiftAssignmentRepository shiftAssignmentRepository,
    StaffProfileRepository staffProfileRepository,
    StaffAvailabilityRepository staffAvailabilityRepository,
    SickLeaveRepository sickLeaveRepository
  ) {
    this.userRepository = userRepository;
    this.departmentRepository = departmentRepository;
    this.shiftRepository = shiftRepository;
    this.shiftAssignmentRepository = shiftAssignmentRepository;
    this.staffProfileRepository = staffProfileRepository;
    this.staffAvailabilityRepository = staffAvailabilityRepository;
    this.sickLeaveRepository = sickLeaveRepository;
  }

  @GetMapping("/wards")
  @Transactional(readOnly = true)
  public List<DepartmentResponse> getWards() {
    requireStaffUser();
    return departmentRepository.findAll().stream()
      .map(department -> new DepartmentResponse(department.getId(), department.getName()))
      .toList();
  }

  @GetMapping("/shifts")
  @Transactional(readOnly = true)
  public List<ShiftResponse> getShifts(@RequestParam(required = false) Long departmentId) {
    requireStaffUser();
    var shifts = departmentId == null
      ? shiftRepository.findAll()
      : shiftRepository.findByDepartmentId(departmentId);

    return shifts.stream()
      .map(shift -> new ShiftResponse(
        shift.getId(),
        shift.getDepartment().getId(),
        shift.getDepartment().getName(),
        shift.getTitle(),
        shift.getStartAt(),
        shift.getEndAt(),
        shift.getRequiredStaff()
      ))
      .toList();
  }

  @GetMapping("/my-shifts")
  @Transactional(readOnly = true)
  public List<MyShiftResponse> getMyShifts() {
    var user = requireStaffUser();

    return shiftAssignmentRepository.findByUserId(user.getId()).stream()
      .map(assignment -> new MyShiftResponse(
        assignment.getId(),
        assignment.getShift().getId(),
        assignment.getShift().getDepartment().getName(),
        assignment.getShift().getTitle(),
        assignment.getShift().getStartAt(),
        assignment.getShift().getEndAt(),
        assignment.getStatus()
      ))
      .toList();
  }

  @GetMapping("/profile")
  @Transactional(readOnly = true)
  public StaffProfileResponse getProfile() {
    var user = requireStaffUser();
    var profile = staffProfileRepository.findByUserId(user.getId()).orElse(null);

    return toProfileResponse(user, profile);
  }

  @GetMapping("/availability")
  @Transactional(readOnly = true)
  public List<AvailabilityResponse> getAvailability() {
    requireStaffUser();

    Map<Long, AvailabilityAggregate> aggregate = new HashMap<>();
    for (var entry : staffAvailabilityRepository.findAll()) {
      var user = entry.getUser();
      var current = aggregate.computeIfAbsent(user.getId(), key -> new AvailabilityAggregate(
        user.getName(),
        user.getRole(),
        entry.getStatus(),
        new ArrayList<>()
      ));
      current.days.add(entry.getDayOfWeek());
    }

    return aggregate.values().stream()
      .sorted(Comparator.comparing(AvailabilityAggregate::name))
      .map(item -> new AvailabilityResponse(item.name, item.role, item.days, item.status))
      .toList();
  }

  @GetMapping("/sick")
  @Transactional(readOnly = true)
  public List<SickLeaveResponse> getSickLeave() {
    requireStaffUser();
    LocalDate today = LocalDate.now();

    return sickLeaveRepository
      .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
      .stream()
      .map(leave -> new SickLeaveResponse(
        leave.getUser().getName(),
        leave.getUser().getRole(),
        leave.getStartDate(),
        leave.getEndDate(),
        leave.getNote()
      ))
      .toList();
  }

  @PutMapping("/profile")
  public StaffProfileResponse updateProfile(@RequestBody StaffProfileRequest request) {
    var user = requireStaffUser();

    if (request.name() != null && !request.name().isBlank()) {
      user.setName(request.name().trim());
    }

    var profile = staffProfileRepository.findByUserId(user.getId())
      .orElseGet(() -> {
        var created = new StaffProfile();
        created.setUser(user);
        return created;
      });

    profile.setPhone(normalize(request.phone()));
    profile.setBio(normalize(request.bio()));

    if (request.departmentId() != null) {
      var department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown department"));
      profile.setDepartment(department);
    } else {
      profile.setDepartment(null);
    }

    userRepository.save(user);
    staffProfileRepository.save(profile);

    return toProfileResponse(user, profile);
  }

  private User requireStaffUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
      || !authentication.isAuthenticated()
      || "anonymousUser".equals(authentication.getPrincipal())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in"));

    if (!STAFF_ROLES.contains(user.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Staff only");
    }

    return user;
  }

  private StaffProfileResponse toProfileResponse(User user, StaffProfile profile) {
    if (profile == null) {
      return new StaffProfileResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        null,
        null,
        null,
        null
      );
    }

    var department = profile.getDepartment();
    return new StaffProfileResponse(
      user.getId(),
      user.getName(),
      user.getEmail(),
      user.getRole(),
      profile.getPhone(),
      department != null ? department.getId() : null,
      department != null ? department.getName() : null,
      profile.getBio()
    );
  }

  private String normalize(String value) {
    if (value == null) return null;
    var trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private record AvailabilityAggregate(String name, String role, String status, List<String> days) {}
}
