package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
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

import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by YangJiali on 2016/8/16 0016.
 */

public class MainActivity extends Activity {
    private ImageView editView,voiceView,searchView;
    private EditText editText;
    private Boolean isEdit;
    private Button send;
    private List<ChatMessage> mData;
    private ChatAdapter mAdapter;
    private TextView preQuestions;
    private ListView mList;
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

        mList = (ListView) findViewById(R.id.list);
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
        //发送请求，初步实现按照书名返回书目列表
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString().trim();
                editText.setText("");
                if (!input.equals("")){
                    //将发送信息显示至聊天框
                    mData.add(new ChatMessage(ChatMessage.Message_To,input));
                    mAdapter = new ChatAdapter(MainActivity.this,mData);
                    mList.setAdapter(mAdapter);
                    OkHttpClientManager.getAsyn("http://voice.tunnel.qydev.com/voice-app/book/name/"+input,
                            new OkHttpClientManager.ResultCallback<String>()
                            {
                                @Override
                                public void onError(Request request, Exception e)
                                {
                                    e.printStackTrace();
                                }
                                @Override
                                public void onResponse(String results)
                                {
                                    if (!results.equals("")){
                                        Intent intent = new Intent(MainActivity.this,BookListActivity.class);
                                        intent.putExtra("results",results);
                                        startActivity(intent);
                                    }
                                    else {
                                        mData.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.book_not_found)));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                    //将请求发送至服务器
//                    OkHttpClient mOkHttpClient = new OkHttpClient();
//                    //创建一个Request
//                    Request request = new Request.Builder()
//                            .url("http://voice.tunnel.qydev.com/voice-app/book/name/"+input)
//                            .build();
//                    //new call
//                    Call call = mOkHttpClient.newCall(request);
//                    //请求加入调度
//                    call.enqueue(new Callback()
//                    {
//                        @Override
//                        public void onFailure(Request request, IOException e)
//                        {
//                        }
//
//                        @Override
//                        public void onResponse(final Response response) throws IOException
//                        {
//                            String getStr =  response.body().string();
//                            Intent intent = new Intent(MainActivity.this,BookListActivity.class);
//                            intent.putExtra("results",getStr);
//                            startActivity(intent);
//                        }
//                   });
                }
                else {
                    mData.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.input_null)));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mList.setSelection(mAdapter.getCount()-1);
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
    //初始化提醒聊天框
    private List<ChatMessage> LoadData()
    {
        List<ChatMessage> Messages=new ArrayList<ChatMessage>();

        ChatMessage Message;

        Message=new ChatMessage(ChatMessage.Message_From,getString(R.string.hint));
        Messages.add(Message);


        return Messages;
    }
}
