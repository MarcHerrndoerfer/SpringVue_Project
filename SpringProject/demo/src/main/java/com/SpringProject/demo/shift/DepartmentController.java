package com.SpringProject.demo.shift;

import com.SpringProject.demo.shift.dto.DepartmentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

  private final DepartmentRepository departmentRepository;

  public DepartmentController(DepartmentRepository departmentRepository) {
    this.departmentRepository = departmentRepository;
  }

  @GetMapping
  public List<DepartmentResponse> getAllDepartments() {
    if (departmentRepository.count() == 0) {
      Stream.of("Emergency", "ICU", "Surgery", "Pediatrics")
        .forEach(name -> {
          Department department = new Department();
          department.setName(name);
          departmentRepository.save(department);
        });
    }

    return departmentRepository.findAll().stream()
      .sorted(Comparator.comparing(Department::getName))
      .map(department -> new DepartmentResponse(department.getId(), department.getName()))
      .collect(Collectors.toList());
  }
}
