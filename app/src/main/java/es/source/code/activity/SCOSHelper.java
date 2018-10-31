package es.source.code.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.provider.Settings;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import es.source.code.model.EventBusMessage;
import es.source.code.util.MailSender;


public class SCOSHelper extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;

    final private String[] mStrings = {"用户使用协议", "关于系统", "电话人工帮助", "短信帮助", "邮件帮助"};

    private GridView mGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scos_helper);

        mGridView = findViewById(R.id.scos_helper_grid);

        initGridView();

        //订阅EventBus
        EventBus.getDefault().register(this);
    }

    /**
     * 处理收到的eventBusMessage
     * @param eventBusMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessage eventBusMessage){
        if(eventBusMessage.getMessage().equals("1")){
            Toast.makeText(SCOSHelper.this,"邮件发送成功",Toast.LENGTH_SHORT).show();
        }else if(eventBusMessage.getMessage().equals("-1")){
            Toast.makeText(SCOSHelper.this,"正在发送邮件...",Toast.LENGTH_SHORT).show();
            Toast.makeText(SCOSHelper.this,"邮件发送中，请耐心等待",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(SCOSHelper.this,"邮件发送失败",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //取消订阅EventBus
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化GridView，包括适配器和监听器
     */
    private void initGridView() {
        mGridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mStrings.length;
            }

            @Override
            public Object getItem(int position) {
                return mStrings[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.scoshelper_griditem, null);
                TextView textView = view.findViewById(R.id.scoshelper_item);
                textView.setText(mStrings[position]);
                return view;
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //用户使用协议
                        Toast.makeText(SCOSHelper.this, "使用协议", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: //关于系统
                        Toast.makeText(SCOSHelper.this, "关于系统", Toast.LENGTH_SHORT).show();
                        break;
                    case 2: //电话人工帮助
                        telephoneHelp();
                        break;
                    case 3: //短信帮助
                        messageHelp();
                        break;
                    case 4: //邮件帮助
                        emailHelp();
                        break;
                }
            }
        });
    }

    /**
     * 处理电话帮助
     */
    private void telephoneHelp() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                Toast.makeText(this, "请授权！", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            } else {
                ActivityCompat.requestPermissions(SCOSHelper.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            makePhoneCall();
        }
    }

    private void makePhoneCall() {
        Toast.makeText(SCOSHelper.this, "处理电话帮助", Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse("tel:5554");
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(intent);
    }

    /**
     * 处理短信帮助
     */
    private void messageHelp(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)||ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){

                Toast.makeText(this, "请授权！", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }else{
                ActivityCompat.requestPermissions(SCOSHelper.this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_SEND_SMS);

            }
        }else{
            sendMessage();
        }
    }

    /**
     * 发送短信
     */
    private void sendMessage(){
        SmsManager smsManager = SmsManager.getDefault();

        PendingIntent sendIntent = PendingIntent.getBroadcast(this,0,new Intent("SENT_SMS_ACTION"),0);

        registerReceiver(new BroadcastReceiver() {//注册广播接受短信发送是否成功的消息
            @Override
            public void onReceive(Context context, Intent intent) {
                if(getResultCode() == Activity.RESULT_OK){
                    Toast.makeText(SCOSHelper.this,"短信发送成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SCOSHelper.this,"短信发送失败",Toast.LENGTH_SHORT).show();
                }
            }
        },new IntentFilter("SENT_SMS_ACTION"));

        smsManager.sendTextMessage("5554",null,"test scos helper",sendIntent,null);

    }

    /**
     * 处理邮件帮助
     */
    private void emailHelp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventBusMessage("-1"));
                try {
                    sendEmail();
                    EventBus.getDefault().post(new EventBusMessage("1"));
                } catch (Exception e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new EventBusMessage("0"));
                }

            }
        }).start();
    }

    /**
     * 发送邮件
     */
    private void sendEmail() throws Exception {

        MailSender sender = new MailSender("18651400987@163.com", "winter1996");
        sender.sendMail("帮助", "在运行软件中出现问题，收到请回复！！！","18651400987@163.com", "twinter@mail.ustc.edu.cn");

    }

    /**
     * 处理权限申请的结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendMessage();
            }else{
                Toast.makeText(this,"授权失败！",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else{
                Toast.makeText(this,"授权失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }
}


