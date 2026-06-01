package com.spbutu.gia.assignments.domain.vo;

import java.util.List;

/**
 * Гибкая конфигурация оценки — адаптирована под реальный сценарий преподавателя.
 * Поддерживает: весовые критерии, накопительную систему, семестры.
 */
public class ScoringConfig {

    /** Тип системы оценки */
    private String type = "weighted"; // weighted, criteria, pass_fail, exam_5point

    /** Список критериев с весами (как в Excel) */
    private List<ScoringCriteria> criteria;

    /** Максимальный балл (обычно 100) */
    private Integer maxTotalScore = 100;

    /** Пороги для автоматической оценки */
    private GradeThresholds thresholds;

    /** Можно ли пересдавать / перезагружать */
    private boolean allowRetake = true;

    /** Накопительная система (баллы переносятся между семестрами) */
    private boolean cumulative = false;

    /** Семестр (1, 2) */
    private Integer semester;

    /** Проходной балл */
    private Integer passingScore = 60;

    /**
     * Критерий оценки с весом.
     * Пример: "Моделирование ВКР" — weight=8%, maxPoints=8
     */
    public static class ScoringCriteria {
        private String name;           // Название
        private String description;    // Описание
        private Integer weight;        // Вес в процентах (8%)
        private Integer maxPoints;     // Максимальный балл (8)
        private Boolean required;      // Обязательный для зачета
        private Integer order;         // Порядок отображения

        // Getters / Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getWeight() { return weight; }
        public void setWeight(Integer weight) { this.weight = weight; }

        public Integer getMaxPoints() { return maxPoints; }
        public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }

        public Integer getOrder() { return order; }
        public void setOrder(Integer order) { this.order = order; }
    }

    /**
     * Пороги для автоматического определения оценки.
     */
    public static class GradeThresholds {
        private Integer excellent;   // 90+ (отлично)
        private Integer good;        // 75-89 (хорошо)
        private Integer satisfactory; // 60-74 (удовл)
        private Integer pass;         // 50-59 (зачет)
        // < 50 — на комиссию / сдает тест

        // Getters / Setters
        public Integer getExcellent() { return excellent; }
        public void setExcellent(Integer excellent) { this.excellent = excellent; }

        public Integer getGood() { return good; }
        public void setGood(Integer good) { this.good = good; }

        public Integer getSatisfactory() { return satisfactory; }
        public void setSatisfactory(Integer satisfactory) { this.satisfactory = satisfactory; }

        public Integer getPass() { return pass; }
        public void setPass(Integer pass) { this.pass = pass; }
    }

    // Getters / Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<ScoringCriteria> getCriteria() { return criteria; }
    public void setCriteria(List<ScoringCriteria> criteria) { this.criteria = criteria; }

    public Integer getMaxTotalScore() { return maxTotalScore; }
    public void setMaxTotalScore(Integer maxTotalScore) { this.maxTotalScore = maxTotalScore; }

    public GradeThresholds getThresholds() { return thresholds; }
    public void setThresholds(GradeThresholds thresholds) { this.thresholds = thresholds; }

    public boolean isAllowRetake() { return allowRetake; }
    public void setAllowRetake(boolean allowRetake) { this.allowRetake = allowRetake; }

    public boolean isCumulative() { return cumulative; }
    public void setCumulative(boolean cumulative) { this.cumulative = cumulative; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }
}
