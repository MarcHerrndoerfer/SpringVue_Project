package com.SpringProject.demo.shift.dto;

import jakarta.validation.constraints.NotBlank;

public record ApproveAssignmentRequest(
  @NotBlank String status
) {
}
