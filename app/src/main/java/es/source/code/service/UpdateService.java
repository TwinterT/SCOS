package es.source.code.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import es.source.code.activity.FoodDetailed;
import es.source.code.activity.R;
import es.source.code.activity.SCOSEntry;
import es.source.code.model.FoodItems;


public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //获取intent中的数据
        Bundle bundle = intent.getBundleExtra("message");
        String tag = bundle.getString("tag");
        if(tag == null){
            //发送接收到库存更新信息
            sendFoodMessage(bundle);
        }else{
            //发送接收到开机启动完成信息
            sendBootStartMessage();
        }
    }

    private void sendBootStartMessage(){
        Intent clickIntent = new Intent(UpdateService.this, SCOSEntry.class);
        PendingIntent contentIntent = PendingIntent.getActivity(UpdateService.this, R.string.app_name,clickIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //初始化通知栏信息
        Notification.Builder mBuilder = new Notification.Builder(UpdateService.this);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setTicker("欢迎点菜！");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo));
        mBuilder.setContentTitle("欢迎点菜！");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentText("欢迎打开SCOS进行点菜");

        //安卓8.0后的通知需要设置渠道否则不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("SCOS");
        }

        Notification notification = mBuilder.build();

        //初始化通知管理并且发送通知
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.string.app_name,notification);
    }


    private void sendFoodMessage(Bundle bundle){
        int number = bundle.getInt("number");
        int pos = bundle.getInt("pos");
        int storage = bundle.getInt("storage");

        String foodName;
        int price;
        //初始化点击的intent数据
        Intent clickIntent = new Intent(UpdateService.this, FoodDetailed.class);
        String name;
        switch (number){
            case FoodItems.HOT_FOOD:name = "hotFood";foodName = FoodItems.hot_food_name[pos];price = FoodItems.hot_food_price[pos];break;
            case FoodItems.COLD_FOOD:name = "coldFood";foodName = FoodItems.cold_food_name[pos];price = FoodItems.cold_food_price[pos];break;
            case FoodItems.SEE_FOOD:name = "seeFood";foodName = FoodItems.see_food_name[pos];price = FoodItems.see_food_price[pos];break;
            default:name = "drink";foodName = FoodItems.drink_name[pos];price = FoodItems.drink_price[pos];break;
        }
        clickIntent.putExtra("data",name);
        clickIntent.putExtra("pos",pos);

        PendingIntent contentIntent = PendingIntent.getActivity(UpdateService.this, R.string.app_name,clickIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //初始化通知栏信息
        Notification.Builder mBuilder = new Notification.Builder(UpdateService.this);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setTicker("新品上架！");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo));
        mBuilder.setContentTitle("新品上架！");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setContentText("类型:"+name +" 菜名: "+foodName+" 价格: "+price);

        //安卓8.0后的通知需要设置渠道否则不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId("SCOS");
        }

        Notification notification = mBuilder.build();

        //初始化通知管理并且发送通知
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.string.app_name,notification);
    }
}
