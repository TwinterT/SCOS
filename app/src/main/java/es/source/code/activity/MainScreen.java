package es.source.code.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainScreen extends AppCompatActivity implements View.OnClickListener{

    private Button button_order;
    private Button button_watch_order;
    private Button button_login;
    private Button button_help;
    //action字符串
    public static final String MAINSCREEN_ACTION = "scos.intent.actioin.SCOSMAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        button_order = findViewById(R.id.order);
        button_watch_order = findViewById(R.id.watch_order);
        button_login = findViewById(R.id.login);
        button_help = findViewById(R.id.help);

        button_login.setOnClickListener(this);

        Intent intent = getIntent();
        String get_from_intent = intent.getStringExtra("data");
        if(get_from_intent!=null && !get_from_intent.equals("FromEntry"))
        {
            button_order.setVisibility(View.GONE);
            button_watch_order.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == button_login.getId()){ //转到Login界面
            Intent intent_toLogin = new Intent(MainScreen.this, LoginOrRegister.class);
            startActivityForResult(intent_toLogin, 0);  //0为向login的请求码
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                String fromLogin = (data != null ? data.getStringExtra("data") : "null");
                if(fromLogin.equals("LoginSuccess")){
                    checkAndSetIsVisible();
                }
            }
        }
    }

    private void checkAndSetIsVisible()  //检查点菜和查看订单按钮时否被隐藏，如果被隐藏则显示
    {
        if(button_order.getVisibility() != View.VISIBLE || button_watch_order.getVisibility() != View.VISIBLE)
        {
            button_order.setVisibility(View.VISIBLE);
            button_watch_order.setVisibility(View.VISIBLE);
        }
    }
}
