package com.spbutu.gia.core.application.dto.testing;

import java.util.List;
import java.util.UUID;

public class SubmitTestRequest {
    private UUID attemptId;
    private List<StudentAnswerInput> answers;

    public UUID getAttemptId() { return attemptId; }
    public void setAttemptId(UUID attemptId) { this.attemptId = attemptId; }

    public List<StudentAnswerInput> getAnswers() { return answers; }
    public void setAnswers(List<StudentAnswerInput> answers) { this.answers = answers; }

    public static class StudentAnswerInput {
        private UUID questionId;
        private UUID selectedOptionId;
        private String textAnswer;

        public UUID getQuestionId() { return questionId; }
        public void setQuestionId(UUID questionId) { this.questionId = questionId; }

        public UUID getSelectedOptionId() { return selectedOptionId; }
        public void setSelectedOptionId(UUID selectedOptionId) { this.selectedOptionId = selectedOptionId; }

        public String getTextAnswer() { return textAnswer; }
        public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    }
}
