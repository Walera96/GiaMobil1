package com.spbutu.gia.core.application.dto.testing;

import java.util.List;
import java.util.UUID;

public class QuestionDto {
    private UUID id;
    private String text;
    private String type;
    private Integer points;
    private Integer orderNumber;
    private List<AnswerOptionDto> options;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getOrderNumber() { return orderNumber; }
    public void setOrderNumber(Integer orderNumber) { this.orderNumber = orderNumber; }

    public List<AnswerOptionDto> getOptions() { return options; }
    public void setOptions(List<AnswerOptionDto> options) { this.options = options; }
}
