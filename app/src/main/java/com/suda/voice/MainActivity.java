package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.io.StringReader;
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
    private String cardid;

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
        //获取用户信息
        SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
        cardid = preferences.getString("cardid","");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 加载历史消息，后期从数据库中读取
     */
    public void initData(){
        ChatMessage Entity = new ChatMessage(0,getString(R.string.tips));
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
        messageList.add(new ChatMessage(ChatMessage.Message_To,message));
        mAdapter.notifyDataSetChanged();
        switch (schema(message))
        {
            case 1:
                messageList.add(new ChatMessage(ChatMessage.Message_From,"请问您要根据什么找书？书名还是作者？"));
                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.add(new ChatMessage(ChatMessage.Message_From,"请说出书名"));
                mAdapter.notifyDataSetChanged();
                break;
            case 3:
                messageList.add(new ChatMessage(ChatMessage.Message_From,"请说出作者"));
                mAdapter.notifyDataSetChanged();
                break;
            case 4:
                getList("book/name/"+message,BookListActivity.class);
                break;
//                OkHttpClientManager.getAsyn(getString(R.string.domain)+"book/name/"+message,
//                        new OkHttpClientManager.ResultCallback<String>()
//                        {
//                            @Override
//                            public void onError(Request request, Exception e)
//                            {
//                                e.printStackTrace();
//                            }
//                            @Override
//                            public void onResponse(String results)
//                            {
//                                if (!results.equals("")){
//                                    if (results.indexOf("not found") > 0){
//                                        messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.server_error)));
//                                        mAdapter.notifyDataSetChanged();
//                                    }
//                                    else
//                                    {
//                                        Intent intent = new Intent(MainActivity.this,BookListActivity.class);
//                                        intent.putExtra("results",results);
//                                        startActivity(intent);
//                                    }
//                                }
//                                else {
//                                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.result_not_found)));
//                                    mAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        });

            case 5:
                getList("book/author/"+message,BookListActivity.class);
                break;
            case 6:
                getList("activity/queryAll",EventListActivity.class);
                break;
            //跳出常见问题列表
            case 7:
                getList("question/queryAll",QuestionListActivity.class);
                break;
            case 8:
                if (cardid.equals("visitor"))
                {
                    messageList.add(new ChatMessage(ChatMessage.Message_From,"您是游客，请登录后查询积分"));
                }
                else
                {
                    OkHttpClientManager.getAsyn(getString(R.string.domain)+"user/queryCredits/"+cardid,
                            new OkHttpClientManager.ResultCallback<String>()
                            {
                                @Override
                                public void onError(Request request, Exception e)
                                {
                                    e.printStackTrace();
                                }
                                @Override
                                public void onResponse(String credit)
                                {
                                    messageList.add(new ChatMessage(ChatMessage.Message_From,"您的积分为"+credit));
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                }
                break;
            case 0:
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
                break;
            default:
                Toast.makeText(MainActivity.this,"说点什么吧",Toast.LENGTH_LONG).show();
                break;
        }
        return;

////            else if (messageList.get(messageList.size()-2).getContent().equals("请说出您要问的问题"))
////            {
////                //对问题进行分词，将所有分词匹配，得到匹配的问题列表
////                Analyzer analyzer = new PaodingAnalyzer();
////                TokenStream tokenStream = analyzer.tokenStream(message,new StringReader(message));
////                try
////                {
////                    Token t;
////                }catch (IOException e)
////                {
////                    e.printStackTrace();
////                }
////            }

    }
    //查询书籍和活动跳转到列表页面
    public void getList(String querychoice , final Class targetActivity)
    {
        OkHttpClientManager.getAsyn(getString(R.string.domain)+querychoice,
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
                                System.out.println(results);
                                Intent intent = new Intent(MainActivity.this,targetActivity);
                                intent.putExtra("results",results);
                                startActivity(intent);
                            }
                        }
                        else {
                            messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.result_not_found)));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    //判断用户要进入的模式
    public int schema(String message)
    {
        //想要找书
        if (message.indexOf("找书") >= 0||message.indexOf("查书") >= 0)
        {
            return 1;
        }
        //已经进入找书模式，需要用户说出书名
        else if (messageList.get(messageList.size()-2).getContent().equals("请问您要根据什么找书？书名还是作者？")&&message.indexOf("书名") >= 0)
        {
            return 2;
        }
        //进入找书模式，需要用户说出作者
        else if (messageList.get(messageList.size()-2).getContent().equals("请问您要根据什么找书？书名还是作者？")&&message.indexOf("作者") >= 0)
        {
            return 3;
        }
        else if (messageList.get(messageList.size()-2).getContent().equals("请说出书名"))
        {
            return 4;
        }
        else if (messageList.get(messageList.size()-2).getContent().equals("请说出作者"))
        {
            return 5;
        }
        //查活动
        else if (message.indexOf("活动") >= 0)
        {
            return 6;
        }
        //查图书馆常见问题
        else if (message.indexOf("问题") >= 0)
        {
            return 7;
        }
        //查个人积分
        else if (message.indexOf("积分") >= 0)
        {
            return 8;
        }
        //输入为空的情况
        else if (message.equals(""))
        {
            return -1;
        }
        //不属于图书馆功能，使用图灵机器人返回信息
        else
        {
            return 0;
        }

    }


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
