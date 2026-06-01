package com.spbutu.gia.assignments.domain.vo;

import java.util.List;

/**
 * Конфигурация оценки задания — гибкая настройка критериев.
 * Хранится в JSONB поле scoring_config.
 */
public class ScoringConfig {
    
    /** Тип оценки: weighted | criteria | rubric | pass_fail | exam_5point */
    private String type = "weighted";
    
    /** Список критериев оценки */
    private List<ScoringCriteria> criteria;
    
    /** Максимальный балл за задание (обычно 100) */
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
    
    /** Автоматическая проверка (например, тесты) */
    private boolean autoCheck = false;
    
    /** Разрешить частичный балл */
    private boolean allowPartialScore = true;
    
    /**
     * Критерий оценки с весом и максимальным баллом.
     * Пример: "Моделирование ВКР" — weight=8%, maxPoints=8
     */
    public static class ScoringCriteria {
        /** Название критерия */
        private String name;
        
        /** Описание */
        private String description;
        
        /** Максимальный балл */
        private Integer maxPoints;
        
        /** Вес в процентах (относительно общей оценки) */
        private Integer weight;
        
        /** Обязательный для зачёта */
        private Boolean required;
        
        /** Порядок отображения */
        private Integer order;
        
        // Getters / Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getMaxPoints() { return maxPoints; }
        public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }
        
        public Integer getWeight() { return weight; }
        public void setWeight(Integer weight) { this.weight = weight; }
        
        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }
        
        public Integer getOrder() { return order; }
        public void setOrder(Integer order) { this.order = order; }
    }
    
    /**
     * Пороги для автоматического определения оценки.
     * Пример: 90+ отлично, 75-89 хорошо, 60-74 удовл, 50-59 зачёт, <50 комиссия
     */
    public static class GradeThresholds {
        private Integer excellent;      // 90+
        private Integer good;           // 75-89
        private Integer satisfactory;   // 60-74
        private Integer pass;           // 50-59
        
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
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<ScoringCriteria> getCriteria() { return criteria; }
    public void setCriteria(List<ScoringCriteria> criteria) { this.criteria = criteria; }
    
    public boolean isAutoCheck() { return autoCheck; }
    public void setAutoCheck(boolean autoCheck) { this.autoCheck = autoCheck; }
    
    public boolean isAllowPartialScore() { return allowPartialScore; }
    public void setAllowPartialScore(boolean allowPartialScore) { this.allowPartialScore = allowPartialScore; }
}
