package com.suda.voice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.squareup.okhttp.Request;
import com.suda.utils.http.okhttp.OkHttpClientManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class BookListActivity extends AppCompatActivity implements OnScrollListener{
    private List<Book> booklist;
    private List<Book> showbooklist;
    private Spinner spinner;
    private TextView load;
    private View view_more;
    private ProgressBar pb;
    private TextView tvLoad;
    private Set<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private BookAdapter bookAdapter;
    private ListView booklist_layout;
    private List<String> publisherList;
    private List<Book> chooseList = new LinkedList<Book>();
    private String choosePublisher;
    private boolean isFirstRow, isLastRow;
    //注册地理位置监听器及定义经纬度
    private LocationClient client = null;
    private LocationClientOption mOption;
    private static double lon;
    private static double lat;


    private int totalCounts;
    private int lastVisibleIndex = 0;
    private ArrayAdapter<Book> adapter;
    // 创建handler接收消息并处理消息
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    // 创建adapter
//                    adapter = new ArrayAdapter<Book>(BookListActivity.this,
//                            android.R.layout.simple_list_item_1, booklist);
                    bookAdapter = new BookAdapter(BookListActivity.this,showbooklist);
                    booklist_layout.setAdapter(bookAdapter);
                    // 添加底部加载布局
                    booklist_layout.addFooterView(view_more);
                    //Log.e("TAG", "lastVisibleIndex = " + showbooklist.size());
                    // 设置监听
                    setListeners();
                    break;
                case 1:
                    bookAdapter.notifyDataSetChanged();
                    break;
            }
        };
    };
    private void setListeners() {
        if (totalCounts > 10) {
            // listView设置滑动简监听
            booklist_layout.setOnScrollListener(this);
        } else {
            // 假如数据总数少于等于10条，直接移除底部的加载布局，不需要再加载更多的数据
            booklist_layout.removeFooterView(view_more);
        }
    }

    private void initData() {
        // 模拟网络请求获取数据，一次获取10条
        new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        showbooklist.add(booklist.get(i));
                        //showbooklist.add(new Book(String.valueOf(i),String.valueOf(i),null,null,null,null,null,null));
                    }
                    lastVisibleIndex += 10;
                    Log.e("TAG", "lastVisibleIndex = " + showbooklist.size());
                    // 给handler发消息更新UI，子线程不可以更新UI
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    /**
     * 监听listView的滑动状态的改变
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.e("TAG", "lastVisibleIndex = " + lastVisibleIndex);
        Log.e("TAG", "adapter.getCount() = " + bookAdapter.getCount());
        pb.setVisibility(View.VISIBLE);
        tvLoad.setVisibility(View.VISIBLE);
        booklist_layout.addFooterView(view_more);
        // 滑到底部后自动加载，判断listView已经停止滚动并且最后可视的条目等于adapter的条目
        // 注意这里在listView设置好adpter后，加了一个底部加载布局。
        // 所以判断条件为：lastVisibleIndex == adapter.getCount()
        if (scrollState == SCROLL_STATE_IDLE
                && lastVisibleIndex+1 == bookAdapter.getCount()) {
            /**
             * 这里也要设置为可见，是因为当你真正从网络获取数据且获取失败的时候。
             * 我在失败的方法里面，隐藏了底部的加载布局并提示用户加载失败。所以再次监听的时候需要
             * 继续显示隐藏的控件。因为我模拟的获取数据，失败的情况这里不给出。实际中简单的加上几句代码就行了。
             */

            loadMoreData();// 加载更多数据
        }
    }

    private void initData(final int start, final int end) {
        // 模拟网络请求获取数据，一次获取10条
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);// 模拟获取数据时的耗时1s
                    for (int i = start; i < end; i++) {
                        showbooklist.add(booklist.get(i));
                    }
                    // 给handler发消息更新UI，子线程不可以更新UI
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }


    private void loadMoreData() {
        // 获取此时adapter中的总条目数
        int start,end;
        int count = bookAdapter.getCount();
        // 一次加载10条数据，即下拉加载的执行
        if (count + 10 < totalCounts) {
            start = count;
            end = start + 10;
            initData(start, end);// 模拟网络获取数据操作
        } else {// 数据不足10条直接加载到结束
            start = count;
            end = totalCounts-1;
            initData(start, end);// 模拟网络获取数据曹祖
            // 数据全部加载完成后，移除底部的view
            //booklist_layout.removeFooterView(view_more);
            Toast.makeText(BookListActivity.this, "数据已经全部加载",1).show();
        }
    }

    /**
     * 监听listView的滑动
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 计算最后可见条目的索引
        lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
        // 当adapter中的所有条目数已经和要加载的数据总条数相等时，则移除底部的View
        if (totalItemCount == totalCounts + 1) {
            // 移除底部的加载布局
            booklist_layout.removeFooterView(view_more);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        initView();
        //获得经纬度
        registerLocation();
        //初始化书目列表
        initBooklist();

        //填充listlayout
        initData();


        //填充list
        //bookAdapter = new BookAdapter(BookListActivity.this,booklist);
        //booklist_layout.setAdapter(bookAdapter);
        System.out.println(totalCounts);
        //初始化下拉列表
        initSpinner();
        booklist_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
                if (("所有出版社").equals(choosePublisher))
                {
                    intent.putExtra("bid",booklist.get(position).getbId());
                }
                else
                {
                    intent.putExtra("bid",chooseList.get(position).getbId());
                }
                startActivity(intent);
            }
        });
    }
    private void initView()
    {

        spinner = (Spinner) findViewById(R.id.spinner);
        booklist_layout = (ListView)findViewById(R.id.book_list);
        load = (TextView)findViewById(R.id.tv_Load);
        // 构建底部加载布局
        view_more = (View) getLayoutInflater()
                .inflate(R.layout.view_more, null);
        // 进度条
        pb = (ProgressBar) view_more.findViewById(R.id.progressBar);
        // “正在加载...”文本控件
        tvLoad = (TextView) view_more.findViewById(R.id.tv_Load);
    }

    private void registerLocation()
    {
        client = new LocationClient(this);
        mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        client.setLocOption(mOption);
    }
    //初始化书目列表和下拉框列表
    private void initBooklist()
    {
        String results = getIntent().getStringExtra("results");

        data_list = new HashSet<String>();
        booklist = new LinkedList<Book>();
        showbooklist = new LinkedList<Book>();
        JSONArray books = JSON.parseArray(results);
        totalCounts = books.size();
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
    }
    //初始化下拉框
    private void initSpinner()
    {
        publisherList = new ArrayList<String>(data_list);
        publisherList.add(0,"所有出版社");
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, publisherList);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加事件Spinner事件监听

        client.registerLocationListener(mListener);
        client.start();
        spinner.setAdapter(arr_adapter);

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                choosePublisher = publisherList.get(arg2);
                System.out.println(choosePublisher);
                //final List<Book> chooseList = new LinkedList<Book>();
                if (choosePublisher.equals("所有出版社"))
                {
                    bookAdapter = new BookAdapter(BookListActivity.this,booklist);
                    booklist_layout.setAdapter(bookAdapter);
                }
                else
                {
                    chooseList.clear();
                    OkHttpClientManager.getAsyn(getString(R.string.domain)+"book/query/"+booklist.get(0).getName()+"/"+choosePublisher+"/10",
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

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //加载适配器
        spinner.setAdapter(arr_adapter);*/

    }
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                lon = location.getLongitude();
                lat = location.getLatitude();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        choosePublisher = publisherList.get(arg2);
                        System.out.println(choosePublisher);
                        //final List<Book> chooseList = new LinkedList<Book>();
                        if (choosePublisher.equals("所有出版社"))
                        {
                            bookAdapter = new BookAdapter(BookListActivity.this,booklist);
                            booklist_layout.setAdapter(bookAdapter);
                        }
                        else
                        {
                            chooseList.clear();
                            OkHttpClientManager.getAsyn(getString(R.string.domain)+"book/query/"+booklist.get(0).getName()+"/"+choosePublisher+"/10",
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

                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                //加载适配器
            }
        }
    };
}
