package jp.co.soliton.keymanager.dbalias;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 5;
    // All Static variables
    // Database Name
    public static final String DATABASE_NAME = "applyManager.db";
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
                + "email TEXT, "
                + "reason TEXT, "
                + "target TEXT, "
                + "status INTEGER, "
                + "epsap_version TEXT, "
                + "challenge INTEGER,"
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "noti_enable_flag INTEGER DEFAULT 1,"
                + "noti_enable_before_flag INTEGER DEFAULT 1,"
                + "noti_enable_before INTEGER DEFAULT 7,"
                + "expiration_date DATETIME,"
                + "cn_value TEXT,"
                + "sn_value TEXT,"
                + "subject_country_name TEXT,"
                + "subject_state_or_province_name TEXT,"
                + "subject_locality_name TEXT,"
                + "subject_organization_name TEXT,"
                + "subject_common_name TEXT,"
                + "subject_email_address TEXT,"
                + "issuer_country_name TEXT,"
                + "issuer_state_or_province_name TEXT,"
                + "issuer_locality_name TEXT,"
                + "issuer_organization_name TEXT,"
                + "issuer_organization_unit_name TEXT,"
                + "issuer_common_name TEXT,"
                + "issuer_email_address TEXT,"
                + "version TEXT,"
                + "serial_number TEXT,"
                + "signature_alogrithm TEXT,"
                + "not_valid_before CURRENT_TIMESTAMP,"
                + "not_valid_after CURRENT_TIMESTAMP,"
                + "public_key_alogrithm TEXT,"
                + "public_key_data TEXT,"
                + "public_signature TEXT,"
                + "certificate_authority TEXT,"
                + "usage TEXT,"
                + "subject_key_identifier TEXT,"
                + "authority_key_identifier TEXT,"
                + "clr_distribution_point_uri TEXT,"
                + "certificate_authority_uri TEXT,"
                + "purpose TEXT"
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
	    if (oldVersion < 4) {
		    // Drop older table if existed
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELEMENT_APPLY);
		    // Create tables again
		    onCreate(db);
	    } else if (oldVersion == 4 && newVersion == 5) {
		    String addVeresionEpsapServer = "ALTER TABLE " + TABLE_ELEMENT_APPLY + " ADD COLUMN epsap_version TEXT";
		    db.execSQL(addVeresionEpsapServer);
	    }
    }
}