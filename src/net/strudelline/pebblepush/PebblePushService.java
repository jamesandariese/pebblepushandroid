package net.strudelline.pebblepush;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/29/13
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PebblePushService extends Service implements Runnable {
    Thread processor;
    List<Intent> intents;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (processor != null) {
            if (processor.isAlive()) {
                return super.onStartCommand(intent, flags, startId);
            }
        }
        if (intents == null) {
            intents = new LinkedList<Intent>();
        }
        processor = new Thread(this);
        processor.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private JSONObject readStreamAsJson(InputStream in) throws JSONException {
        Scanner s = new Scanner(in, "UTF-8").useDelimiter("\\A");
        String json = s.hasNext() ? s.next() : "";
        JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
        return object;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendAlertToPebble(String title, String message) {
        // this must be called every <time interval> in order toe nsure
        // that the paused state is checked.
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", title);
        data.put("body", message);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "pebblepush");
        i.putExtra("notificationData", notificationData);

        Log.d("pebblepush", "About to send a modal alert to Pebble: " + notificationData);
        if (PebblePushPauser.isPaused()) {
            Log.i("pebblepush", "PebblePush is paused.  Storing intent instead.");
            intents.add(i);
        } else {
            for (Intent backedUpIntent : intents) {
                Log.i("pebblepush", "PebblePush is unpaused.  Sending stored intent");
                sendBroadcast(i);
            }
            intents.clear();
            sendBroadcast(i);
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                SharedPreferences prefs = getSharedPreferences("net.strudelline.pebblepush", 0);
                String user = prefs.getString("userId", null);
                if (user == null || user.length() == 0) {
                    return;
                }
                Uri.Builder uriBuilder = Uri.parse("http://gandi.strudelline.net:8088/pull").buildUpon();
                uriBuilder.appendQueryParameter("user", user);
                Log.i("pebblepush", "Listening on " + uriBuilder.toString());
                URL url = new URL(uriBuilder.toString());
                URLConnection conn = url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                JSONObject json = readStreamAsJson(in);
                JSONObject response = json.getJSONObject("response");
                String title = response.getString("title");
                String message = response.getString("message");
                sendAlertToPebble(title, message);
                //Toast toast = Toast.makeText(this, "Title: " + title + " message:" + message, Toast.LENGTH_SHORT);
                //toast.show();
            } catch (Exception e) {
                Log.i("pebblepush", "Error " + e.toString());
                //Toast toast = Toast.makeText(this, "Error " + e.toString(), Toast.LENGTH_SHORT);
                //toast.show();
            }
        }
    }
}
