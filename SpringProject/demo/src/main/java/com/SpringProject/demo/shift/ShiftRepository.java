package com.SpringProject.demo.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
  @Query("SELECT s FROM Shift s WHERE s.startAt >= :now ORDER BY s.startAt ASC")
  List<Shift> findUpcomingShifts(@Param("now") LocalDateTime now);

  @Query("SELECT s FROM Shift s WHERE s.department.id = :deptId AND s.startAt >= :now ORDER BY s.startAt ASC")
  List<Shift> findByDepartmentAndAfter(@Param("deptId") Long deptId, @Param("now") LocalDateTime now);
}
