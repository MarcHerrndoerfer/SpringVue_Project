package com.SpringProject.demo.staff.dto;

import java.time.LocalDate;

public record SickLeaveResponse(String name, String role, LocalDate startDate, LocalDate endDate, String note) {}
