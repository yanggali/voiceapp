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


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.suda.utils.dictionary.Dictionary;
import com.suda.utils.http.okhttp.OkHttpClientManager;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<Integer,String> fuzzybook_List;

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
    //重新返回主界面
    @Override
    protected void onRestart() {
        super.onRestart();
        if(messageList.get(messageList.size()-2).getmType() == 0)
        {
            messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
        }
        else
        {
            messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.issatisfied)));
        }
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
        if (!message.equals(""))
        {
            messageList.add(new ChatMessage(ChatMessage.Message_To,message));
            mAdapter.notifyDataSetChanged();
        }
        else
        {
            messageList.add(new ChatMessage(ChatMessage.Message_From,"说点什么吧"));
            mAdapter.notifyDataSetChanged();
            return;
        }
        switch (schema(message))
        {
            case 1:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.book_by_what)));
                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.book_by_name)));
                mAdapter.notifyDataSetChanged();
                break;
            case 3:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.book_by_author)));
                mAdapter.notifyDataSetChanged();
                break;
            //模糊查询
            case 4:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
                mAdapter.notifyDataSetChanged();
                resultsList(message);
                //getList("book/name/"+message,BookListActivity.class);
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

            case 12:
//                Dictionary dic = new Dictionary();
//                int num = dic.getnum(message);
//                if (num > 0)
//                {
//                    String accurate_name = fuzzybook_List.get(Integer.valueOf(num));
//                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
//                    mAdapter.notifyDataSetChanged();
//                    getList("book/name/"+accurate_name,BookListActivity.class);
//                }
//                else if(Integer.valueOf(message) > 0)
//                {
//                    String accurate_name = fuzzybook_List.get(Integer.valueOf(message));
//                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
//                    mAdapter.notifyDataSetChanged();
//                    getList("book/name/"+accurate_name,BookListActivity.class);
//                }
//                else
//                {
//                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.error)));
//                    mAdapter.notifyDataSetChanged();
//                }
                try {
                    Dictionary dic = new Dictionary();
                    int num = dic.getnum(message);
                    if (num > 0 && num <= 50)
                    {
                        String accurate_name = fuzzybook_List.get(Integer.valueOf(num));
                        messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
                        mAdapter.notifyDataSetChanged();
                        getList("book/name/"+accurate_name,BookListActivity.class);
                    }
                    else if (Integer.valueOf(message) > 0)
                    {
                        String accurate_name = fuzzybook_List.get(Integer.valueOf(message));
                        messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
                        mAdapter.notifyDataSetChanged();
                        getList("book/name/"+accurate_name,BookListActivity.class);
                    }
                    else
                    {
                        messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.error)));
                        mAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e)
                {
                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.error)));
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case 5:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
                mAdapter.notifyDataSetChanged();
                getList("book/author/"+message,BookListActivity.class);
                break;
            case 6:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.wait)));
                mAdapter.notifyDataSetChanged();
                getList("activity/queryAll",EventListActivity.class);
                break;
            //跳出常见问题列表
            case 7:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.ask_question)));
                mAdapter.notifyDataSetChanged();
//                getList("question/queryAll",QuestionListActivity.class);
                break;

            case 8:
                if (cardid.equals("visitor"))
                {
                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.visitor_limit)));
                    messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
                    mAdapter.notifyDataSetChanged();
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
            //问具体问题
            case 9:
                List<Term> term = BaseAnalysis.parse(message).getTerms();
                String keywords = "";
                for (int i = 0; i < term.size(); i++) {
                    String words = term.get(i).getName();// 获取单词
                    String nominal = term.get(i).getNatureStr();// 获取词性
                    if (term.get(i).getNatureStr().equals("v")||term.get(i).getNatureStr().equals("n"))
                    {
                        keywords+=term.get(i).getName()+"&";
                    }
                }
                System.out.println(keywords.substring(0,keywords.length()-1));
//                OkHttpClientManager.getAsyn(getString(R.string.domain)+"question/keyword/"+cardid,
//                        new OkHttpClientManager.ResultCallback<String>()
//                        {
//                            @Override
//                            public void onError(Request request, Exception e)
//                            {
//                                e.printStackTrace();
//                            }
//                            @Override
//                            public void onResponse(String credit)
//                            {
//                                messageList.add(new ChatMessage(ChatMessage.Message_From,"您的积分为"+credit));
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        });
                break;
            //对查询结果满意
            case 10:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.satisfied)));
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.if_end)));
                mAdapter.notifyDataSetChanged();
                break;
            //对查询结果不满意或者满意之后还要继续提问
            case 11:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
                mAdapter.notifyDataSetChanged();
                break;
            //用户退出登录
            case 13:
                Intent it = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(it);
                break;
            case 14:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.choose_accurate_one)));
                mAdapter.notifyDataSetChanged();
                break;
            case 15:
                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
                mAdapter.notifyDataSetChanged();
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
                                System.out.println(robotMessage.getCode());
                                if(robotMessage.getCode().equals("200000"))
                                {
                                    messageList.add(new ChatMessage(ChatMessage.Message_From,robotMessage.getText()+"<a href=\""+robotMessage.getUrl()+"\">"+robotMessage.getUrl()+"</a>"));
                                }
                                else
                                {
                                    messageList.add(new ChatMessage(ChatMessage.Message_From,robotMessage.getText()));
                                }
                                messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
                                mAdapter.notifyDataSetChanged();

                            }
                        });
                break;
            default:
                Toast.makeText(MainActivity.this,"说点什么吧",Toast.LENGTH_LONG).show();
                break;
        }
        return;

    }
    //返回模糊匹配结果
    public void resultsList(String query)
    {
        //清除缓存
        fuzzybook_List = new HashMap<>();
        OkHttpClientManager.getAsyn(getString(R.string.domain)+"/book/key/"+query,
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
                        if (results.equals("")){
                            messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.result_not_found)));
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Set<String> bookname_list = new HashSet<String>();
                            JSONObject booknames = JSON.parseObject(results);
                            StringBuilder sb=new StringBuilder();
                            //最多取前50本书
                            int booksize = 50<booknames.size() ? 50:booknames.size();
                            for (int i=0;i <booksize;i++) {
                                String bname = (String) booknames.get(String.valueOf(i));
                                fuzzybook_List.put(i+1,bname);
                                sb.append(i+1).append("、").append(bname).append("<br/>");
                            }
