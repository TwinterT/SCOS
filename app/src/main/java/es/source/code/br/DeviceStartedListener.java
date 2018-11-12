package es.source.code.br;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import es.source.code.activity.R;
import es.source.code.service.UpdateService;
import es.source.code.util.Constant;


public class DeviceStartedListener extends BroadcastReceiver {

    public static String TAG = "DeviceStartedListener";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, UpdateService.class);
            serviceIntent.putExtra("tag","phoneStarted");

            //安卓8.0如果要启动服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            }else {
                context.startService(serviceIntent);
            }

        }else if(intent.getAction().equals(Constant.CANCEL_NOTIFICATION_ACTION)){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(R.string.app_name);
        }
    }
}
