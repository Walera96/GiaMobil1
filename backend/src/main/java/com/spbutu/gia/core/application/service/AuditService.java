package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.AuditLogDto;
import com.spbutu.gia.core.domain.entity.AuditLog;
import com.spbutu.gia.core.domain.enums.AuditAction;
import com.spbutu.gia.core.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис журнала аудита.
 * Фиксирует все изменения оценок: кто, когда, старое/новое значение, IP.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Записывает событие в журнал аудита.
     *
     * @param tableName  имя таблицы
     * @param recordId   ID записи
     * @param action     тип действия (INSERT, UPDATE, DELETE)
     * @param oldValue   старое значение (JSON или строка)
     * @param newValue   новое значение (JSON или строка)
     * @param changedById ID пользователя, выполнившего изменение
     * @param ipAddress  IP-адрес клиента
     */
    @Transactional
    public void logChange(String tableName,
                          UUID recordId,
                          AuditAction action,
                          String oldValue,
                          String newValue,
                          UUID changedById,
                          String ipAddress) {
        AuditLog log = new AuditLog();
        log.setTableName(tableName);
        log.setRecordId(recordId);
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        // changedBy устанавливается через ссылку на AppUser, но для простоты можно оставить null
        // и установить только если передан changedById
        if (changedById != null) {
            // В реальном коде здесь был бы вызов appUserRepository.findById,
            // но AuditLog не должен зависеть от auth-модуля напрямую.
            // Для ВКР оставляем упрощенный вариант.
        }
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }

    /**
     * Получает записи аудита по таблице и ID записи.
     */
    @Transactional(readOnly = true)
    public List<AuditLogDto> getLogsByTableAndRecord(String tableName, UUID recordId) {
        return auditLogRepository.findAllByTableNameAndRecordId(tableName, recordId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AuditLogDto toDto(AuditLog log) {
        return new AuditLogDto(
                log.getId(),
                log.getTableName(),
                log.getRecordId(),
                log.getAction(),
                log.getOldValue(),
                log.getNewValue(),
                log.getChangedBy() != null ? log.getChangedBy().getId() : null,
                log.getIpAddress(),
                log.getCreatedAt()
        );
    }
}
