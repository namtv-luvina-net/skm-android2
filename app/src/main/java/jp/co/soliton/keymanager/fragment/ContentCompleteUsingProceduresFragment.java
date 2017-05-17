package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.CompleteUsingProceduresActivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentCompleteUsingProceduresFragment extends Fragment {

	boolean isTablet;
    private TextView txtCN;
    private TextView txtSN;
    private TextView txtEpDate;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		View  view;
		if (isTablet) {
			view = inflater.inflate(R.layout.fragment_complete_using_procedures_tablet, container, false);
		} else {
			view = inflater.inflate(R.layout.fragment_complete_using_procedures_phone, container, false);
		}
        txtCN = (TextView) view.findViewById(R.id.txtCN);
        txtSN = (TextView) view.findViewById(R.id.txtSN);
        txtEpDate = (TextView) view.findViewById(R.id.txtEpDate);
		return view;
	}

    @Override
    public void onResume() {
        super.onResume();
	    ElementApply elementApply = ((CompleteUsingProceduresActivity)getActivity()).getElementApply();
        txtCN.setText(elementApply.getcNValue());
        txtSN.setText(elementApply.getsNValue());
        txtEpDate.setText(elementApply.getExpirationDate().split(" ")[0]);
    }

	public void backToTop(View v) {
		((CompleteUsingProceduresActivity)getActivity()).backToTop(v);
	}
}
