
package net.haltcondition.anode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper {

    private static final String TAG = "DB";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "data";
    private static final String ACCOUNT_TABLE = "accounts";
    private static final String SERVICE_TABLE = "services";
    private static final String LASTUSAGE_TABLE = "usage";
    private static final String HISTORY_TABLE = "history";

    private static final String ACCOUNT_TABLE_CREATE = "create table if not exists "+ ACCOUNT_TABLE +" ("+
        "_id integer primary key,"+  // FIXME: Should be autoincrement eventually
        "username text not null,"+
        "password text not null);";

    private static final String SERVICE_TABLE_CREATE = "create table if not exists "+ SERVICE_TABLE +" ("+
        "_id integer primary key,"+  // FIXME: Should be autoincrement eventually
        "service_id text not null);";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ACCOUNT_TABLE_CREATE);
            db.execSQL(SERVICE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+ACCOUNT_TABLE+";");  // FIXME
            onCreate(db);
        }
    }

    public DbHelper(Context ctx) {
        this.mCtx = ctx;
    }

    public DbHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /* ************************************************************ */

    public void setAccount(Account acc) {
        ContentValues val = new ContentValues();
        val.put("_id", 1);  // FIXME
        val.put("username", acc.getUsername());
        val.put("password", acc.getPassword());

        // FIXME: Is this legit?
        long ret = mDb.replace(ACCOUNT_TABLE, null, val);

        // long ret = mDb.insert(ACCOUNT_TABLE, null, val);
        // if (ret == -1) {
        //     // Exists?
        //     ret = mDb.update(ACCOUNT_TABLE, val, "where _id=?", new String[] {"1"});
        // }
    }

    public Account getAccount()
    {
        Log.i(TAG, "Fetching Account");

        Cursor cur = mDb.query(ACCOUNT_TABLE, new String[] {"username", "password"},
                        "_id=1", null, null, null, null, null);
        if (cur == null || cur.getCount() == 0) {
            return null;
        }
        cur.moveToFirst();
        Account acc = new Account();
        acc.setUsername(cur.getString(0));
        acc.setPassword(cur.getString(1));
        return acc;
    }

    /* ************************************************************ */

    public void setServiceId(Account acc, Service svc)
    {
        ContentValues val = new ContentValues();
        val.put("_id", 1);  // FIXME
        val.put("service", svc.getServiceId());

        // FIXME: Is this legit?
        long ret = mDb.replace(SERVICE_TABLE, null, val);

    }

    public Service getService()
    {
        Log.i(TAG, "Fetching Service");

        Cursor cur = mDb.query(SERVICE_TABLE, new String[] {"service"},
                        "_id=1", null, null, null, null, null);
        if (cur == null || cur.getCount() == 0) {
            return null;
        }
        cur.moveToFirst();
        return new Service(cur.getString(0));
    }

}
