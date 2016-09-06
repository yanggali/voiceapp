package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.squareup.okhttp.Request;
import com.suda.utils.chatrobot.RobotMessage;
import com.suda.utils.chatrobot.TulingRobot;
import com.suda.utils.http.okhttp.OkHttpClientManager;

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
    private RecognizerDialog iatDialog;
    private ListView mList;
    private List<ChatMessage> messageList = new ArrayList<ChatMessage>();

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

        initData();

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

        //点击语音输入
        voiceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //语音配置对象初始化
                SpeechUtility.createUtility(
                        MainActivity.this, SpeechConstant.APPID + "=57b428c3");
                //有交互动画的语音识别器
                iatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
                iatDialog.setListener(new RecognizerDialogListener() {
                    String resultJson = "[";
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                        System.out.println("-----------------   onResult   -----------------");
                        if (!isLast){
                            resultJson += recognizerResult.getResultString() + ",";
                        } else {
                            resultJson += recognizerResult.getResultString() + "]";
                        }

                        if (isLast) {
                            //解析语音识别后返回的json格式字符串
                            Gson gson = new Gson();
                            List<DictationResult> resultList = gson.fromJson(resultJson,
                                    new TypeToken<List<DictationResult>>(){
                                    }.getType());
                            String result = "";
                            for (int i=0; i<resultList.size()-1; i++){
                                result += resultList.get(i).toString();
                            }
                            send(result);
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {
                        speechError.getPlainDescription(true);
                    }
                });

                iatDialog.show();
            }
        });

        //第position项被点击是触发该方法
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //文字输入模式发送按钮
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString().trim();
                send(input);
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

    /**
     * 加载历史消息，后期从数据库中读取
     */
    public void initData(){
        ChatMessage Entity = new ChatMessage(0,"欢迎回来，很高兴为您服务！");
        messageList.add(Entity);
        mAdapter = new ChatAdapter(MainActivity.this,messageList);
        mList.setAdapter(mAdapter);
    }

    public static final String TAG = "MainActivity";
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS){
                Toast.makeText(MainActivity.this, "初始化失败，错误码：" + code,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 发送消息
     * @param message the content of the message
     */
    public void send(String message){
        editText.setText("");
        SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
        String name = preferences.getString("name","");
        String password = preferences.getString("password","");

        if (!message.equals("")){
            messageList.add(new ChatMessage(ChatMessage.Message_To,message));
            mAdapter.notifyDataSetChanged();
            if(message.indexOf("找书") >= 0)
            {
                //游客登录状态，无查书权限
                if (name.equals("visitor"))
                {
                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.no_authority)));
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                else
                {
                    messageList.add(new ChatMessage(ChatMessage.Message_From,"请说出你要找的书名"));
                    mAdapter.notifyDataSetChanged();
                    return;
                }


            }
            else if (messageList.get(messageList.size()-2).getContent().equals("请说出你要找的书名"))
            {
                OkHttpClientManager.getAsyn("http://voice.tunnel.qydev.com/voice-app/book/name/"+message,
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
                                    if (results.indexOf("not found") > 0){
                                        messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.server_error)));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(MainActivity.this,BookListActivity.class);
                                        intent.putExtra("results",results);
                                        startActivity(intent);
                                    }
                                }
                                else {
                                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.book_not_found)));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
            else
            {
                OkHttpClientManager.getAsyn(TulingRobot.setParams(message),
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
                                messageList.add(new ChatMessage(ChatMessage.Message_From,robotMessage.getText()));
                                mAdapter.notifyDataSetChanged();
                            }
                        });
            }

        }
        else {
            Toast.makeText(MainActivity.this,"说点什么吧",Toast.LENGTH_LONG).show();
        }
    }

//    /**
//     * 发送文字输入的消息
//     */
//    public void send(){
//
//    }

    //聊天框样式展示
    private List<ChatMessage> LoadData()
    {
        List<ChatMessage> Messages=new ArrayList<ChatMessage>();

        ChatMessage Message;

        Message=new ChatMessage(ChatMessage.Message_From,"山重水复疑无路，柳暗花明又一村。小荷才露尖尖角");
        Messages.add(Message);


        return Messages;
    }
}
