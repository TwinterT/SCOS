package es.source.code.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import es.source.code.util.Constant;

public class SCOSEntry extends AppCompatActivity {//implements View.OnTouchListener,GestureDetector.OnGestureListener{

//    private RelativeLayout mRelativeLayout;
//    private GestureDetector mGestureDetector;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                Intent intent = new Intent(Constant.MAINSCREEN_ACTION);
                    startActivity(intent);
                    finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.entry);
        getWindow().setStatusBarColor(0xffffff);

        //权限检查
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            //获取
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,
                                                                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                                                        Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_FINE_LOCATION,
                                                                        Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }






//        mRelativeLayout = findViewById(R.id.relativeLayout);
//        //定义一个手势检测器
//        mRelativeLayout.setOnTouchListener(this);
//        mRelativeLayout.setLongClickable(true);
//        mGestureDetector = new GestureDetector(this);

        Toast.makeText(SCOSEntry.this,"欢迎来到SCOS！",Toast.LENGTH_LONG).show();

        waitForTwoSecond();
    }

    private void waitForTwoSecond(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        });
        thread.start();
    }

//    @Override
//        public boolean onDown(MotionEvent e) {
//            return false;
//            }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//
//            }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//            return false;
//            }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return false;
//            }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//
//            }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            //检测到在屏幕上的滑动，根据相对位置来判断滑动的方向
//            float offsetX = e1.getX() - e2.getX();
//            float offsetY = e1.getY() - e2.getY();
//            if(Math.abs(offsetX) > Math.abs(offsetY)) { //在X上的偏移大于Y上的偏移时，为左右滑动
//                if(offsetX > 0 ){ //此时为向左滑动
//                    Intent intent = new Intent(Constant.MAINSCREEN_ACTION);
//                    startActivity(intent);
//                }
//            }
//
//            return true;
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return mGestureDetector.onTouchEvent(event);
//    }
}
