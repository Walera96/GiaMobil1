package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.DraftDocument;
import com.spbutu.gia.core.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий черновиков документов.
 */
@Repository
public interface DraftDocumentRepository extends JpaRepository<DraftDocument, UUID> {

    List<DraftDocument> findAllByProtocolId(UUID protocolId);

    Optional<DraftDocument> findByProtocolIdAndDocumentType(UUID protocolId, DocumentType documentType);
}
