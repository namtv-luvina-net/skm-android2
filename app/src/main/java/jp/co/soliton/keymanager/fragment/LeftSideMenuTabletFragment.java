package jp.co.soliton.keymanager.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideMenuTabletFragment extends Fragment {

	Button btnSetting;
	TextView textViewGuide3;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_left_side_menu_tablet, container, false);
		textViewGuide3 = (TextView) view.findViewById(R.id.tv_guide_3);
		btnSetting = (Button) view.findViewById(R.id.btnSetting);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		btnSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MenuAcivity)getActivity()).startSettingActivity();
			}
		});
		String str1 = getActivity().getString(R.string.guide_31);
		String str2 = getActivity().getString(R.string.guide_32);
		textViewGuide3.measure(0,0);
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(str1 + "   " + str2);
		Bitmap bitmapSetting = BitmapFactory.decodeResource( getResources(), R.drawable.gear_guide);
		bitmapSetting = scaleBitmap(bitmapSetting, textViewGuide3.getMeasuredHeight(), true);
		ImageSpan imageSpan = new ImageSpan(getActivity(), bitmapSetting, ImageSpan.ALIGN_BASELINE);
		ssb.setSpan(imageSpan, str1.length() + 1  , str1.length() + 2 , Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		textViewGuide3.setText( ssb, TextView.BufferType.SPANNABLE );
	}

	public static Bitmap scaleBitmap(Bitmap realImage, float newSize, boolean filter) {
		newSize *= 0.67;
		float ratio = Math.min(newSize / realImage.getWidth(), newSize / realImage.getHeight());
		int width = Math.round(ratio * realImage.getWidth());
		int height = Math.round(ratio * realImage.getHeight());
		Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
				height, filter);
		return newBitmap;
	}
}
