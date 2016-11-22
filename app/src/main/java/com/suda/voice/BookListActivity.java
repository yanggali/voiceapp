package com.suda.voice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BookListActivity extends AppCompatActivity {
    List<Book> booklist;
    private Spinner spinner;
    private Set<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private BookAdapter bookAdapter;
    private ListView booklist_layout;
    private List<String> publisherList;
    private List<Book> chooseList = new LinkedList<Book>();

    //注册地理位置监听器及定义经纬度
    private LocationManager locationmanger;
    private String locationProvider;
    private double lon;
    private double lat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        //注册地理位置监听器及定义经纬度的具体实现
        locationmanger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationmanger.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationmanger.getLastKnownLocation(locationProvider);
        if (location != null) {
            lon = location.getLongitude();
            lat = location.getLatitude();

        }
        //以上部分为获得经纬度

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 初始化下拉列表
        spinner = (Spinner)findViewById(R.id.spinner);
        data_list = new HashSet<String>();
        booklist_layout = (ListView)findViewById(R.id.book_list);
        String results = getIntent().getStringExtra("results");
        //booklist存放所有的查询结果信息
        booklist = new LinkedList<Book>();
        JSONArray books = JSON.parseArray(results);

        for (int i=0;i <books.size();i++) {
            JSONObject jsonBook = books.getJSONObject(i);
            final String bid = jsonBook.getString("bid");//获取book对象的参数
            String bname = jsonBook.getString("name");
            String bauthor = jsonBook.getString("author");
            String bpublisher = jsonBook.getString("publisher");
            String isbn = jsonBook.getString("isbn");
            //初始化下拉列表
            data_list.add(bpublisher);
            Book temp = new Book(bid, bname, bauthor, bpublisher, isbn, null, null, null);
            booklist.add(temp);
        }
//        bookAdapter = new BookAdapter(BookListActivity.this,booklist);
//        booklist_layout.setAdapter(bookAdapter);
        //将hashset转换成arraylist
        publisherList = new ArrayList<String>(data_list);
        publisherList.add(0,"所有出版社");
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, publisherList);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String choosePublisher = publisherList.get(arg2);
                //final List<Book> chooseList = new LinkedList<Book>();
                if (choosePublisher.equals("所有出版社"))
                {
                    bookAdapter = new BookAdapter(BookListActivity.this,booklist);
                    booklist_layout.setAdapter(bookAdapter);
                }
                else
                {

                    String publisher;
                    for (int i = 0;i < booklist.size();i++)
                    {
                        publisher = booklist.get(i).getbPublisher();
                        //选定出版社的书目列表筛选
                        if (publisher.equals(choosePublisher))
                        {
                            OkHttpClientManager.getAsyn(getString(R.string.domain)+"book/query/"+booklist.get(i).getName()+"/"+publisher+"/10",
                                    new OkHttpClientManager.ResultCallback<List<Book>>()
                                    {
                                        @Override
                                        public void onError(Request request, Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onResponse(List<Book> books)
                                        {
                                            chooseList.clear();
                                            function fun = new function();
                                            for (int i = 0;i < books.size();i++)
                                            {
                                                chooseList.add(books.get(i));
                                            }

                                            //排序功能
                                            fun.sortdis(chooseList,lon,lat);
                                            //

                                            //chooseList中保存了选择出版社之后的书目的所有信息，对chooseList根据地理位置重新排序就好了，这里可以写一个方法，不要直接写在下面
                                            bookAdapter = new BookAdapter(BookListActivity.this,chooseList);
                                            booklist_layout.setAdapter(bookAdapter);
                                        }
                                    });
                        }
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //加载适配器
        spinner.setAdapter(arr_adapter);
        bookAdapter = new BookAdapter(BookListActivity.this,booklist);
        booklist_layout.setAdapter(bookAdapter);

        booklist_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
                intent.putExtra("bid",booklist.get(position).getbId());
                intent.putExtra("bid",booklist.get(position).getbId());
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("book",booklist.get(position));
//                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
