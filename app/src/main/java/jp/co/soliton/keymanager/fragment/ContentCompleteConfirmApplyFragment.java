package jp.co.soliton.keymanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentCompleteConfirmApplyFragment extends Fragment {

	private boolean isTablet;
	private View layoutComplete;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		View  view;
		if (isTablet) {
			view = inflater.inflate(R.layout.fragment_complete_confirm_apply_tablet, container, false);
			layoutComplete = view.findViewById(R.id.layoutComplete);
		} else {
			view = inflater.inflate(R.layout.fragment_complete_confirm_apply_phone, container, false);
			layoutComplete = view.findViewById(R.id.layoutComplete);
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int status  = ((CompleteConfirmApplyActivity)getActivity()).getStatus();
		if (status == ElementApply.STATUS_APPLY_APPROVED) {
			//
		} else if (status == ElementApply.STATUS_APPLY_PENDING) {
			layoutComplete.setVisibility(View.GONE);
			((CompleteConfirmApplyActivity)getActivity()).showMessage(getString(R.string.message_pending), getString(R.string
					.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					Intent intent = new Intent(getActivity(), MenuAcivity.class);
					StringList.GO_TO_LIST_APPLY = "1";
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_REJECT) {
			layoutComplete.setVisibility(View.GONE);
			((CompleteConfirmApplyActivity)getActivity()).showMessage(getString(R.string.message_reject), getString(R.string.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					getActivity().finish();
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_CANCEL) {
			layoutComplete.setVisibility(View.GONE);
			((CompleteConfirmApplyActivity)getActivity()).showMessage(getString(R.string.message_cancel), getString(R.string.title_cancel), new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					getActivity().finish();
				}
			});
		} else {
			Intent intent = new Intent(getActivity(), MenuAcivity.class);
			StringList.GO_TO_LIST_APPLY = "1";
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	public void clickStart(View v) {
		((CompleteConfirmApplyActivity)getActivity()).clickStart(v);
	}

}
