package com.reiyu.sleepin;
/**
 * Created by Satomi on 1/16/16.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SessionReceiver extends BroadcastReceiver {
    private static final int PERIOD = 900000; // 15 minutes
    private static final int INITIAL_DELAY = 5000; // 5 seconds

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            WakefulIntentService.sendWakefulWork(context, SessionService.class);
        } else {
            scheduleAlarms(context);
        }
    }

    static void scheduleAlarms(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INITIAL_DELAY,
                PERIOD, pi);
    }
}