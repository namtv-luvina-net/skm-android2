package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentCompleteConfirmApplyFragment extends Fragment {

	private Button btnStartUsing;
	private View layoutComplete;
	int status;
	ElementApply element;
	InformCtrl m_InformCtrl;

	public static Fragment newInstance(int status, ElementApply element, InformCtrl m_InformCtrl) {
		ContentCompleteConfirmApplyFragment f = new ContentCompleteConfirmApplyFragment();
		f.status = status;
		f.element = element;
		f.m_InformCtrl = m_InformCtrl;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View  view = inflater.inflate(R.layout.fragment_complete_confirm_apply_tablet, container, false);
		layoutComplete = view.findViewById(R.id.layoutComplete);
		btnStartUsing = (Button) view.findViewById(R.id.btnStartUsing);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (status == ElementApply.STATUS_APPLY_APPROVED) {
			//
		} else if (status == ElementApply.STATUS_APPLY_PENDING) {
			layoutComplete.setVisibility(View.GONE);
			showMessageTablet(getString(R.string.message_pending), getString(R.string.approval_confirmation), new
					DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					((MenuAcivity)getActivity()).startListConfirmApplyFragment(MenuAcivity.SCROLL_TO_RIGHT);
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_REJECT) {
			layoutComplete.setVisibility(View.GONE);
			showMessageTablet(getString(R.string.message_reject), getString(R.string.approval_confirmation), new
					DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					((MenuAcivity)getActivity()).startDetailConfirmApplyFragment(MenuAcivity.SCROLL_TO_RIGHT);
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_CANCEL) {
			layoutComplete.setVisibility(View.GONE);
			showMessageTablet(getString(R.string.message_cancel), getString(R.string.title_cancel), new
					DialogMessageTablet.OnOkDismissMessageListener() {
						@Override
						public void onOkDismissMessage() {
							((MenuAcivity)getActivity()).startDetailConfirmApplyFragment(MenuAcivity.SCROLL_TO_RIGHT);
						}
					});
		} else {
			((MenuAcivity)getActivity()).gotoMenuTablet();
		}
	}

	private void showMessageTablet(String message, String titleDialog, DialogMessageTablet.OnOkDismissMessageListener
			listener) {
		DialogMessageTablet dlgMessage = new DialogMessageTablet(getActivity(), message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.setTitleDialog(titleDialog);
		dlgMessage.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		btnStartUsing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MenuAcivity)getActivity()).startUsingProceduresFragment(m_InformCtrl, element);
			}
		});
	}
}
