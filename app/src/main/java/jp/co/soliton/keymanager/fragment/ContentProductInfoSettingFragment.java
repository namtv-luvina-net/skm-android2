package jp.co.soliton.keymanager.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;
import jp.co.soliton.keymanager.common.EmailCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentProductInfoSettingFragment extends TabletBaseSettingFragment {

	private Button btnLogSendMail;
	private ProgressDialog progressDialog;
	private Button btnSettingProductInfo;
	private Button btnPrivacyPolicy;

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
		btnLogSendMail = viewFragment.findViewById(R.id.btnLogSendMail);
		btnPrivacyPolicy = viewFragment.findViewById(R.id.btnPrivacyPolicy);
		textViewBack = viewFragment.findViewById(R.id.textViewBack);
		tvTitleHeader = viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.label_product_setting));
		btnSettingProductInfo = viewFragment.findViewById(R.id.btnSettingProductInfo);
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
				confirmPrivacyPolicy();
			}
		});

		btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openUrlPrivacyPolicy();
			}
		});

		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
	}

	private void confirmPrivacyPolicy() {
		final DialogApplyConfirm dialog = new DialogApplyConfirm(getActivity());
		String message = getResources().getString(R.string.content_privacy_policy);
		dialog.setOnClickOK(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				sendDiagnostics();
			}
		});
		dialog.setOnClickCancel(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		dialog.setTextDisplay("", message, "", "");
		dialog.show();
	}
	private void sendDiagnostics() {
		progressDialog.show();
		progressDialog.setMessage(getActivity().getString(R.string.creating_diagnostic_infomation));
		new ProcessInfoAndZipTask(getActivity(), new ProcessInfoAndZipTask.EndProcessInfoTask() {
			@Override
			public void endConnection(ProcessInfoAndZipTask.ContentZip contentZip) {
				progressDialog.dismiss();
				LogCtrl.getInstance().info("Diag: Zip archiving successful");
				EmailCtrl.sentEmailInfo(getActivity(), contentZip);
			}
		}).execute();
	}

	private void openUrlPrivacyPolicy() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string
				.url_privacy_policy)));
		if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivity(browserIntent);
		} else {
			Toast.makeText(getActivity(), getString(R.string.browse_is_not_installed), Toast.LENGTH_SHORT).show();
		}
	}
}
