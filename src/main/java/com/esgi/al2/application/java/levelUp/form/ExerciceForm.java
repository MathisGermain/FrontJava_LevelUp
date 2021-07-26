package com.esgi.al2.application.java.levelUp.form;

public class ExerciceForm {

    private Integer exerciceId;
    private String statement;
    private String title;
    private String code;
    private String langage;


    public Integer getExerciceId() {
        return exerciceId;
    }

    public void setExerciceId(Integer exerciceId) {
        this.exerciceId = exerciceId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLangage() {
        return langage;
    }

    public void setLangage(String langage) {
        this.langage = langage;
    }
}
