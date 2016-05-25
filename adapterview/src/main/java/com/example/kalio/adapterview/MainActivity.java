package com.example.kalio.adapterview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.kalio.adapterview.models.Entry;
import com.example.kalio.adapterview.models.News;
import com.example.kalio.adapterview.models.Post;
import com.example.kalio.adapterview.models.Rss;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements TabHost.OnTabChangeListener, AdapterView.OnItemClickListener {

    public static final String TAG = "kalious.MainActivity";
    public static final String NEWS_URL = "http://blog.teamtreehouse.com";
    public static final String GOOGLE_API = "http://ajax.googleapis.com";
    static final String PROXY_ADDRESS = null;
    static final int PROSY_PORT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. TabHost 생성 및 이벤트 등록
        //      1) TabHost 화면 구성
        //      2) Tab 하나를 의미하는 TabSpec 객체 생성 후 TabHost 객체에 추가
        //      3) 이벤트 등록
        initTab();
        // 2. proxy 설정
        //      1) Proxy 객체 생성
        //      2) OkHttpClient 객체 생성 및 Proxy 설정 적용
        //      3) 이후 Retrofit2 라이브러리의 HTTP 클라이언트로 okHttpClient 객체 사용
        initHttpClient();
        // 3. ListView 생성 및 이벤트 등록
        initListView();
        // 4. retrofit2 수행
        //      1) 첫 번째 화면을 보여주기 위해서 호출한다.
        requestNewsInfo(mListView1);
    }

    // Proxy, Http Client(OkHttpClient) 설정
    public OkHttpClient mOkHttpClient = null;

    private void initHttpClient() {
        if (PROXY_ADDRESS != null && !PROXY_ADDRESS.equals("")) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_ADDRESS, PROSY_PORT));
            mOkHttpClient = new OkHttpClient.Builder().proxy(proxy).build();
        } else {
            mOkHttpClient = new OkHttpClient.Builder().build();
        }
    }

    // "Back" 버튼에 대한 App. 종료 이벤트 처리를 수행한다.
    double mInitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mInitTime > 3000) {
                Toast.makeText(this, "'뒤로'버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            mInitTime = System.currentTimeMillis();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ============================================================================================
    // TabHost 설정
    // ============================================================================================
    TabHost mTabHost;

    private void initTab() {
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        // activity_main.xml 레이아웃 파일에 설정한대로
        // Tab 버튼에 해당하는 TabWidget 과 Tab 내용에 해당하는 FrameLayout 의 위치를 확보한다.
        if (mTabHost != null) {
            mTabHost.setup();

            // Tab 은 Tab 버튼과 내용이 TabSpec 으로 결합된 형태
            // 별도로 이벤트를 처리하지 않아도 Tab 버튼을 누르면 Tab 내용이 표시된다.
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec("tab1");
            tabSpec.setIndicator("News");
            tabSpec.setContent(R.id.listview1);
            mTabHost.addTab(tabSpec);

            tabSpec = mTabHost.newTabSpec("tab2");
            tabSpec.setIndicator("SERI");
            tabSpec.setContent(R.id.listview2);
            mTabHost.addTab(tabSpec);

            tabSpec = mTabHost.newTabSpec("tab3");
            tabSpec.setIndicator("Bloter");
            tabSpec.setContent(R.id.listview3);
            mTabHost.addTab(tabSpec);

            tabSpec = mTabHost.newTabSpec("tab4");
            tabSpec.setIndicator("한경");
            tabSpec.setContent(R.id.listview4);
            mTabHost.addTab(tabSpec);

            // Tab 은 "onTabChanged" 이벤트가 존재하는데 이 이벤트를 구현 후 등록한다.
            mTabHost.setOnTabChangedListener(this);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        switch (tabId) {
            case "tab1":
                requestNewsInfo(mListView1);
                break;
            case "tab2":
                requestRssFeedInfo(mListView2, "http://www.seri.org/forum/rss_xml/rss_report.xml");
                break;
            case "tab3":
                requestRssFeedInfo(mListView3, "http://feeds.feedburner.com/Bloter");
                break;
            case "tab4":
                requestRssFeedInfo(mListView4, "http://rss.hankyung.com/economy.xml");
                break;
            default:
                Toast.makeText(MainActivity.this, tabId, Toast.LENGTH_SHORT).show();
        }
    }

    // ============================================================================================
    // ListView 설정
    // ============================================================================================
    ListView mListView1;
    ListView mListView2;
    ListView mListView3;
    ListView mListView4;

    // Tab 내용은 ListView 로 구성되어 있다.
    // ListView 리소스를 획득하고 이벤트를 등록한다.
    private void initListView() {
        mListView1 = (ListView) findViewById(R.id.listview1);
        mListView2 = (ListView) findViewById(R.id.listview2);
        mListView3 = (ListView) findViewById(R.id.listview3);
        mListView4 = (ListView) findViewById(R.id.listview4);

        mListView1.setOnItemClickListener(this);
        mListView2.setOnItemClickListener(this);
        mListView3.setOnItemClickListener(this);
        mListView4.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Post post = (Post) parent.getItemAtPosition(position);
        if (post.thumbnail == null) {
            Log.d(TAG, "null");
        } else {
            Log.d(TAG, post.thumbnail);
        }

        Toast.makeText(MainActivity.this, post.title, Toast.LENGTH_SHORT).show();
    }

    // ============================================================================================
    // Menu 설정 (2016.04.28)
    // ============================================================================================
    // 아래 이벤트만 구현하면 ActionBar 에 메뉴가 표시된다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 메뉴의 항목을 선택했을 경우 항목에 맞는 이벤트 처리를 할 수 있다.
    // 여기서는 "Settings" 메뉴를 클릭했을 때 화면 이동을 처리한다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    // ============================================================================================
    // News, Rss REST API 호출 메소드 정의
    // 1. REST API 호출 후 JSON 데이터 획득
    // 2. JSON 데이터 처리 (GSON 라이브러리) 후 Adapter 에 전달
    // 3. Adapter 와 ListView 연결
    // 4. [참고] HttpGet, HttpClient, HttpResponse (deprecated from api level 23)
    // ============================================================================================

    private void requestNewsInfo(final ListView listView) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .client(mOkHttpClient)
                        .baseUrl(NEWS_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        RestApi service = retrofit.create(RestApi.class);

        Call<News> call = service.getNewsInfo();
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    ArrayList<Post> posts = response.body().posts;

                    NewsAdapter adapter = new NewsAdapter(MainActivity.this, posts);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }

    private void requestRssFeedInfo(final ListView listView, String feed_url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit =
                new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(GOOGLE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi service = retrofit.create(RestApi.class);

        Call<Rss> call = service.getRssInfo("2.0", feed_url);
        call.enqueue(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    ArrayList<Entry> entries = response.body().responseData.feed.entries;

                    RssAdapter adapter = new RssAdapter(MainActivity.this, R.layout.feed_list_item, entries);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Rss> call, Throwable t) {

            }
        });

    }
}
