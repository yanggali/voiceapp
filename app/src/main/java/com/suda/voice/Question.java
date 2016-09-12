package com.suda.voice;

/**
 * Edited by YangJiali on 2016/9/11 0011.
 */
public class Question {
    private String id;
    private String title;
    private String answer;

    public Question() {
    }

    public Question(String id, String question, String answer) {
        this.id = id;
        this.title = question;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
