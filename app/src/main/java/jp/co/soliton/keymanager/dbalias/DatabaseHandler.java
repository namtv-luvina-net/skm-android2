package jp.co.soliton.keymanager.dbalias;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;
    // All Static variables
    // Database Name
    private static final String DATABASE_NAME = "aliasManager.db";
    // Alias table name
    private static final String TABLE_ALIAS = "alias";
    private static final String TABLE_ELEMENT_APPLY = "elementApply";
    // Alias Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NAME_SUBJECTDN = "name_subjectdn";
    private static final String KEY_DATE_FINAL = "date_final";
    private static final String KEY_BEFORE_DATE_FINAL = "before_date_final";
    private static final String KEY_SATATUS_NOTIFICATION_DATE_FINAL = "satatus_notification_date_final";
    private static final String KEY_SATATUS_NOTIFICATION_BEFORE_DATE_FINAL = "satatus_notification_before_date_final";

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
        String CREATE_ALIAS_TABLE = "CREATE TABLE " + TABLE_ALIAS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_NAME_SUBJECTDN + " TEXT,"
                + KEY_DATE_FINAL + " LONG,"
                + KEY_BEFORE_DATE_FINAL + " LONG,"
                + KEY_SATATUS_NOTIFICATION_DATE_FINAL + " INTEGER,"
                + KEY_SATATUS_NOTIFICATION_BEFORE_DATE_FINAL + " INTEGER"
                + ")";
        db.execSQL(CREATE_ALIAS_TABLE);
        //create table element Apply
        String CREATE_ELEMENT_TABLE = "CREATE TABLE " + TABLE_ELEMENT_APPLY + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "host_name TEXT, "
                + "user_id TEXT, "
                + "password TEXT, "
                + "email TEXT, "
                + "reason TEXT, "
                + "target TEXT, "
                + "status INTEGER, "
                + "challenge INTEGER,"
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENT_APPLY);
        // Create tables again
        onCreate(db);

    }

    /**
     * This method Adding ItemAlias into DB
     *
     * @param itemAlias
     */
    public void addAlias(ItemAlias itemAlias) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, itemAlias.getAlias()); // alias Name
        values.put(KEY_NAME_SUBJECTDN, itemAlias.getNameSubjectDN());// alias name subjectdn
        values.put(KEY_DATE_FINAL, itemAlias.getFinalDate()); // alias final date
        values.put(KEY_BEFORE_DATE_FINAL, itemAlias.getBeforeFinalDate()); // alias before final date
        values.put(KEY_SATATUS_NOTIFICATION_DATE_FINAL, itemAlias.getStatusNotificationFinalDate());
        values.put(KEY_SATATUS_NOTIFICATION_BEFORE_DATE_FINAL, itemAlias.getStatusNotificationBeforeFinalDate());
        db.insert(TABLE_ALIAS, null, values);// Inserting Row
        db.close(); // Closing database connection
    }

    /**
     * This method Getting single ItemAlias by id in DB
     *
     * @param id
     * @return
     */
    public ItemAlias getAlias(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALIAS, new String[]{KEY_ID,
                        KEY_NAME, KEY_NAME_SUBJECTDN, KEY_DATE_FINAL, KEY_BEFORE_DATE_FINAL, KEY_SATATUS_NOTIFICATION_DATE_FINAL, KEY_SATATUS_NOTIFICATION_BEFORE_DATE_FINAL}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        ItemAlias itemAlias = new ItemAlias(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4), cursor.getInt(5), cursor.getInt(6));
        return itemAlias;
    }

    /**
     * This method Getting single ItemAlias by dateFinal in DB
     *
     * @param dateFinal
     * @return
     */
    public ItemAlias getAlias(Long dateFinal) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALIAS, new String[]{KEY_ID,
                        KEY_NAME, KEY_NAME_SUBJECTDN, KEY_DATE_FINAL, KEY_BEFORE_DATE_FINAL, KEY_SATATUS_NOTIFICATION_DATE_FINAL, KEY_SATATUS_NOTIFICATION_BEFORE_DATE_FINAL}, KEY_DATE_FINAL + "=?",
                new String[]{String.valueOf(dateFinal)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        ItemAlias itemAlias = new ItemAlias(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4), cursor.getInt(5), cursor.getInt(6));
        return itemAlias;
    }

    /**
     * This method Getting All ItemAlias in DB
     *
     * @return
     */
    public List<ItemAlias> getAllAlias() {
        List<ItemAlias> itemAliasList = new ArrayList<ItemAlias>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALIAS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemAlias itemAlias = new ItemAlias(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4), cursor.getInt(5), cursor.getInt(6));
                itemAliasList.add(itemAlias);
            } while (cursor.moveToNext());
        }
        return itemAliasList;
    }

    /**
     * This method Updating ItemAlias in DB
     *
     * @param alias
     * @return
     */
    public int updateAlias(ItemAlias alias) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, alias.getiD());
        // alias Name
        values.put(KEY_NAME, alias.getAlias());
        // alias name subjectdn
        values.put(KEY_NAME_SUBJECTDN, alias.getNameSubjectDN());
        // alias final date
        values.put(KEY_DATE_FINAL, alias.getFinalDate());
        // alias before final date
        values.put(KEY_BEFORE_DATE_FINAL, alias.getBeforeFinalDate());
        // alias status notification final date
        values.put(KEY_SATATUS_NOTIFICATION_DATE_FINAL, alias.getStatusNotificationFinalDate());
        // alias status notification before final date
        values.put(KEY_SATATUS_NOTIFICATION_BEFORE_DATE_FINAL, alias.getStatusNotificationBeforeFinalDate());
        return db.update(TABLE_ALIAS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(alias.getiD())});
    }

    /**
     * This method Deleting ItemAlias in DB
     *
     * @param alias
     */
    public void deleteAlias(ItemAlias alias) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALIAS, KEY_ID + " = ?",
                new String[]{String.valueOf(alias.getiD())});
        db.close();
    }

    public void saveElementApply(ElementApply elementApply) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("host_name", elementApply.getHost());
        values.put("user_id", elementApply.getUserId());
        values.put("password", elementApply.getPassword());
        values.put("email", elementApply.getEmail());
        values.put("reason", elementApply.getReason());
        values.put("target", elementApply.getTarger());
        values.put("status", elementApply.getStatus());
        values.put("challenge", elementApply.isChallenge() ? 1 : 0);
        int id = getIdElementApply(elementApply.getHost(), elementApply.getUserId());
        if (id > 0) {
            values.put("updated_at", getDateTime());
            db.update(TABLE_ELEMENT_APPLY, values, "id="+id, null);
        } else {
            db.insert(TABLE_ELEMENT_APPLY, null, values);// Inserting Row
        }
        db.close(); // Closing database connection
    }

    private int getIdElementApply(String host_name, String user_id) {
        int id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT id FROM " + TABLE_ELEMENT_APPLY + " where host_name = ? AND user_id = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{host_name,user_id});
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * This method Getting All ElementApply in DB
     *
     * @return
     */
    public List<ElementApply> getAllElementApply() {
        List<ElementApply> elementApplyList = new ArrayList<ElementApply>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                ElementApply elementApply = new ElementApply();
                elementApply.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                elementApply.setHost(cursor.getString(cursor.getColumnIndexOrThrow("host_name")));
                elementApply.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
                elementApply.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                elementApply.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                elementApply.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                elementApply.setTarger(cursor.getString(cursor.getColumnIndexOrThrow("target")));
                elementApply.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
                elementApply.setChallenge(cursor.getInt(cursor.getColumnIndexOrThrow("challenge")) > 0 ? true : false);
//                elementApply.setCreateDate(Date.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("created_at"))));

                elementApplyList.add(elementApply);
            } while (cursor.moveToNext());
        }
        return elementApplyList;
    }

    /**
     * This method Getting All ElementApply in DB
     *
     * @return
     */
    public int getCountElementApply() {
        int total = 0;
        // Select All Query
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ELEMENT_APPLY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                total = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return total;
    }
}