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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import es.source.code.activity.FoodView;
import es.source.code.model.EventBusMessage;
import es.source.code.model.FoodItems;

public class ServerObserverService extends Service {

    public static final int MSG_BIND = -1;

    public static boolean ServiceRunningTag = false;

    private static final String URLSTRING = "http://192.168.1.104:8080/SCOSServer/FoodUpdateService";

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
                        JSONObject jsonObject;
                        int type = 1,pos = 0,storage = 0,price = 0;
                        try {
                            jsonObject = httpGet();
                            //四种类型的菜
                            type = jsonObject.getInt("type");
                            //在选中的菜品中的位置
                            pos = jsonObject.getInt("pos");
                            //库存量
                            storage = jsonObject.getInt("storage");
                            //价格
                            price = jsonObject.getInt("price");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message message = new Message();

                        //打包数据
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", type);
                        bundle.putInt("pos", pos);
                        bundle.putInt("storage", storage);
                        bundle.putInt("price",price);

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
                    //休眠3000ms
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private JSONObject httpGet() throws Exception {
        //网络连接的建立
        URL url = new URL(URLSTRING);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");


        //获得输入流
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        //接收数据
        StringBuilder data = new StringBuilder("");
        String line;
        while((line = bufferedReader.readLine())!=null){
            data.append(line);
        }

        //关闭输入流
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        return new JSONObject(data.toString());
    }
}
