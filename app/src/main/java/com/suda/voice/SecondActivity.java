package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;
import com.suda.voice.R;

public class SecondActivity extends Activity {
    private EditText editText;
    private Button login;
    private Button visitor;
    private Intent it;
    private SharedPreferences sharedinfo;
    private SharedPreferences.Editor editor;
    private ConnectivityManager connectivityManager;//用于判断是否有网络
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        connectivityManager = (ConnectivityManager) SecondActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        final NetworkInfo info = connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
        editText = (EditText)findViewById(R.id.cardid);
        editText.requestFocus();

        findViewById(R.id.loginpage).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        //用户登录
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (info == null)
                {
                    Toast.makeText(SecondActivity.this, "检查网络连接是否打开", Toast.LENGTH_SHORT).show();
                    return;
                }
                //保存用户信息
                final String cardid = ((EditText)findViewById(R.id.cardid)).getText().toString().trim();
                final String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
                if (cardid.equals("") || password.equals(""))
                {
                    Toast.makeText(SecondActivity.this,"请输入卡号和密码",Toast.LENGTH_LONG).show();
                    return;
                }
                //验证登录
                else
                {
                    OkHttpClientManager.postAsyn(getString(R.string.domain)+"user/login",new OkHttpClientManager.ResultCallback<String>()
                    {
                        @Override
                        public void onError(Request request, Exception e) {

                        }
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success"))
                            {
                                sharedinfo = getSharedPreferences("user", Context.MODE_PRIVATE);
                                editor = sharedinfo.edit();
                                editor.putString("cardid",cardid);
                                editor.putString("password",password);
                                editor.commit();
                                Toast.makeText(SecondActivity.this,"用户登录成功",Toast.LENGTH_LONG).show();
                                it = new Intent(SecondActivity.this,MainActivity.class);
                                startActivity(it);
                                SecondActivity.this.finish();
                            }
                            else
                            {
                                Toast.makeText(SecondActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                            }
                        }
                    },new OkHttpClientManager.Param[]{
                            new OkHttpClientManager.Param("cardid", cardid),
                            new OkHttpClientManager.Param("password", password)});
                }
            }
        });
        //游客身份登录
        visitor = (Button)findViewById(R.id.visitor);
        visitor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info == null)
                {
                    Toast.makeText(SecondActivity.this, "检查网络连接是否打开", Toast.LENGTH_SHORT).show();
                    return;
                }
                sharedinfo = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedinfo.edit();
                editor.putString("cardid", "visitor");
                editor.commit();
                Toast.makeText(SecondActivity.this,"游客身份登录",Toast.LENGTH_LONG).show();

                it = new Intent(SecondActivity.this,MainActivity.class);
                startActivity(it);
                SecondActivity.this.finish();
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
}
