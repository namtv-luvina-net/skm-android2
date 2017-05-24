package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentStartUsingProceduresFragment extends Fragment {

	public static Fragment newInstance() {
		ContentStartUsingProceduresFragment f = new ContentStartUsingProceduresFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View  view = inflater.inflate(R.layout.fragment_content_start_using_procedures, container, false);
		return view;
	}
}
