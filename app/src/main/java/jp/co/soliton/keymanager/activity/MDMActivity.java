package jp.co.soliton.keymanager.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import jp.co.soliton.keymanager.EpsapAdminReceiver;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterMDM;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;
import jp.co.soliton.keymanager.mdm.MDMControl;
import jp.co.soliton.keymanager.mdm.MDMFlgs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class MDMActivity extends BaseSettingPhoneActivity {

	private Button btnDeleteMDM;
	private ListView listView;
	private AdapterMDM adapterMDM;
	private List<AdapterMDM.ItemMDM> listItemMDM;
	private MDMFlgs mdm;
	private RelativeLayout viewProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdm_setting);
	    btnDeleteMDM = (Button) findViewById(R.id.btnDeleteMDM);
	    listView = (ListView) findViewById(R.id.listMdmItem);
	    viewProgressBar = (RelativeLayout) findViewById(R.id.pb);
	    listItemMDM = new ArrayList<>();
	    adapterMDM = new AdapterMDM(this, listItemMDM);
	    listView.setAdapter(adapterMDM);
	    mdm = (MDMFlgs) getIntent().getSerializableExtra("mdm");
    }

    @Override
    protected void onResume() {
        super.onResume();
	    prepareData();
        setupControl();
    }

	@Override
	protected void setTextTitle() {
		tvTitleHeader.setText(getString(R.string.profile));
	}

	public void setupControl() {
		btnDeleteMDM.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final DialogApplyConfirm dialog = new DialogApplyConfirm(MDMActivity.this);
				dialog.setTextDisplay(null, getString(R.string.content_delete_mdm_profile_dialog)
						, getString(R.string.label_dialog_cancel), getString(R.string.label_dialog_delete_cert));
				dialog.setOnClickOK(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteMDM();
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
    }

	private void deleteMDM() {
		//Stop service
		MDMControl mdmctrl = new MDMControl(this, mdm.GetUDID());
		//Delete AdminDevice
		DevicePolicyManager m_DPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName m_DeviceAdmin = new ComponentName(this, EpsapAdminReceiver.class);
		if (m_DPM.isAdminActive(m_DeviceAdmin)) {
			m_DPM.removeActiveAdmin(m_DeviceAdmin);
		}
		//CheckOutMDM
		viewProgressBar.setVisibility(View.VISIBLE);
		MDMControl.CheckOutMdmTask checkOutMdmTask = new MDMControl.CheckOutMdmTask(this, new MDMControl.CheckOutListener() {
			@Override
			public void checkOutComplete() {
				LogCtrl.getInstance().info("Setting: MDM profile has deleted");
				viewProgressBar.setVisibility(View.GONE);
				finish();
			}
		});
		checkOutMdmTask.execute();
	}

	private void prepareData() {
		boolean isEnableLock = ((mdm.GetAccessRight() & mdm.getM_n_devicelock()) == mdm.getM_n_devicelock());
		boolean isEnableWipe = ((mdm.GetAccessRight() & mdm.getM_n_devaiceerace()) == mdm.getM_n_devaiceerace());
		boolean isEnableDeviceInfo = ((mdm.GetAccessRight() & mdm.getM_n_deviceinf()) == mdm.getM_n_deviceinf());
		boolean isEnableNetworkInfo = ((mdm.GetAccessRight() & mdm.getM_n_network()) == mdm.getM_n_network());
		boolean isEnableAppInfo = ((mdm.GetAccessRight() & mdm.getM_n_inst_appinf()) == mdm.getM_n_inst_appinf());
		AdapterMDM.ItemMDM itemLock = new AdapterMDM.ItemMDM(getString(R.string.device_lock), isEnableLock);
		AdapterMDM.ItemMDM itemWipe = new AdapterMDM.ItemMDM(getString(R.string.remote_wipe), isEnableWipe);
		AdapterMDM.ItemMDM itemDeviceInfo = new AdapterMDM.ItemMDM(getString(R.string.device_info), isEnableDeviceInfo);
		AdapterMDM.ItemMDM itemNetworkInfo = new AdapterMDM.ItemMDM(getString(R.string.network_info), isEnableNetworkInfo);
		AdapterMDM.ItemMDM itemAppInfo = new AdapterMDM.ItemMDM(getString(R.string.app_info), isEnableAppInfo);
		listItemMDM.clear();
		listItemMDM.add(itemLock);
		listItemMDM.add(itemWipe);
		listItemMDM.add(itemDeviceInfo);
		listItemMDM.add(itemNetworkInfo);
		listItemMDM.add(itemAppInfo);
		adapterMDM.setListItemMDM(listItemMDM);
		adapterMDM.notifyDataSetChanged();
	}
}
