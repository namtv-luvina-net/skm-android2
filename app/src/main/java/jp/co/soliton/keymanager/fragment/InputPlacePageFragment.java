package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page choose certificate type VPN or Wifi
 */

public class InputPlacePageFragment extends InputBasePageFragment {

    private Button btnTargetVPN;
    private Button btnTargetWiFi;
    private LinearLayout zoneInputPlace;

    public static Fragment newInstance(Context context) {
        InputPlacePageFragment f = new InputPlacePageFragment();
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewPagerInputActivity) {
            this.pagerInputActivity = (ViewPagerInputActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_input_place, null);
        zoneInputPlace = (LinearLayout) root.findViewById(R.id.zoneInputPlace);
        btnTargetVPN = (Button) root.findViewById(R.id.btnTargetVPN);
        btnTargetWiFi = (Button) root.findViewById(R.id.btnTargetWifi);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Execute action
        btnTargetVPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerInputActivity.getInputApplyInfo().setPlace(TARGET_VPN);
                pagerInputActivity.gotoPage(3);
            }
        });
        btnTargetWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerInputActivity.getInputApplyInfo().setPlace(TARGET_WiFi);
                pagerInputActivity.gotoPage(3);
            }
        });
    }

    public void hideScreen() {
        if (zoneInputPlace == null) {
            return;
        }
        zoneInputPlace.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
