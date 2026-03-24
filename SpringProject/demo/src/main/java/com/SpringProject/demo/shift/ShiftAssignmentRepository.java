package com.SpringProject.demo.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {
  @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.shift.id = :shiftId")
  List<ShiftAssignment> findByShift(@Param("shiftId") Long shiftId);

  @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.shift.id = :shiftId AND sa.user.id = :userId")
  Optional<ShiftAssignment> findByShiftAndUser(@Param("shiftId") Long shiftId, @Param("userId") Long userId);

  @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.user.id = :userId ORDER BY sa.shift.startAt DESC")
  List<ShiftAssignment> findByUser(@Param("userId") Long userId);
}
