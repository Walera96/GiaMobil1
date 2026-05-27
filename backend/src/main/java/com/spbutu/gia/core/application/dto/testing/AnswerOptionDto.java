package com.spbutu.gia.core.application.dto.testing;

import java.util.UUID;

public class AnswerOptionDto {
    private UUID id;
    private String text;
    private Boolean isCorrect;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}
