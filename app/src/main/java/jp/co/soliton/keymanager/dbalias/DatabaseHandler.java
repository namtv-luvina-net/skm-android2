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
    public static final String TABLE_ELEMENT_APPLY = "elementApply";
	// Column
	public static final String ID_COLUMN = "id";
	public static final String HOST_NAME_COLUMN = "host_name";
	public static final String PORT_SSL_COLUMN = "port_ssl";
	public static final String PORT_COLUMN = "port";
	public static final String USER_ID_COLUMN = "user_id";
	public static final String EMAIL_COLUMN = "email";
	public static final String REASON_COLUMN = "reason";
	public static final String TARGET_COLUMN = "target";
	public static final String STATUS_COLUMN = "status";
	public static final String EPSAP_VERSION_COLUMN = "epsap_version";
	public static final String CHALLENGE_COLUMN = "challenge";
	public static final String UPDATE_AT_COLUMN = "updated_at";
	public static final String CREATE_AT_COLUMN = "created_at";
	public static final String NOTI_ENABLE_FLAG_COLUMN = "noti_enable_flag";
	public static final String NOTI_ENABLE_BEFORE_FLAG_COLUMN = "noti_enable_before_flag";
	public static final String NOTI_ENABLE_BEFORE_COLUMN = "noti_enable_before";
	public static final String EXPIRATION_DATE_COLUMN = "expiration_date";
	public static final String CN_VALUE_COLUMN = "cn_value";
	public static final String SN_VALUE_COLUMN = "sn_value";
	public static final String SUBJECT_COUNTRY_NAME_COLUMN = "subject_country_name";
	public static final String SUBJECT_STATE_OR_PROVINCE_NAME_COLUMN = "subject_state_or_province_name";
	public static final String SUBJECT_LOCALITY_NAME_COLUMN = "subject_locality_name";
	public static final String SUBJECT_ORGANIZATION_NAME_COLUMN = "subject_organization_name";
	public static final String SUBJECT_COMMON_NAME_COLUMN = "subject_common_name";
	public static final String SUBJECT_EMAIL_ADDRESS_COLUMN = "subject_email_address";
	public static final String ISSUER_COUNTRY_NAME_COLUMN = "issuer_country_name";
	public static final String ISSUER_STATE_OR_PROVINCE_NAME_COLUMN = "issuer_state_or_province_name";
	public static final String ISSUER_LOCALITY_NAME_COLUMN = "issuer_locality_name";
	public static final String ISSUER_ORGANIZATION_NAME_COLUMN = "issuer_organization_name";
	public static final String ISSUER_ORGANIZATION_UNIT_NAME_COLUMN = "issuer_organization_unit_name";
	public static final String ISSUER_COMMON_NAME_COLUMN = "issuer_common_name";
	public static final String ISSUER_EMAIL_ADDRESS_COLUMN = "issuer_email_address";
	public static final String VERSION_COLUMN = "version";
	public static final String SERIAL_NUMBER_COLUMN = "serial_number";
	public static final String SIGNATURE_ALOGRITHM_COLUMN = "signature_alogrithm";
	public static final String NOT_VALID_BEFORE_COLUMN = "not_valid_before";
	public static final String NOT_VALID_AFTER_COLUMN = "not_valid_after";
	public static final String PUBLIC_KEY_ALOGRITHM_COLUMN = "public_key_alogrithm";
	public static final String PUBLIC_KEY_DATA_COLUMN = "public_key_data";
	public static final String PUBLIC_SIGNATURE_COLUMN = "public_signature";
	public static final String CERTIFICATE_AUTHORITY_COLUMN = "certificate_authority";
	public static final String USAGE_COLUMN = "usage";
	public static final String SUBJECT_KEY_IDENTIFIER_COLUMN = "subject_key_identifier";
	public static final String AUTHORITY_KEY_IDENTIFIER_COLUMN = "authority_key_identifier";
	public static final String CLR_DISTRIBUTION_POINT_URI_COLUMN = "clr_distribution_point_uri";
	public static final String CERTIFICATE_AUTHORITY_URI_COLUMN = "certificate_authority_uri";
	public static final String PURPOSE_COLUMN = "purpose";

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
                + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HOST_NAME_COLUMN + " TEXT, "
                + PORT_SSL_COLUMN + " INTEGER, "
                + PORT_COLUMN + " INTEGER, "
                + USER_ID_COLUMN + " TEXT, "
                + EMAIL_COLUMN + " TEXT, "
                + REASON_COLUMN + " TEXT, "
                + TARGET_COLUMN + " TEXT, "
                + STATUS_COLUMN + " INTEGER, "
                + EPSAP_VERSION_COLUMN + " TEXT, "
                + CHALLENGE_COLUMN + " INTEGER,"
                + UPDATE_AT_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + CREATE_AT_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + NOTI_ENABLE_FLAG_COLUMN + " INTEGER DEFAULT 1,"
                + NOTI_ENABLE_BEFORE_FLAG_COLUMN + " INTEGER DEFAULT 1,"
                + NOTI_ENABLE_BEFORE_COLUMN + " INTEGER DEFAULT 7,"
                + EXPIRATION_DATE_COLUMN + " DATETIME,"
                + CN_VALUE_COLUMN + " TEXT,"
                + SN_VALUE_COLUMN + " TEXT,"
                + SUBJECT_COUNTRY_NAME_COLUMN + " TEXT,"
                + SUBJECT_STATE_OR_PROVINCE_NAME_COLUMN + " TEXT,"
                + SUBJECT_LOCALITY_NAME_COLUMN + " TEXT,"
                + SUBJECT_ORGANIZATION_NAME_COLUMN + " TEXT,"
                + SUBJECT_COMMON_NAME_COLUMN + " TEXT,"
                + SUBJECT_EMAIL_ADDRESS_COLUMN + " TEXT,"
                + ISSUER_COUNTRY_NAME_COLUMN +  " TEXT,"
                + ISSUER_STATE_OR_PROVINCE_NAME_COLUMN + " TEXT,"
                + ISSUER_LOCALITY_NAME_COLUMN + " TEXT,"
                + ISSUER_ORGANIZATION_NAME_COLUMN + " TEXT,"
                + ISSUER_ORGANIZATION_UNIT_NAME_COLUMN + " TEXT,"
                + ISSUER_COMMON_NAME_COLUMN + " TEXT,"
                + ISSUER_EMAIL_ADDRESS_COLUMN + " TEXT,"
                + VERSION_COLUMN + " TEXT,"
                + SERIAL_NUMBER_COLUMN + " TEXT,"
                + SIGNATURE_ALOGRITHM_COLUMN + " TEXT,"
                + NOT_VALID_BEFORE_COLUMN + " CURRENT_TIMESTAMP,"
                + NOT_VALID_AFTER_COLUMN + " CURRENT_TIMESTAMP,"
                + PUBLIC_KEY_ALOGRITHM_COLUMN + " TEXT,"
                + PUBLIC_KEY_DATA_COLUMN + " TEXT,"
                + PUBLIC_SIGNATURE_COLUMN + " TEXT,"
                + CERTIFICATE_AUTHORITY_COLUMN + " TEXT,"
                + USAGE_COLUMN + " TEXT,"
                + SUBJECT_KEY_IDENTIFIER_COLUMN + " TEXT,"
                + AUTHORITY_KEY_IDENTIFIER_COLUMN + " TEXT,"
                + CLR_DISTRIBUTION_POINT_URI_COLUMN + " TEXT,"
                + CERTIFICATE_AUTHORITY_URI_COLUMN + " TEXT,"
                + PURPOSE_COLUMN + " TEXT"
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
		    String addVeresionEpsapServer = "ALTER TABLE " + TABLE_ELEMENT_APPLY + " ADD COLUMN " + EPSAP_VERSION_COLUMN + " TEXT";
		    db.execSQL(addVeresionEpsapServer);
	    }
    }
}