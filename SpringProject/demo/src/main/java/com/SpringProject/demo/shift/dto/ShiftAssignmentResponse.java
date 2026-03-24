package com.SpringProject.demo.shift.dto;

import java.time.LocalDateTime;

public record ShiftAssignmentResponse(
  Long id,
  Long shiftId,
  String shiftTitle,
  String department,
  LocalDateTime startAt,
  LocalDateTime endAt,
  Long userId,
  String userName,
  String userEmail,
  String userRole,
  String status,
  LocalDateTime requestedAt,
  LocalDateTime decidedAt,
  String decidedByName
) {
}
