package com.SpringProject.demo.shift.dto;

import jakarta.validation.constraints.NotNull;

public record RequestShiftRequest(
  @NotNull Long shiftId
) {
}
