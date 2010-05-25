package net.haltcondition.anode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Anode extends Activity
{
    private static final String TAG = "Anode";

    // FIXME: Ugh, must be nicer way
    private static final int MENU_SETTINGS = Menu.FIRST;
    private static final int MENU_UPDATE =   Menu.FIRST + 1;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
            Intent i = new Intent(this, EditAccount.class);
            startActivityForResult(i, MENU_SETTINGS);
            return true;
          case MENU_UPDATE:
            //sync();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
