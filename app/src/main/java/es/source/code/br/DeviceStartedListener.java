package es.source.code.br;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import es.source.code.activity.R;


public class DeviceStartedListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent("es.source.code.service.openUpdateService");
            serviceIntent.putExtra("tag","startSCOSEntry");
            context.startService(serviceIntent);

        }else if(intent.getAction().equals("es.source.code.NOTIFICATION_CANCEL")){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(R.string.app_name);
        }
    }
}
