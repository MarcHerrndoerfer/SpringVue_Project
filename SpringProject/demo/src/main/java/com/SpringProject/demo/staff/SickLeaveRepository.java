package com.SpringProject.demo.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
  List<SickLeave> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);
}
