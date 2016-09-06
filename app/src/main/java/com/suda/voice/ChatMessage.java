package com.suda.voice;

/**
 * Created by YangJiali on 2016/8/16 0016.
 */
public class ChatMessage {
    public static final int Message_From = 0;
    public static final int Message_To = 1;

    private int mType;
    private String Content;
    public ChatMessage(){};
    public ChatMessage(int mType, String Content) {
        this.mType = mType;
        this.Content = Content;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
