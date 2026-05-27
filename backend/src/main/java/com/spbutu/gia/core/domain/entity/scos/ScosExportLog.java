package com.spbutu.gia.core.domain.entity.scos;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scos_export_log")
public class ScosExportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "export_date")
    private LocalDateTime exportDate;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "record_count")
    private Integer recordCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExportStatus status;

    @Column(name = "error_details", length = 4000)
    private String errorDetails;

    @Column(name = "direction_code")
    private String directionCode;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "file_content", columnDefinition = "TEXT")
    private String fileContent;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ExportStatus {
        SUCCESS, PARTIAL, ERROR
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getExportDate() { return exportDate; }
    public void setExportDate(LocalDateTime exportDate) { this.exportDate = exportDate; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getRecordCount() { return recordCount; }
    public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }

    public ExportStatus getStatus() { return status; }
    public void setStatus(ExportStatus status) { this.status = status; }

    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getFileContent() { return fileContent; }
    public void setFileContent(String fileContent) { this.fileContent = fileContent; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
