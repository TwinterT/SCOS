package es.source.code.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener{

    private EditText user_name;
    private EditText password;
    private Button login;
    private Button back;
    private Intent mIntent;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        //初始化各个控件
        user_name = findViewById(R.id.login_or_register_editText_username);
        password = findViewById(R.id.login_or_register_editText_password);
        login = findViewById(R.id.login_or_register_Button_login);
        back = findViewById(R.id.login_or_register_Button_return);

        //按照要求把密码栏和登录按钮不使能
        if("".equals(user_name.getText().toString())){
            login.setEnabled(false);
            password.setEnabled(false);
        }

        //登录和返回设置监听
        login.setOnClickListener(this);
        back.setOnClickListener(this);

        user_name.addTextChangedListener(new TextWatcher() { //为username添加一个监听器
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!judgeRegex(user_name.getText().toString())){ //未通过正则表达式判断
                    login.setEnabled(false);
                    password.setEnabled(false);
                    user_name.setError("输入内容不符合规则");
                }else if(user_name.getText().toString().trim().length() == 0){//用户名为空
                    login.setEnabled(false);
                    password.setEnabled(false);
                }else if(password.getText().toString().length() !=0) {//当用户名满足条件且密码不为空
                    password.setEnabled(true);
                    login.setEnabled(true);
                }else{ //用户名满足要求且密码为空
                    password.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password.addTextChangedListener(new TextWatcher() { //给密码设置监听
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password.getText().toString().length() <=0){ //密码为空
                    login.setEnabled(false);
                }else{ //密码不为空
                    login.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //登录中通知栏
        mProgressDialog = new ProgressDialog(this);

        mIntent = getIntent();
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
                    Toast.makeText(LoginOrRegister.this,"登录成功",Toast.LENGTH_SHORT).show();
                }
            });

            //监听cancel实践
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(LoginOrRegister.this, "取消登录",Toast.LENGTH_SHORT).show();
                }
            });
            mProgressDialog.setMessage("登录中，请等待");
            mProgressDialog.show();
            mHandler.postDelayed(mDialog_key, 2000);

        }else if(v.getId() == back.getId()){
            //当返回按钮被按下时
            mIntent.putExtra("data","Return");
            setResult(Activity.RESULT_CANCELED, mIntent);
            finish();

        }
    }


    //使用Handler延迟处理ProgressDialog
    private Handler mHandler = new Handler();
    private Runnable mDialog_key = new Runnable() {
        @Override
        public void run() {
            mProgressDialog.dismiss();

            //登录成功 返回LoginSuccess字符串
            mIntent.putExtra("data","LoginSuccess");
            setResult(Activity.RESULT_OK, mIntent);
            finish();
        }
    };

}


