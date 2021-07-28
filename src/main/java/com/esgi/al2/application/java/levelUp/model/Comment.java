package com.esgi.al2.application.java.levelUp.model;

public class Comment {

    private String responseid;

    private String userid;

    private String content;

    public Comment(String responseid, String userid, String content) {
        this.responseid = responseid;
        this.userid = userid;
        this.content = content;
    }

    public String getResponseid() {
        return responseid;
    }

    public void setResponseid(String responseid) {
        this.responseid = responseid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
