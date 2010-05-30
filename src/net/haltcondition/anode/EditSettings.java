package net.haltcondition.anode;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 2010
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation..
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class EditSettings
    extends Activity
    implements Handler.Callback

{
    private static final String TAG = "EditAccount";

    private static final int MENU_ABOUT = Menu.FIRST;

    private static final long DEFAULT_FREQ = AlarmManager.INTERVAL_HOUR;

    private EditText eUsername;
    private EditText ePassword;
    private CheckBox wifi_only;
    private long updateFreq = DEFAULT_FREQ;

    private static class DelayOption {
        public String name;
        public Long alarmCode;
        public DelayOption(String name, Long code) {
            this.name = name;
            this.alarmCode = code;
        }

        @Override public String toString() { return name; }
    }

    private static final DelayOption DO_15M = new DelayOption("15 minutes", AlarmManager.INTERVAL_FIFTEEN_MINUTES);
    private static final DelayOption DO_30M = new DelayOption("30 minutes", AlarmManager.INTERVAL_HALF_HOUR);
    private static final DelayOption DO_60M = new DelayOption("Hourly", AlarmManager.INTERVAL_HOUR);
    private static final DelayOption DO_12H = new DelayOption("12 Hours", AlarmManager.INTERVAL_HALF_DAY);


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Log.i(TAG, "Starting account edit");

        final SettingsHelper settings = new SettingsHelper(this);


        // Account
        eUsername = (EditText) findViewById(R.id.username);
        ePassword = (EditText) findViewById(R.id.password);

        Account account = settings.getAccount();
        eUsername.setText(account.getUsername());
        ePassword.setText(account.getPassword());


        // Update frequency
        Spinner s = (Spinner)findViewById(R.id.refreshinterval);

        long curr = settings.getUpdateInterval();
        final List<DelayOption> freqlist = new ArrayList<DelayOption>();
        freqlist.add(DO_15M);
        freqlist.add(DO_30M);
        freqlist.add(DO_60M);
        freqlist.add(DO_12H);

        ArrayAdapter<DelayOption> options = new ArrayAdapter<DelayOption>(this, android.R.layout.simple_spinner_item, freqlist);
        options.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(options);
        for (DelayOption o: freqlist) {
            if (o.alarmCode == curr) {
                s.setSelection(freqlist.indexOf(o));
                break;
            }
        }
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                updateFreq = freqlist.get(position).alarmCode;
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                updateFreq = DEFAULT_FREQ;
            }
        });


        // WiFi only
        wifi_only = (CheckBox)findViewById(R.id.wifi_only);
        wifi_only.setChecked(settings.getWifiOnly());


        // Save
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    Log.i(TAG, "Got save button click");
                    initiateServiceFetch();
                }
            });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    /* ************************************************************ */

    private ProgressDialog progress;
    private ExecutorService pool = Executors.newSingleThreadExecutor();

    @Override
    public boolean handleMessage(Message msg)
    {
        if (msg.what == HttpWorker.MsgCode.UPDATEMSG.ordinal()) {
            progress.setMessage((String)msg.obj);

        } else if (msg.what == HttpWorker.MsgCode.ERRORMSG.ordinal()) {
            String err = getResources().getString(R.string.failed_to_fetch_service) +msg.obj;
            Log.e(TAG, err);

            progress.dismiss();
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();

        } else if (msg.what == HttpWorker.MsgCode.RESULT.ordinal()) {
            Service service = (Service)msg.obj;
            Log.i(TAG, "Got service "+service.getServiceName()+": "+service.getServiceId());

            progress.dismiss();

            saveSettings(service);

            Toast.makeText(this, getResources().getString(R.string.got_service) +" \""+service.getServiceName()+"\"", Toast.LENGTH_SHORT).show();

            // Close
            setResult(RESULT_OK);
            finish();

        } else {
            Log.w(TAG, "Received unknown Message ID");
            return false;
        }

        return true;
    }

    private void saveSettings(Service svc)
    {
        Log.i(TAG, "Got account");

        final SettingsHelper settings = new SettingsHelper(this);

        Account account = new Account(eUsername.getText().toString(),
                                      ePassword.getText().toString());

        settings.setAll(account, svc, updateFreq, wifi_only.isChecked());

        Intent i = new Intent(Common.SETTINGS_UPDATE);
        sendBroadcast(i);

    }

    private void initiateServiceFetch()
    {
        Log.i(TAG, "Fetching Service ID");

        Account account = new Account(eUsername.getText().toString(),
                                      ePassword.getText().toString());

        progress = ProgressDialog.show(this, getResources().getString(R.string.fetching_service),
                                       getResources().getString(R.string.starting_worker),
                                       false, true);


        HttpWorker<Service> serviceWorker =
            new HttpWorker<Service>(new Handler(this), account, Common.INODE_URI_BASE, new ServiceParser());
        pool.execute(serviceWorker);
    }

    /* ************************************************************ */
    // Menus

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ABOUT, 0, R.string.menu_about)
            .setIcon(android.R.drawable.ic_menu_info_details);

        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          case MENU_ABOUT:
            startActivity(new Intent(this, About.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
