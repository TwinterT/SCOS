package es.source.code.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import es.source.code.activity.FoodView;
import es.source.code.activity.R;
import es.source.code.model.User;

public class FoodViewRecyclerAdapter extends RecyclerView.Adapter<FoodViewHolder>{

    private Context mContext;
    private String[] names;
    private int[] images;
    private int[] prices;
    private int[] storage;
    private int tag;
    private User user;

    /**
     *
     * @param tag from User ex:HOTFOOD,COLDFOOD
     * @param names
     * @param images
     * @param prices
     * @param storage
     * @param context
     * @param user
     */
    public FoodViewRecyclerAdapter(int tag,String[] names,int[] images,int[] prices,int[] storage,Context context,User user){
        this.tag = tag;
        this.names = names;
        this.images = images;
        this.prices = prices;
        this.storage = storage;
        this.mContext = context;
        this.user = user;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater lf = LayoutInflater.from(mContext);
        View view = lf.inflate(R.layout.dishes_item,null);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int i) {
        foodViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyRecycleOnClickListener.onItemClidked(v,foodViewHolder.getLayoutPosition());
            }
        });
        foodViewHolder.image.setImageResource(images[i]);

        foodViewHolder.title.setText(names[i]);

        foodViewHolder.price.setText(""+prices[i]+" 元");

        foodViewHolder.storage.setText("库存 " + storage[i] + " 份");

        int buyNumber = user.getTagPosition(tag,i);
        foodViewHolder.alreadyBuy.setText(Integer.valueOf(buyNumber).toString());

        Button add = foodViewHolder.add;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clicked",tag+"is clicked");
                int number = user.getTagPosition(tag,i);
                if(Integer.parseInt(foodViewHolder.alreadyBuy.getText().toString())<FoodItems.getStorage(tag)[i]) {
                    //当小于库存量时
                    user.addTag(tag,i);
                    foodViewHolder.alreadyBuy.setText(Integer.valueOf(number+1).toString());
                }else {
                    Toast.makeText(mContext, "库存不足", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button subtract = foodViewHolder.subtract;
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = user.getTagPosition(tag,i);
                if(Integer.parseInt(foodViewHolder.alreadyBuy.getText().toString())>0){
                    //当已经点了的菜品大于0时
                    user.minusTag(tag,i);
                    foodViewHolder.alreadyBuy.setText(Integer.valueOf(number-1).toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    private FoodViewHolder.MyRecycleOnClickListener mMyRecycleOnClickListener;

    public void setOnItemClickListener(FoodViewHolder.MyRecycleOnClickListener MyRecycleOnClickListener){
        mMyRecycleOnClickListener = MyRecycleOnClickListener;
    }
}
