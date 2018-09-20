package es.source.code.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class SCOSEntry extends AppCompatActivity implements View.OnTouchListener,GestureDetector.OnGestureListener{

    private RelativeLayout mRelativeLayout;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);

        mRelativeLayout = findViewById(R.id.relativeLayout);
        //定义一个手势检测器
        mRelativeLayout.setOnTouchListener(this);
        mRelativeLayout.setLongClickable(true);
        mGestureDetector = new GestureDetector(this);
    }


    @Override
        public boolean onDown(MotionEvent e) {
            return false;
            }

    @Override
    public void onShowPress(MotionEvent e) {

            }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
            return false;
            }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
            }

    @Override
    public void onLongPress(MotionEvent e) {

            }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //检测到在屏幕上的滑动，根据相对位置来判断滑动的方向
            float offsetX = e1.getX() - e2.getX();
            float offsetY = e1.getY() - e2.getY();
            if(Math.abs(offsetX) > Math.abs(offsetY)) { //在X上的偏移大于Y上的偏移时，为左右滑动
                if(offsetX > 0 ){ //此时为向左滑动
                    Intent intent = new Intent(MainScreen.MAINSCREEN_ACTION);
                    intent.putExtra("data", "1FromEntry");
                    startActivity(intent);
                }
            }

            return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
