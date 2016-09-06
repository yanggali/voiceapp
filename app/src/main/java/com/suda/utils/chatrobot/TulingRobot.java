package com.suda.utils.chatrobot;

import android.content.Intent;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;
import com.suda.voice.BookListActivity;
import com.suda.voice.ChatMessage;
import com.suda.voice.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by YangJiali on 2016/9/6 0006.
 * 图灵聊天机器人
 */
public class TulingRobot {
    private static String API_KEY = "3914cb17f5f942609fefa061cc8a29e4";
    private static String URL = "http://www.tuling123.com/openapi/api";
    private static ChatMessage message= new ChatMessage();
    /**
     * 发送一个消息，并得到返回的消息
     * @param msg
     * @return
     */
    public static ChatMessage sendMsg(String msg) {
        //查询Url
        String requestUrl = setParams(msg);
        OkHttpClientManager.getAsyn(requestUrl,
                new OkHttpClientManager.ResultCallback<RobotMessage>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(RobotMessage robotMessage)
                    {
                        message = new ChatMessage(ChatMessage.Message_From,robotMessage.getText());
                    }
                });
        return message;
    }
    /**
     * 拼接Url
     * @param msg
     * @return
     */
    public static String setParams(String msg) {
        /** 利用Java中URLEncoder对其进行编码，如果不能实现，抛出异常 */
        try {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return URL + "?key=" + API_KEY + "&info=" + msg;
    }
}
