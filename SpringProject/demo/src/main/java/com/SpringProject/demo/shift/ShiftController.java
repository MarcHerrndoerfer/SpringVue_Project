package com.SpringProject.demo.shift;

import com.SpringProject.demo.shift.dto.CreateShiftRequest;
import com.SpringProject.demo.shift.dto.ShiftAssignmentResponse;
import com.SpringProject.demo.shift.dto.ShiftResponse;
import com.SpringProject.demo.user.User;
import com.SpringProject.demo.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

  private final ShiftRepository shiftRepository;
  private final ShiftAssignmentRepository assignmentRepository;
  private final DepartmentRepository departmentRepository;
  private final UserRepository userRepository;

  public ShiftController(
    ShiftRepository shiftRepository,
    ShiftAssignmentRepository assignmentRepository,
    DepartmentRepository departmentRepository,
    UserRepository userRepository
  ) {
    this.shiftRepository = shiftRepository;
    this.assignmentRepository = assignmentRepository;
    this.departmentRepository = departmentRepository;
    this.userRepository = userRepository;
  }

  private static final Set<String> STAFF_ROLES = Set.of("DOCTOR", "NURSE", "ADMIN");

  @PostMapping
  public ShiftResponse createShift(@RequestBody @Valid CreateShiftRequest req) {
    User user = getAuthenticatedStaffUser();

    if (req.endAt().isBefore(req.startAt()) || req.endAt().isEqual(req.startAt())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endAt must be after startAt");
    }

    Department department = departmentRepository.findById(req.departmentId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));

    Shift shift = new Shift();
    shift.setDepartment(department);
    shift.setTitle(req.title().trim());
    shift.setStartAt(req.startAt());
    shift.setEndAt(req.endAt());
    shift.setRequiredStaff(req.requiredStaff());
    shift.setCreatedBy(user);

    shift = shiftRepository.save(shift);

    return new ShiftResponse(
      shift.getId(),
      shift.getDepartment().getName(),
      shift.getTitle(),
      shift.getStartAt(),
      shift.getEndAt(),
      shift.getRequiredStaff(),
      0,
      shift.getCreatedBy().getName()
    );
  }

  @GetMapping("/available")
  public List<ShiftResponse> getAvailable() {
    LocalDateTime now = LocalDateTime.now();
    List<Shift> shifts = shiftRepository.findUpcomingShifts(now);

    return shifts.stream().map(shift -> {
      long assignedCount = assignmentRepository.findByShift(shift.getId())
        .stream()
        .filter(a -> "APPROVED".equals(a.getStatus()) || "PENDING".equals(a.getStatus()))
        .count();

      return new ShiftResponse(
        shift.getId(),
        shift.getDepartment().getName(),
        shift.getTitle(),
        shift.getStartAt(),
        shift.getEndAt(),
        shift.getRequiredStaff(),
        (int) assignedCount,
        shift.getCreatedBy().getName()
      );
    }).collect(Collectors.toList());
  }

  @GetMapping("/available/department/{deptId}")
  public List<ShiftResponse> getAvailableByDepartment(@PathVariable Long deptId) {
    LocalDateTime now = LocalDateTime.now();
    List<Shift> shifts = shiftRepository.findByDepartmentAndAfter(deptId, now);

    return shifts.stream().map(shift -> {
      long assignedCount = assignmentRepository.findByShift(shift.getId())
        .stream()
        .filter(a -> "APPROVED".equals(a.getStatus()) || "PENDING".equals(a.getStatus()))
        .count();

      return new ShiftResponse(
        shift.getId(),
        shift.getDepartment().getName(),
        shift.getTitle(),
        shift.getStartAt(),
        shift.getEndAt(),
        shift.getRequiredStaff(),
        (int) assignedCount,
        shift.getCreatedBy().getName()
      );
    }).collect(Collectors.toList());
  }

  @PostMapping("/{shiftId}/request")
  public void requestShift(@PathVariable Long shiftId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    Shift shift = shiftRepository.findById(shiftId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));

    // Check if already requested
    if (assignmentRepository.findByShiftAndUser(shiftId, user.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already requested this shift");
    }

    ShiftAssignment assignment = new ShiftAssignment();
    assignment.setShift(shift);
    assignment.setUser(user);
    assignment.setStatus("PENDING");
    assignmentRepository.save(assignment);
  }

  @DeleteMapping("/{shiftId}/cancel")
  public void cancelRequest(@PathVariable Long shiftId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    ShiftAssignment assignment = assignmentRepository.findByShiftAndUser(shiftId, user.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

    if ("APPROVED".equals(assignment.getStatus())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel approved request");
    }

    assignmentRepository.delete(assignment);
  }

  @GetMapping("/my-assignments")
  public List<ShiftAssignmentResponse> getMyAssignments() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    return assignmentRepository.findByUser(user.getId()).stream()
      .map(this::toAssignmentResponse)
      .collect(Collectors.toList());
  }

  private ShiftAssignmentResponse toAssignmentResponse(ShiftAssignment assignment) {
    return new ShiftAssignmentResponse(
      assignment.getId(),
      assignment.getShift().getId(),
      assignment.getShift().getTitle(),
      assignment.getShift().getDepartment().getName(),
      assignment.getShift().getStartAt(),
      assignment.getShift().getEndAt(),
      assignment.getUser().getId(),
      assignment.getUser().getName(),
      assignment.getUser().getEmail(),
      assignment.getUser().getRole(),
      assignment.getStatus(),
      assignment.getRequestedAt(),
      assignment.getDecidedAt(),
      assignment.getDecidedBy() != null ? assignment.getDecidedBy().getName() : null
    );
  }

  private User getAuthenticatedStaffUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    if (!STAFF_ROLES.contains(user.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to create shifts");
    }

    return user;
  }
}
