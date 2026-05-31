package com.spbutu.gia.assignments.domain.vo;

/**
 * Value Object: прикреплённый файл к заданию или сдаче.
 */
public record AttachedFile(
        String fileName,
        String fileUrl,
        Long fileSize,
        String mimeType
) {
}
