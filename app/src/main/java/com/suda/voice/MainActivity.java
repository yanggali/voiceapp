package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.suda.helloworld.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private ImageView editView,voiceView,searchView;
    private EditText editText;
    private Boolean isEdit;
    private Button send;
    private List<ChatMessage> mData;
    private ChatAdapter mAdapter;
    private TextView preQuestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isEdit = true;
        editView = (ImageView)findViewById(R.id.edit);
        voiceView = (ImageView)findViewById(R.id.voice);
        searchView = (ImageView)findViewById(R.id.search);
        editText = (EditText)findViewById(R.id.editText);
        send = (Button)findViewById(R.id.send);
        preQuestions = (TextView)findViewById(R.id.preQuestions);
        preQuestions.setMovementMethod(new ScrollingMovementMethod());

        ListView mList = (ListView) findViewById(R.id.list);
        mData = new ArrayList<ChatMessage>();
        mData = LoadData();
        mAdapter = new ChatAdapter(MainActivity.this,mData);
        mList.setAdapter(mAdapter);

        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit){
                    editText.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                    voiceView.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);
                    isEdit = false;
                }
                else {
                    editText.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                    voiceView.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                    isEdit = true;

                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //点击屏幕其他地方 隐藏输入框
        findViewById(R.id.mainpage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private List<ChatMessage> LoadData()
    {
        List<ChatMessage> Messages=new ArrayList<ChatMessage>();

        ChatMessage Message;

        Message=new ChatMessage(ChatMessage.Message_From,"山重水复疑无路，柳暗花明又一村。小荷才露尖尖角");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_To,"柳暗花明又一村");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_From,"青青子衿，悠悠我心");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_To,"但为君故，沉吟至今");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_From,"这是你做的Android程序吗？");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_To,"是的，这是一个仿微信的聊天界面");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_From,"为什么下面的消息发送不了呢");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_To,"呵呵，我会告诉你那是直接拿图片做的么");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_From,"哦哦，呵呵，你又在偷懒了");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_To,"因为这一部分不是今天的重点啊");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_From,"好吧，可是怎么发图片啊");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.Message_To,"很简单啊，你继续定义一种布局类型，然后再写一个布局就可以了");
        Messages.add(Message);
        return Messages;
    }
}
