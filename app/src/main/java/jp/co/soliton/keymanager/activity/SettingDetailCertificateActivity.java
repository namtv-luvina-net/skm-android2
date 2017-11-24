package jp.co.soliton.keymanager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import jp.co.soliton.keymanager.ItemChildDetailCertSetting;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.adapter.AdapterSettingDetailCertificate;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.InfoDetailCertificateSetting;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by luongdolong on 4/3/2017.
 */

public class SettingDetailCertificateActivity extends BaseSettingPhoneActivity {
    private ElementApplyManager elementMgr;
    private ElementApply elementApply;
	private ExpandableListView expandableListView;
	private List<String> listDataHeader;
	private List<List<ItemChildDetailCertSetting>> listDataChild;
	private AdapterSettingDetailCertificate adapterSettingDetailCertificate;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_detail_certificate);
	    elementMgr = ElementApplyManager.getInstance(this);
	    id = getIntent().getStringExtra(StringList.ELEMENT_APPLY_ID);
	    elementApply = elementMgr.getElementApply(id);
		expandableListView = (ExpandableListView) findViewById(R.id.expand_detail_cert);
	    adapterSettingDetailCertificate = new AdapterSettingDetailCertificate(this, false);
	    expandableListView.setAdapter(adapterSettingDetailCertificate);
    }

    @Override
    protected void onResume() {
	    super.onResume();
	    prepareData();
    }

	@Override
	protected void initBtnMenuDetailSetting() {
		btnMenuDetailSetting.setVisibility(View.VISIBLE);
		btnMenuDetailSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onMenuSettingClick(v);
			}
		});
	}

	@Override
	protected void setTextBtnBack() {
		textViewBack.setText(getString(R.string.list_cert));
	}

	@Override
	protected void setTextTitle() {
		if (elementApply.getcNValue() != null) {
			tvTitleHeader.setText(elementApply.getcNValue());
		}else {
			tvTitleHeader.setText("");
		}
	}

	private void onMenuSettingClick(View v) {
		final CharSequence[] items = {getResources().getString(R.string.label_dialog_delete_cert),
				getResources().getString(R.string.notif_setting),
				getResources().getString(R.string.label_dialog_Cancle)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.select_apid));

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					dialog.dismiss();
					confirmDeleteCert();
				} else if (item == 1) {
					dialog.dismiss();
					Intent intent = new Intent(SettingDetailCertificateActivity.this, NotificationSettingActivity.class);
					intent.putExtra(NotificationSettingActivity.KEY_NOTIF_MODE, NotificationSettingActivity.NotifModeEnum.ONE);
					intent.putExtra(StringList.ELEMENT_APPLY_ID, id);
					startActivity(intent);
				} else if (item == 3) {
					dialog.dismiss();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void confirmDeleteCert() {
        final DialogApplyConfirm dialog = new DialogApplyConfirm(this);
        dialog.setTextDisplay(getString(R.string.confirm), getString(R.string.content_dialog_ask_delete_cert)
                , getString(R.string.no), getString(R.string.yes));
        dialog.setOnClickOK(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                elementMgr.deleteElementApply(id);
				LogCtrl.getInstance().info("Certificate: Deleted");
				LogCtrl.getInstance().debug("CN=" + elementApply.getcNValue() + "S/N=" + elementApply.getsNValue());
                AlarmReceiver alarm = new AlarmReceiver();
                alarm.setupNotification(getApplicationContext());
                finish();
            }
        });
        dialog.show();
    }

	private void prepareData() {
		listDataHeader  = InfoDetailCertificateSetting.prepareHeader(this);
		listDataChild = InfoDetailCertificateSetting.prepareChild(this, elementApply);
		adapterSettingDetailCertificate.setListDataHeader(listDataHeader);
		adapterSettingDetailCertificate.setListDataChild(listDataChild);
		adapterSettingDetailCertificate.notifyDataSetChanged();
	}
}
