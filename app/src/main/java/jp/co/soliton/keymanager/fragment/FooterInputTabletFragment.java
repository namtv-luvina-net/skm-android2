package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class FooterInputTabletFragment extends Fragment {

	public interface InterfaceForFooter {
		void clickNext();
		void clickBack();
		void clickSkip();
	}

	public interface InterfaceForContent {
		void updateButtonFooterStatus(int position);
		void clickBackButton();
		void setStatusBackNext(int position);
		void goneSkipButton();
		void visibleSkipButton();
		void disableNextButton();
		void enableNextButton();
	}

	private TextView btnSkip;
	private Button btnNext;
	private Button btnBack;
	InterfaceForFooter interfaceForFooter;

	public static Fragment newInstance() {
		FooterInputTabletFragment f = new FooterInputTabletFragment();
		return f;
	}

	public void setInterfaceForFooter(InterfaceForFooter interfaceForFooter) {
		this.interfaceForFooter = interfaceForFooter;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_footer_input, container, false);
		btnSkip = (TextView) view.findViewById(R.id.btnSkip);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				interfaceForFooter.clickNext();
			}
		});
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				interfaceForFooter.clickBack();
			}
		});
		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				interfaceForFooter.clickSkip();
			}
		});
	}

	public void goneSkip() {
		btnSkip.setVisibility(View.GONE);
	}
	public void visibleSkip() {
		btnSkip.setVisibility(View.VISIBLE);
	}

	public void invisibleBack() {
		btnBack.setVisibility(View.INVISIBLE);
	}

	public void visibleBack() {
		btnBack.setVisibility(View.VISIBLE);
	}

	public void disableNext() {
		btnNext.setEnabled(false);
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnNext.setBackgroundDrawable( getResources().getDrawable(R.drawable.background_btn_disable) );
		} else {
			btnNext.setBackground( getResources().getDrawable(R.drawable.background_btn_disable));
		}
	}

	public void enableNext() {
		btnNext.setEnabled(true);
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnNext.setBackgroundDrawable( getResources().getDrawable(R.drawable.background_btn_ctrl_apid) );
		} else {
			btnNext.setBackground( getResources().getDrawable(R.drawable.background_btn_ctrl_apid));
		}
	}

	public void invisibleNext() {
		btnNext.setVisibility(View.INVISIBLE);
	}

	public void visibleNext() {
		btnNext.setVisibility(View.VISIBLE);
	}

	public void updateButtonFooterStatus(int position){}
	public void clickBackButton(){}
	public void setStatusBackNext(int position){}
	public void goneSkipButton(){}
	public void visibleSkipButton(){}
	public void disableNextButton(){}
	public void enableNextButton(){}
}
