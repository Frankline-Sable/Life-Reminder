package com.maseno.franklinesable.lifereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Frankline Sable on 8/14/2016. From Maseno University in Kenya. Life Reminder
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long rowId=intent.getExtras().getLong(app_database.tb_Struct.KEY_ROW_ID);
        MyWakefulReceiver.acquireStaticLock(context);
        Intent serviceIntent=new Intent(context, reminder_Service.class);
        serviceIntent.putExtra(app_database.tb_Struct.KEY_ROW_ID, rowId);
        context.startService(serviceIntent);
    }
}
