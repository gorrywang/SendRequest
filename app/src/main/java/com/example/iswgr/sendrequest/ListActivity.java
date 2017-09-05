package com.example.iswgr.sendrequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iswgr.sendrequest.gson.ListGson;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ListActivity extends AppCompatActivity {

    private String token;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    List<ListGson.ListBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        //获取token
        token = getToken();
        //获取数据
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("access_token", token);
        client.get("http://api.csdn.net/blog/getarticlelist", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //解析数据
                Gson gson = new Gson();
                ListGson listGson = gson.fromJson(responseString, ListGson.class);
                //显示数据
                showData(listGson);
            }
        });
    }

    /**
     * 显示数据
     */
    private void showData(ListGson listGson) {
        list = listGson.getList();
        MyAdapter adapter = new MyAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(ListActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化数据
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.list_tool_title);
        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_load);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("请选择文章");

    }

    /**
     * 获取token
     */
    private String getToken() {
        return getIntent().getStringExtra("token");
    }

    /**
     * 适配器
     */
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            final View inflate = LayoutInflater.from(ListActivity.this).inflate(R.layout.item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(inflate);
            inflate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    ListGson.ListBean listBean = list.get(adapterPosition);
                    Intent intent = new Intent(ListActivity.this, MainActivity.class);
                    intent.putExtra("url", listBean.getUrl());
                    startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ListGson.ListBean listBean = list.get(position);
            holder.showView.setText("访问量:" + listBean.getView_count());
            holder.title.setText(listBean.getTitle());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, showView;

            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.item_title);
                showView = itemView.findViewById(R.id.item_view);
            }
        }
    }


    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
