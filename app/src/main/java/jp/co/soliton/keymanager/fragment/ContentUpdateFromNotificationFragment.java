package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentUpdateFromNotificationFragment extends Fragment {

	String id;
	Button btnStartUpdate;

	public static Fragment newInstance(String id) {
		ContentUpdateFromNotificationFragment f = new ContentUpdateFromNotificationFragment();
		f.id = id;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notif_update_tablet, container, false);
		btnStartUpdate = (Button) view.findViewById(R.id.btnStartUpdate);
		return view;
	}

    @Override
    public void onResume() {
        super.onResume();
	    btnStartUpdate.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    ((MenuAcivity)getActivity()).startUpdateFragmentFromNotification(id);
		    }
	    });
    }
}
