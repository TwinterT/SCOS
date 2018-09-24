package es.source.code.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.source.code.model.FoodItems;

public class FoodDetailed extends AppCompatActivity {

    private Intent mIntent;

    private ViewPager mViewPager;

    private List<View> mViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detailed);

        mViewPager = findViewById(R.id.food_detailed_viewPager);
        mViewList = new ArrayList<>();

        mIntent = getIntent();
        String str = mIntent.getStringExtra("data");
        int pos = mIntent.getIntExtra("pos",0);
        String tag = mIntent.getStringExtra("tag");

        initViewList(str,tag);

        mViewPager.setAdapter(new MyAdapter());

        mViewPager.setCurrentItem(pos);
    }

    private void initViewList(String message,String tag){
        if(message.equals("hotFood")){
            initViewList(FoodItems.hot_food_name,FoodItems.hot_food_price,FoodItems.hot_food_image,tag);
        }else if(message.equals("coldFood")){
            initViewList(FoodItems.cold_food_name,FoodItems.cold_food_price,FoodItems.cold_food_image,tag);
        }else if(message.equals("seeFood")){
            initViewList(FoodItems.see_food_name,FoodItems.see_food_price,FoodItems.see_food_image,tag);
        }else if(message.equals("drink")){
            initViewList(FoodItems.drink_name,FoodItems.drink_price,FoodItems.drink_image,tag);
        }
    }

    private void initViewList(String[] names,int[] prices,int[] images,String tag){


        for(int i = 0 ;i<names.length;i++){
            final View mView = getLayoutInflater().inflate(R.layout.food_detail_view,null);
            final Button button= mView.findViewById(R.id.food_detail_button);
            ImageView imageView = mView.findViewById(R.id.food_detail_image);
            TextView textView_name = mView.findViewById(R.id.food_detail_name);
            TextView textView_price = mView.findViewById(R.id.food_detail_price);
            imageView.setImageResource(images[i]);
            textView_name.setText(names[i]);
            textView_price.setText(""+prices[i]+" 元");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(button.getText().toString().equals("退点")){
                        button.setText("点菜");
                        button.setTextColor(Color.BLACK);
                    }else if(button.getText().toString().equals("点菜")){
                        button.setText("退点");
                        button.setTextColor(Color.RED);
                    }
                }
            });
            if(tag.charAt(i) == '1'){
                button.setText("退点");
            }else if(tag.charAt(i) == '0'){
                button.setText("点菜");
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
}
