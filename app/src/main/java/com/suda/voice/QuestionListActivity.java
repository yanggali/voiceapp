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

public class QuestionListActivity extends AppCompatActivity {

    List<Question> questionlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        ListView questionlist_layout = (ListView)findViewById(R.id.question_list);
        String results = getIntent().getStringExtra("results");
        questionlist = new LinkedList<Question>();
        JSONArray questions = JSON.parseArray(results);
        QuestionAdapter questionAdapter;
        for (int i=0;i <questions.size();i++) {
            JSONObject jsonEvent = questions.getJSONObject(i);
            final String qid = jsonEvent.getString("id");//获取book对象的参数
            String title = jsonEvent.getString("title");
            String answer = jsonEvent.getString("answer");
            Question question = new Question(qid,title,answer);
            questionlist.add(question);
        }
        questionAdapter = new QuestionAdapter(QuestionListActivity.this,questionlist);
        questionlist_layout.setAdapter(questionAdapter);

        questionlist_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QuestionListActivity.this, QuestionActivity.class);
                intent.putExtra("qid",questionlist.get(position).getId());
                startActivity(intent);
            }
        });
    }
}
