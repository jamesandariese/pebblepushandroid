package net.strudelline.pebblepush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/30/13
 * Time: 1:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class PebblePushStarter extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.i("pebblepush", "in PebblePushStarter");
        Intent serviceIntent = new Intent(context, PebblePushService.class);
        context.startService(serviceIntent);
    }
}
