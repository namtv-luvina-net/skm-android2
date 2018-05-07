package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.util.List;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_RIGHT;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentCompleteConfirmApplyFragment extends Fragment {

	private Button btnStartUsing;
	private View layoutComplete;
	private int status;
	private ElementApply element;
	private InformCtrl m_InformCtrl;
	private View viewFragment;

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
		viewFragment = inflater.inflate(R.layout.fragment_complete_confirm_apply_tablet, container, false);
		layoutComplete = viewFragment.findViewById(R.id.layoutComplete);
		btnStartUsing = (Button) viewFragment.findViewById(R.id.btnStartUsing);
		return viewFragment;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (status == ElementApply.STATUS_APPLY_APPROVED) {
			//
		} else if (status == ElementApply.STATUS_APPLY_PENDING) {
			LogCtrl.getInstance().info("Apply: Application is still pending");
			layoutComplete.setVisibility(View.GONE);
			showMessageTablet(getString(R.string.message_pending), getString(R.string.approval_confirmation), new
					DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					final List<ElementApply> listElementApply = ((MenuAcivity)getActivity()).getListElementApply();
					if (listElementApply.size() == 1) {
						StringList.ID_DETAIL_CURRENT = String.valueOf(listElementApply.get(0).getId());
						((MenuAcivity)getActivity()).startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
					} else {
						((MenuAcivity)getActivity()).startListConfirmApplyFragment(SCROLL_TO_RIGHT);
					}
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_REJECT) {
			LogCtrl.getInstance().info("Apply: Application has rejected");
			layoutComplete.setVisibility(View.GONE);
			showMessageTablet(getString(R.string.message_reject), getString(R.string.approval_confirmation), new
					DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					((MenuAcivity)getActivity()).startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_CANCEL) {
			LogCtrl.getInstance().info("Apply: Application has withdrawn");
			layoutComplete.setVisibility(View.GONE);
			showMessageTablet(getString(R.string.message_cancel), getString(R.string.title_cancel), new
					DialogApplyMessage.OnOkDismissMessageListener() {
						@Override
						public void onOkDismissMessage() {
							((MenuAcivity)getActivity()).startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
						}
					});
		} else {
			((MenuAcivity)getActivity()).gotoMenuTablet();
		}
	}

	private void showMessageTablet(String message, String titleDialog, DialogApplyMessage.OnOkDismissMessageListener
			listener) {
		DialogApplyMessage dlgMessage = new DialogApplyMessage(getActivity(), message);
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
