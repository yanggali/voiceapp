package com.suda.voice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;

import java.io.IOException;

public class BookActivity extends AppCompatActivity {
    private ImageView backimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        String bid = getIntent().getStringExtra("bid");
        backimg = (ImageView)findViewById(R.id.back);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //将请求发送至服务器加载书目详情
        OkHttpClientManager.getAsyn("http://voice.tunnel.qydev.com/voice-app/book/bid/"+bid,
                new OkHttpClientManager.ResultCallback<Book>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Book book)
                    {
                        TextView bname = (TextView)findViewById(R.id.bookname);
                        TextView bauthor = (TextView)findViewById(R.id.author);
                        TextView bpublisher = (TextView) findViewById(R.id.publisher);
                        TextView callno = (TextView)findViewById(R.id.callno);
                        TextView isbn = (TextView) findViewById(R.id.isbn);
                        TextView holdstatus = (TextView)findViewById(R.id.hold);
                        bname.setText(bname.getText()+":"+book.getName());
                        bauthor.setText(bauthor.getText()+":"+book.getbAuthor());
                        bpublisher.setText(bpublisher.getText()+":"+book.getbPublisher());
                        isbn.setText(isbn.getText()+":"+book.getIsbn());
                        callno.setText(callno.getText()+":"+book.getCallNo());
                        String hold = "";
                        for (int i = 0 ;i<book.getHoldPlaces().length;i++)
                        {
                            hold += book.getHoldPlaces()[i]+"  "+book.getHoldStatus()[i]+"\n";
                        }
                        holdstatus.setText(holdstatus.getText()+":"+hold);
                    }
                });
        //创建okHttpClient对象
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        //创建一个Request
//        Request request = new Request.Builder()
//                .url("http://voice.tunnel.qydev.com/voice-app/book/bid/"+bid)
//                .build();
//        //new call
//        Call call = mOkHttpClient.newCall(request);
//        //请求加入调度
//        call.enqueue(new Callback()
//        {
//            @Override
//            public void onFailure(Request request, IOException e)
//            {
//            }
//
//            @Override
//            public void onResponse(final Response response) throws IOException
//            {
//                final String getStr =  response.body().string();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView bname = (TextView)findViewById(R.id.bookname);
//                        TextView bauthor = (TextView)findViewById(R.id.author);
//                        TextView bpublisher = (TextView) findViewById(R.id.publisher);
//                        TextView callno = (TextView)findViewById(R.id.callno);
//                        TextView isbn = (TextView) findViewById(R.id.isbn);
//                        TextView holdstatus = (TextView)findViewById(R.id.hold);
//                        JSONObject book_object = JSON.parseObject(getStr);
//                        bname.setText(book_object.getString("name"));
//                        bauthor.setText(book_object.getString("author"));
//                        bpublisher.setText(book_object.getString("publisher"));
//                        isbn.setText(book_object.getString("isbn"));
//                        callno.setText(book_object.getString("callno"));
//                        holdstatus.setText(book_object.getString("holdPlaces")+"\n"+book_object.getString("holdStatus"));
//                    }
//                });
//            }
//        });

    }
}
