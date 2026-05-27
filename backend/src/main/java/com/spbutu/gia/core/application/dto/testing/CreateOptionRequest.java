package com.spbutu.gia.core.application.dto.testing;

public class CreateOptionRequest {
    private String text;
    private Boolean isCorrect;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}
