package com.SpringProject.demo.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {
  List<ShiftAssignment> findByUserId(Long userId);
}
