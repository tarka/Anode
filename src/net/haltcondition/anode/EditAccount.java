package net.haltcondition.anode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
    private Account account;

    private DbHelper dbh;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Log.i(TAG, "Starting account edit");


        eUsername = (EditText) findViewById(R.id.username);
        ePassword = (EditText) findViewById(R.id.password);


        dbh = new DbHelper(this);
        dbh.open();

        Account acc = dbh.getAccount();
        if (acc != null) {
            eUsername.setText(acc.getUsername());
            ePassword.setText(acc.getPassword());
        }


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
        dbh.close();
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
            String err = "Failed to fetch service ID: "+msg.obj;
            Log.e(TAG, err);

            progress.dismiss();
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();

        } else if (msg.what == HttpWorker.MsgCode.RESULT.ordinal()) {
            progress.dismiss();
            dbh.setAccount(account);
            dbh.setServiceId(account, (Service)msg.obj);

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

        account = new Account();
        account.setUsername(eUsername.getText().toString());
        account.setPassword(ePassword.getText().toString());

        Log.i(TAG, "Fetching Service ID");
        progress = ProgressDialog.show(this, "Fetching Service Info", "Starting HTTP Worker ...",
                                       false, true);

        String uri = getResources().getString(R.string.inode_api_url);
        ServiceParser parser = new ServiceParser();

        HttpWorker<Service> serviceWorker =
            new HttpWorker<Service>(new Handler(this), account, uri, parser);

        pool.execute(serviceWorker);
    }

}
