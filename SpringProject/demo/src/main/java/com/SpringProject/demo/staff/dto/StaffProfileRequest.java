package com.SpringProject.demo.staff.dto;

public record StaffProfileRequest(
  String name,
  String phone,
  Long departmentId,
  String bio
) {}
