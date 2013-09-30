package net.strudelline.pebblepush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/30/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class PebblePushPauser extends BroadcastReceiver {
    private static boolean paused;

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean paused) {
        PebblePushPauser.paused = paused;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.getpebble.action.PEBBLE_CONNECTED")) {
            Log.i("pebblepush", "Unpausing pebblepush");
            setPaused(false);
        } else if (intent.getAction().equals("com.getpebble.action.PEBBLE_DISCONNECTED")) {
            Log.i("pebblepush", "Pausing pebblepush");
            setPaused(true);
        } else {
            Log.w("pebblepush", "Received unexpected intent in PebblePushPauser: " + intent.getAction());
        }
    }
}