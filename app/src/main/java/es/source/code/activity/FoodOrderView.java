package es.source.code.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.source.code.model.FoodItems;
import es.source.code.model.User;

public class FoodOrderView extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private final static int NOTORDER = 0;
    private final static int ORDER = 1;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TextView numberTextView;
    private TextView priceTextView;

    private int notOrderTotalPrice = 0;
    private int OrderTotalPrice = 0;

    private View notOrderView;
    private ListView notOrderListView;

    private View orderedView;
    private ListView orderListView;

    private Button mButton;

    private List<View> mViewList;

    private StringBuilder positionNotOrder;
    private int numberHotFoodNotOrder = 0, numberColdFoodNotOrder = 0, numberSeeFoodNotOrder = 0, numberDrinkNotOrder = 0;

    //根据intent传入的user初始化该user
    private User user;
    private Intent mIntent;

    private ProgressDialog mProgressDialog;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                initOrderList();
                numberTextView.setText(""+user.getPositioinOrder().length());
                priceTextView.setText(""+user.getTotalPrice());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_order_view);

        mIntent = getIntent();
        user = (User)mIntent.getSerializableExtra("user");
        int pos = mIntent.getIntExtra("pos",0);

        initComponent();
        initPosition();
        initTotalPrice();
        initTabViewPager();

        initNotOrderList();
        initOrderList();

        if(pos == 1)mTabLayout.getTabAt(pos).select();
        else mTabLayout.getTabAt(0).select();

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mButton.getText().toString().equals("结账")){
                        settleAccount();
                }else if(mButton.getText().toString().equals("提交订单")){
                    if(notOrderListView.getCount()!=0){
                        //此处把已经点的菜提交至User,并且初始化数据
                        user.setPositioinOrder(positionNotOrder);
                        user.setNumberHotFoodNotOrder(numberHotFoodNotOrder);
                        user.setNumberColdFoodNotOrder(numberColdFoodNotOrder);
                        user.setNumberSeeFoodNotOrder(numberSeeFoodNotOrder);
                        user.setNumberDrinkNotOrder(numberDrinkNotOrder);
                        user.setTotalPrice(notOrderTotalPrice);
                        user.initCommitedTag();

                        Toast.makeText(FoodOrderView.this,"提交订单成功",Toast.LENGTH_SHORT).show();
                        //把本地数据清空
                        positionNotOrder = new StringBuilder();
                        numberHotFoodNotOrder = 0;
                        numberColdFoodNotOrder = 0;
                        numberSeeFoodNotOrder = 0;
                        numberDrinkNotOrder = 0;
                        notOrderTotalPrice = 0;

                        //把User中已经点的菜清空
                        user.initTag();

                        initTotalPrice();
                        initNotOrderList();
                        initOrderList();
                    }else{
                        Toast.makeText(FoodOrderView.this,"请先点菜",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    /**
     * 结账处理
     */
    private void  settleAccount(){
        if(orderListView.getCount() == 0){
            Toast.makeText(FoodOrderView.this,"无菜品需要结账",Toast.LENGTH_SHORT).show();
        }else{
            MyAsyncTask myAsyncTask = new MyAsyncTask("支付中");
            myAsyncTask.execute("支付中");
        }
    }

    /**
     * 初始化各个组件
     */
    private void initComponent(){
        mButton = findViewById(R.id.food_order_view_buy);

        mTabLayout = findViewById(R.id.food_order_tablayout);
        mViewPager = findViewById(R.id.food_order_viewpager);
        notOrderView = getLayoutInflater().inflate(R.layout.not_ordered,null);
        orderedView =getLayoutInflater().inflate(R.layout.order,null);

        mViewList = new ArrayList<>();
        mViewList.add(notOrderView);
        mViewList.add(orderedView);
        numberTextView = findViewById(R.id.food_order_view_number);
        priceTextView = findViewById(R.id.food_order_view_total_price);
    }


    /**
     * 初始化总价
     */
    private void initTotalPrice(){
        int i = 0;
        for(;i<numberHotFoodNotOrder;i++){
            notOrderTotalPrice += FoodItems.hot_food_price[Integer.parseInt(""+positionNotOrder.charAt(i))];
        }
        for(;i<numberHotFoodNotOrder+numberColdFoodNotOrder;i++){
            notOrderTotalPrice += FoodItems.cold_food_price[Integer.parseInt(""+positionNotOrder.charAt(i))];
        }
        for(;i<numberHotFoodNotOrder+numberColdFoodNotOrder+numberSeeFoodNotOrder;i++){
            notOrderTotalPrice += FoodItems.see_food_price[Integer.parseInt(""+positionNotOrder.charAt(i))];
        }
        for(;i<numberHotFoodNotOrder+numberColdFoodNotOrder+numberSeeFoodNotOrder+numberDrinkNotOrder;i++){
            notOrderTotalPrice += FoodItems.drink_price[Integer.parseInt(""+positionNotOrder.charAt(i))];
        }
    }



    /**
     * 根据Intent传来的内容初始化NotOrder的菜品
     */
    private void initPosition(){

        positionNotOrder = new StringBuilder();
        String temp = user.getTagHotFood().toString();
        for(int i = 0; i<temp.length();i++){
            if(temp.charAt(i) == '1'){
                positionNotOrder.append(i);
                numberHotFoodNotOrder++;
            }
        }
        temp = user.getTagColdFood().toString();
        for(int i = 0; i<temp.length();i++){
            if(temp.charAt(i) == '1'){
                positionNotOrder.append(i);
                numberColdFoodNotOrder++;
            }
        }
        temp = user.getTagSeeFood().toString();
        for(int i = 0; i<temp.length();i++){
            if(temp.charAt(i) == '1'){
                positionNotOrder.append(i);
                numberSeeFoodNotOrder++;
            }
        }
        temp = user.getTagDrink().toString();
        for(int i = 0; i<temp.length();i++){
            if(temp.charAt(i) == '1'){
                positionNotOrder.append(i);
                numberDrinkNotOrder++;
            }
        }

    }

    /**
     * 初始化未点的菜
     */
    private void initNotOrderList(){

        notOrderListView = notOrderView.findViewById(R.id.not_order_listview);

        notOrderListView.setAdapter(new BaseAdapter() {
            private int mChildCount = 0;
            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                mChildCount = getCount();
            }

            @Override
            public int getCount() {
                return positionNotOrder.length();
            }

            @Override
            public Object getItem(int position) {
                return "烤肉";
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.ordered_item, parent, false);
                }

                ImageView imageView = convertView.findViewById(R.id.have_ordered_item_image);
                TextView textView_name = convertView.findViewById(R.id.have_ordered_item_name);
                TextView textView_price = convertView.findViewById(R.id.have_ordered_item_price);
                TextView textView_number =convertView.findViewById(R.id.have_ordered_item_number);

                int pos = Integer.parseInt(""+positionNotOrder.charAt(position));

                if(position < numberHotFoodNotOrder){
                    imageView.setImageResource(FoodItems.hot_food_image[pos]);
                    textView_name.setText(FoodItems.hot_food_name[pos]);
                    textView_price.setText(""+FoodItems.hot_food_price[pos]);
                    textView_number.setText("1 份");
                }else if(position < numberColdFoodNotOrder + numberHotFoodNotOrder){
                    imageView.setImageResource(FoodItems.cold_food_image[pos]);
                    textView_name.setText(FoodItems.cold_food_name[pos]);
                    textView_price.setText(""+FoodItems.cold_food_price[pos]);
                    textView_number.setText("1 份");
                }else  if(position< numberColdFoodNotOrder + numberHotFoodNotOrder + numberSeeFoodNotOrder){
                    imageView.setImageResource(FoodItems.see_food_image[pos]);
                    textView_name.setText(FoodItems.see_food_name[pos]);
                    textView_price.setText(""+FoodItems.see_food_price[pos]);
                    textView_number.setText("1 份");
                }else{
                    imageView.setImageResource(FoodItems.drink_image[pos]);
                    textView_name.setText(FoodItems.drink_name[pos]);
                    textView_price.setText(""+FoodItems.drink_price[pos]);
                    textView_number.setText("1 份");
                }

                return convertView;
            }
        });

        numberTextView.setText(""+notOrderListView.getCount());
        priceTextView.setText(""+notOrderTotalPrice);
    }


    /**
     * 初始化点了的菜
     */
    private void initOrderList(){
        orderListView = orderedView.findViewById(R.id.ordered_listview);

        orderListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return user.getPositioinOrder().length();
            }

            @Override
            public Object getItem(int position) {
                return "烤肉";
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.ordered_item, parent, false);
                }

                ImageView imageView = convertView.findViewById(R.id.have_ordered_item_image);
                TextView textView_name = convertView.findViewById(R.id.have_ordered_item_name);
                TextView textView_price = convertView.findViewById(R.id.have_ordered_item_price);
                TextView textView_number =convertView.findViewById(R.id.have_ordered_item_number);

                int pos = Integer.parseInt(""+user.getPositioinOrder().charAt(position));

                if(position < user.getNumberHotFoodNotOrder()){
                    imageView.setImageResource(FoodItems.hot_food_image[pos]);
                    textView_name.setText(FoodItems.hot_food_name[pos]);
                    textView_price.setText(""+FoodItems.hot_food_price[pos]);
                    textView_number.setText("1 份");
                }else if(position < user.getNumberColdFoodNotOrder() + user.getNumberHotFoodNotOrder()){
                    imageView.setImageResource(FoodItems.cold_food_image[pos]);
                    textView_name.setText(FoodItems.cold_food_name[pos]);
                    textView_price.setText(""+FoodItems.cold_food_price[pos]);
                    textView_number.setText("1 份");
                }else  if(position<user.getNumberColdFoodNotOrder() + user.getNumberHotFoodNotOrder() + user.getNumberSeeFoodNotOrder()){
                    imageView.setImageResource(FoodItems.see_food_image[pos]);
                    textView_name.setText(FoodItems.see_food_name[pos]);
                    textView_price.setText(""+FoodItems.see_food_price[pos]);
                    textView_number.setText("1 份");
                }else{
                    imageView.setImageResource(FoodItems.drink_image[pos]);
                    textView_name.setText(FoodItems.drink_name[pos]);
                    textView_price.setText(""+FoodItems.drink_price[pos]);
                    textView_number.setText("1 份");
                }

                return convertView;
            }
        });
    }


    /**
     * 当选定的不同的tab时对标签进行变色
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition() == NOTORDER){
            numberTextView.setText(""+positionNotOrder.length());
            priceTextView.setText(""+notOrderTotalPrice);
            mButton.setText("提交订单");
            mButton.setBackgroundColor(Color.parseColor("#3498DB"));
        }else if(tab.getPosition() == ORDER){
            numberTextView.setText(""+user.getPositioinOrder().length());
            priceTextView.setText(""+user.getTotalPrice());
            mButton.setText("结账");
            mButton.setBackgroundColor(Color.parseColor("#2ECC71"));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void closeDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
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

        mTabLayout.setOnTabSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        mIntent.putExtra("user",user);
        setResult(2,mIntent);
        finish();
        super.onBackPressed();
    }

    class MyAsyncTask extends AsyncTask<String,Integer,String>{

        private String title;
        public MyAsyncTask(String title){
            super();
            this.title = title;
        }
        @Override
        protected void onPreExecute() {
            Toast.makeText(FoodOrderView.this,"正在支付中，请稍等...",Toast.LENGTH_SHORT).show();
            if(mProgressDialog == null|| !mProgressDialog.isShowing()){
                mProgressDialog = new ProgressDialog(FoodOrderView.this);
                mProgressDialog.setTitle("请稍等");
                mProgressDialog.setMessage(title+"...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(FoodOrderView.this,"支付完成",Toast.LENGTH_SHORT).show();
            String temp = "本次支付"+priceTextView.getText().toString()+"元，获得积分"+priceTextView.getText().toString()+"分";
            Toast.makeText(FoodOrderView.this,temp,Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(1);
            closeDialog();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
            mProgressDialog.setSecondaryProgress(0);
        }
        @Override
        protected void onCancelled(String s) {
            System.out.println("======================================");
            Toast.makeText(FoodOrderView.this,"支付中断",Toast.LENGTH_SHORT).show();
            closeDialog();
        }
        @Override
        protected String doInBackground(String... strings) {
            int ratio = 0;
            for(;ratio<=100;ratio+=5){
                if(!mProgressDialog.isShowing()){
                    cancel(true);
                    return strings[0];
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(ratio);
            }
            user.setPositioinOrder(new StringBuilder(""));
            user.setNumberHotFoodNotOrder(0);
            user.setNumberColdFoodNotOrder(0);
            user.setNumberSeeFoodNotOrder(0);
            user.setNumberDrinkNotOrder(0);
            user.setTotalPrice(0);
            return strings[0];
        }
    }
}


