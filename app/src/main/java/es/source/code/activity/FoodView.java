package es.source.code.activity;


import android.annotation.SuppressLint;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.source.code.model.EventBusMessage;
import es.source.code.model.FoodItems;
import es.source.code.model.User;
import es.source.code.service.ServerObserverService;

public class FoodView extends AppCompatActivity implements TabLayout.OnTabSelectedListener, ServiceConnection{

    private final String ORDER = "点菜";
    private final String UNSUBSCRIBE = "退点";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private View hotFoodView;
    private BaseAdapter hotFoodAdapter;
    private ListView hotFoodListView;

    private View coldFoodView;
    private BaseAdapter coldFoodAdapter;
    private ListView coldFoodListView;

    private View seeFoodView;
    private BaseAdapter seeFoodAdapter;
    private ListView seeFoodListView;

    private View drinkView;
    private BaseAdapter drinkAdapter;
    private ListView drinkListView;

    private List<View> mViewList;

    private Intent mIntent;

    private User user;

    //FoodView的handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 10){
                //处理从服务端收到的message
                Bundle bundle = msg.getData();
                int number = bundle.getInt("number",0);
                int pos = bundle.getInt("pos");
                int storage = bundle.getInt("storage");
                switch (number){
                    case FoodItems.HOT_FOOD:
                        FoodItems.hot_food_storage[pos] = storage;
                        hotFoodAdapter.notifyDataSetChanged();
                        break;
                    case FoodItems.COLD_FOOD:
                        FoodItems.cold_food_storage[pos] = storage;
                        coldFoodAdapter.notifyDataSetChanged();
                        break;
                    case FoodItems.SEE_FOOD:
                        FoodItems.see_food_storage[pos] = storage;
                        seeFoodAdapter.notifyDataSetChanged();
                        break;
                    case FoodItems.DRINK:
                        FoodItems.drink_storage[pos] = storage;
                        drinkAdapter.notifyDataSetChanged();
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

        mIntent = getIntent();
        user = (User)mIntent.getSerializableExtra("user");

        //启动服务
        Intent intent = new Intent(FoodView.this, ServerObserverService.class);
        //使用handle发送信息
        bindService(intent,FoodView.this, Context.BIND_AUTO_CREATE);

        initComponent();
        initTabViewPager();
        setSupportActionBar(mToolbar);

        initListAdapter();

        initListView(hotFoodListView,hotFoodAdapter);
        initListView(coldFoodListView,coldFoodAdapter);
        initListView(seeFoodListView,seeFoodAdapter);
        initListView(drinkListView,drinkAdapter);
    }

    /**
     * 初始化各个组件
     */
    private void initComponent(){
        mTabLayout = findViewById(R.id.food_view_tabLayout);
        mViewPager = findViewById(R.id.food_view_viewPager);

        hotFoodView = getLayoutInflater().inflate(R.layout.hot_food,null);
        coldFoodView = getLayoutInflater().inflate(R.layout.cold_food,null);
        seeFoodView = getLayoutInflater().inflate(R.layout.see_food,null);
        drinkView = getLayoutInflater().inflate(R.layout.drinks,null);

        mViewList = new ArrayList<>();
        mViewList.add(hotFoodView);
        mViewList.add(coldFoodView);
        mViewList.add(seeFoodView);
        mViewList.add(drinkView);

        mTabLayout.setOnTabSelectedListener(this);

        hotFoodListView = hotFoodView.findViewById(R.id.hot_food_list_view);
        coldFoodListView = coldFoodView.findViewById(R.id.cold_food_list_view);
        seeFoodListView = seeFoodView.findViewById(R.id.see_food_list_view);
        drinkListView = drinkView.findViewById(R.id.drinks_list_view);

        mToolbar = findViewById(R.id.food_view_toolbar);
    }

    private void initListAdapter(){
        hotFoodAdapter = new MyBaseAdapter(FoodItems.hot_food_name,FoodItems.hot_food_image,FoodItems.hot_food_price);
        coldFoodAdapter = new MyBaseAdapter(FoodItems.cold_food_name,FoodItems.cold_food_image,FoodItems.cold_food_price);
        seeFoodAdapter = new MyBaseAdapter(FoodItems.see_food_name,FoodItems.see_food_image,FoodItems.see_food_price);
        drinkAdapter = new MyBaseAdapter(FoodItems.drink_name,FoodItems.drink_image,FoodItems.drink_price);
    }

