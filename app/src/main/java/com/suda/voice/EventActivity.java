package com.suda.voice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;

import java.text.SimpleDateFormat;

public class EventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        String eid = getIntent().getStringExtra("eid");

        //将请求发送至服务器加载书目详情
        OkHttpClientManager.getAsyn(getString(R.string.domain) + "activity/id/" + eid,
                new OkHttpClientManager.ResultCallback<Event>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Event event) {
                        TextView etitle = (TextView) findViewById(R.id.event_name);
                        TextView etype = (TextView) findViewById(R.id.event_type);
                        TextView starttime = (TextView) findViewById(R.id.event_starttime);
                        TextView endtime = (TextView) findViewById(R.id.event_endtime);
                        TextView content = (TextView) findViewById(R.id.event_content);
                        etitle.setText( event.getTitle());
                        etype.setText( event.getType());
                        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String stime = bartDateFormat.format(event.getStartTime());
                        String etime = bartDateFormat.format(event.getEndTime());
                        starttime.setText(stime);
                        endtime.setText(etime);
                        content.setText(event.getContent());
                    }
                });
    }
}
