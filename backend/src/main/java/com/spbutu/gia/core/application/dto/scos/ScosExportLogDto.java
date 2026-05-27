package com.spbutu.gia.core.application.dto.scos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScosExportLogDto {
    private UUID id;
    private LocalDateTime exportDate;
    private String fileName;
    private Integer recordCount;
    private String status;
    private String errorDetails;
    private String directionCode;
    private String academicYear;
    private String createdBy;
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getExportDate() { return exportDate; }
    public void setExportDate(LocalDateTime exportDate) { this.exportDate = exportDate; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Integer getRecordCount() { return recordCount; }
    public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
