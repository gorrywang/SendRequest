package com.example.iswgr.sendrequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.iswgr.sendrequest.utils.DefaultUtils.APP_KEY;
import static com.example.iswgr.sendrequest.utils.DefaultUtils.APP_Secret;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText num, pwd;
    private Button btn;
    private Toolbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        num = (EditText) findViewById(R.id.login_edit_num);
        pwd = (EditText) findViewById(R.id.login_edit_pwd);
        btn = (Button) findViewById(R.id.login_btn_login);
        bar = (Toolbar) findViewById(R.id.login_tool_title);
        btn.setOnClickListener(this);
        setSupportActionBar(bar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("登录");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_login:
                //登录
                //获取账号与密码
                String n = num.getText().toString().trim();
                String p = pwd.getText().toString().trim();
                //判断是否为空
                if (TextUtils.isEmpty(n) && TextUtils.isEmpty(p)) {
                    Toast.makeText(LoginActivity.this, "请输入账号与密码", Toast.LENGTH_LONG).show();
                    return;
                }
                //登录
                login(n, p);
                break;
        }
    }

    /**
     * 判断登录
     */
    private void login(String n, String p) {
        //登录
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("username", n);
        params.add("password", p);
        params.add("client_id", APP_KEY);
        params.add("client_secret", APP_Secret);
        params.add("grant_type", "password");
        client.get("http://api.csdn.net/oauth2/access_token", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //判断数据
                try {
                    JSONObject object = new JSONObject(responseString);
                    String access_token = object.getString("access_token");
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    //跳转
                    Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                    intent.putExtra("token", access_token);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
