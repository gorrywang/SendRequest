package com.example.iswgr.sendrequest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String url;
    private TextView show;
    private Button btn;
    private RadioGroup group, groupNum;
    private boolean bool = false;
    private int count = 0;
    private int num = 0;
    private Toolbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = getIntent().getStringExtra("url");
        initView();
        //显示对话框
        showDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                bool = false;
                finish();
                break;
        }
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("警告");
        builder.setMessage("使用本软件刷CSDN访问量，" +
                "每日访问次数过多可能导致C币被封或账号被封，" +
                "本人概不负责，" +
                "软件中可以设置访问次数，" +
                "如果介意请退出。");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setCancelable(false).show();
    }

    /**
     * 初始化view
     */
    private void initView() {
        show = (TextView) findViewById(R.id.show);
        btn = (Button) findViewById(R.id.btn);
        group = (RadioGroup) findViewById(R.id.group);
        groupNum = (RadioGroup) findViewById(R.id.group2);
        btn.setOnClickListener(this);
        bar = (Toolbar) findViewById(R.id.ac_bar_title);
        setSupportActionBar(bar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("刷访问量");
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
//                Toast.makeText(MainActivity.this, "请输入链接" + i, Toast.LENGTH_LONG).show();
                check();
                break;
        }
    }

    private void check() {
        if (!bool) {
            //点击开始
            //判断值
            bool = true;
            //设置按钮
            btn.setText("停止");
            //开启提示
            Toast.makeText(MainActivity.this, "已开启访问", Toast.LENGTH_LONG).show();
            //设置记录次数为0
            count = 0;
            //设置次数值
            num = getNum();
            //获取线程
            for (int i = 0; i < getThread(); i++) {
                start();
            }
        } else {
            //点击关闭
            //设置判断值
            bool = false;
            //设置按钮
            btn.setText("开始");
            //关闭提示
            Toast.makeText(MainActivity.this, "已关闭访问", Toast.LENGTH_LONG).show();
        }
    }

    private int getNum() {
        int radioButtonId = groupNum.getCheckedRadioButtonId();
        int i = groupNum.indexOfChild(groupNum.findViewById(radioButtonId));
        int ii = 0;
        switch (i) {
            case 0:
                ii = 500;
                break;
            case 1:
                ii = 1500;
                break;
            case 2:
                ii = 3000;
                break;
            case 3:
                ii = -1;
                break;
        }
        return ii;
    }

    private int error = 0;

    /**
     * 启动
     */
    private void start() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(MainActivity.this, url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                error++;
                if (error == 3) {
                    //错误，关闭
                    Toast.makeText(MainActivity.this, "连续错误3次，自动关闭,请检查链接", Toast.LENGTH_SHORT).show();
                    bool = false;
                    btn.setText("开始");
                } else {
                    start();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (bool) {
                    if (error != 0) {
                        error = 0;
                    }
                    show.setText((++count) + "");
                    //判断次数
                    if (!(num == -1)) {
                        //有限次
                        if (count == num) {
                            Toast.makeText(MainActivity.this, "自动关闭", Toast.LENGTH_SHORT).show();
                            bool = false;
                            btn.setText("开始");
                        }
                    }
                    //在开始一次
                    if (bool) {
                        start();
                    }
                }
            }
        });
    }

    private int getThread() {
        int id = group.getCheckedRadioButtonId();
        int i = group.indexOfChild(group.findViewById(id)) + 1;
        return i;
    }
}
