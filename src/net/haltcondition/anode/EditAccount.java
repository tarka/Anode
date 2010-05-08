package net.haltcondition.anode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditAccount extends Activity
{
    private static final String TAG = "EditAccount";

    private EditText eUsername;
    private EditText ePassword;

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
                    setResult(RESULT_OK);
                    finish();
                }
            });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dbh.close();
    }


    private void saveState()
    {
        Log.i(TAG, "Setting account");

        Account acc = new Account();
        acc.setUsername(eUsername.getText().toString());
        acc.setPassword(ePassword.getText().toString());
        dbh.setAccount(acc);
    }

}
