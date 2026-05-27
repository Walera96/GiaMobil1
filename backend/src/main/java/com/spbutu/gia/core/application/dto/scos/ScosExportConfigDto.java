package com.spbutu.gia.core.application.dto.scos;

import java.util.UUID;

public class ScosExportConfigDto {
    private UUID id;
    private String directionCode;
    private String scosDirectionCode;
    private String scosDirectionName;
    private Boolean isActive;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getScosDirectionCode() { return scosDirectionCode; }
    public void setScosDirectionCode(String scosDirectionCode) { this.scosDirectionCode = scosDirectionCode; }

    public String getScosDirectionName() { return scosDirectionName; }
    public void setScosDirectionName(String scosDirectionName) { this.scosDirectionName = scosDirectionName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
