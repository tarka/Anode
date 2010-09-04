package net.haltcondition.anode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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
public class Anode
    extends Activity
    implements Handler.Callback
{
    private static final String TAG = "Anode";

    // FIXME: Ugh, must be nicer way
    private static final int MENU_SETTINGS = Menu.FIRST;
    private static final int MENU_UPDATE =   Menu.FIRST + 1;

    private ExecutorService pool = Executors.newSingleThreadExecutor();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }


    /* ************************************************************ */
    // Upater thread

    private void fetchUsage()
    {
        // Delegate fetch to thread
        SettingsHelper settings = new SettingsHelper(this);
        Account account = settings.getAccount();
        Service service = settings.getService();

        if (account == null || service == null) {
            Log.w(TAG, "Account or Service not available, doing nothing");
            return;
        }

        HttpWorker<Usage> usageWorker =
            new HttpWorker<Usage>(new Handler(this), account,
                                  Common.usageUri(service), new UsageParser(), this);
        pool.execute(usageWorker);

    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.i(TAG, "Got message: "+ msg.what);

        if (msg.what == HttpWorker.MsgCode.ENDMSG.ordinal()) {
            // Not used
            Log.w(TAG, "Got unused ENDMSG");

        } else if (msg.what == HttpWorker.MsgCode.UPDATEMSG.ordinal()) {
            // Not used, just logged for now
            Log.i(TAG, "Update: "+msg.obj);

        } else if (msg.what == HttpWorker.MsgCode.ERRORMSG.ordinal()) {
            Log.e(TAG, "Error: "+msg.obj);
            // FIXME: Display somehow

        } else if (msg.what == HttpWorker.MsgCode.RESULT.ordinal()) {
            Log.i(TAG, "Got Result");
            setUsage((Usage)msg.obj);
        }

        return true;
    }


    private void setUsage(Usage usage)
    {
        UsageView view = (UsageView) findViewById(R.id.main_usageview);
        view.setUsage(usage.getPercentageUsed().floatValue()/100, usage.getPercentageIntoPeriod().floatValue()/100);


        // FIXME: Needs parcelable usage
        // Broadcast to others (mainly widget)
        //Intent i = new Intent(Common.USAGE_UPDATE);
        //i.putExtra(Common.USAGE_UPDATE, usage);
        //sendBroadcast(i);
    }



    /* ************************************************************ */
    // Menus

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings)
            .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_UPDATE, 0, R.string.menu_update)
            // FIXME: Use stock icon when available
            .setIcon(R.drawable.ic_menu_refresh);

        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          case MENU_SETTINGS:
            Intent i = new Intent(this, EditSettings.class);
            startActivityForResult(i, MENU_SETTINGS);
            return true;
          case MENU_UPDATE:
            fetchUsage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
