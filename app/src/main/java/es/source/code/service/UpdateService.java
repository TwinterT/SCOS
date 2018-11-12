package es.source.code.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import es.source.code.activity.FoodDetailed;
import es.source.code.activity.MainScreen;;
import es.source.code.activity.R;
import es.source.code.activity.SCOSEntry;
import es.source.code.br.DeviceStartedListener;
import es.source.code.util.Constant;
import es.source.code.util.FoodItems;


public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //获取intent中的数据
        Bundle bundle = intent.getBundleExtra("message");
        String tag = intent.getStringExtra("tag");
        if(tag.equals("startSCOSEntry")){
        //发送接收到库存更新信息
            sendRemoteMessage(bundle);
        }else if(tag.equals("phoneStarted")){
            //发送接收到开机启动完成信息
//            Bundle testBundle = new Bundle();
//            testBundle.putInt("type",1);
//            testBundle.putInt("pos",1);
//            testBundle.putInt("storage",10);
//            testBundle.putInt("price",40);
//            sendRemoteMessage(testBundle);
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
        startForeground(1, notification);
    }


    private void sendFoodMessage(Bundle bundle){
        int type = bundle.getInt("type");
        int pos = bundle.getInt("pos");
        int storage = bundle.getInt("storage");
        int price = bundle.getInt("price");

        String foodName;
        //初始化点击的intent数据
        Intent clickIntent = new Intent(UpdateService.this, FoodDetailed.class);
        int name;
        switch (type){
            case FoodItems.HOT_FOOD:name = FoodItems.HOT_FOOD;foodName = FoodItems.hot_food_name[pos];break;
            case FoodItems.COLD_FOOD:name = FoodItems.COLD_FOOD;foodName = FoodItems.cold_food_name[pos];break;
            case FoodItems.SEE_FOOD:name = FoodItems.SEE_FOOD;foodName = FoodItems.see_food_name[pos];break;
            default:name = FoodItems.DRINK;foodName = FoodItems.drink_name[pos];break;
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

    private void sendRemoteMessage(Bundle bundle){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int type = bundle.getInt("type");
        int pos = bundle.getInt("pos");
        int storage = bundle.getInt("storage");
        int price = bundle.getInt("price");

        Intent pIntent = new Intent(UpdateService.this,MainScreen.class);
        String foodName;
        String name;
        switch (type){
            case FoodItems.HOT_FOOD:name = "hotFood";foodName = FoodItems.hot_food_name[pos];break;
            case FoodItems.COLD_FOOD:name = "coldFood";foodName = FoodItems.cold_food_name[pos];break;
            case FoodItems.SEE_FOOD:name = "seeFood";foodName = FoodItems.see_food_name[pos];break;
            default:name = "drink";foodName = FoodItems.drink_name[pos];break;
        }
        pIntent.putExtra("data",name);
        pIntent.putExtra("pos",pos);

        PendingIntent nIntent = PendingIntent.getActivity(UpdateService.this,R.string.app_name,pIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews notify_food = new RemoteViews(UpdateService.this.getPackageName(),R.layout.notification);

        //设置通知栏组件内容和监听
        notify_food.setTextViewText(R.id.notification_title,"新品上架！");
        notify_food.setImageViewResource(R.id.notification_icon,R.drawable.logo);
        String contentString = "类型：" + name + "菜名：" + foodName + "价格：" + price;
        notify_food.setTextViewText(R.id.notification_content,contentString);
        Intent intent = new Intent(this,DeviceStartedListener.class);
        intent.setAction(Constant.CANCEL_NOTIFICATION_ACTION);
        PendingIntent intentLast = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notify_food.setOnClickPendingIntent(R.id.notification_button,intentLast);
        Notification.Builder builder;
        //安卓8.0后的通知需要设置渠道否则不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("SCOS","SCOS",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(this,notificationChannel.getId());
        }else{
            builder = new Notification.Builder(UpdateService.this);
        }
        builder.setContentIntent(nIntent);
        builder.setContent(notify_food);
        builder.setTicker("新品上架！");
        builder.setSmallIcon(R.drawable.logo);

        Notification notify = builder.build();

        notificationManager.notify(R.string.app_name,notify);

        //播放声音
        MediaPlayer mediaPlayer = MediaPlayer.create(this,Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

    }
}
