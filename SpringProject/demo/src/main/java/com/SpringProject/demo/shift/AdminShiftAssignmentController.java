package com.SpringProject.demo.shift;

import com.SpringProject.demo.shift.dto.ApproveAssignmentRequest;
import com.SpringProject.demo.shift.dto.ShiftAssignmentResponse;
import com.SpringProject.demo.user.User;
import com.SpringProject.demo.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/shift-assignments")
public class AdminShiftAssignmentController {

  private final ShiftAssignmentRepository assignmentRepository;
  private final UserRepository userRepository;

  public AdminShiftAssignmentController(
    ShiftAssignmentRepository assignmentRepository,
    UserRepository userRepository
  ) {
    this.assignmentRepository = assignmentRepository;
    this.userRepository = userRepository;
  }

  private void checkAdminRole() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
    }

    User user = userRepository.findByEmail(authentication.getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    if (!"ADMIN".equals(user.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access this");
    }
  }

  @GetMapping("/pending")
  public List<ShiftAssignmentResponse> getPendingAssignments() {
    checkAdminRole();

    return assignmentRepository.findAll().stream()
      .filter(a -> "PENDING".equals(a.getStatus()))
      .map(this::toResponse)
      .collect(Collectors.toList());
  }

  @GetMapping("/all")
  public List<ShiftAssignmentResponse> getAllAssignments() {
    checkAdminRole();

    return assignmentRepository.findAll().stream()
      .map(this::toResponse)
      .collect(Collectors.toList());
  }

  @GetMapping("/shift/{shiftId}")
  public List<ShiftAssignmentResponse> getAssignmentsByShift(@PathVariable Long shiftId) {
    checkAdminRole();

    return assignmentRepository.findByShift(shiftId).stream()
      .map(this::toResponse)
      .collect(Collectors.toList());
  }

  @PatchMapping("/{assignmentId}")
  public ShiftAssignmentResponse updateAssignment(
    @PathVariable Long assignmentId,
    @RequestBody ApproveAssignmentRequest req
  ) {
    checkAdminRole();

    ShiftAssignment assignment = assignmentRepository.findById(assignmentId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

    User admin = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    assignment.setStatus(req.status());
    assignment.setDecidedAt(LocalDateTime.now());
    assignment.setDecidedBy(admin);

    assignmentRepository.save(assignment);
    return toResponse(assignment);
  }

  @DeleteMapping("/{assignmentId}")
  public void deleteAssignment(@PathVariable Long assignmentId) {
    checkAdminRole();

    ShiftAssignment assignment = assignmentRepository.findById(assignmentId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

    assignmentRepository.delete(assignment);
  }

  private ShiftAssignmentResponse toResponse(ShiftAssignment a) {
    return new ShiftAssignmentResponse(
      a.getId(),
      a.getShift().getId(),
      a.getShift().getTitle(),
      a.getShift().getDepartment().getName(),
      a.getShift().getStartAt(),
      a.getShift().getEndAt(),
      a.getUser().getId(),
      a.getUser().getName(),
      a.getUser().getEmail(),
      a.getUser().getRole(),
      a.getStatus(),
      a.getRequestedAt(),
      a.getDecidedAt(),
      a.getDecidedBy() != null ? a.getDecidedBy().getName() : null
    );
  }
}