//                            Iterator<String> it = bookname_list.iterator();
//                            int i = 1;
//                            while (it.hasNext())
//                            {
//                                fuzzybook_List.put(i,it.next());
//                                i++;
//                            }
                            messageList.add(new ChatMessage(ChatMessage.Message_From,sb.toString()));
                            messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.choose_accurate_one)));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
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
                            //messageList.add(new ChatMessage(ChatMessage.Message_From,getString(R.string.tips)));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    //判断用户要进入的模式
    public int schema(String message)
    {
        //想要找书
        Boolean tips = messageList.get(messageList.size()-2).getContent().equals(getString(R.string.tips));

        String last_tip = messageList.get(messageList.size()-2).getContent();
        if (((tips)&&(message.indexOf("1") >= 0||message.indexOf("一")>=0))
                ||message.indexOf("找书") >= 0||message.indexOf("查书") >= 0)
        {
            return 1;
        }
        //已经进入找书模式，需要用户说出书名
        else if (last_tip.equals(getString(R.string.book_by_what))&&(message.indexOf("书名") >= 0||message.indexOf("1")>=0||message.indexOf("一")>=0))
        {
            return 2;
        }
        //进入找书模式，需要用户说出作者
        else if (last_tip.equals(getString(R.string.book_by_what))&&(message.indexOf("作者") >= 0||message.indexOf("2")>=0||message.indexOf("二")>=0))
        {
            return 3;
        }
        else if (last_tip.equals(getString(R.string.book_by_name)))
        {
            return 4;
        }
        else if (last_tip.equals(getString(R.string.choose_accurate_one)))
        {
            return 12;
        }
        else if (last_tip.equals(getString(R.string.book_by_author)))
        {
            return 5;
        }
        //查活动
        else if (message.indexOf("活动") >= 0||(last_tip.equals(getString(R.string.tips))
                &&(message.indexOf("2") >= 0||message.indexOf("二") >= 0)))
        {
            return 6;
        }
        //查图书馆常见问题
        else if (((tips)&&(message.indexOf("3") >= 0||message.indexOf("三")>=0))
                ||message.indexOf("问题") >= 0)
        {
            return 7;
        }
        //查个人积分
        else if (((tips)&&(message.indexOf("4") >= 0||message.indexOf("四")>=0))
                ||message.indexOf("积分") >= 0)
        {
            return 8;
        }
        else if (last_tip.equals(getString(R.string.ask_question)))
        {
            return 9;
        }
        else if(last_tip.equals(getString(R.string.issatisfied))&&
                (message.indexOf("满意")>=0||message.indexOf("一")>=0||message.indexOf("1")>=0))
        {
            return 10;
        }
        else if(last_tip.equals(getString(R.string.issatisfied))&&
                (message.indexOf("不满意")>=0||message.indexOf("二")>=0||message.indexOf("2")>=0))
        {
            return 11;
        }
        else if (last_tip.equals(getString(R.string.result_not_found)))
        {
            String schema = messageList.get(messageList.size()-5).getContent();
            if (schema.equals(getString(R.string.book_by_name)))
            {
                return 4;
            }
            else if (schema.equals(getString(R.string.book_by_author)))
            {
                return 5;
            }
            else if (schema.equals(getString(R.string.choose_accurate_one)))
            {
                return 12;
            }
            else
            {
                return 11;
            }
        }
        else if (last_tip.equals(getString(R.string.if_end))&&(message.indexOf("1")>=0||message.indexOf("一") >= 0))
        {
            return 13;
        } else if (last_tip.equals(getString(R.string.if_end)) && (message.indexOf("2") >= 0 || message.indexOf("二") >= 0)) {
            return 11;
        }
        else if (last_tip.equals(getString(R.string.error))&&(message.indexOf("1") >= 0 || message.indexOf("一") >= 0))
        {
            return 14;
        }
        else if (last_tip.equals(getString(R.string.error))&&(message.indexOf("2") >= 0 || message.indexOf("二") >= 0))
        {
            return 15;
        }
        //输入为空的情况
        else if (message.equals("")) {
            return -1;
        }
        //不属于图书馆功能，使用图灵机器人返回信息
        else {
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
