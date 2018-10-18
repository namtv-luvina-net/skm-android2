package jp.co.soliton.keymanager.dbalias;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.EpsapVersion;

import java.util.ArrayList;
import java.util.List;

import static jp.co.soliton.keymanager.dbalias.DatabaseHandler.*;

/**
 * Created by lexuanvinh on 2/22/2017.
 */

public class ElementApplyManager {

	private DatabaseHandler databaseHandler;

	public static ElementApplyManager instance;
	private SQLiteDatabase sqLiteDB;

	public static synchronized ElementApplyManager getInstance(Context context) {
		if (null == instance) {
			instance = new ElementApplyManager(context.getApplicationContext());
		}
		return instance;
	}

	private ElementApplyManager(Context context) {
		databaseHandler = new DatabaseHandler(context);
	}

	public void openDatabase() {
		try {
			sqLiteDB = databaseHandler.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
			LogCtrl.getInstance().error("Error with DB Open:" + e.toString());
			new Exception("Error with DB Open");
		}
	}

	public void closeDatabase() {
		if (sqLiteDB != null && sqLiteDB.isOpen()) {
			sqLiteDB.close();
		}
	}

	public void saveElementApply(ElementApply elementApply) {
		ContentValues values = new ContentValues();
		values.put(HOST_NAME_COLUMN, CommonUtils.removeHttp(elementApply.getHost()));
		values.put(PORT_COLUMN, elementApply.getPort());
		values.put(PORT_SSL_COLUMN, elementApply.getPortSSL());
		values.put(USER_ID_COLUMN, elementApply.getUserId());
		if (elementApply.getEmail() != null && elementApply.getEmail() != "") {
			values.put(EMAIL_COLUMN, elementApply.getEmail());
		}
		if (elementApply.getReason() != null && elementApply.getReason() != "") {
			values.put(REASON_COLUMN, elementApply.getReason());
		}
		values.put(TARGET_COLUMN, elementApply.getTarget());
		values.put(STATUS_COLUMN, elementApply.getStatus());
		values.put(EPSAP_VERSION_COLUMN, elementApply.getVersionEpsAp());
		values.put(CHALLENGE_COLUMN, elementApply.isChallenge() ? 1 : 0);
		values.put(UPDATE_AT_COLUMN, DateUtils.getCurrentDateSystem());
		int id;
		if (EpsapVersion.checkVersionValidUseApid(elementApply.getVersionEpsAp())) {
			id = getIdElementApply(elementApply.getHost(), elementApply.getUserId(), elementApply.getTarget());
		} else {
			id = getIdElementApply(elementApply.getHost(), elementApply.getUserId());
		}
		try {
			openDatabase();
			if (id > 0) {
				sqLiteDB.update(TABLE_ELEMENT_APPLY, values, "id=" + id, null);
			} else {
				sqLiteDB.insert(TABLE_ELEMENT_APPLY, null, values);// Inserting Row
			}
		} finally {
			closeDatabase();
		}
	}

