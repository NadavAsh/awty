package edu.washington.nadava.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MainActivity";

    public static final int MINUTE = 1000;
    public static final String MESSAGE = "com.washington.nadava.awty.sendmessage.MESSAGE";
    public static final String NUMBER = "com.washington.nadava.awty.sendmessage.NUMBER";
    public static final String INTERVAL = "com.washington.nadava.awty.sendmessage.INTERVAL";


    private boolean running;
    private BroadcastReceiver alarmReceiver;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Alarm received.");
                String message = intent.getStringExtra(MESSAGE);
                if (message == null || message.isEmpty()) {
                    message = "Are we there yet?";
                }

                String number = intent.getStringExtra(NUMBER);
                if (number == null || number.isEmpty()) {
                    number = "(425) 555-1212";
                }

                String fullMessage = number + ": " + message;
                Toast.makeText(MainActivity.this, fullMessage, Toast.LENGTH_SHORT).show();


                alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                // Use set alarm instead of repeating because repeating screws up on newer devices.
                if (running) {
                    alarmManager.set(AlarmManager.RTC,
                            System.currentTimeMillis() + intent.getIntExtra(INTERVAL, MINUTE),
                            alarmIntent);
                }
            }
        };

        registerReceiver(alarmReceiver, new IntentFilter("com.washington.nadava.awty.sendmessage"));

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        final TextView messageText = (TextView)findViewById(R.id.editText);
        final TextView phoneText = (TextView)findViewById(R.id.phoneText);
        final TextView intervalText = (TextView)findViewById(R.id.intervalText);
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
                    i.putExtra(NUMBER, phoneText.getText().toString());

                    int interval;
                    if (intervalText.getText().length() == 0) interval = MINUTE;
                    else
                        interval = Integer.parseInt(intervalText.getText().toString()) * MINUTE;
                    i.putExtra(INTERVAL, interval);

                    alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    // Use set instead of repeating because set is better.
                    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + interval, alarmIntent);
                } else {
                    toggle.setText(R.string.start);
                    alarmIntent.cancel();
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(alarmIntent);
        unregisterReceiver(alarmReceiver);
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
