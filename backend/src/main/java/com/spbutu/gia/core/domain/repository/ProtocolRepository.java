package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий протоколов заседаний.
 */
@Repository
public interface ProtocolRepository extends JpaRepository<Protocol, UUID> {

    Optional<Protocol> findByMeetingId(UUID meetingId);

    @Query("SELECT DISTINCT pr.protocol FROM ProtocolRecord pr WHERE pr.student.id = :studentId")
    List<Protocol> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT DISTINCT pr.protocol FROM ProtocolRecord pr WHERE pr.student.group.id = :groupId")
    List<Protocol> findByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT DISTINCT pr.protocol FROM ProtocolRecord pr WHERE pr.student.group.direction.id = :directionId")
    List<Protocol> findByDirectionId(@Param("directionId") UUID directionId);

    @Query("SELECT DISTINCT pr.protocol FROM ProtocolRecord pr WHERE " +
           "LOWER(pr.student.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(pr.student.firstName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Protocol> findByStudentNameContaining(@Param("name") String name);
}
