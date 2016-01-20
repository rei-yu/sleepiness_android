package com.reiyu.sleepin;
/**
 * Created by Satomi on 1/16/16.
 */

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Calendar;
import java.util.Random;

public class SessionReceiver extends BroadcastReceiver {
    private static final int alarm1 = 1;
    private static final int alarm2 = 2;
    private static final int alarm3 = 3;
    private static final int alarm4 = 4;
    private static final int alarm5 = 5;
    private static final int alarm6 = 6;
    private static final int alarm7 = 7;
    private static final int alarm8 = 8;

    private static final int alarm10 = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarm_id = intent.getIntExtra("@string/alarm_id", 0);
        Intent intentNew;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if ((alarm_id > 0) && (alarm_id < 10)) {
            intentNew = new Intent(context, ReflectFragment.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, alarm_id, intentNew, 0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("振り返りの時間です！")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("振り返りの時間です！")
                    .setContentText("タップして振り返る")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.cancelAll();
            notificationManager.notify(R.string.app_name, notification);
        } else if (alarm_id == 10) {
            intentNew = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, alarm_id, intentNew, 0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("Session開始です！")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Session開始です！")
                    .setContentText("起床記録も忘れずにつけて下さい！")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationManager.cancelAll();
            notificationManager.notify(R.string.app_name, notification);
        } else {
            if (intent.getAction() == null) {
                WakefulIntentService.sendWakefulWork(context, SessionService.class);
            } else {
                Log.e("SessionReceiver", "getAction() is not null");
            }
        }
    }

    static void scheduleAlarms(Context context, int hour, int minute, int alarm_id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, SessionReceiver.class);
        intent.putExtra("@string/alarm_id", alarm_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Random rnd = new Random();
        int ran = rnd.nextInt(100);
        cal.set(Calendar.SECOND, ran);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.e("scheduleAlarms time", String.valueOf(System.currentTimeMillis()));

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
