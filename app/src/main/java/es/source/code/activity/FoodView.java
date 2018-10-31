package es.source.code.activity;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import es.source.code.util.FoodItems;
import es.source.code.model.User;
import es.source.code.service.ServerObserverService;
import es.source.code.util.FoodViewDecoration;
import es.source.code.util.FoodViewHolder;
import es.source.code.util.FoodViewRecyclerAdapter;

public class FoodView extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ServiceConnection{

    public static final String ORDER = "点菜";
    public static final String UNSUBSCRIBE = "退点";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private RecyclerView hotFoodRecyclerView;
    private FoodViewRecyclerAdapter hotFoodRecyclerViewAdapter;

    private RecyclerView coldFoodRecyclerView;
    private FoodViewRecyclerAdapter coldFoodRecyclerViewAdapter;

    private RecyclerView seeFoodRecyclerView;
    private FoodViewRecyclerAdapter seeFoodRecyclerViewAdapter;

    private RecyclerView drinkRecyclerView;
    private FoodViewRecyclerAdapter drinkRecyclerViewAdapter;


    private List<View> mViewList;

    private Intent mIntent;

    private User user;

    private boolean isBind = false;

    private MenuItem item;

    //FoodView的handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 10){
                //处理从服务端收到的message
                Bundle bundle = msg.getData();
                int type = bundle.getInt("type",0);
                int pos = bundle.getInt("pos");
                int storage = bundle.getInt("storage");
                int price = bundle.getInt("price");
                switch (type){
                    case FoodItems.HOT_FOOD:
                        FoodItems.hot_food_storage[pos] = storage;
                        FoodItems.hot_food_price[pos] = price;
 //                       hotFoodAdapter.notifyDataSetChanged();
                        hotFoodRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case FoodItems.COLD_FOOD:
                        FoodItems.cold_food_storage[pos] = storage;
                        FoodItems.cold_food_price[pos] = price;
                        coldFoodRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case FoodItems.SEE_FOOD:
                        FoodItems.see_food_storage[pos] = storage;
                        FoodItems.see_food_price[pos] = price;
                        seeFoodRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case FoodItems.DRINK:
                        FoodItems.drink_storage[pos] = storage;
                        FoodItems.drink_price[pos] = price;
                        drinkRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    default:
                }
            }
        }
    };

    //service方的Messenger
    private Messenger serviceMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_view);
        getWindow().setStatusBarColor(Color.parseColor("#303030"));

        mIntent = getIntent();
        user = (User)mIntent.getSerializableExtra("user");

        initComponent();
        initTabViewPager();
        setSupportActionBar(mToolbar);

        initListAdapter();

        initRecycleView(User.HOTFOOD,hotFoodRecyclerView,hotFoodRecyclerViewAdapter);
        initRecycleView(User.COLDFOOD,coldFoodRecyclerView,coldFoodRecyclerViewAdapter);
        initRecycleView(User.SEEFOOD,seeFoodRecyclerView,seeFoodRecyclerViewAdapter);
        initRecycleView(User.DRINK,drinkRecyclerView,drinkRecyclerViewAdapter);

        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //启动服务
        Intent intent = new Intent(FoodView.this, ServerObserverService.class);
        //使用handle发送信息
        bindService(intent,FoodView.this, Context.BIND_AUTO_CREATE);
    }

    private void initRecycleView(final int flag, RecyclerView recyclerView, FoodViewRecyclerAdapter adapter){
        adapter.setOnItemClickListener(new FoodViewHolder.MyRecycleOnClickListener() {
            @Override
            public void onItemClidked(View v, int pos) {
                Intent intent = new Intent(FoodView.this,FoodDetailed.class);
                String tag = "";
                tag = user.getTag(flag).toString();
                intent.putExtra("data",flag);
                intent.putExtra("pos",pos);
                intent.putExtra("tag",tag);
                intent.putExtra("user",user);
                startActivityForResult(intent,1);
            }
        });

        recyclerView.setAdapter(adapter);

        //显示分割线
        recyclerView.setBackgroundColor(Color.DKGRAY);
        recyclerView.addItemDecoration(new FoodViewDecoration());
    }


    /**
     * 初始化各个组件
     */
    private void initComponent(){
        mTabLayout = findViewById(R.id.food_view_tabLayout);
        mViewPager = findViewById(R.id.food_view_viewPager);

        hotFoodRecyclerView = new RecyclerView(this);
        hotFoodRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));

        coldFoodRecyclerView = new RecyclerView(this);
        coldFoodRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));

        seeFoodRecyclerView = new RecyclerView(this);
        seeFoodRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));

        drinkRecyclerView = new RecyclerView(this);
        drinkRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));

        mViewList = new ArrayList<>();
        mViewList.add(hotFoodRecyclerView);
        mViewList.add(coldFoodRecyclerView);
        mViewList.add(seeFoodRecyclerView);
        mViewList.add(drinkRecyclerView);


        mTabLayout.setOnTabSelectedListener(this);

        mToolbar = findViewById(R.id.food_view_toolbar);
        mToolbar.setBackgroundColor(Color.parseColor("#303030"));


        item = findViewById(R.id.food_view_startService);
    }

    private void initListAdapter(){
        hotFoodRecyclerViewAdapter = new FoodViewRecyclerAdapter(User.HOTFOOD,FoodItems.hot_food_name,FoodItems.hot_food_image,FoodItems.hot_food_price,FoodItems.hot_food_storage,this,user);
        coldFoodRecyclerViewAdapter = new FoodViewRecyclerAdapter(User.COLDFOOD,FoodItems.cold_food_name,FoodItems.cold_food_image,FoodItems.cold_food_price,FoodItems.cold_food_storage,this,user);
        seeFoodRecyclerViewAdapter = new FoodViewRecyclerAdapter(User.SEEFOOD,FoodItems.see_food_name,FoodItems.see_food_image,FoodItems.see_food_price,FoodItems.see_food_storage,this,user);
        drinkRecyclerViewAdapter = new FoodViewRecyclerAdapter(User.DRINK,FoodItems.drink_name,FoodItems.drink_image,FoodItems.drink_price,FoodItems.drink_storage,this,user);


    }

    /**
     * 初始化ViewPager
     */
    private void initTabViewPager(){
        mViewPager.setAdapter(new MyAdapter());

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mTabLayout.getTabAt(position).select();
            }
        });
    }

    /**
     * 接口TabLayout.OnTabSelectedListener内容
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * 接口TabLayout.OnTabSelectedListener内容
     * @param tab
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    /**
     * 接口TabLayout.OnTabSelectedListener内容
     * @param tab
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * 自定义的ViewPager适配器
     */
    private class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViewList.get(position));
        }
    }

    /**
     * ActionBar内容
     * 用于绑定ActionBar的menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.food_view_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * ActionBar内容
     * 用于响应ActionBar的点击操作
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.food_view_already_ordered:{
                Intent intent = new Intent(FoodView.this,FoodOrderView.class);
                intent.putExtra("user",user);
                intent.putExtra("pos",1);
                startActivityForResult(intent,0);
                return true;
            }
            case R.id.food_view_watch_ordered:{
                Intent intent = new Intent(FoodView.this,FoodOrderView.class);
                intent.putExtra("user",user);
                intent.putExtra("pos",0);
                startActivityForResult(intent,0);
                return true;
            }
            case R.id.food_view_call_for_service: {
                Toast.makeText(FoodView.this,"呼叫服务",Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.food_view_startService: {
                if(item.getTitle().equals("启动实时更新")) {
                    item.setTitle("停止实时更新");

                    System.out.println("send----------------");
                    System.out.println("send stop----------------");
                    //新建消息
                    Message message = new Message();
                    message.what = 1;
                    //使用service的messenger发送消息
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                }else if(item.getTitle().equals("停止实时更新")){
                    item.setTitle("启动实时更新");

                    //新建消息
                    Message message = new Message();
                    message.what = 0;
                    //使用service的messenger发送消息
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                return true;
            }
            default:return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 接口ServiceConnection内容
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        isBind = true;
        //获得service的messenger
        serviceMessenger = new Messenger(service);

        //创建Activity的messenger
        Messenger messenger = new Messenger(mHandler);

        //创建消息
        Message msg = new Message();
        msg.what = ServerObserverService.MSG_BIND;
        msg.replyTo = messenger;

        //使用服务方的msg来发送本activity的messenger
        try {
            //在Service中就可以接收到Activity中的Messenger  用于Service与Activity之间的双向通信
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接口ServiceConnection内容
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



    /**
     * 当返回到上个界面时，传回user按钮
     * 重载的方法，当返回按钮按下时调用
     */
    @Override
    public void onBackPressed() {
        mIntent.putExtra("user",user);
        setResult(1,mIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isBind){
            unbindService(FoodView.this);
        }
    }

    /**
     * 根据foodDetail返回更新listview
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            user = (User)data.getSerializableExtra("user");
            int name = data.getIntExtra("name",0);
            if(name == User.HOTFOOD){
                hotFoodRecyclerViewAdapter.notifyDataSetChanged();
            }else if(name == User.COLDFOOD){
               coldFoodRecyclerViewAdapter.notifyDataSetChanged();
            }else if(name == User.SEEFOOD){
                seeFoodRecyclerViewAdapter.notifyDataSetChanged();
            }else if(name == User.DRINK){
                drinkRecyclerViewAdapter.notifyDataSetChanged();
            }

        }else if(requestCode == 0){
            user = (User)data.getSerializableExtra("user");
            System.out.println("--------------------------------flush");
            initListAdapter();

            initRecycleView(User.HOTFOOD,hotFoodRecyclerView,hotFoodRecyclerViewAdapter);
            initRecycleView(User.COLDFOOD,coldFoodRecyclerView,coldFoodRecyclerViewAdapter);
            initRecycleView(User.SEEFOOD,seeFoodRecyclerView,seeFoodRecyclerViewAdapter);
            initRecycleView(User.DRINK,drinkRecyclerView,drinkRecyclerViewAdapter);
        }
    }
}




