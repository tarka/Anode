package net.haltcondition.anode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EditAccount
    extends Activity
    implements Handler.Callback

{
    private static final String TAG = "EditAccount";

    private EditText eUsername;
    private EditText ePassword;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Log.i(TAG, "Starting account edit");


        eUsername = (EditText) findViewById(R.id.username);
        ePassword = (EditText) findViewById(R.id.password);

        Account account = new SettingsHelper(this).getAccount();
        eUsername.setText(account.getUsername());
        ePassword.setText(account.getPassword());

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    Log.i(TAG, "Got save button click");
                    saveState();
                    //setResult(RESULT_OK);
                    //finish();
                }
            });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /* ************************************************************ */

    @Override
    protected void onNewIntent(Intent intent)
    {
        Log.i(TAG, "GOT INTENT "+intent);
    }

    

    /* ************************************************************ */

    private ProgressDialog progress;
    private ExecutorService pool = Executors.newFixedThreadPool(10);

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

            new SettingsHelper(this).setService(service);

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

    private void saveState()
    {
        Log.i(TAG, "Got account");

        Account account = new Account(eUsername.getText().toString(),
                                      ePassword.getText().toString());
        new SettingsHelper(this).setAccount(account);


        Log.i(TAG, "Fetching Service ID");
        progress = ProgressDialog.show(this, getResources().getString(R.string.fetching_service), getResources().getString(R.string.starting_worker),
                                       false, true);

        String uri = getResources().getString(R.string.inode_api_url);
        ServiceParser parser = new ServiceParser();

        HttpWorker<Service> serviceWorker =
            new HttpWorker<Service>(new Handler(this), account, uri, parser);

        pool.execute(serviceWorker);
    }

}
