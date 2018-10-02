package es.source.code.activity;


import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

import es.source.code.model.FoodItems;
import es.source.code.model.User;

public class FoodView extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private final String ORDER = "点菜";
    private final String UNSUBSCRIBE = "退点";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private View hotFoodView;
    private ListView hotFoodListView;

    private View coldFoodView;
    private ListView coldFoodListView;

    private View seeFoodView;
    private ListView seeFoodListView;

    private View drinkView;
    private ListView drinkListView;

    private List<View> mViewList;

    private Intent mIntent;

    private User user;

    private boolean tag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_view);

        mIntent = getIntent();
        user = (User)mIntent.getSerializableExtra("user");

        initComponent();
        initTabViewPager();
        setSupportActionBar(mToolbar);

        initListView(hotFoodListView, FoodItems.hot_food_name,FoodItems.hot_food_price,FoodItems.hot_food_image);
        initListView(coldFoodListView, FoodItems.cold_food_name,FoodItems.cold_food_price,FoodItems.cold_food_image);
        initListView(seeFoodListView, FoodItems.see_food_name,FoodItems.see_food_price,FoodItems.see_food_image);
        initListView(drinkListView, FoodItems.drink_name,FoodItems.drink_price,FoodItems.drink_image);
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


    /**
     * 初始化listView组件
     * @param listView   需要被初始化的listview
     * @param names  item的名字数组
     * @param prices     item的价格数组
     * @param images    item的图片存放地址
     */
    private void initListView(final ListView listView, final String[] names, final int[] prices, final int[] images){
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

        listView.setAdapter(new BaseAdapter() {

            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
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

                final Button button = convertView.findViewById(R.id.order_item_button);
                int k = 0;
                if(parent == hotFoodListView){
                    k = user.getTagPositionHotFood(position);
                }else if(parent == coldFoodListView){
                    k = user.getTagPositionColdFood(position);
                }else if(parent == seeFoodListView){
                    k = user.getTagPositionSeeFood(position);
                }else if(parent == drinkListView){
                    k = user.getTagPositionDrink(position);
                }

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
                                user.setTagHotFood(position,'1');
                            }else if(parent == coldFoodListView){
                                user.setTagColdFood(position,'1');
                            }else if(parent == seeFoodListView){
                                user.setTagSeeFood(position,'1');
                            }else if(parent == drinkListView){
                                user.setTagDrink(position,'1');
                            }
                            button.setText(UNSUBSCRIBE);
                            button.setTextColor(Color.RED);
                        }else if(button.getText().toString().equals(UNSUBSCRIBE)){
                            Toast.makeText(FoodView.this,"退点成功 " + getItemId(position),Toast.LENGTH_SHORT).show();
                            if(parent == hotFoodListView){
                                user.setTagHotFood(position,'0');
                            }else if(parent == coldFoodListView){
                                user.setTagColdFood(position,'0');
                            }else if(parent == seeFoodListView){
                                user.setTagSeeFood(position,'0');
                            }else if(parent == drinkListView){
                                user.setTagDrink(position,'0');
                            }
                            button.setText(ORDER);
                            button.setTextColor(Color.BLACK);
                        }
                    }
                });

                return convertView;
            }
        });
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.food_view_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.food_view_already_ordered:{
                Intent intent = new Intent(FoodView.this,FoodOrderView.class);
                intent.putExtra("user",user);
                intent.putExtra("pos",1);
                startActivity(intent);
            }return true;
            case R.id.food_view_watch_ordered:{
                Intent intent = new Intent(FoodView.this,FoodOrderView.class);
                intent.putExtra("user",user);
                intent.putExtra("pos",0);
                startActivity(intent);
            }return true;
            case R.id.food_view_call_for_service:Toast.makeText(FoodView.this,"呼叫服务",Toast.LENGTH_SHORT).show();return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 当返回到上个界面时，传回user按钮
     */
    @Override
    public void onBackPressed() {
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
        }
    }
}




