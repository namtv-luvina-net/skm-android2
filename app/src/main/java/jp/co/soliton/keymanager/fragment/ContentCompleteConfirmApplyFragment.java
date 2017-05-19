package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
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
			((CompleteConfirmApplyActivity)getActivity()).showMessagePending();
		} else if (status == ElementApply.STATUS_APPLY_REJECT) {
			layoutComplete.setVisibility(View.GONE);
			((CompleteConfirmApplyActivity)getActivity()).showMessageRejected();
		} else if (status == ElementApply.STATUS_APPLY_CANCEL) {
			layoutComplete.setVisibility(View.GONE);
			((CompleteConfirmApplyActivity)getActivity()).showMessageWithdrawn();
		} else {
			((CompleteConfirmApplyActivity)getActivity()).gotoMenu();
		}
	}

	public void clickStart(View v) {
		((CompleteConfirmApplyActivity)getActivity()).clickStart(v);
	}

}
