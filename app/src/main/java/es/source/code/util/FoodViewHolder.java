package es.source.code.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import es.source.code.activity.R;

public class FoodViewHolder extends RecyclerView.ViewHolder {

    public TextView storage;
    public TextView price;
    public TextView title;
    public ImageView image;
    public Button add;
    public Button subtract;
    public TextView alreadyBuy;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        storage = itemView.findViewById(R.id.order_item_storage);
        price = itemView.findViewById(R.id.order_item_price);
        title = itemView.findViewById(R.id.order_item_name);
        image = itemView.findViewById(R.id.order_item_image);
        add = itemView.findViewById(R.id.order_item_add);
        subtract = itemView.findViewById(R.id.order_item_subtract);
        alreadyBuy = itemView.findViewById(R.id.order_item_buy_number);
    }

    public interface MyRecycleOnClickListener{
        void onItemClidked(View v,int pos);
    }
}
