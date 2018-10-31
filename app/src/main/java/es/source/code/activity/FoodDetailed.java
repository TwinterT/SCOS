package es.source.code.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.source.code.util.FoodItems;
import es.source.code.model.User;

public class FoodDetailed extends AppCompatActivity {

    private static final String TAG = "FoodDetailed";

    private Intent mIntent;

    private ViewPager mViewPager;

    private List<View> mViewList;

    private User user;

    private int name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detailed);

        mViewPager = findViewById(R.id.food_detailed_viewPager);
        mViewList = new ArrayList<>();

        mIntent = getIntent();
        name = mIntent.getIntExtra("data",1);
        int pos = mIntent.getIntExtra("pos",0);
        Log.d(TAG, "onCreate: "+pos);
        String tag = mIntent.getStringExtra("tag");
        user = (User)mIntent.getSerializableExtra("user");

        initViewList(name,tag);

        mViewPager.setAdapter(new MyAdapter());

        mViewPager.setCurrentItem(pos);
    }


    /**
     * 判断传进来的是那个列表HotFood，ColdFood等等
     * @param message 传来的列表名字
     * @param tag  列表中所有食物点菜的状态（0，1），退点还是点菜
     */
    private void initViewList(int message,String tag){
        if(message == User.HOTFOOD){
            initViewList(FoodItems.hot_food_name,FoodItems.hot_food_price,FoodItems.hot_food_image,tag,message);
        }else if(message == User.COLDFOOD){
            initViewList(FoodItems.cold_food_name,FoodItems.cold_food_price,FoodItems.cold_food_image,tag,message);
        }else if(message == User.SEEFOOD){
            initViewList(FoodItems.see_food_name,FoodItems.see_food_price,FoodItems.see_food_image,tag,message);
        }else if(message == User.DRINK){
            initViewList(FoodItems.drink_name,FoodItems.drink_price,FoodItems.drink_image,tag,message);
        }
    }

    /**
     * 初始化列表
     * @param names 列表食物的名字
     * @param prices 列表食物的价格
     * @param images 列表食物的图像
     * @param tag 列表食物的所有点菜状态（0，1）
     * @param message  列表名字
     */
    private void initViewList(String[] names, int[] prices, int[] images, String tag, final int message){


        for(int i = 0 ;i<names.length;i++){
            final View mView = getLayoutInflater().inflate(R.layout.food_detail_view,null);
            final Button button= mView.findViewById(R.id.food_detail_button);
            ImageView imageView = mView.findViewById(R.id.food_detail_image);
            TextView textView_name = mView.findViewById(R.id.food_detail_name);
            TextView textView_price = mView.findViewById(R.id.food_detail_price);
            imageView.setImageResource(images[i]);
            textView_name.setText(names[i]);
            textView_price.setText(""+prices[i]+" 元");
            final int finalI = i;
            if(user != null){
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(button.getText().toString().equals("退点")){
                            button.setText("点菜");
                            button.setTextColor(Color.BLACK);
                            user.setTagPosition(message,finalI,'0');
                        }else if(button.getText().toString().equals("点菜")){
                            button.setText("退点");
                            button.setTextColor(Color.RED);
                            user.setTagPosition(message,finalI,'1');
                        }
                    }
                });
                if(tag.charAt(i) == '1'){
                    button.setText("退点");
                    button.setTextColor(Color.RED);
                }else if(tag.charAt(i) == '0'){
                    button.setText("点菜");
                    button.setTextColor(Color.BLACK);
                }
            }else{
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
            }

            mViewList.add(mView);
        }
    }

    private class MyAdapter extends PagerAdapter {

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
    public void onBackPressed() {
        mIntent.putExtra("user",user);
        mIntent.putExtra("name",name);
        setResult(2,mIntent);
        finish();
        super.onBackPressed();
    }
}
