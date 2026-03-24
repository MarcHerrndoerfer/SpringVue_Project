package com.SpringProject.demo.shift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateShiftRequest(
  @NotNull Long departmentId,
  @NotBlank String title,
  @NotNull LocalDateTime startAt,
  @NotNull LocalDateTime endAt,
  @NotNull @Min(1) Integer requiredStaff
) {
}
