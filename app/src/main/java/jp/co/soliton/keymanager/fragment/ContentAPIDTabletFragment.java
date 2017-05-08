package jp.co.soliton.keymanager.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.manager.APIDManager;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentAPIDTabletFragment extends Fragment {

	private Button btnCopy;
	private Button btnMail;
	private TextView contentWifi;
	private TextView contentVPN;
	private TextView titleVPN;
	private TextView titleWifi;
	private StringBuilder builderAPID;
	private LogCtrl logCtrl;
	private APIDManager apidManager;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content_apid_tablet, container, false);
		btnCopy = (Button) view.findViewById(R.id.btnCopy);
		btnMail = (Button) view.findViewById(R.id.btnMail);
		contentWifi = (TextView) view.findViewById(R.id.content_wifi);
		contentVPN = (TextView) view.findViewById(R.id.content_vpn);
		titleVPN = (TextView) view.findViewById(R.id.title_vpn);
		titleWifi = (TextView) view.findViewById(R.id.title_wifi);
		logCtrl = LogCtrl.getInstance(getActivity());
		apidManager = new APIDManager(getActivity());
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateViewTitle();
		String strVpnID = apidManager.getStrVpnID();
		String strUDID = apidManager.getStrUDID();
		builderAPID = new StringBuilder();
		builderAPID.append(getResources().getString(R.string.main_apid_vpn) + "\n");
		builderAPID.append(strVpnID +"\n\n");
		builderAPID.append(getResources().getString(R.string.main_apid_wifi) + "\n");
		builderAPID.append(strUDID);

		contentVPN.setText(strVpnID);
		contentWifi.setText(strUDID);

		btnCopy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setClipboard();
			}
		});
		btnMail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMail();
			}
		});
	}

	private void updateViewTitle() {
		titleWifi.measure(0, 0);
		titleVPN.measure(0, 0);
		int widthWifi = titleWifi.getMeasuredWidth();
		int widthVpn = titleVPN.getMeasuredWidth();
		if (widthWifi < widthVpn) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleWifi.getLayoutParams();
			params.width = widthVpn;
			titleWifi.setLayoutParams(params);
		} else {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleVPN.getLayoutParams();
			params.width = widthWifi;
			titleVPN.setLayoutParams(params);
		}
	}

	private void setClipboard() {
		ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = android.content.ClipData.newPlainText("Text", builderAPID.toString());
		clipboard.setPrimaryClip(clip);
	}

	private void sendMail() {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("message/rfc822");
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
		email.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.apid_subject_email).toString());
		email.putExtra(Intent.EXTRA_TEXT, builderAPID.toString());
		try {
			startActivity(Intent.createChooser(email, ""));
		} catch (android.content.ActivityNotFoundException ex) {
			logCtrl.loggerDebug("APIDActivity:sendMail: There are no email clients installed.");
			Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}
