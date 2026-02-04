package com.SpringProject.demo.staff.dto;

import java.util.List;

public record AvailabilityResponse(String name, String role, List<String> days, String status) {}
