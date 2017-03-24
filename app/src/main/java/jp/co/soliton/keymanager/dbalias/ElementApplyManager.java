package jp.co.soliton.keymanager.dbalias;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lexuanvinh on 2/22/2017.
 */

public class ElementApplyManager {
    private static final String TABLE_ELEMENT_APPLY = "elementApply";

    private DatabaseHandler databaseHandler;

    public ElementApplyManager(Context context) {
        this.databaseHandler = new DatabaseHandler(context);
    }

    public void saveElementApply(ElementApply elementApply) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("host_name", elementApply.getHost());
        values.put("port", elementApply.getPort());
        values.put("port_ssl", elementApply.getPortSSL());
        values.put("user_id", elementApply.getUserId());
        if (elementApply.getEmail() != null && elementApply.getEmail() != "") {
            values.put("email", elementApply.getEmail());
        }
        if (elementApply.getReason() != null && elementApply.getReason() != "") {
            values.put("reason", elementApply.getReason());
        }
        values.put("target", elementApply.getTarger());
        values.put("status", elementApply.getStatus());
        values.put("challenge", elementApply.isChallenge() ? 1 : 0);
        int id = getIdElementApply(elementApply.getHost(), elementApply.getUserId());
        if (id > 0) {
            values.put("updated_at", getDateWithFomat("yyyy/MM/dd HH:mm:ss"));
            db.update(TABLE_ELEMENT_APPLY, values, "id="+id, null);
        } else {
            db.insert(TABLE_ELEMENT_APPLY, null, values);// Inserting Row
        }
        db.close(); // Closing database connection
    }

    public int getIdElementApply(String host_name, String user_id) {
        int id = 0;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        String Query = "SELECT id FROM " + TABLE_ELEMENT_APPLY + " where host_name = ? AND user_id = ? "
                + "AND status NOT IN (?,?)";
        Cursor cursor = db.rawQuery(Query, new String[]{host_name,user_id,
                String.valueOf(ElementApply.STATUS_APPLY_APPROVED),String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }

    private String getDateWithFomat(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
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
        String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY
                + " WHERE status NOT IN (?,?)"
                + " ORDER BY id DESC";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{
                String.valueOf(ElementApply.STATUS_APPLY_APPROVED),String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                ElementApply elementApply = new ElementApply();
                elementApply.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                elementApply.setHost(cursor.getString(cursor.getColumnIndexOrThrow("host_name")));
                elementApply.setPortSSL(cursor.getString(cursor.getColumnIndexOrThrow("port_ssl")));
                elementApply.setPort(cursor.getString(cursor.getColumnIndexOrThrow("port")));
                elementApply.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
                elementApply.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                elementApply.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
                elementApply.setTarger(cursor.getString(cursor.getColumnIndexOrThrow("target")));
                elementApply.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
                elementApply.setChallenge(cursor.getInt(cursor.getColumnIndexOrThrow("challenge")) > 0 ? true : false);
                elementApply.setUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));

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
    public List<ElementApply> getAllCertificate() {
        List<ElementApply> elementApplyList = new ArrayList<ElementApply>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY
                + " WHERE status = " + ElementApply.STATUS_APPLY_APPROVED
                + " ORDER BY expiration_date ASC";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                ElementApply elementApply = new ElementApply();
                elementApply.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                elementApply.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
                elementApply.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
                elementApply.setNotiEnableBefore(cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before")));
                elementApply.setExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow("expiration_date")));

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
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ELEMENT_APPLY
                + " WHERE status NOT IN (?,?)";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{
                String.valueOf(ElementApply.STATUS_APPLY_APPROVED),String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                total = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return total;
    }

    /**
     * This method Getting single ElementApply by id in DB
     *
     * @param id
     * @return
     */
    public ElementApply getElementApply(String id) {
        String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY
                + " WHERE id = " + id;
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        ElementApply elementApply = new ElementApply();

        cursor.moveToFirst();
        elementApply.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        elementApply.setHost(cursor.getString(cursor.getColumnIndexOrThrow("host_name")));
        elementApply.setPortSSL(cursor.getString(cursor.getColumnIndexOrThrow("port_ssl")));
        elementApply.setPort(cursor.getString(cursor.getColumnIndexOrThrow("port")));
        elementApply.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        elementApply.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        elementApply.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
        elementApply.setTarger(cursor.getString(cursor.getColumnIndexOrThrow("target")));
        elementApply.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
        elementApply.setChallenge(cursor.getInt(cursor.getColumnIndexOrThrow("challenge")) > 0 ? true : false);
        elementApply.setUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        elementApply.setExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow("expiration_date")));
        return elementApply;
    }

    /**
     * This method Deleting ElementApply in DB
     *
     * @param id
     */
    public void deleteElementApply(String id) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_ELEMENT_APPLY, " id = ?",
                new String[]{id});
        db.close();
    }

    public void updateStatus(int status, String id) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update(TABLE_ELEMENT_APPLY, values, "id="+id, null);
        db.close(); // Closing database connection
    }

    public void updateElementCertificate(ElementApply element) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("expiration_date", element.getExpirationDate());
        values.put("cn_value", element.getcNValue());
        values.put("sn_value", element.getsNValue());
        values.put("status", ElementApply.STATUS_APPLY_APPROVED);
        db.update(TABLE_ELEMENT_APPLY, values, "id="+element.getId(), null);
        db.close(); // Closing database connection
    }

    public boolean hasReApplyCertificate() {
        List<ElementApply> elementApplyList = new ArrayList<ElementApply>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY
                + " WHERE status = " + ElementApply.STATUS_APPLY_APPROVED
                + " ORDER BY id DESC";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        if (cursor.moveToFirst()) {
            do {
                try {
                    Date expirationDate = formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow("expiration_date")));
                    Date current_date = new Date();
                    //Comparing dates
                    long difference = expirationDate.getTime() - current_date.getTime();
                    long differenceDates = difference / (24 * 60 * 60 * 1000);
                    if (differenceDates < cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before")) ) {
                        return true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return false;
    }
}
