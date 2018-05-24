package com.pythe.pojo;

import java.util.Date;

public class TblStudent {
    private Long studentid;

    private String username;

    private String userimg;

    private String password;

    private String phonenum;

    private Integer gradeid;

    private String mnp1Openid;

    private Date created;

    private Date updated;

    private String openid;

    private Integer status;

    private String knowlegespace;

    private String shares;

    private Integer level;

    private String type;

    private String token;

    private String wxtoken;

    private String unionId;

    private Integer score;

    public Long getStudentid() {
        return studentid;
    }

    public void setStudentid(Long studentid) {
        this.studentid = studentid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg == null ? null : userimg.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum == null ? null : phonenum.trim();
    }

    public Integer getGradeid() {
        return gradeid;
    }

    public void setGradeid(Integer gradeid) {
        this.gradeid = gradeid;
    }

    public String getMnp1Openid() {
        return mnp1Openid;
    }

    public void setMnp1Openid(String mnp1Openid) {
        this.mnp1Openid = mnp1Openid == null ? null : mnp1Openid.trim();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getKnowlegespace() {
        return knowlegespace;
    }

    public void setKnowlegespace(String knowlegespace) {
        this.knowlegespace = knowlegespace == null ? null : knowlegespace.trim();
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares == null ? null : shares.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getWxtoken() {
        return wxtoken;
    }

    public void setWxtoken(String wxtoken) {
        this.wxtoken = wxtoken == null ? null : wxtoken.trim();
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId == null ? null : unionId.trim();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}