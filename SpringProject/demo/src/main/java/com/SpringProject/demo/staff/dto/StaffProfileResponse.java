package com.SpringProject.demo.staff.dto;

public record StaffProfileResponse(
  Long id,
  String name,
  String email,
  String role,
  String phone,
  Long departmentId,
  String departmentName,
  String bio
) {}
