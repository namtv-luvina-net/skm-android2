package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentDetailConfirmFragment extends Fragment {

	private ElementApplyManager elementMgr;
	private TextView tvHostName;
	private TextView tvUserId;
	private TextView tvDate;
	private TextView tvStatus;
	private TextView tvDeleteApply;
	private TextView tvConfirmApply;
	private String[] listData = new String[4];
	private View viewFragment;

	public static Fragment newInstance() {
		ContentDetailConfirmFragment f = new ContentDetailConfirmFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_detail_confirm_apply_tablet, container, false);
		elementMgr = new ElementApplyManager(getActivity());
		tvHostName = (TextView) viewFragment.findViewById(R.id.tvHostName);
		tvUserId = (TextView) viewFragment.findViewById(R.id.titleUserId);
		tvDate = (TextView) viewFragment.findViewById(R.id.tvDate);
		tvStatus = (TextView) viewFragment.findViewById(R.id.tvStatus);
		tvDeleteApply = (TextView) viewFragment.findViewById(R.id.tvDeleteApply);
		tvConfirmApply = (TextView) viewFragment.findViewById(R.id.tvConfirmApply);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupDisplay();
	}

	private void setupDisplay() {
		ElementApply detail = elementMgr.getElementApply(StringList.ID_DETAIL_CURRENT);
		if (detail.getHost() != null) {
			tvHostName.setText(detail.getHost());
			listData[0] = detail.getHost();
		}
		if (detail.getUserId() != null) {
			tvUserId.setText(detail.getUserId());
			listData[1] = detail.getUserId();
		}
		if (detail.getUpdateDate() != null) {
			String updateDate = detail.getUpdateDate().split(" ")[0];
			updateDate = updateDate.replace("-", "/");
			tvDate.setText(updateDate);
			listData[2] = updateDate;
		}
		String desLeftSideTablet = "";
		String status = "";
		if (detail.getStatus() == ElementApply.STATUS_APPLY_CANCEL) {
			status = getString(R.string.stt_cancel);
			desLeftSideTablet = getString(R.string.des_leftside_detail_confirm_tablet_withdrawn);
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
			status = getString(R.string.stt_waiting_approval);
			desLeftSideTablet = getString(R.string.des_leftside_detail_confirm_tablet_cofirms_status);
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_REJECT) {
			status = getString(R.string.stt_rejected);
			desLeftSideTablet = getString(R.string.des_leftside_detail_confirm_tablet_rejected);
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_FAILURE) {
			status = getString(R.string.failure);
			desLeftSideTablet = getString(R.string.failed_to_get_ca);
		}
		tvStatus.setText(status);
		listData[3] = status;
		((MenuAcivity) getActivity()).updateDesLeftSideDetailConfirm(desLeftSideTablet);

		if (detail.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
			tvConfirmApply.setText(getString(R.string.confirm_apply_status));
			tvConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MenuAcivity) getActivity()).clickConfirmApply(listData);
				}
			});
			tvDeleteApply.setText(getString(R.string.withdrawal_apply));
			tvDeleteApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MenuAcivity) getActivity()).clickWithdrawApply(listData);
				}
			});
		} else {
			tvConfirmApply.setText(getString(R.string.re_apply));
			tvConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MenuAcivity) getActivity()).clickReApply();
				}
			});

			tvDeleteApply.setText(getString(R.string.delete_apply));
			tvDeleteApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MenuAcivity) getActivity()).clickDeleteApplyTablet();
				}
			});
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
