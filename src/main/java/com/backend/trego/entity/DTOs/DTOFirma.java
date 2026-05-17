package com.backend.trego.entity.DTOs;

/**
 * DTO con los datos firmados para subir un archivo
 * (por ejemplo, una imagen) a un servicio externo como Cloudinary.
 */
public class DTOFirma {

    private String firma;
    private Long timestamp;
    private String apiKey;
    private String cloudName;
    private String uploadUrl;
    private String publicId;

    public DTOFirma() {
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
}
