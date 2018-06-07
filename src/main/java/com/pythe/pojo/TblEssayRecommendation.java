package com.pythe.pojo;

public class TblEssayRecommendation {
    private Long essayid;

    private Integer type;

    private String recommendation;

    public Long getEssayid() {
        return essayid;
    }

    public void setEssayid(Long essayid) {
        this.essayid = essayid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation == null ? null : recommendation.trim();
    }
}