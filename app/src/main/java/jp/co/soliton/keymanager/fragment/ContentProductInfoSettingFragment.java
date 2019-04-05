package jp.co.soliton.keymanager.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import jp.co.soliton.keymanager.*;
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

	private Switch traceModeSwitch;
	private RelativeLayout traceModeItem;
	public static final int MAX_CLICK_COUNT = 7;
	private int clickCount;
	private Toast toast;

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
		traceModeSwitch = viewFragment.findViewById(R.id.sw_trace_mode);
		traceModeItem = viewFragment.findViewById(R.id.traceLogsItem);
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

		Context context = SKMApplication.getAppContext();
		SharedPreferences sharedPref = context.getSharedPreferences(StringList.m_str_store_preference,
				Context.MODE_PRIVATE);
		boolean isTraceMode = sharedPref.getBoolean(StringList.TRACE_LOG_KEY, false);
		clickCount = isTraceMode ? 0 : MAX_CLICK_COUNT;
		traceModeItem.setVisibility(isTraceMode ? View.VISIBLE : View.GONE);


		btnSettingProductInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleTapProductItem();
			}
		});

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

		traceModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				traceModeItem.setVisibility(View.GONE);
				traceModeSwitch.setChecked(true);
				clickCount = MAX_CLICK_COUNT;
				Context context = SKMApplication.getAppContext();
				SharedPreferences sharedPref = context.getSharedPreferences(StringList.m_str_store_preference,
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(StringList.TRACE_LOG_KEY, false);
				editor.commit();
				LogCtrl.getInstance().updateTraceMode();
			}
		});
	}

	private void handleTapProductItem() {
		clickCount--;
		Context context = SKMApplication.getAppContext();
		if (clickCount < MAX_CLICK_COUNT - 2) {
			if (toast != null) {
				toast.cancel();
			}
			if (clickCount > 0) {
				toast = Toast.makeText(context, String.format(getString(R.string.trace_mode_notify), clickCount), Toast
						.LENGTH_SHORT);
			} else {
				toast = Toast.makeText(context, getString(R.string.trace_mode_enable), Toast.LENGTH_SHORT);
				if (clickCount == 0) {
					SharedPreferences sharedPref = context.getSharedPreferences(StringList.m_str_store_preference,
							Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putBoolean(StringList.TRACE_LOG_KEY, true);
					editor.commit();
					traceModeItem.setVisibility(View.VISIBLE);
					LogCtrl.getInstance().updateTraceMode();
				}
			}
			toast.show();
		}
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
