package com.spbutu.gia.assignments.dto;

import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO для сдачи задания студентом.
 */
public class SubmissionDto {

    @NotNull(message = "Файлы решения обязательны")
    private List<AttachedFile> solutionFiles;

    private String studentComment;

    // Getters / Setters
    public List<AttachedFile> getSolutionFiles() { return solutionFiles; }
    public void setSolutionFiles(List<AttachedFile> solutionFiles) { this.solutionFiles = solutionFiles; }

    public String getStudentComment() { return studentComment; }
    public void setStudentComment(String studentComment) { this.studentComment = studentComment; }
}
