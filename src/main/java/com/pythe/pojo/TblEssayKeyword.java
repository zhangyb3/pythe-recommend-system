package com.pythe.pojo;

public class TblEssayKeyword {
    private Long essayId;

    private Integer essayType;

    private Integer grade;

    private String keyword;

    public Long getEssayId() {
        return essayId;
    }

    public void setEssayId(Long essayId) {
        this.essayId = essayId;
    }

    public Integer getEssayType() {
        return essayType;
    }

    public void setEssayType(Integer essayType) {
        this.essayType = essayType;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }
}