	public int getIdElementApply(String host_name, String user_id) {
		int id = 0;
		try {
			openDatabase();
			String Query = "SELECT " + ID_COLUMN + " FROM " + TABLE_ELEMENT_APPLY + " where " + HOST_NAME_COLUMN + " = ? " +
					"AND " +

					USER_ID_COLUMN + " = ? AND " + STATUS_COLUMN + " NOT IN (?,?)";
			Cursor cursor = sqLiteDB.rawQuery(Query, new String[]{host_name, user_id,
					String.valueOf(ElementApply.STATUS_APPLY_APPROVED), String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
			if (cursor.moveToFirst()) {
				do {
					id = cursor.getInt(0);
				} while (cursor.moveToNext());
			}
			cursor.close();
		} finally {
			closeDatabase();
		}
		return id;
	}

	public int getIdElementApply(String host_name, String user_id, String target) {
		int id = 0;
		Cursor cursor = null;
		try {
			String Query = "SELECT " + ID_COLUMN + " FROM " + TABLE_ELEMENT_APPLY + " where " + HOST_NAME_COLUMN + " = ? " +
					"AND " + USER_ID_COLUMN + " = ? AND " + TARGET_COLUMN + " = ? AND " + TARGET_COLUMN + " NOT IN (?,?)";
			openDatabase();
			cursor = sqLiteDB.rawQuery(Query, new String[]{host_name, user_id, target,
					String.valueOf(ElementApply.STATUS_APPLY_APPROVED), String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
			if (cursor.moveToFirst()) {
				do {
					id = cursor.getInt(0);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			closeDatabase();
		}
		return id;
	}

	/**
	 * This method Getting All ElementApply in DB
	 *
	 * @return
	 */
	public List<ElementApply> getAllElementApply() {
		Cursor cursor = null;
		List<ElementApply> elementApplyList = new ArrayList<>();
		try {
			openDatabase();
			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY
					+ " WHERE " + STATUS_COLUMN + " NOT IN (?,?)"
					+ " ORDER BY " + ID_COLUMN + " DESC";
			cursor = sqLiteDB.rawQuery(selectQuery, new String[]{
					String.valueOf(ElementApply.STATUS_APPLY_APPROVED), String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
			// looping through all rows and adding to list

			if (cursor.moveToFirst()) {
				do {
					ElementApply elementApply = new ElementApply();
					elementApply.setId(getColumnIntWithCursor(cursor, ID_COLUMN));
					elementApply.setHost(CommonUtils.removeHttp(getColumnStringWithCursor(cursor, HOST_NAME_COLUMN)));
					elementApply.setPortSSL(getColumnStringWithCursor(cursor, PORT_SSL_COLUMN));
					elementApply.setPort(getColumnStringWithCursor(cursor, PORT_COLUMN));
					elementApply.setUserId(getColumnStringWithCursor(cursor, USER_ID_COLUMN));
					elementApply.setEmail(getColumnStringWithCursor(cursor, EMAIL_COLUMN));
					elementApply.setReason(getColumnStringWithCursor(cursor, REASON_COLUMN));
					elementApply.setVersionEpsAp(getColumnStringWithCursor(cursor, EPSAP_VERSION_COLUMN));
					elementApply.setTarger(getColumnStringWithCursor(cursor, TARGET_COLUMN));
					elementApply.setStatus(getColumnIntWithCursor(cursor, STATUS_COLUMN));
					elementApply.setChallenge(getColumnIntWithCursor(cursor, CHALLENGE_COLUMN) > 0);
					elementApply.setUpdateDate(getColumnStringWithCursor(cursor, UPDATE_AT_COLUMN));
					elementApply.setNotiEnableFlag(1 == getColumnIntWithCursor(cursor, NOTI_ENABLE_FLAG_COLUMN));
					elementApply.setNotiEnableBeforeFlag(1 == getColumnIntWithCursor(cursor,
							NOTI_ENABLE_BEFORE_FLAG_COLUMN));

					elementApply.setNotiEnableBefore(getColumnIntWithCursor(cursor, NOTI_ENABLE_BEFORE_COLUMN));
					elementApply.setExpirationDate(getColumnStringWithCursor(cursor, EXPIRATION_DATE_COLUMN));
					elementApplyList.add(elementApply);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			closeDatabase();
		}
		return elementApplyList;
	}

	/**
	 * This method Getting All ElementApply in DB
	 *
	 * @return
	 */
	public List<ElementApply> getAllCertificate() {
		Cursor cursor = null;
		List<ElementApply> elementApplyList = new ArrayList<ElementApply>();
		try {
			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY
					+ " WHERE status = " + ElementApply.STATUS_APPLY_APPROVED
					+ " ORDER BY " + EXPIRATION_DATE_COLUMN + " ASC";
			openDatabase();
			cursor = sqLiteDB.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					ElementApply elementApply = new ElementApply();
					elementApply.setId(getColumnIntWithCursor(cursor, ID_COLUMN));
					elementApply.setUserId(getColumnStringWithCursor(cursor, USER_ID_COLUMN));
					elementApply.setStatus(getColumnIntWithCursor(cursor, STATUS_COLUMN));
					elementApply.setVersionEpsAp(getColumnStringWithCursor(cursor, EPSAP_VERSION_COLUMN));
					elementApply.setNotiEnableFlag(1 == getColumnIntWithCursor(cursor, NOTI_ENABLE_FLAG_COLUMN));
					elementApply.setNotiEnableBeforeFlag(1 == getColumnIntWithCursor(cursor,
							NOTI_ENABLE_BEFORE_FLAG_COLUMN));

					elementApply.setNotiEnableBefore(getColumnIntWithCursor(cursor, NOTI_ENABLE_BEFORE_COLUMN));
					elementApply.setExpirationDate(getColumnStringWithCursor(cursor, EXPIRATION_DATE_COLUMN));
					elementApply.setcNValue(getColumnStringWithCursor(cursor, CN_VALUE_COLUMN));
					elementApply.setSerialNumber(getColumnStringWithCursor(cursor, SERIAL_NUMBER_COLUMN));
					elementApply.setTarger(getColumnStringWithCursor(cursor, TARGET_COLUMN));

					elementApplyList.add(elementApply);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			closeDatabase();
		}
		return elementApplyList;
	}

	private int getColumnIntWithCursor(Cursor cursor, String column) {
		return cursor.getInt(cursor.getColumnIndexOrThrow(column));
	}

	private String getColumnStringWithCursor(Cursor cursor, String column) {
		return cursor.getString(cursor.getColumnIndexOrThrow(column));
	}

	/**
	 * This method Getting All ElementApply in DB
	 *
	 * @return
	 */
	public int getCountElementApply() {
		Cursor cursor = null;
		int total = 0;
		try {
			// Select All Query
			String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ELEMENT_APPLY
					+ " WHERE " + STATUS_COLUMN + " NOT IN (?,?)";
			openDatabase();
			cursor = sqLiteDB.rawQuery(selectQuery, new String[]{
					String.valueOf(ElementApply.STATUS_APPLY_APPROVED), String.valueOf(ElementApply.STATUS_APPLY_CLOSED)});
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					total = cursor.getInt(0);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			closeDatabase();
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
		Cursor cursor = null;
		ElementApply elementApply = new ElementApply();
		try {
			String selectQuery = "SELECT  * FROM " + TABLE_ELEMENT_APPLY + " WHERE " + ID_COLUMN + " = " + id;
			openDatabase();
			cursor = sqLiteDB.rawQuery(selectQuery, null);

			cursor.moveToFirst();
			elementApply.setId(getColumnIntWithCursor(cursor, ID_COLUMN));
			elementApply.setHost(CommonUtils.removeHttp(getColumnStringWithCursor(cursor, HOST_NAME_COLUMN)));
			elementApply.setPortSSL(getColumnStringWithCursor(cursor, PORT_SSL_COLUMN));
			elementApply.setPort(getColumnStringWithCursor(cursor, PORT_COLUMN));
			elementApply.setUserId(getColumnStringWithCursor(cursor, USER_ID_COLUMN));
			elementApply.setcNValue(getColumnStringWithCursor(cursor, CN_VALUE_COLUMN));
			elementApply.setEmail(getColumnStringWithCursor(cursor, EMAIL_COLUMN));
			elementApply.setReason(getColumnStringWithCursor(cursor, REASON_COLUMN));
			elementApply.setTarger(getColumnStringWithCursor(cursor, TARGET_COLUMN));
			elementApply.setStatus(getColumnIntWithCursor(cursor, STATUS_COLUMN));
			elementApply.setVersionEpsAp(getColumnStringWithCursor(cursor, EPSAP_VERSION_COLUMN));
			elementApply.setChallenge(getColumnIntWithCursor(cursor, CHALLENGE_COLUMN) > 0);
			elementApply.setUpdateDate(getColumnStringWithCursor(cursor, UPDATE_AT_COLUMN));
			elementApply.setExpirationDate(getColumnStringWithCursor(cursor, EXPIRATION_DATE_COLUMN));

			elementApply.setNotiEnableFlag(1 == getColumnIntWithCursor(cursor, NOTI_ENABLE_FLAG_COLUMN));
			elementApply.setNotiEnableBeforeFlag(1 == getColumnIntWithCursor(cursor, NOTI_ENABLE_BEFORE_FLAG_COLUMN));
			elementApply.setNotiEnableBefore(getColumnIntWithCursor(cursor, NOTI_ENABLE_BEFORE_COLUMN));

			elementApply.setSubjectCountryName(getColumnStringWithCursor(cursor, SUBJECT_COUNTRY_NAME_COLUMN));
			elementApply.setSubjectStateOrProvinceName(getColumnStringWithCursor(cursor,
					SUBJECT_STATE_OR_PROVINCE_NAME_COLUMN));
			elementApply.setSubjectLocalityName(getColumnStringWithCursor(cursor, SUBJECT_LOCALITY_NAME_COLUMN));
			elementApply.setSubjectOrganizationName(getColumnStringWithCursor(cursor, SUBJECT_ORGANIZATION_NAME_COLUMN));
			elementApply.setSubjectCommonName(getColumnStringWithCursor(cursor, SUBJECT_COMMON_NAME_COLUMN));
			elementApply.setSubjectEmailAddress(getColumnStringWithCursor(cursor, SUBJECT_EMAIL_ADDRESS_COLUMN));
			elementApply.setIssuerCountryName(getColumnStringWithCursor(cursor, ISSUER_COUNTRY_NAME_COLUMN));
			elementApply.setIssuerStateOrProvinceName(getColumnStringWithCursor(cursor,
					ISSUER_STATE_OR_PROVINCE_NAME_COLUMN));

			elementApply.setIssuerLocalityName(getColumnStringWithCursor(cursor, ISSUER_LOCALITY_NAME_COLUMN));
			elementApply.setIssuerOrganizationName(getColumnStringWithCursor(cursor, ISSUER_ORGANIZATION_NAME_COLUMN));
			elementApply.setIssuerOrganizationUnitName(getColumnStringWithCursor(cursor,
					ISSUER_ORGANIZATION_UNIT_NAME_COLUMN));
			elementApply.setIssuerCommonName(getColumnStringWithCursor(cursor, ISSUER_COMMON_NAME_COLUMN));
			elementApply.setIssuerEmailAdress(getColumnStringWithCursor(cursor, ISSUER_EMAIL_ADDRESS_COLUMN));
			elementApply.setVersion(getColumnStringWithCursor(cursor, VERSION_COLUMN));
			elementApply.setSerialNumber(getColumnStringWithCursor(cursor, SERIAL_NUMBER_COLUMN));
			elementApply.setSignatureAlogrithm(getColumnStringWithCursor(cursor, SIGNATURE_ALOGRITHM_COLUMN));
			elementApply.setNotValidBefore(getColumnStringWithCursor(cursor, NOT_VALID_BEFORE_COLUMN));
			elementApply.setNotValidAfter(getColumnStringWithCursor(cursor, NOT_VALID_AFTER_COLUMN));
			elementApply.setPublicKeyAlogrithm(getColumnStringWithCursor(cursor, PUBLIC_KEY_ALOGRITHM_COLUMN));
			elementApply.setPublicKeyData(getColumnStringWithCursor(cursor, PUBLIC_KEY_DATA_COLUMN));
			elementApply.setPublicSignature(getColumnStringWithCursor(cursor, PUBLIC_SIGNATURE_COLUMN));
			elementApply.setCertificateAuthority(getColumnStringWithCursor(cursor, CERTIFICATE_AUTHORITY_COLUMN));
			elementApply.setUsage(getColumnStringWithCursor(cursor, USAGE_COLUMN));
			elementApply.setSubjectKeyIdentifier(getColumnStringWithCursor(cursor, SUBJECT_KEY_IDENTIFIER_COLUMN));
			elementApply.setAuthorityKeyIdentifier(getColumnStringWithCursor(cursor, AUTHORITY_KEY_IDENTIFIER_COLUMN));
			elementApply.setClrDistributionPointUri(getColumnStringWithCursor(cursor, CLR_DISTRIBUTION_POINT_URI_COLUMN));
			elementApply.setCertificateAuthorityUri(getColumnStringWithCursor(cursor, CERTIFICATE_AUTHORITY_URI_COLUMN));
			elementApply.setPurpose(getColumnStringWithCursor(cursor, PURPOSE_COLUMN));
			elementApply.setRfc822Name(getColumnStringWithCursor(cursor, RFC822_NAME));
		} finally {
			cursor.close();
			closeDatabase();
		}
		return elementApply;
	}

	/**
	 * This method Deleting ElementApply in DB
	 *
	 * @param id
	 */
	public void deleteElementApply(String id) {
		try {
			openDatabase();
			sqLiteDB.delete(TABLE_ELEMENT_APPLY, " id = ?", new String[]{id});
		} finally {
			closeDatabase();
		}
	}

	public void updateStatus(int status, String id) {
		ContentValues values = new ContentValues();
		values.put(STATUS_COLUMN, status);
		try {
			openDatabase();
			sqLiteDB.update(TABLE_ELEMENT_APPLY, values, ID_COLUMN + "=" + id, null);
		} finally {
			closeDatabase();
		}
	}

	public void updateNotifSettingElement(boolean notifFlag, boolean notifBeforeFlag, int notifBefore, String id) {
		ContentValues values = new ContentValues();
		values.put(NOTI_ENABLE_FLAG_COLUMN, notifFlag ? 1 : 0);
		values.put(NOTI_ENABLE_BEFORE_FLAG_COLUMN, notifBeforeFlag ? 1 : 0);
		values.put(NOTI_ENABLE_BEFORE_COLUMN, notifBefore);
		try {
			openDatabase();
			sqLiteDB.update(TABLE_ELEMENT_APPLY, values, ID_COLUMN + "=" + id, null);
		} finally {
			closeDatabase();
		}
	}

	public void updateElementCertificate(ElementApply element) {
		ContentValues values = new ContentValues();
		values.put(EXPIRATION_DATE_COLUMN, element.getExpirationDate());
		values.put(CN_VALUE_COLUMN, element.getcNValue());
		values.put(SN_VALUE_COLUMN, element.getsNValue());
		values.put(STATUS_COLUMN, ElementApply.STATUS_APPLY_APPROVED);
		values.put(EPSAP_VERSION_COLUMN, element.getVersionEpsAp());

		values.put(NOTI_ENABLE_FLAG_COLUMN, element.isNotiEnableFlag() ? 1 : 0);
		values.put(NOTI_ENABLE_BEFORE_FLAG_COLUMN, element.isNotiEnableBeforeFlag() ? 1 : 0);
		values.put(NOTI_ENABLE_BEFORE_COLUMN, element.getNotiEnableBefore());

		values.put(SUBJECT_COUNTRY_NAME_COLUMN, element.getSubjectCountryName());
		values.put(SUBJECT_STATE_OR_PROVINCE_NAME_COLUMN, element.getSubjectStateOrProvinceName());
		values.put(SUBJECT_LOCALITY_NAME_COLUMN, element.getSubjectLocalityName());
		values.put(SUBJECT_ORGANIZATION_NAME_COLUMN, element.getSubjectOrganizationName());
		values.put(SUBJECT_COMMON_NAME_COLUMN, element.getSubjectCommonName());
		values.put(SUBJECT_EMAIL_ADDRESS_COLUMN, element.getSubjectEmailAddress());
		values.put(ISSUER_COUNTRY_NAME_COLUMN, element.getIssuerCountryName());
		values.put(ISSUER_STATE_OR_PROVINCE_NAME_COLUMN, element.getIssuerStateOrProvinceName());
		values.put(ISSUER_LOCALITY_NAME_COLUMN, element.getIssuerLocalityName());
		values.put(ISSUER_ORGANIZATION_NAME_COLUMN, element.getIssuerOrganizationName());
		values.put(ISSUER_ORGANIZATION_UNIT_NAME_COLUMN, element.getIssuerOrganizationUnitName());
		values.put(ISSUER_COMMON_NAME_COLUMN, element.getIssuerCommonName());
		values.put(ISSUER_EMAIL_ADDRESS_COLUMN, element.getIssuerEmailAdress());
		values.put(VERSION_COLUMN, element.getVersion());
		values.put(SERIAL_NUMBER_COLUMN, element.getSerialNumber());
		values.put(SIGNATURE_ALOGRITHM_COLUMN, element.getSignatureAlogrithm());
		values.put(NOT_VALID_BEFORE_COLUMN, element.getNotValidBefore());
		values.put(NOT_VALID_AFTER_COLUMN, element.getNotValidAfter());
		values.put(PUBLIC_KEY_ALOGRITHM_COLUMN, element.getPublicKeyAlogrithm());
		values.put(PUBLIC_KEY_DATA_COLUMN, element.getPublicKeyData());
		values.put(PUBLIC_SIGNATURE_COLUMN, element.getPublicSignature());
		values.put(CERTIFICATE_AUTHORITY_COLUMN, element.getCertificateAuthority());
		values.put(USAGE_COLUMN, element.getUsage());
		values.put(SUBJECT_KEY_IDENTIFIER_COLUMN, element.getSubjectKeyIdentifier());
		values.put(AUTHORITY_KEY_IDENTIFIER_COLUMN, element.getAuthorityKeyIdentifier());
		values.put(CLR_DISTRIBUTION_POINT_URI_COLUMN, element.getClrDistributionPointUri());
		values.put(CERTIFICATE_AUTHORITY_URI_COLUMN, element.getCertificateAuthorityUri());
		values.put(PURPOSE_COLUMN, element.getPurpose());
		values.put(RFC822_NAME, element.getRfc822Name());
		try {
			openDatabase();
			sqLiteDB.update(TABLE_ELEMENT_APPLY, values, "id=" + element.getId(), null);
		} finally {
			closeDatabase();
		}
	}

	public boolean hasCertificate() {
		int total = 0;
		Cursor cursor = null;
		// Select All Query
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_ELEMENT_APPLY
				+ " WHERE " + STATUS_COLUMN + " = ?";
		try {
			openDatabase();
			cursor = sqLiteDB.rawQuery(selectQuery, new String[]{
					String.valueOf(ElementApply.STATUS_APPLY_APPROVED)});
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					total = cursor.getInt(0);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			closeDatabase();
		}
		return total > 0;
	}
}
