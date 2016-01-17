package com.reiyu.sleepin;

/**
 * Created by Satomi on 1/14/16.
 */

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SessionService extends WakefulIntentService {
    public SessionService() {
        super("ScheduledService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        Log.e("dowakefulWork", "running");

        SessionReceiver.scheduleAlarms(this);
    }
}