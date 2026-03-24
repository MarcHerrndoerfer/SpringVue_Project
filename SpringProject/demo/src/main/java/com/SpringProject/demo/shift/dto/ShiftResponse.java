package com.SpringProject.demo.shift.dto;

import java.time.LocalDateTime;

public record ShiftResponse(
  Long id,
  String department,
  String title,
  LocalDateTime startAt,
  LocalDateTime endAt,
  int requiredStaff,
  int assignedCount,
  String createdBy
) {
}
