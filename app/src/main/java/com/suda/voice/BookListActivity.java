package com.suda.voice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    List<Book> booklist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        ListView booklist_layout = (ListView)findViewById(R.id.book_list);
        String results = getIntent().getStringExtra("results");
        booklist = new LinkedList<Book>();
        JSONArray books = JSON.parseArray(results);
        BookAdapter bookAdapter;
        for (int i=0;i <books.size();i++) {
            JSONObject jsonBook = books.getJSONObject(i);
            final String bid = jsonBook.getString("bid");//获取book对象的参数
            String bname = jsonBook.getString("name");
            String bauthor = jsonBook.getString("author");
            String bpublisher = jsonBook.getString("publisher");
            String isbn = jsonBook.getString("isbn");
            Book book = new Book(bid, bname, bauthor, bpublisher, isbn, null, null, null);
            booklist.add(book);
        }
        bookAdapter = new BookAdapter(BookListActivity.this,booklist);
        booklist_layout.setAdapter(bookAdapter);

        booklist_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
                intent.putExtra("bid",booklist.get(position).getbId());
                startActivity(intent);
            }
        });
    }

}
