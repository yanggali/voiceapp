package com.suda.voice;

/**
 * Edited by YangJiali on 2016/9/11 0011.
 */
public class Event {
    private String id;
    private String type;
    private String title;
    private String content;
    private String starttime;
    private String endtime;

    public Event() {
    }

    public Event(String id, String type, String title, String content, String startTime, String endTime) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.starttime = startTime;
        this.endtime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartTime() {
        return starttime;
    }

    public void setStartTime(String startTime) {
        this.starttime = startTime;
    }

    public String getEndTime() {
        return endtime;
    }

    public void setEndTime(String endTime) {
        this.endtime = endTime;
    }
}
