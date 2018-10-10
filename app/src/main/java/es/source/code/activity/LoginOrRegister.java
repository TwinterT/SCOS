package es.source.code.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.source.code.model.User;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener{

    private String URLSTRING = "http://192.168.1.104:8080/SCOSServer/LoginValidator";

    private EditText user_name;
    private EditText password;
    private Button login;
    private Button back;
    private Intent mIntent;
    private TextView register;

    private ProgressDialog mProgressDialog;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        initComponent();

        //判断是否已经登录过，若为未登录过则隐藏登录按钮
        String userName = mSharedPreferences.getString("userName","");
        if(!userName.equals("")){
            user_name.setText(userName);
        }

        user_name.addTextChangedListener(new TextWatcher() { //为username添加一个监听器
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!judgeRegex(user_name.getText().toString())){ //未通过正则表达式判断
                    user_name.setError("输入内容不符合规则");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 初始化各个控件
     */
    private void initComponent(){
        //初始化各个控件
        mIntent = getIntent();
        user_name = findViewById(R.id.login_or_register_editText_username);
        password = findViewById(R.id.login_or_register_editText_password);
        login = findViewById(R.id.login_or_register_Button_login);
        back = findViewById(R.id.login_or_register_Button_return);
        register = findViewById(R.id._or_register_textView_register);
        mSharedPreferences = getSharedPreferences("SCOSData", Context.MODE_MULTI_PROCESS);
        mEditor = mSharedPreferences.edit();

        //登录和返回设置监听
        login.setOnClickListener(this);
        back.setOnClickListener(this);
        register.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        //圆形转动的进度条
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置是否可以通过点击Back键取消
        mProgressDialog.setCancelable(false);
        //设置是否可以点击Dialog外取消进度条
        mProgressDialog.setCanceledOnTouchOutside(false);
        //设置title的图标
        mProgressDialog.setIcon(R.drawable.launch);
    }


    /***
     * 判断是否满足正则表达式要求
     * @param str
     * @return
     */
    public boolean judgeRegex(String str){
        Pattern p = Pattern.compile("^[0-9A-Za-z]+$");   //正则表达式

        Matcher matcher = p.matcher(str);

        return matcher.matches();
    }

    /***
     * 处理点击事件
     * @param v
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View v) {
        if(v.getId() == login.getId()){ //当登录按钮被按下
            if(judgeRegex(user_name.getText().toString())&& !password.getText().toString().equals("")) {

                //定义登录异步任务
                new AsyncTask<String,Void,Boolean>(){
                    /**
                     * 处理登陆前事件
                     */
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //设置标题
                        mProgressDialog.setTitle("登录中");
                        mProgressDialog.setMessage("登录中，请等待");
                        mProgressDialog.show();
                    }

                    /**
                     * 处理登录事件
                     * @param strings 第一个参数为用户名，第二个参数为密码，
                     * @return
                     */
                    @Override
                    protected Boolean doInBackground(String... strings) {

                        int code;
                        try {
                            code = httpPost(false,strings);
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            code = 0;
                        }
                        return code==1;
                    }

                    /**
                     * 处理登录后事件
                     * @param aBoolean
                     */
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        mProgressDialog.dismiss();

                        if(aBoolean){
                            Toast.makeText(LoginOrRegister.this,"登录成功",Toast.LENGTH_SHORT).show();

                            //登录成功写入数据到SharedPreferences
                            mEditor.putString("userName",user_name.getText().toString());
                            mEditor.putInt("loginState",1);
                            mEditor.commit();

                            //登录成功 返回LoginSuccess字符串和User
                            User loginUser = new User();
                            loginUser.setUserName(user_name.getText().toString());
                            loginUser.setPassword(password.getText().toString());
                            loginUser.setOldUser(true);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user",loginUser);
                            bundle.putString("data","LoginSuccess");
                            mIntent.putExtras(bundle);
                            setResult(Activity.RESULT_OK, mIntent);
                            finish();
                        }else{
                            Toast.makeText(LoginOrRegister.this,"登录失败，请检查网络连接",Toast.LENGTH_SHORT).show();
                        }


                    }
                }.execute(user_name.getText().toString(),password.getText().toString());

            }else{
                Toast.makeText(LoginOrRegister.this,"请输入正确的用户名密码",Toast.LENGTH_SHORT).show();
            }

        }else if(v.getId() == back.getId()){
            //当返回按钮被按下时
            String userName = mSharedPreferences.getString("userName","");
            if(!userName.equals("")){
                mEditor.putInt("loginState",0);
                mEditor.commit();
            }

            mIntent.putExtra("data","Return");
            setResult(Activity.RESULT_CANCELED, mIntent);
            finish();

        }else if(v.getId() == register.getId()){
            //当注册按钮被按下时
            if(judgeRegex(user_name.getText().toString())&& !password.getText().toString().equals("")) {

                new AsyncTask<String,Void,Boolean>(){

                    /**
                     * 处理注册前事件
                     */
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //设置标题
                        mProgressDialog.setTitle("注册中");
                        mProgressDialog.setMessage("注册中，请等待");
                        mProgressDialog.show();
                    }

                    /**
                     * 处理注册事件
                     * @param strings 第一个参数为用户名，第二个参数为密码，
                     * @return 是否注册成功
                     */
                    @Override
                    protected Boolean doInBackground(String... strings) {
                        int code;
                        try {
                            code = httpPost(true,strings);
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            code = 0;
                        }
                        return code==1;
                    }

                    /**
                     * 处理注册后事件
                     * @param aBoolean 是否注册成功
                     */
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if(aBoolean){
                            //注册成功写入数据到SharedPreferences
                            mEditor.putString("userName",user_name.getText().toString());
                            mEditor.putInt("loginState",1);
                            mEditor.commit();

                            //初始化user
                            User loginUser = new User();
                            loginUser.setUserName(user_name.getText().toString());
                            loginUser.setPassword(password.getText().toString());
                            loginUser.setOldUser(false);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", loginUser);
                            bundle.putString("data", "RegisterSuccess");
                            mIntent.putExtras(bundle);
                            setResult(Activity.RESULT_OK, mIntent);
                            Toast.makeText(LoginOrRegister.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(LoginOrRegister.this, "注册失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(user_name.getText().toString(),password.getText().toString());

            }else{
                Toast.makeText(LoginOrRegister.this,"请输入正确的用户名和密码",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 执行登录操作
     * @param tag 是否注册操作
     * @param strings 用户名和密码
     * @return 登陆成功为1，登录失败为0
     * @throws Exception 联网失败
     */
    private int httpPost(boolean tag,String...strings) throws Exception {

        //网络连接的建立
        URL url = new URL(URLSTRING);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        //获得输出流
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(),"utf-8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        //编写数据
        JSONObject reqJson = new JSONObject();
        reqJson.put("userName",strings[0]);
        reqJson.put("password",strings[1]);
        reqJson.put("isRegister",tag);

        //发送数据
        bufferedWriter.write(reqJson.toString());
        bufferedWriter.flush();

        //获得输入流
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        //接收数据
        StringBuilder data = new StringBuilder("");
        String line;
        while((line = bufferedReader.readLine())!=null){
            data.append(line);
        }
        JSONObject reply = new JSONObject(data.toString());

        //关闭输入流
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return reply.getInt("RESULTCODE");
    }

}


