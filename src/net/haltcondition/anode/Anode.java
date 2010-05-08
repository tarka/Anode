package net.haltcondition.anode;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Anode extends Activity
{
    // FIXME: Ugh, must be nicer way
    private static final int MENU_SETTINGS = Menu.FIRST;
    private static final int MENU_UPDATE = Menu.FIRST+1;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings)
            .setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_UPDATE, 0, R.string.menu_update)
            .setIcon(R.drawable.ic_menu_refresh);  // FIXME: Use stock icon when available
        
        return result;
    }

}
