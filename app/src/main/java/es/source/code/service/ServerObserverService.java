package es.source.code.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
import org.greenrobot.eventbus.Subscribe;

import java.util.Random;

import es.source.code.activity.FoodView;
import es.source.code.model.EventBusMessage;
import es.source.code.model.FoodItems;

public class ServerObserverService extends Service {

    public static final int MSG_BIND = -1;

    public static boolean ServiceRunningTag = false;

    //判断发送数据线程是否运行
    private boolean threadRunningTag = false;

    //随机数产生器
    private Random mRandom = new Random();

    //获得绑定的Activity的messenger
    Messenger activityMessenger;

    //消息处理
    @SuppressLint("HandlerLeak")
    private Handler cMessageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                //开启服务器线程
                if(!threadRunningTag){
                    threadRunningTag = true;
                }
            }else if(msg.what == 0){
                //关闭服务器线程
                threadRunningTag = false;
            }else if(msg.what == MSG_BIND){
                //获得绑定的Activity的messenger
                activityMessenger = msg.replyTo;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Messenger messenger = new Messenger(cMessageHandler);
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("create0-------------------------");
        ServiceRunningTag = true;
        threadRun();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceRunningTag = false;
        threadRunningTag = false;
    }


    /**
     * 启动模拟服务器发送数据进程
     */
    private void  threadRun(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(ServiceRunningTag){

                    if(threadRunningTag) {
                        //四种类型的菜中随机选择一个
                        int number = mRandom.nextInt(4) + 1;

                        //在选中的菜品中随机选一个位置
                        int pos = mRandom.nextInt(FoodItems.getName(number).length);

                        //随机生成一个库存量
                        int storage = mRandom.nextInt(30);

                        Message message = new Message();

                        //打包数据
                        Bundle bundle = new Bundle();
                        bundle.putInt("number", number);
                        bundle.putInt("pos", pos);
                        bundle.putInt("storage", storage);

                        message.setData(bundle);
                        message.what = 10;

                        //发送数据
                        try {
                            activityMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        //启用updateService
                        Intent intent = new Intent(ServerObserverService.this,UpdateService.class);
                        intent.putExtra("message",bundle);
                        startService(intent);

                    }
                    //休眠300ms
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
