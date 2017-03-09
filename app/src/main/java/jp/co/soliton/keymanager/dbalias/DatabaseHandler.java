package jp.co.soliton.keymanager.dbalias;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 3;
    // All Static variables
    // Database Name
    private static final String DATABASE_NAME = "applyManager.db";
    private static final String TABLE_ELEMENT_APPLY = "elementApply";

    /**
     * @param context
     */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method create SQLiteDatabase
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table element Apply
        String CREATE_ELEMENT_TABLE = "CREATE TABLE " + TABLE_ELEMENT_APPLY + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "host_name TEXT, "
                + "port_ssl INTEGER, "
                + "port INTEGER, "
                + "user_id TEXT, "
                + "password TEXT, "
                + "email TEXT, "
                + "reason TEXT, "
                + "target TEXT, "
                + "status INTEGER, "
                + "challenge INTEGER,"
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "noti_enable_flag INTEGER DEFAULT 1,"
                + "noti_enable_before INTEGER DEFAULT 7,"
                + "expiration_date DATETIME,"
                + "cn_value TEXT,"
                + "sn_value TEXT"
                + ")";
        db.execSQL(CREATE_ELEMENT_TABLE);
    }

    /**
     * This method Upgrading database
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENT_APPLY);
        // Create tables again
        onCreate(db);
    }
}