    /**
     * 初始化listView组件
     * @param listView   需要被初始化的listview
     * @param baseAdapter 适配器
     */
    private void initListView(final ListView listView,BaseAdapter baseAdapter){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FoodView.this,FoodDetailed.class);
                String str = "";
                String tag = "";
                if(parent == hotFoodListView){
                    str = "hotFood";
                    tag = user.getTagHotFood().toString();
                }
                else if(parent == coldFoodListView){
                    str = "coldFood";
                    tag = user.getTagColdFood().toString();
                }
                else if(parent == seeFoodListView){
                    str = "seeFood";
                    tag = user.getTagSeeFood().toString();
                }
                else if(parent == drinkListView){
                    str = "drink";
                    tag = user.getTagDrink().toString();
                }
                intent.putExtra("data",str);
                intent.putExtra("pos",position);
                intent.putExtra("tag",tag);
                intent.putExtra("user",user);
                startActivityForResult(intent,1);
            }
        });

        listView.setAdapter(baseAdapter);
    }

    /**
     * 自定义的listview的适配器类
     */
    private class MyBaseAdapter extends BaseAdapter{
        private String[] names;
        private int[] images;
        private int[] prices;

        public MyBaseAdapter(String[] names,int[] images,int[] prices){
            this.names = names;
            this.images = images;
            this.prices = prices;
        }
        @Override
        public int getCount() {
            return names.length;

        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.dishes_item, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.order_item_image);
            imageView.setImageResource(images[position]);

            TextView textView_name = convertView.findViewById(R.id.order_item_name);
            textView_name.setText(names[position]);

            TextView textView_price = convertView.findViewById(R.id.order_item_price);
            textView_price.setText(""+prices[position]+" 元");

            TextView textView_storage = convertView.findViewById(R.id.order_item_storage);

            final Button button = convertView.findViewById(R.id.order_item_button);
            int storage = 0;
            int k = 0;
            if(parent == hotFoodListView){
                storage = Integer.parseInt(FoodItems.getStorage(FoodItems.HOT_FOOD)[position]+"");
                k = user.getTagPositionHotFood(position);
            }else if(parent == coldFoodListView){
                storage = Integer.parseInt(FoodItems.getStorage(FoodItems.COLD_FOOD)[position]+"");
                k = user.getTagPositionColdFood(position);
            }else if(parent == seeFoodListView){
                storage = Integer.parseInt(FoodItems.getStorage(FoodItems.SEE_FOOD)[position]+"");
                k = user.getTagPositionSeeFood(position);
            }else if(parent == drinkListView){
                storage = Integer.parseInt(FoodItems.getStorage(FoodItems.DRINK)[position]+"");
                k = user.getTagPositionDrink(position);
            }

            textView_storage.setText("库存 " + storage + " 份");

            if(k == 0){
                button.setText(ORDER);
                button.setTextColor(Color.BLACK);
            }
            else if(k==1){
                button.setText(UNSUBSCRIBE);
                button.setTextColor(Color.RED);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(button.getText().toString().equals(ORDER)){
                        Toast.makeText(FoodView.this,"点菜成功 " + getItemId(position),Toast.LENGTH_SHORT).show();
                        if(parent == hotFoodListView){
                            user.setTagPositionHotFood(position,'1');
                        }else if(parent == coldFoodListView){
                            user.setTagPositionColdFood(position,'1');
                        }else if(parent == seeFoodListView){
                            user.setTagPositionSeeFood(position,'1');
                        }else if(parent == drinkListView){
                            user.setTagPositionDrink(position,'1');
                        }
                        button.setText(UNSUBSCRIBE);
                        button.setTextColor(Color.RED);
                    }else if(button.getText().toString().equals(UNSUBSCRIBE)){
                        Toast.makeText(FoodView.this,"退点成功 " + getItemId(position),Toast.LENGTH_SHORT).show();
                        if(parent == hotFoodListView){
                            user.setTagPositionHotFood(position,'0');
                        }else if(parent == coldFoodListView){
                            user.setTagPositionColdFood(position,'0');
                        }else if(parent == seeFoodListView){
                            user.setTagPositionSeeFood(position,'0');
                        }else if(parent == drinkListView){
                            user.setTagPositionDrink(position,'0');
                        }
                        button.setText(ORDER);
                        button.setTextColor(Color.BLACK);
                    }
                }
            });

            return convertView;
        }
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
        unbindService(FoodView.this);
        mIntent.putExtra("user",user);
        setResult(1,mIntent);
        finish();
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
            String name = data.getStringExtra("name");
            BaseAdapter adapter = null;
            if(name.equals("hotFood")){
                adapter = (BaseAdapter) hotFoodListView.getAdapter();
            }else if(name.equals("coldFood")){
                adapter = (BaseAdapter) coldFoodListView.getAdapter();
            }else if(name.equals("seeFood")){
                adapter = (BaseAdapter) seeFoodListView.getAdapter();
            }else if(name.equals("drink")){
                adapter = (BaseAdapter) drinkListView.getAdapter();
            }
            adapter.notifyDataSetChanged();
        }else if(requestCode == 0){
            user = (User)data.getSerializableExtra("user");
            initListView(hotFoodListView,hotFoodAdapter);
            initListView(coldFoodListView,coldFoodAdapter);
            initListView(seeFoodListView,seeFoodAdapter);
            initListView(drinkListView,drinkAdapter);
        }
    }

}




