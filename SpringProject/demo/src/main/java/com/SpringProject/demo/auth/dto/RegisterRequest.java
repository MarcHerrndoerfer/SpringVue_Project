package com.SpringProject.demo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
  @NotBlank(message = "Name is required")
  @Size(max = 120, message = "Name must be less than 120 characters")
  String name,

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 190, message = "Email must be less than 190 characters")
  String email,

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  String password,

  @NotBlank(message = "Role is required")
  @Size(max = 30, message = "Role must be less than 30 characters")
  String role
) {}
