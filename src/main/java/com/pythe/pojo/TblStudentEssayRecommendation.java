package com.pythe.pojo;

import java.util.Date;

public class TblStudentEssayRecommendation {
    private Long id;

    private Long studentId;

    private Date deriveTime;

    private Boolean feedback;

    private Integer deriveAlgorithm;

    private String essays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Date getDeriveTime() {
        return deriveTime;
    }

    public void setDeriveTime(Date deriveTime) {
        this.deriveTime = deriveTime;
    }

    public Boolean getFeedback() {
        return feedback;
    }

    public void setFeedback(Boolean feedback) {
        this.feedback = feedback;
    }

    public Integer getDeriveAlgorithm() {
        return deriveAlgorithm;
    }

    public void setDeriveAlgorithm(Integer deriveAlgorithm) {
        this.deriveAlgorithm = deriveAlgorithm;
    }

    public String getEssays() {
        return essays;
    }

    public void setEssays(String essays) {
        this.essays = essays == null ? null : essays.trim();
    }
}