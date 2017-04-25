package jp.co.soliton.keymanager.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.*;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentMenuTabletFragment extends Fragment {

	RelativeLayout rlMenuStart;
	RelativeLayout rlMenuAPID;
	RelativeLayout rlMenuConfirmApply;
	ElementApplyManager elementMgr;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		elementMgr = new ElementApplyManager(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content_menu_tablet, container, false);
		rlMenuStart = (RelativeLayout) view.findViewById(R.id.rl_menu_start);
		rlMenuAPID = (RelativeLayout) view.findViewById(R.id.rl_menu_apid);
		rlMenuConfirmApply = (RelativeLayout) view.findViewById(R.id.rl_menu_confirm_apply);

		Log.d("datnd", "onCreateView: rlMenuConfirmApply = " + rlMenuConfirmApply.getId());
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateMenuConfirm();
		setupControl();
	}

	private void setupControl() {
			rlMenuStart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (elementMgr.hasCertificate()) {
						Intent intent = new Intent(getActivity(), ListCertificateActivity.class);
						startActivity(intent);
					} else {
						InputApplyInfo.deletePref(getActivity());
						Intent intent = new Intent(getActivity(), ViewPagerInputActivity.class);
						startActivity(intent);
					}
				}
			});

			rlMenuAPID.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), APIDActivity.class);
					startActivity(intent);
				}
			});
	}

	private void updateMenuConfirm() {
        final int totalApply = elementMgr.getCountElementApply();
        if (totalApply <= 0) {
            rlMenuConfirmApply.setVisibility(View.GONE);
        } else {
	        rlMenuConfirmApply.setVisibility(View.VISIBLE);
	        rlMenuConfirmApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (totalApply == 1) {
                        List<ElementApply> listElementApply = elementMgr.getAllElementApply();
                        Intent intent = new Intent(getActivity(), DetailConfirmActivity.class);
                        intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(0).getId()));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), ListConfirmActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
	}
}
