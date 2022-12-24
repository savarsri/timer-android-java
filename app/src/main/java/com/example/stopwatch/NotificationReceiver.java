package com.example.stopwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity.player.stop();
        MainActivity.player.reset();
        MainActivity.timerUpRelLayout.setVisibility(View.INVISIBLE);
        MainActivity.notificationManagerCompat.cancel(1);
    }
}
