package com.esgi.al2.application.java.levelUp.model;

import java.time.LocalDateTime;

public class Response {

    private String userid;
    private String exerciseid;
    private String codeSent;

    public Response(String userid, String exerciseid, String codeSent) {
        this.userid = userid;
        this.exerciseid = exerciseid;
        this.codeSent = codeSent;
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
}
