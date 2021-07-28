package com.esgi.al2.application.java.levelUp.model;

import java.time.LocalDateTime;

public class ResponseApi {

    private String id;

    private String userid;

    private String exerciseid;

    private String codeSent;

    private String status;

    private String resultconsole;

    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getExerciseid() {
        return exerciseid;
    }

    public void setExerciseid(String exerciseid) {
        this.exerciseid = exerciseid;
    }

    public String getCodeSent() {
        return codeSent;
    }

    public void setCodeSent(String codeSent) {
        this.codeSent = codeSent;
    }

    public String getResultconsole() {
        return codeSent;
    }

    public void setResultconsole(String resultconsole) {
        this.codeSent = resultconsole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}