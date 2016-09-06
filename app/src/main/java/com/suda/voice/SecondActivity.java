package com.suda.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suda.voice.R;

public class SecondActivity extends Activity {
    private EditText editText;
    private Button login;
    private Button visitor;
    private Intent it;
    private SharedPreferences sharedinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        editText = (EditText)findViewById(R.id.username);
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
                //保存用户信息
                sharedinfo = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedinfo.edit();
                String name = ((EditText)findViewById(R.id.username)).getText().toString().trim();
                String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
                if (name.equals("") || password.equals(""))
                {
                    Toast.makeText(SecondActivity.this,"请输入用户名和密码",Toast.LENGTH_LONG).show();
                    return;
                }
                editor.putString("name",name);
                editor.putString("password",password);
                editor.commit();
                Toast.makeText(SecondActivity.this,"用户登录成功",Toast.LENGTH_LONG).show();

                it = new Intent(SecondActivity.this,MainActivity.class);
                startActivity(it);
                SecondActivity.this.finish();
            }
        });
        //游客身份登录
        visitor = (Button)findViewById(R.id.visitor);
        visitor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedinfo = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedinfo.edit();
                editor.putString("name", "visitor");
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
