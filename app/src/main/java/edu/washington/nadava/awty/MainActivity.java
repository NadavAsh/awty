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

import org.w3c.dom.Text;


public class MainActivity extends ActionBarActivity {
    public static final int MINUTE = 1000;
    public static final String MESSAGE = "com.washington.nadava.awty.sendmessage.MESSAGE";
    public static final String NUMBER = "com.washington.nadava.awty.sendmessage.NUMBER";

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
                String number = intent.getStringExtra(NUMBER);
                if (number == null) {
                    number = "(425) 555-1212";
                }
                String message = number + ": " + intent.getStringExtra(MESSAGE);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(alarmReceiver, new IntentFilter("com.washington.nadava.awty.sendmessage"));

        final AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

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

                    int interval = Integer.parseInt(intervalText.getText().toString()) * MINUTE;
                    alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + interval,
                                              interval, alarmIntent);
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
