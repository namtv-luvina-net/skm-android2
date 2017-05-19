package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.DetailConfirmActivity;
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
	private TextView title;
	private TextView tvDeleteApply;
	private TextView tvConfirmApply;
	private String id;
	boolean isTablet;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		View  view;
		if (isTablet) {
			view = inflater.inflate(R.layout.fragment_detail_confirm_apply_tablet, container, false);
		} else {
			view = inflater.inflate(R.layout.fragment_detail_confirm_phone, container, false);
			title = (TextView) view.findViewById(R.id.tvTitleHeader);
		}
		elementMgr = new ElementApplyManager(getActivity());
		tvHostName = (TextView) view.findViewById(R.id.tvHostName);
		tvUserId = (TextView) view.findViewById(R.id.tvUserId);
		tvDate = (TextView) view.findViewById(R.id.tvDate);
		tvStatus = (TextView) view.findViewById(R.id.tvStatus);
		tvDeleteApply = (TextView) view.findViewById(R.id.tvDeleteApply);
		tvConfirmApply = (TextView) view.findViewById(R.id.tvConfirmApply);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		id = ((DetailConfirmActivity)getActivity()).getId();
		setupDisplay();
	}

	private void setupDisplay() {
		if (!isTablet) {
			title.setText(getString(R.string.approval_confirmation));
		}
		ElementApply detail = elementMgr.getElementApply(id);
		if (detail.getHost() != null) {
			tvHostName.setText(detail.getHost());
		}
		if (detail.getUserId() != null) {
			tvUserId.setText(detail.getUserId());
		}
		if (detail.getUpdateDate() != null) {
			String updateDate = detail.getUpdateDate().split(" ")[0];
			tvDate.setText(updateDate.replace("-", "/"));
		}
		if (detail.getStatus() == ElementApply.STATUS_APPLY_CANCEL) {
			tvStatus.setText(getText(R.string.stt_cancel));
			if (isTablet) {
				((DetailConfirmActivity) getActivity()).updateDesLeftSide(getString(R.string.des_leftside_detail_confirm_tablet_withdrawn));
			}
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
			tvStatus.setText(getText(R.string.stt_waiting_approval));
			if (isTablet) {
				((DetailConfirmActivity) getActivity()).updateDesLeftSide(getString(R.string.des_leftside_detail_confirm_tablet_cofirms_status));
			}
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_REJECT) {
			tvStatus.setText(getText(R.string.stt_rejected));
			if (isTablet) {
				((DetailConfirmActivity) getActivity()).updateDesLeftSide(getString(R.string.des_leftside_detail_confirm_tablet_rejected));
			}
		}

		if (detail.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
			tvConfirmApply.setText(getString(R.string.confirm_apply_status));
			tvConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((DetailConfirmActivity)getActivity()).clickConfirmApply(v);
				}
			});
			tvDeleteApply.setText(getString(R.string.withdrawal_apply));
			tvDeleteApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((DetailConfirmActivity)getActivity()).clickWithdrawApply(v);
				}
			});
		} else {
			tvConfirmApply.setText(getString(R.string.re_apply));
			tvConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((DetailConfirmActivity)getActivity()).clickReApply(v);
				}
			});

			tvDeleteApply.setText(getString(R.string.delete_apply));
			tvDeleteApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((DetailConfirmActivity)getActivity()).clickDeleteApply(v);
				}
			});
		}
	}

}
