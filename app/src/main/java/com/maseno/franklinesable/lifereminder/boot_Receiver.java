package com.maseno.franklinesable.lifereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Frankline Sable on 7/22/2016. From Maseno University in Kenya. LifeReminderEpic
 */
public class boot_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

          ////Set the alarm here
        }
    }
}
