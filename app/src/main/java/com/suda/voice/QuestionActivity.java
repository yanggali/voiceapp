package com.suda.voice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;

public class QuestionActivity extends AppCompatActivity {

    private ImageView backimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        String qid = getIntent().getStringExtra("qid");
        backimg = (ImageView)findViewById(R.id.back);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //将请求发送至服务器加载书目详情
        OkHttpClientManager.getAsyn(getString(R.string.domain)+"question/id/"+qid,
                new OkHttpClientManager.ResultCallback<Question>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
//                        e.printStackTrace();
                        TextView qtitle = (TextView)findViewById(R.id.question_title);
                        TextView qanswer = (TextView)findViewById(R.id.question_answer);
                        qtitle.setText("问题");
                        qtitle.setText("答案");
                    }

                    @Override
                    public void onResponse(Question question)
                    {

                        TextView qtitle = (TextView)findViewById(R.id.question_title);
                        TextView qanswer = (TextView)findViewById(R.id.question_answer);
                        qtitle.setText(question.getTitle());
                        qanswer.setText(question.getAnswer());
//                        TextView bpublisher = (TextView) findViewById(R.id.publisher);
//                        TextView callno = (TextView)findViewById(R.id.callno);
//                        TextView isbn = (TextView) findViewById(R.id.isbn);
//                        TextView holdstatus = (TextView)findViewById(R.id.hold);
//                        bname.setText(bname.getText()+":"+question.getName());
//                        bauthor.setText(bauthor.getText()+":"+book.getbAuthor());
//                        bpublisher.setText(bpublisher.getText()+":"+book.getbPublisher());
//                        isbn.setText(isbn.getText()+":"+book.getIsbn());
//                        callno.setText(callno.getText()+":"+book.getCallNo());

                    }
                });

    }
}
