package com.spbutu.gia.core.application.dto.scos;

import java.util.List;
import java.util.UUID;

public class ScosExportPackageDto {
    private UUID packageId;
    private String exportDate;
    private List<ScosStudentResultDto> results;
    private int totalCount;
    private int awardedCount;
    private int notAwardedCount;
    private List<String> validationErrors;

    public UUID getPackageId() { return packageId; }
    public void setPackageId(UUID packageId) { this.packageId = packageId; }

    public String getExportDate() { return exportDate; }
    public void setExportDate(String exportDate) { this.exportDate = exportDate; }

    public List<ScosStudentResultDto> getResults() { return results; }
    public void setResults(List<ScosStudentResultDto> results) { this.results = results; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getAwardedCount() { return awardedCount; }
    public void setAwardedCount(int awardedCount) { this.awardedCount = awardedCount; }

    public int getNotAwardedCount() { return notAwardedCount; }
    public void setNotAwardedCount(int notAwardedCount) { this.notAwardedCount = notAwardedCount; }

    public List<String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }
}
