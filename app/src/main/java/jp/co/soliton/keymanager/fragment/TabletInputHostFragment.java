package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputHostFragment extends TabletInputFragment {

	public static Fragment newInstance(Context context) {
		Log.d("datnd", "newInstance: TabletInputHostFragment");
		TabletInputHostFragment f = new TabletInputHostFragment();
		return f;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Log.d("datnd", "onAttach: ");
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d("datnd", "onCreateView: fragment_input_host_tablet==============================================");
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_input_host_tablet, null);
		return view;
	}

	@Override
	public void nextAction() {

	}
}