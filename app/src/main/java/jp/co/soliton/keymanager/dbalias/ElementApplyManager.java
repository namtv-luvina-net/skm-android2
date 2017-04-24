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
                elementApply.setNotiEnableFlag(1 == cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_flag")));
                elementApply.setNotiEnableBeforeFlag(1 == cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before_flag")));
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
                elementApply.setNotiEnableFlag(1 == cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_flag")));
                elementApply.setNotiEnableBeforeFlag(1 == cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before_flag")));
                elementApply.setNotiEnableBefore(cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before")));
                elementApply.setExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow("expiration_date")));
                elementApply.setcNValue(cursor.getString(cursor.getColumnIndexOrThrow("cn_value")));

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
	    elementApply.setcNValue(cursor.getString(cursor.getColumnIndexOrThrow("cn_value")));
        elementApply.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        elementApply.setReason(cursor.getString(cursor.getColumnIndexOrThrow("reason")));
        elementApply.setTarger(cursor.getString(cursor.getColumnIndexOrThrow("target")));
        elementApply.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
        elementApply.setChallenge(cursor.getInt(cursor.getColumnIndexOrThrow("challenge")) > 0 ? true : false);
        elementApply.setUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        elementApply.setExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow("expiration_date")));

        elementApply.setNotiEnableFlag(1 == cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_flag")));
        elementApply.setNotiEnableBeforeFlag(1 == cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before_flag")));
        elementApply.setNotiEnableBefore(cursor.getInt(cursor.getColumnIndexOrThrow("noti_enable_before")));

        elementApply.setSubjectCountryName(cursor.getString(cursor.getColumnIndexOrThrow("subject_country_name")));
        elementApply.setSubjectStateOrProvinceName(cursor.getString(cursor.getColumnIndexOrThrow("subject_state_or_province_name")));
        elementApply.setSubjectLocalityName(cursor.getString(cursor.getColumnIndexOrThrow("subject_locality_name")));
        elementApply.setSubjectOrganizationName(cursor.getString(cursor.getColumnIndexOrThrow("subject_organization_name")));
        elementApply.setSubjectCommonName(cursor.getString(cursor.getColumnIndexOrThrow("subject_common_name")));
        elementApply.setSubjectEmailAddress(cursor.getString(cursor.getColumnIndexOrThrow("subject_email_address")));
        elementApply.setIssuerCountryName(cursor.getString(cursor.getColumnIndexOrThrow("issuer_country_name")));
        elementApply.setIssuerStateOrProvinceName(cursor.getString(cursor.getColumnIndexOrThrow("issuer_state_or_province_name")));
        elementApply.setIssuerLocalityName(cursor.getString(cursor.getColumnIndexOrThrow("issuer_locality_name")));
        elementApply.setIssuerOrganizationName(cursor.getString(cursor.getColumnIndexOrThrow("issuer_organization_name")));
        elementApply.setIssuerOrganizationUnitName(cursor.getString(cursor.getColumnIndexOrThrow("issuer_organization_unit_name")));
        elementApply.setIssuerCommonName(cursor.getString(cursor.getColumnIndexOrThrow("issuer_common_name")));
        elementApply.setIssuerEmailAdress(cursor.getString(cursor.getColumnIndexOrThrow("issuer_email_address")));
        elementApply.setVersion(cursor.getString(cursor.getColumnIndexOrThrow("version")));
        elementApply.setSerialNumber(cursor.getString(cursor.getColumnIndexOrThrow("serial_number")));
        elementApply.setSignatureAlogrithm(cursor.getString(cursor.getColumnIndexOrThrow("signature_alogrithm")));
        elementApply.setNotValidBefore(cursor.getString(cursor.getColumnIndexOrThrow("not_valid_before")));
        elementApply.setNotValidAfter(cursor.getString(cursor.getColumnIndexOrThrow("not_valid_after")));
        elementApply.setPublicKeyAlogrithm(cursor.getString(cursor.getColumnIndexOrThrow("public_key_alogrithm")));
        elementApply.setPublicKeyData(cursor.getString(cursor.getColumnIndexOrThrow("public_key_data")));
        elementApply.setPublicSignature(cursor.getString(cursor.getColumnIndexOrThrow("public_signature")));
        elementApply.setCertificateAuthority(cursor.getString(cursor.getColumnIndexOrThrow("certificate_authority")));
        elementApply.setUsage(cursor.getString(cursor.getColumnIndexOrThrow("usage")));
        elementApply.setSubjectKeyIdentifier(cursor.getString(cursor.getColumnIndexOrThrow("subject_key_identifier")));
        elementApply.setAuthorityKeyIdentifier(cursor.getString(cursor.getColumnIndexOrThrow("authority_key_identifier")));
        elementApply.setClrDistributionPointUri(cursor.getString(cursor.getColumnIndexOrThrow("clr_distribution_point_uri")));
        elementApply.setCertificateAuthorityUri(cursor.getString(cursor.getColumnIndexOrThrow("certificate_authority_uri")));
        elementApply.setPurpose(cursor.getString(cursor.getColumnIndexOrThrow("purpose")));

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

    public void updateNotifSettingElement(boolean notifFlag, boolean notifBeforeFlag, int notifBefore, String id) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("noti_enable_flag", notifFlag ? 1 : 0);
        values.put("noti_enable_before_flag", notifBeforeFlag ? 1 : 0);
        values.put("noti_enable_before", notifBefore);
        db.update(TABLE_ELEMENT_APPLY, values, "id="+id, null);
        db.close(); // Closing database connection
    }

    public void updateNotifSetting(boolean notifFlag, boolean notifBeforeFlag, int notifBefore) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("noti_enable_flag", notifFlag ? 1 : 0);
        values.put("noti_enable_before_flag", notifBeforeFlag ? 1 : 0);
        values.put("noti_enable_before", notifBefore);
        db.update(TABLE_ELEMENT_APPLY, values, "status IN (" +
                String.valueOf(ElementApply.STATUS_APPLY_APPROVED) + "," +
                String.valueOf(ElementApply.STATUS_APPLY_CLOSED) + ")", null);
        db.close();
    }

    public void updateElementCertificate(ElementApply element) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("expiration_date", element.getExpirationDate());
        values.put("cn_value", element.getcNValue());
        values.put("sn_value", element.getsNValue());
        values.put("status", ElementApply.STATUS_APPLY_APPROVED);

        values.put("noti_enable_flag", element.isNotiEnableFlag() ? 1 : 0);
        values.put("noti_enable_before_flag", element.isNotiEnableBeforeFlag() ? 1 : 0);
        values.put("noti_enable_before", element.getNotiEnableBefore());

        values.put("subject_country_name", element.getSubjectCountryName());
        values.put("subject_state_or_province_name", element.getSubjectStateOrProvinceName());
        values.put("subject_locality_name", element.getSubjectLocalityName());
        values.put("subject_organization_name", element.getSubjectOrganizationName());
        values.put("subject_common_name", element.getSubjectCommonName());
        values.put("subject_email_address", element.getSubjectEmailAddress());
        values.put("issuer_country_name", element.getIssuerCountryName());
        values.put("issuer_state_or_province_name", element.getIssuerStateOrProvinceName());
        values.put("issuer_locality_name", element.getIssuerLocalityName());
        values.put("issuer_organization_name", element.getIssuerOrganizationName());
        values.put("issuer_organization_unit_name", element.getIssuerOrganizationUnitName());
        values.put("issuer_common_name", element.getIssuerCommonName());
        values.put("issuer_email_address", element.getIssuerEmailAdress());
        values.put("version", element.getVersion());
        values.put("serial_number", element.getSerialNumber());
        values.put("signature_alogrithm", element.getSignatureAlogrithm());
        values.put("not_valid_before", element.getNotValidBefore());
        values.put("not_valid_after", element.getNotValidAfter());
        values.put("public_key_alogrithm", element.getPublicKeyAlogrithm());
        values.put("public_key_data", element.getPublicKeyData());
        values.put("public_signature", element.getPublicSignature());
        values.put("certificate_authority", element.getCertificateAuthority());
        values.put("usage", element.getUsage());
        values.put("subject_key_identifier", element.getSubjectKeyIdentifier());
        values.put("authority_key_identifier", element.getAuthorityKeyIdentifier());
        values.put("clr_distribution_point_uri", element.getClrDistributionPointUri());
        values.put("certificate_authority_uri", element.getCertificateAuthorityUri());
        values.put("purpose", element.getPurpose());

        db.update(TABLE_ELEMENT_APPLY, values, "id="+element.getId(), null);
        db.close(); // Closing database connection
    }

    public boolean hasCertificate() {
        int total = 0;
        // Select All Query
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ELEMENT_APPLY
                + " WHERE status = ?";
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{
                String.valueOf(ElementApply.STATUS_APPLY_APPROVED)});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                total = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        if (total > 0) {
            return true;
        }
        return false;
    }
}
