package com.SpringProject.demo.staff.dto;

import java.time.LocalDateTime;

public record MyShiftResponse(
  Long id,
  Long shiftId,
  String ward,
  String title,
  LocalDateTime startAt,
  LocalDateTime endAt,
  String status
) {}
