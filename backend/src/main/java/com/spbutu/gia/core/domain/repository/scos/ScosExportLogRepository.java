package com.spbutu.gia.core.domain.repository.scos;

import com.spbutu.gia.core.domain.entity.scos.ScosExportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScosExportLogRepository extends JpaRepository<ScosExportLog, UUID> {

    List<ScosExportLog> findAllByOrderByCreatedAtDesc();

    List<ScosExportLog> findByDirectionCodeOrderByCreatedAtDesc(String directionCode);
}
