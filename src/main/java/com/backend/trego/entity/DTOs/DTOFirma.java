package com.backend.trego.entity.DTOs;

// Datos firmados para subir un archivo (p. ej. una imagen) a Cloudinary.
public class DTOFirma {

    private String firma;
    private Long timestamp;
    private String apiKey;
    private String cloudName;
    private String uploadUrl;
    private String publicId;
    
    public String getFirma() {
        return firma;
    }
    public Long getTimestamp() {
        return timestamp;
    }
    public String getApiKey() {
        return apiKey;
    }
    public String getCloudName() {
        return cloudName;
    }
    public String getUploadUrl() {
        return uploadUrl;
    }
    public String getPublicId() {
        return publicId;
    }
    
    public DTOFirma(String firma, Long timestamp, String apiKey, String cloudName, String uploadUrl, String publicId) {
        this.firma = firma;
        this.timestamp = timestamp;
        this.apiKey = apiKey;
        this.cloudName = cloudName;
        this.uploadUrl = uploadUrl;
        this.publicId = publicId;
    }


}
