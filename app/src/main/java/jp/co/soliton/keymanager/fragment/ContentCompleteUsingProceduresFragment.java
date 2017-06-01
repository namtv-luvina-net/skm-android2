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
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentCompleteUsingProceduresFragment extends Fragment {

	private View viewFragment;
    private TextView txtCN;
    private TextView txtSN;
    private TextView txtEpDate;
    private TextView backToTop;
	private ElementApply elementApply;

	public static Fragment newInstance(ElementApply elementApply) {
		ContentCompleteUsingProceduresFragment f = new ContentCompleteUsingProceduresFragment();
		f.elementApply = elementApply;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_complete_using_procedures_tablet, container, false);
        txtCN = (TextView) viewFragment.findViewById(R.id.txtCN);
        txtSN = (TextView) viewFragment.findViewById(R.id.txtSN);
        txtEpDate = (TextView) viewFragment.findViewById(R.id.txtEpDate);
		backToTop = (TextView) viewFragment.findViewById(R.id.backToTop);
		return viewFragment;
	}

    @Override
    public void onResume() {
        super.onResume();
        txtCN.setText(elementApply.getcNValue());
        txtSN.setText(elementApply.getsNValue());
        txtEpDate.setText(elementApply.getExpirationDate().split(" ")[0]);
	    backToTop.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    ((MenuAcivity)getActivity()).gotoMenuTablet();
		    }
	    });
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
