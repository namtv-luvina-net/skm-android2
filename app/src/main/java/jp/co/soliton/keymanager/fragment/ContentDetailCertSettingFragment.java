package jp.co.soliton.keymanager.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.ItemChildDetailCertSetting;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.SettingTabletActivity;
import jp.co.soliton.keymanager.adapter.AdapterSettingDetailCertificate;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.InfoDetailCertificateSetting;
import jp.co.soliton.keymanager.customview.DialogConfirmTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_LEFT;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentDetailCertSettingFragment extends TabletBaseSettingFragment {

	private ExpandableListView expandableListView;
	private List<String> listDataHeader;
	private List<List<ItemChildDetailCertSetting>> listDataChild;
	private AdapterSettingDetailCertificate adapterSettingDetailCertificate;
	private ElementApplyManager elementMgr;
	private String id;
	private ElementApply elementApply;

	public static Fragment newInstance(String id) {
		ContentDetailCertSettingFragment f = new ContentDetailCertSettingFragment();
		f.id = id;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		elementMgr = new ElementApplyManager(getActivity());
		elementApply = elementMgr.getElementApply(id);
		viewFragment = inflater.inflate(R.layout.fragment_setting_detail_certificate, container, false);
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(elementApply.getcNValue());
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		moreOption = (Button) viewFragment.findViewById(R.id.more_option);
		moreOption.setVisibility(View.VISIBLE);
		expandableListView = (ExpandableListView) viewFragment.findViewById(R.id.expand_detail_cert);
		adapterSettingDetailCertificate = new AdapterSettingDetailCertificate(getActivity(), true);
		expandableListView.setAdapter(adapterSettingDetailCertificate);
		expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		prepareData();
		expandableListView.setClickable(false);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		prepareData();
		moreOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence[] items = {getResources().getString(R.string.label_dialog_delete_cert),
						getResources().getString(R.string.notif_setting),
						getResources().getString(R.string.label_dialog_Cancle)};

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
				builder.setTitle(getResources().getString(R.string.select_apid));

				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						dialog.dismiss();
						if (item == 0) {
							confirmDeleteCert();
						} else if (item == 1) {
							((SettingTabletActivity)getActivity()).gotoNotificationOneSetting(SCROLL_TO_LEFT);
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	@Override
	protected void setTextBtnBack() {
		textViewBack.setText(getString(R.string.list_cert));
	}

	private void confirmDeleteCert() {
		final DialogConfirmTablet dialog = new DialogConfirmTablet(getActivity());
		dialog.setTextDisplay(getString(R.string.dialog_delete_title), getString(R.string.dialog_delete_msg)
				, getString(R.string.label_dialog_Cancle), getString(R.string.label_dialog_delete_cert));
		dialog.setOnClickOK(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				elementMgr.deleteElementApply(id);
				AlarmReceiver alarm = new AlarmReceiver();
				alarm.setupNotification(getActivity());
				getActivity().onBackPressed();
			}
		});
		dialog.show();
	}

	private void prepareData() {
		listDataHeader  = InfoDetailCertificateSetting.prepareHeader(getActivity());
		listDataChild = InfoDetailCertificateSetting.prepareChild(getActivity(), elementApply);
		adapterSettingDetailCertificate.setListDataHeader(listDataHeader);
		adapterSettingDetailCertificate.setListDataChild(listDataChild);
		adapterSettingDetailCertificate.notifyDataSetChanged();
	}
}
