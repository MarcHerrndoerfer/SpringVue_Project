package com.SpringProject.demo.staff.dto;

import java.time.LocalDateTime;

public record ShiftResponse(
  Long id,
  Long departmentId,
  String departmentName,
  String title,
  LocalDateTime startAt,
  LocalDateTime endAt,
  Integer requiredStaff
) {}
