package es.source.code.br;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class DeviceStartedListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent serviceIntent = new Intent("es.source.code.service.openUpdateService");
            serviceIntent.putExtra("tag","startSCOSEntry");
            context.startService(serviceIntent);
        }
    }
}
