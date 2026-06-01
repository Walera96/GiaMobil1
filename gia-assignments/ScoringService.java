package com.spbutu.gia.assignments.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.spbutu.gia.assignments.domain.Assignment;
import com.spbutu.gia.assignments.domain.AssignmentSubmission;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Сервис автоматического расчёта оценки по критериям.
 * Адаптирован под реальный сценарий преподавателя (Excel-таблица).
 */
@Service
public class ScoringService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Рассчитать итоговую оценку на основе процентов по критериям.
     * 
     * @param config   конфигурация оценки
     * @param score    JSON с процентами по каждому критерию
     * @return объект с детальной разбивкой и итогом
     */
    public ScoreResult calculateScore(ScoringConfig config, JsonNode score) {
        if (config == null || config.getCriteria() == null || score == null) {
            return null;
        }

        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode criteriaScores = objectMapper.createArrayNode();
        
        BigDecimal totalPoints = BigDecimal.ZERO;
        BigDecimal totalMaxPoints = BigDecimal.ZERO;
        
        for (ScoringConfig.ScoringCriteria criteria : config.getCriteria()) {
            String criteriaName = criteria.getName();
            Integer maxPoints = criteria.getMaxPoints();
            Integer weight = criteria.getWeight();
            
            // Ищем процент выполнения критерия в score JSON
            BigDecimal percentage = BigDecimal.ZERO;
            if (score.has(criteriaName)) {
                percentage = new BigDecimal(score.get(criteriaName).asText());
            } else if (score.has(normalizeKey(criteriaName))) {
                percentage = new BigDecimal(score.get(normalizeKey(criteriaName)).asText());
            }
            
            // Баллы = процент × максимальный балл
            BigDecimal points = percentage.multiply(new BigDecimal(maxPoints))
                .setScale(2, RoundingMode.HALF_UP);
            
            // Взвешенный балл = баллы × (вес / 100) — если нужно нормализовать
            // В Excel: процент × вес = итог. Но у нас maxPoints уже = вес.
            // Поэтому points = percentage × maxPoints (maxPoints = вес в баллах)
            
            ObjectNode criteriaResult = objectMapper.createObjectNode();
            criteriaResult.put("criteria_id", criteriaName);
            criteriaResult.put("criteria_name", criteriaName);
            criteriaResult.put("percentage", percentage.doubleValue());
            criteriaResult.put("points", points.doubleValue());
            criteriaResult.put("max_points", maxPoints);
            criteriaResult.put("weight", weight);
            
            criteriaScores.add(criteriaResult);
            totalPoints = totalPoints.add(points);
            totalMaxPoints = totalMaxPoints.add(new BigDecimal(maxPoints));
        }
        
        result.set("criteria_scores", criteriaScores);
        result.put("total_score", totalPoints.doubleValue());
        result.put("max_total_score", totalMaxPoints.doubleValue());
        
        // Определяем итоговую оценку по порогам
        String grade = determineGrade(config, totalPoints);
        result.put("grade", grade);
        result.put("passed", totalPoints.compareTo(new BigDecimal(config.getPassingScore())) >= 0);
        
        ScoreResult sr = new ScoreResult();
        sr.setScoreJson(result);
        sr.setTotalScore(totalPoints);
        sr.setGrade(grade);
        sr.setPassed(totalPoints.compareTo(new BigDecimal(config.getPassingScore())) >= 0);
        
        return sr;
    }
    
    /**
     * Определить оценку по порогам.
     */
    private String determineGrade(ScoringConfig config, BigDecimal totalScore) {
        ScoringConfig.GradeThresholds t = config.getThresholds();
        if (t == null) {
            return totalScore.compareTo(new BigDecimal(config.getPassingScore())) >= 0 ? "зачет" : "не зачет";
        }
        
        int score = totalScore.intValue();
        if (t.getExcellent() != null && score >= t.getExcellent()) return "отлично";
        if (t.getGood() != null && score >= t.getGood()) return "хорошо";
        if (t.getSatisfactory() != null && score >= t.getSatisfactory()) return "удовл";
        if (t.getPass() != null && score >= t.getPass()) return "зачет";
        return "на комиссию";
    }
    
    /**
     * Нормализовать ключ для JSON (убрать пробелы, нижний регистр).
     */
    private String normalizeKey(String key) {
        return key.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
    
    /**
     * Результат расчёта оценки.
     */
    public static class ScoreResult {
        private JsonNode scoreJson;
        private BigDecimal totalScore;
        private String grade;
        private boolean passed;
        
        public JsonNode getScoreJson() { return scoreJson; }
        public void setScoreJson(JsonNode scoreJson) { this.scoreJson = scoreJson; }
        
        public BigDecimal getTotalScore() { return totalScore; }
        public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
        
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
    }
}
