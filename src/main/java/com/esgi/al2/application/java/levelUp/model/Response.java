package com.esgi.al2.application.java.levelUp.model;

import java.time.LocalDateTime;

public class Response {

    private String user_id;
    private String exercise_id;
    private String codeSent;

    public Response(String user_id, String exercise_id, String codeSent) {
        this.user_id = user_id;
        this.exercise_id = exercise_id;
        this.codeSent = codeSent;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(String exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getCodeSent() {
        return codeSent;
    }

    public void setCodeSent(String codeSent) {
        this.codeSent = codeSent;
    }
}
