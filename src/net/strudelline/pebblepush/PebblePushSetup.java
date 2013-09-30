package net.strudelline.pebblepush;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class PebblePushSetup extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final EditText userId = (EditText) findViewById(R.id.editText);
        final Button save = (Button) findViewById(R.id.button);
        SharedPreferences prefs = getSharedPreferences("net.strudelline.pebblepush", 0);
        String user = prefs.getString("userId", null);
        if (user == null || user.length() == 0  ) {
            UUID uuid = UUID.randomUUID();
            userId.setText(uuid.toString());
        } else {
            userId.setText(user);
        }
        final Activity outerThis = this;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("net.strudelline.pebblepush", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userId", userId.getText().toString());
                editor.apply();
                Toast toast = Toast.makeText(outerThis, "Starting Service", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(outerThis, PebblePushService.class);
                startService(intent);
            }
        });

    }
}
