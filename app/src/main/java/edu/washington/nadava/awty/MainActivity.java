package edu.washington.nadava.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    public static final String MESSAGE = "com.washington.nadava.awty.sendmessage.MESSAGE";
    private boolean running;
    PendingIntent alarmIntent;
    BroadcastReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE), Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(alarmReceiver, new IntentFilter("com.washington.nadava.awty.sendmessage"));

        final AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        final TextView messageText = (TextView)findViewById(R.id.editText);
        final Button toggle = (Button)findViewById(R.id.toggleButton);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = !running;


                if (running) {
                    toggle.setText(R.string.stop);

                    Intent i = new Intent();
                    i.setAction("com.washington.nadava.awty.sendmessage");
                    i.putExtra(MESSAGE, messageText.getText().toString());
                    alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000,
                                              10000, alarmIntent);
                } else {
                    toggle.setText(R.string.start);
                    alarmIntent.cancel();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
