package com.spbutu.gia.core.domain.repository.deanery;

import com.spbutu.gia.core.domain.entity.deanery.ContingentMovement;
import com.spbutu.gia.core.domain.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContingentMovementRepository extends JpaRepository<ContingentMovement, UUID> {

    List<ContingentMovement> findAllByStudentId(UUID studentId);

    List<ContingentMovement> findAllByMovementType(MovementType movementType);

    List<ContingentMovement> findAllByOrderId(UUID orderId);
}
