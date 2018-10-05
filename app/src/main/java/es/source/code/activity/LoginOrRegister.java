package es.source.code.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.source.code.model.User;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener{

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

        //初始化各个控件
        mIntent = getIntent();
        user_name = findViewById(R.id.login_or_register_editText_username);
        password = findViewById(R.id.login_or_register_editText_password);
        login = findViewById(R.id.login_or_register_Button_login);
        back = findViewById(R.id.login_or_register_Button_return);
        register = findViewById(R.id._or_register_textView_register);
        mSharedPreferences = getSharedPreferences("SCOSData", Context.MODE_MULTI_PROCESS);
        mEditor = mSharedPreferences.edit();
        mProgressDialog = new ProgressDialog(this);


        Toast.makeText(LoginOrRegister.this, "如需注册请先输入用户名密码在点击下方注册按钮",Toast.LENGTH_LONG).show();


        //判断是否已经登录过，若为未登录过则隐藏登录按钮
        String userName = mSharedPreferences.getString("userName","");
        if(userName.equals("")){
            login.setVisibility(View.INVISIBLE);
            login.setEnabled(false);
        }else {
            register.setVisibility(View.INVISIBLE);
            register.setEnabled(false);
            user_name.setText(userName);


            //判断是否处在登录状态
            int loginState = mSharedPreferences.getInt("loginState",0);
            if(loginState == 1){
                login.setEnabled(false);
                user_name.setEnabled(false);
                password.setEnabled(false);
            }else{
                login.setEnabled(true);
                user_name.setEnabled(true);
                login.setEnabled(true);
            }
        }

        //登录和返回设置监听
        login.setOnClickListener(this);
        back.setOnClickListener(this);
        register.setOnClickListener(this);

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
    @Override
    public void onClick(View v) {
        if(v.getId() == login.getId()){ //当登录按钮被按下
            if(judgeRegex(user_name.getText().toString())&& !password.getText().toString().equals("")) {

                //圆形转动的进度条
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                //设置是否可以通过点击Back键取消
                mProgressDialog.setCancelable(false);

                //设置是否可以点击Dialog外取消进度条
                mProgressDialog.setCanceledOnTouchOutside(false);

                //设置title的图标
                mProgressDialog.setIcon(R.drawable.launch);

                //设置标题
                mProgressDialog.setTitle("登录中");

                //dismiss监听
                mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(LoginOrRegister.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });

                //监听cancel实践
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(LoginOrRegister.this, "取消登录", Toast.LENGTH_SHORT).show();
                    }
                });
                mProgressDialog.setMessage("登录中，请等待");
                mProgressDialog.show();
                mHandler.postDelayed(mDialog_key, 2000);
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
                Toast.makeText(LoginOrRegister.this,"请输入正确的用户名和密码",Toast.LENGTH_SHORT).show();
            }
        }
    }


    //使用Handler延迟处理ProgressDialog
    private Handler mHandler = new Handler();
    private Runnable mDialog_key = new Runnable() {
        @Override
        public void run() {
            mProgressDialog.dismiss();


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
        }
    };

}


