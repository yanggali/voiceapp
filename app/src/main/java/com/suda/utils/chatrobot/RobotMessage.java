package com.suda.utils.chatrobot;

/**
 * Created by YangJiali on 2016/9/6 0006.
 * 图灵机器人返回消息
 */
public class RobotMessage {
    public String code;
    public String text;
    public String url;
    public RobotMessage(String code,String text,String url) {
        this.code = code;
        this.text = text;
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
