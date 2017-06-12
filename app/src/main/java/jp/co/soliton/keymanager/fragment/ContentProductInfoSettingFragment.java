package jp.co.soliton.keymanager.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;
import jp.co.soliton.keymanager.common.EmailCtrl;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentProductInfoSettingFragment extends TabletBaseSettingFragment {

	private Button btnLogSendMail;
	private ProgressDialog progressDialog;
	private Button btnSettingProductInfo;

	public static Fragment newInstance() {
		ContentProductInfoSettingFragment f = new ContentProductInfoSettingFragment();
		return f;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		progressDialog = new ProgressDialog(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_product_info, container, false);
		btnLogSendMail = (Button) viewFragment.findViewById(R.id.btnLogSendMail);
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.label_product_setting));
		btnSettingProductInfo = (Button) viewFragment.findViewById(R.id.btnSettingProductInfo);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupControl();
	}

	public void setupControl() {
		String nameApp = getString(R.string.app_name);
		String version = getString(R.string.version);
		String verApp = BuildConfig.VERSION_NAME;
		String productInfo = nameApp + "\n" + version +" " +verApp;
		btnSettingProductInfo.setText(productInfo);

		btnLogSendMail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog.show();
				progressDialog.setMessage(getActivity().getString(R.string.creating_diagnostic_infomation));
				new ProcessInfoAndZipTask(getActivity(), new ProcessInfoAndZipTask.EndProcessInfoTask() {
					@Override
					public void endConnection(ProcessInfoAndZipTask.ContentZip contentZip) {
						progressDialog.dismiss();
						EmailCtrl.sentEmailInfo(getActivity(), contentZip);
					}
				}).execute();
			}
		});
		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
	}
}
