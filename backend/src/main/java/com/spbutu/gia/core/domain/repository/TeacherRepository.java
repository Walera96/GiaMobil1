package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    List<Teacher> findByDepartment(String department);
    List<Teacher> findByLastNameContainingIgnoreCase(String lastName);
}
