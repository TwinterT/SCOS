package es.source.code.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import es.source.code.model.User;

public class MainScreen extends AppCompatActivity{

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

    private TextView address;
    private TextView temperature;

    private SharedPreferences mSharedPreferences;

    GridView mGridView;

    private LocationClient mLocationClient;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = findViewById(R.id.main_screen_toolbar);

        setSupportActionBar(toolbar);


        address = findViewById(R.id.main_screen_toolbar_address);
        temperature = findViewById(R.id.main_screen_toolbar_degree);


        mIntent = getIntent();

        //根据SharedPreferences的loginState决定canBeVisible的值
        mSharedPreferences = getSharedPreferences("SCOSData", Context.MODE_MULTI_PROCESS);
        if(mSharedPreferences.getInt("loginState",0) == 1){
            canBeVisible = true;
            user = new User();
        }

        setMStringsAndMImages();
        initGridViewListner();

        //定位服务开始
        initLocatOption();

        findViewById(R.id.main_screen_toolbar_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainScreen.this,"获取地址中...",Toast.LENGTH_SHORT).show();
                mLocationClient.start();
            }
        });

        findViewById(R.id.main_screen_toolbar_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainScreen.this,"获取地址中...",Toast.LENGTH_SHORT).show();
                mLocationClient.start();
            }
        });

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


    /**
     * 初始化使用百度地图SDK定位的选项
     */
    private void initLocatOption(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                StringBuilder stringBuilder = new StringBuilder(256);

                //获得地址
//                stringBuilder.append(bdLocation.getAddrStr());

                //获得省份
//                stringBuilder.append(bdLocation.getProvince());

                //获得城市
                String city = bdLocation.getCity();

                stringBuilder.append(city);
                stringBuilder.append(bdLocation.getDistrict());
                stringBuilder.append(bdLocation.getStreet());

                //根据城市来获得温度信息
                getWeatherData(city.replace("市",""));

                address.setText(stringBuilder.toString());
            }
        });
        LocationClientOption option = new LocationClientOption();
        //省电模式的定位
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");

        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);

        //开启定位
        //mLocationClient.start();
    }

    /**
     * 实现天气信息的获取
     * @param city
     */
    private void getWeatherData(String city){
        String cityCode = null;
        mRequestQueue = Volley.newRequestQueue(this);
        try {
            cityCode = URLEncoder.encode(city,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://wthrcdn.etouch.cn/weather_mini?city=" + cityCode,  // 根据城市名获取天气JSon
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = null;
                        try {
                            data = new JSONObject(response.getString("data"));
                            JSONArray forecast = data.getJSONArray("forecast");
                            JSONObject todayWeather = forecast.getJSONObject(0);


                            String wendu = data.getString("wendu");
                            temperature.setText("当前温度："+wendu+"℃");
                            //String ganmao = data.getString("ganmao");
                            String high = todayWeather.getString("high");
                            String low = todayWeather.getString("low") + "~";
                            String date = todayWeather.getString("date") + "";
                            String type = todayWeather.getString("type")+"\n";
                            String city = data.getString("city") + "\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stop();
    }
}
