package com.pc.magso;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static retrofit2.converter.scalars.ScalarsConverterFactory.create;

public class MainActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private SwipeRefreshLayout swipeRefresh;
    private MaterialSearchBar searchBar;
    private ListView mListView;
    private Context mContext;
    private listAdapter adapter;
    private String searchText;
    private List<MagBean> mData = new ArrayList<MagBean>();
    private boolean isLoading = false;
    private boolean stopLoading = false;
    private LinearLayout footer;
    private FrameLayout gray;

    private String route = "http://zhongzicili.com/zhongzi/";
    private String order = "0";
    private String filter = "0";
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        dropDown();
        scrollListen();
        popupMenu();
        listClick();
    }

    private void listClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ScrollView scrollview = new ScrollView(mContext);
                        LinearLayout linearLayout = new LinearLayout(mContext);
                        linearLayout.setPadding(10, 0, 0, 0);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        try {
                            Document doc = Jsoup.connect(mData.get(position).getLink())
                                    .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                    .get();
                            Elements block = doc.getElementsByClass("filelist");
                            for (Element i : block
                                    ) {
                                Elements items = i.getElementsByTag("p");
                                Log.d(TAG, "itemsize:" + items.size());
                                for (int j = 0; j < items.size(); j++) {
                                    TextView tv = new TextView(mContext);
                                    if(items.size()==1){
                                        tv.setText(items.get(j).text().toString());
                                    }else {
                                        tv.setText(j + 1 + "-" + items.get(j).child(1).text().toString() + " (" + items.get(j).child(2).text().toString() + ")");
                                    }
                                    linearLayout.addView(tv);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        scrollview.addView(linearLayout);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog dialog = new AlertDialog.Builder(mContext)
                                        .setTitle("文件列表")
                                        .setView(scrollview)
                                        .setPositiveButton("确定", null).create();
                                dialog.show();
                            }
                        });

                    }
                }).start();
            }
        });
    }

    private void scrollListen() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 && firstVisibleItem + visibleItemCount + 3 >= totalItemCount && !stopLoading) {
                    if (!isLoading) {
                        isLoading = true;
                        double s = (double) totalItemCount / 10;
                        int index = (int) Math.ceil(s) + 1;
                        netWorkLoading(searchText, route, String.valueOf(index));
                    }
                }
            }
        });
    }

    private void popupMenu() {
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.route1:
                        route = "http://zhongzicili.com/zhongzi/";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "线路一", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.route2:
                        route = "http://cilifuli.com/";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "线路二", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.route3:
                        Toast.makeText(mContext, "线路三", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.route4:
                        Toast.makeText(mContext, "线路四", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.order1:
                        order = "0";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "按默认排序", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.order2:
                        order = "1";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "按收录时间排序", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.order3:
                        order = "2";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "按大小排序", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.order4:
                        order = "3";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "按文件数排序", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.order5:
                        order = "4";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "按人气排序", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.filter1:
                        filter = "0";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "全部", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.filter2:
                        filter = "2";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "视频", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.filter3:
                        filter = "3";
                        onSearchConfirmed(searchText);
                        Toast.makeText(mContext, "音乐", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.d(TAG, "default");
                        break;
                }
                return true;
            }
        });
    }

    private void dropDown() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (searchText == null) {
                    swipeRefresh.setRefreshing(false);
                } else {
                    stopLoading = false;
                    mListView.removeFooterView(footer);
                    swipeRefresh.setRefreshing(true);
                    mData.removeAll(mData);
                    netWorkLoading(searchText, route, "1");
                }
            }
        });
    }

    private void initView() {
        mContext = this;
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setCardViewElevation(5);
        searchBar.inflateMenu(R.menu.main);
        mListView = (ListView) findViewById(R.id.listView);
        searchBar.setOnSearchActionListener(this);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        gray = (FrameLayout) findViewById(R.id.gray);
        footer = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.load_more, null);
        adapter = new listAdapter(mContext, mData);
        mListView.setAdapter(adapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#D4D4D4")));
        mListView.setDividerHeight(1);
        swipeRefresh.bringToFront();
    }

    private void netWorkLoading(String name, String route, String page) {
        if (name == null || name == "") {
            return;
        }
        String reqUrl = page + "-" + order + "-" + filter + ".html";
        Log.d(TAG, "netWorkLoading....");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(route).
                addConverterFactory(create()).build();
        final NetClient mClient = retrofit.create(NetClient.class);
        Call<String> call = mClient.getRespos(name, reqUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                String data = response.body();
                if (data != null) {
                    int size = parseData(data);
                    Log.d(TAG, "size:" + size);
                    if (size < 10) {
                        stopLoading = true;
                        mListView.addFooterView(footer);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private int parseData(String data) {
        Document doc = Jsoup.parse(data);
        Elements items = doc.getElementsByClass("item");
        for (Element item : items
                ) {
            MagBean bean = new MagBean();
            Element dt = item.child(0);
            Elements links = dt.getElementsByTag("a");
            for (Element link : links
                    ) {
                bean.setName(link.text());
                bean.setLink(link.attr("href"));
            }
            bean.setDate(item.child(1).child(0).child(0).text());
            bean.setSize(item.child(1).child(1).child(0).text());
            bean.setCount(item.child(1).child(2).child(0).text());
            bean.setPopular(item.child(1).child(4).child(0).text());
            bean.setMagnet(item.child(1).child(5).child(0).attr("href"));
            mData.add(bean);
        }
        return items.size();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (enabled) {
            gray.setVisibility(View.VISIBLE);
            gray.bringToFront();
            searchBar.bringToFront();
        } else {
            gray.setVisibility(View.GONE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.bringToFront();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        if (text == null || text == "") {
            return;
        }
        Log.d(TAG, "onSearchConfirmed: ");
        findViewById(R.id.gray).setVisibility(View.GONE);
        swipeRefresh.setRefreshing(true);
        searchBar.disableSearch();
        searchText = text.toString();
        stopLoading = false;
        mListView.removeFooterView(footer);
        mData.removeAll(mData);
        netWorkLoading(searchText, route, "1");
        swipeRefresh.bringToFront();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}