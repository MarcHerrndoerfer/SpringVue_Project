package com.SpringProject.demo.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
  List<Shift> findByDepartmentId(Long departmentId);
}
