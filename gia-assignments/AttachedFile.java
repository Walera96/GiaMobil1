package com.spbutu.gia.assignments.domain.vo;

/**
 * Файл, прикреплённый к заданию или сдаче.
 * Хранится в JSONB.
 */
public class AttachedFile {
    
    /** Имя файла */
    private String fileName;
    
    /** MIME-тип */
    private String contentType;
    
    /** Размер в байтах */
    private Long size;
    
    /** URL для скачивания или ID в хранилище */
    private String url;
    
    /** Время загрузки */
    private String uploadedAt;
    
    // Getters / Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
