package com.pythe.pojo;

public class TblStudentWithBLOBs extends TblStudent {
    private String description;

    private String collection;

    private String favorite;

    private String complaintlist;

    private String collectSentence;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection == null ? null : collection.trim();
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite == null ? null : favorite.trim();
    }

    public String getComplaintlist() {
        return complaintlist;
    }

    public void setComplaintlist(String complaintlist) {
        this.complaintlist = complaintlist == null ? null : complaintlist.trim();
    }

    public String getCollectSentence() {
        return collectSentence;
    }

    public void setCollectSentence(String collectSentence) {
        this.collectSentence = collectSentence == null ? null : collectSentence.trim();
    }
}