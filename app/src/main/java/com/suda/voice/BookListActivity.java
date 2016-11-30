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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    private boolean isFirstRow, isLastRow;
    //注册地理位置监听器及定义经纬度
    private LocationManager locationmanger;
    private String locationProvider;
    private double lon;
    private double lat;

    //滚动监听类
    class MyScrollListener implements AbsListView.OnScrollListener {
        /**
         * 当滚动时触发的方法
         * firstVisiableItem--当前list中能见的第一个item下标，也就是当前被滚到最前面的
         * visiableItemCount--当前可以看见的item的总数
         * totalItemcount--总共的item数量
         */
        @Override
        public void onScroll(AbsListView arg0, int firstVisiableItem, int visiableItemCount, int totalItemcount) {
            //判断当前是否滚动到第一行(根据前面对参数的解释相信你能够理解)
            if (firstVisiableItem == 0 && totalItemcount > 0) {
                isFirstRow = true;
            } else {
                //否者不是第一行
                isFirstRow = false;
            }
            //判断是否到了滚动到了最后一行
            if (firstVisiableItem + visiableItemCount == totalItemcount && totalItemcount > 0) {
                isLastRow = true;
            } else {
                isLastRow = false;
            }
        }

        /**
         * 当滚动状态发生变化时触发的方法
         * scrollState--发生的变化的状态,共有：SCROLL_STATE_IDLE(滚动,并且手指已经离开屏幕),SCROLL_STATE_FLING(手指快速滚动时),SCROLL_STATE_TOUCH_SCROLL(手指滚动，但是手指依然在屏幕上)
         */
        @Override
        public void onScrollStateChanged(AbsListView arg0, int scrollState) {
            //这个方法的使用视情况而定,这里不需要用到
        }
    }

    //触摸监听类
    class MyTouchListener implements View.OnTouchListener {
        /**
         * 其中MotionEvent为重要参数，它提供了触摸事件的所有参数和方法
         */

        @Override
        public boolean onTouch(View v, MotionEvent event) {
                    /*
                        这里需要做的几件事:
                        1,当发生down事件时，记录发生该事件的y坐标
                        2,当发生move事件时，判断移动中的y坐标是否能够达成下拉或上拉事件
                        3,当发生up事件时，如果达成上拉或下拉事件时，触发对应事件
                    */

            //为了能够形成视觉上的上拉下托简单效果，需要设置如下变量
            //用于获取显示屏相关内容
            DisplayMetrics dm = getResources().getDisplayMetrics();
            //获取dpi值，以便较为精确的算出移动的像素
            float density = dm.densityDpi / 100;
            //得到布局参数，用它来实现视觉上的拖动效果
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            //设置一个变量来记录原始y坐标
            float or_y = 0f;

            //event中调用getAction()来获取当前触发的是什么事件,这里我们要用到down,move,up这三个事件
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //发生down事件记录原始y坐标,getY()方法可重载，用于判断多点触控，这个不需要判断
                    or_y = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //当手势移动时,获取当前y坐标，并与之前的坐标进行对比，来判断是往上还是往下
                    //设置变量获取移动值，往上托为正值，往下拉为负值
                    int y_dis = (int) (or_y - event.getY());
                    //判断是否达成拖动条件
                    if (isFirstRow && y_dis < 0) {//处于第一行并且继续往下拉动
                        //得到移动距离的绝对值并且算出其移动的相对距离,
                        float distance = Math.abs(y_dis / density / 2);
                        lp.topMargin = (int) distance;
                        //形成往下拉的效果,其中listView为对应的控件(不一定是ListView)
                        booklist_layout.setLayoutParams(lp);
                    } else if (isLastRow && y_dis > 0) {//处于最后一行并且继续网上拉时
                        //同理
                        float distance = Math.abs(y_dis / density / 2);
                        lp.bottomMargin = (int) distance;
                        booklist_layout.setLayoutParams(lp);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("实现下拉");
                    //当发生up事件时,如果达成条件可触发事件，即触发对应事件
//                    if (isFirstRow) {
//                        lp.topMargin = 0;
//                        //调用获取刷新数据的接口方法getNewest();
//                    } else if (isLastRow) {
//                        lp.bottomMargin = 0;
//                        getMore();
//                    }
//                    booklist_layout.setLayoutParams(lp);
                    break;
            }
            return true;
        }
    }
    private void getMore()
    {
        //ArrayList<Book> temp = new ArrayList<>();
        booklist.add(new Book("123", "你好", "杨佳莉", "苏大", "1234", null, null, null));
        bookAdapter.notifyDataSetChanged();
    }

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
        //booklist_layout.setOnTouchListener(new MyTouchListener());
        //booklist_layout.setOnScrollListener(new MyScrollListener());
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
