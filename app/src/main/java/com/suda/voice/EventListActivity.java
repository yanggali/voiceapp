package com.suda.voice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {
    List<Event> eventlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ListView eventlist_layout = (ListView)findViewById(R.id.event_list);
        String results = getIntent().getStringExtra("results");
        eventlist = new LinkedList<Event>();
        JSONArray events = JSON.parseArray(results);
        EventAdapter eventAdapter;
        for (int i=0;i <events.size();i++) {
            JSONObject jsonEvent = events.getJSONObject(i);
            final String eid = jsonEvent.getString("id");//获取book对象的参数
            String title = jsonEvent.getString("title");
            String type = jsonEvent.getString("type");
            String content = jsonEvent.getString("content");
            String starttime = jsonEvent.getString("starttime");
            String endtime = jsonEvent.getString("endtime");
            Event event = new Event(eid,title,type,content,starttime,endtime);
            eventlist.add(event);
        }
        eventAdapter = new EventAdapter(EventListActivity.this,eventlist);
        eventlist_layout.setAdapter(eventAdapter);

//        eventlist_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(EventListActivity.this, EventActivity.class);
//                intent.putExtra("eid",eventlist.get(position).getId());
//                startActivity(intent);
//            }
//        });
    }
}
