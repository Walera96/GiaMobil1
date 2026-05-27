package com.spbutu.gia.core.domain.repository.scos;

import com.spbutu.gia.core.domain.entity.scos.ScosExportConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScosExportConfigRepository extends JpaRepository<ScosExportConfig, UUID> {

    Optional<ScosExportConfig> findByDirectionCode(String directionCode);

    Optional<ScosExportConfig> findByDirectionCodeAndIsActiveTrue(String directionCode);
}
