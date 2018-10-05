package es.source.code.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;

import es.source.code.model.User;

public class MainScreen extends AppCompatActivity{

    public static final String MAINSCREEN_ACTION = "scos.intent.actioin.SCOSMAIN";
    private final String[] Strings = {"点菜","查看订单","登录/注册","系统帮助"};
    private final int[] images = {R.drawable.order, R.drawable.watch_order, R.drawable.login, R.drawable.help};
    private int ORDER = 0;
    private int WATCH_ORDER = 1;
    private int LOGIN = 2;
    private int HELP = 3;

    private boolean canBeVisible = false;
    private String[] mStrings;
    private int[] mImages;
    private Intent mIntent;
    private User user;

    private SharedPreferences mSharedPreferences;

    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mIntent = getIntent();

        //根据SharedPreferences的loginState决定canBeVisible的值
        mSharedPreferences = getSharedPreferences("SCOSData", Context.MODE_MULTI_PROCESS);
        if(mSharedPreferences.getInt("loginState",0) == 1){
            canBeVisible = true;
            user = new User();
        }

        setMStringsAndMImages();
        initGridViewListner();

    }

    /**
     * 根据canBeVisible来设置mStrings和mImages
     */
    private void setMStringsAndMImages(){
        if(!canBeVisible){
            mStrings = Arrays.copyOfRange(Strings, 2,4);
            mImages = Arrays.copyOfRange(images,2,4);
            ORDER = -1;
            WATCH_ORDER = -1;
            LOGIN = 0;
            HELP = 1;
        }else{
            mStrings = Arrays.copyOf(Strings,Strings.length);
            mImages = Arrays.copyOf(images,images.length);
            ORDER = 0;
            WATCH_ORDER = 1;
            LOGIN = 2;
            HELP = 3;
        }
        initGridViewAdapter();
    }

    /**
     * 初始化GridView组件
     */
    private void initGridViewAdapter(){

        mGridView = findViewById(R.id.main_screen_grid);

        //为GridView组件设置适配器
        mGridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mStrings.length;
            }

            @Override
            public Object getItem(int position) {
                return mImages[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.main_screen_grid_item, null);
                Button btn = view.findViewById(R.id.main_screen_grid_item_button);
                btn.setText(mStrings[position]);
                Drawable drawable = getResources().getDrawable(mImages[position]);

                //如果不设置bound图片将不会显示
                drawable.setBounds(0, 0, 100, 100);
                btn.setCompoundDrawables(null, drawable, null, null);
                return view;
            }
        } );


    }

    private void initGridViewListner(){
        //为GridView设置click监听器
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == LOGIN){
                    turnToLogin();
                }else if(position == ORDER){
                    turnToFoodView();
                }else if(position == WATCH_ORDER){
                    turnToWatchOrder();
                }else if(position == HELP){
                    turnToHelper();
                }
            }
        });
    }

    /**
     * 转到login界面
     */
    private void turnToLogin(){
        Intent intent_toLogin = new Intent(MainScreen.this, LoginOrRegister.class);
        startActivityForResult(intent_toLogin, 0);  //0为向login的请求码
    }

    private void turnToFoodView(){
        Intent intent_toFoodView = new Intent(MainScreen.this, FoodView.class);
        intent_toFoodView.putExtra("user",user);
        startActivityForResult(intent_toFoodView,1);
    }

    private  void turnToWatchOrder(){
        Intent intent_toWatchOrder = new Intent(MainScreen.this, FoodOrderView.class);
        intent_toWatchOrder.putExtra("user",user);
        startActivity(intent_toWatchOrder);
    }

    private void turnToHelper(){
        Intent intent_toHelper = new Intent(MainScreen.this,SCOSHelper.class);
        startActivity(intent_toHelper);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
               Bundle bundle = data != null ? data.getExtras() : null;
               if(bundle != null){
                   String message = bundle.getString("data");
                   if(message.equals("RegisterSuccess")){
                       Toast.makeText(MainScreen.this,"欢迎您成为SCOS新用户",Toast.LENGTH_SHORT).show();
                   }
                   int loginState = mSharedPreferences.getInt("loginState",0);
                   if(loginState == 0){
                       user = null;
                       canBeVisible = false;
                   }else if(loginState == 1){
                       user = new User();
                       canBeVisible = true;
                   }
                   setMStringsAndMImages();
               }
        }else if(requestCode == 1){
            user = (User)data.getSerializableExtra("user");
        }
    }


}
