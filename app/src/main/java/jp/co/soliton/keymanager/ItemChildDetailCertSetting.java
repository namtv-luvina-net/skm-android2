package jp.co.soliton.keymanager;

/**
 * Created by nguyenducdat on 6/7/2017.
 */

public class ItemChildDetailCertSetting {
	private String title;
	private String detail;
	private boolean isOneRow;

	public ItemChildDetailCertSetting(String title, String detail) {
		if (ValidateParams.nullOrEmpty(title)) {
			this.title = "";
		} else {
			this.title = title;
		}
		if (ValidateParams.nullOrEmpty(detail)) {
			this.detail = "";
		} else {
			this.detail = detail;
		}
		this.isOneRow = true;
	}
	public ItemChildDetailCertSetting(String title, String detail, boolean isOneRow) {
		if (ValidateParams.nullOrEmpty(title)) {
			this.title = "";
		} else {
			this.title = title;
		}
		if (ValidateParams.nullOrEmpty(detail)) {
			this.detail = "";
		} else {
			this.detail = detail;
		}
		this.isOneRow = isOneRow;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public boolean isOneRow() {
		return isOneRow;
	}

	public void setOneRow(boolean oneRow) {
		isOneRow = oneRow;
	}
}
