package jp.co.soliton.keymanager.customview;

import android.app.Dialog;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.NumberPicker;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

public class ExpiredTimeNotifyBottomSheetDialog extends BottomSheetDialogFragment {

	BottomSheetBehavior mBottomSheetBehavior;
	NumberPicker numberPicker;
	private TextView tvCancel, tvApply;
	int downloadLimit;
	TimeSpecificListener listener;

	public interface TimeSpecificListener {
		void saveTimeDownload(int timeDownload);
	}

	public static ExpiredTimeNotifyBottomSheetDialog newInstance(int downloadLimit) {
		ExpiredTimeNotifyBottomSheetDialog frag = new ExpiredTimeNotifyBottomSheetDialog();
		frag.downloadLimit = downloadLimit;
		return frag;
	}

	public void setListener(TimeSpecificListener listener) {
		this.listener = listener;
	}

	private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior
			.BottomSheetCallback() {
		@Override
		public void onStateChanged(@NonNull View bottomSheet, int newState) {
			if (newState == BottomSheetBehavior.STATE_HIDDEN) {
				dismiss();
			}
		}

		@Override
		public void onSlide(@NonNull View bottomSheet, float slideOffset) {
		}
	};

	@Override
	public void setupDialog(Dialog dialog, int style) {
		super.setupDialog(dialog, style);
		View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_time_notify, null);
		numberPicker = contentView.findViewById(R.id.number_picker);
		tvCancel = contentView.findViewById(R.id.tv_cancel);
		tvApply = contentView.findViewById(R.id.tv_apply);
		dialog.setContentView(contentView);
		CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
				.getLayoutParams();
		CoordinatorLayout.Behavior behavior = params.getBehavior();
		if (behavior != null && behavior instanceof BottomSheetBehavior) {
			((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
		}
		initPicker();
		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		tvApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				listener.saveTimeDownload(numberPicker.getValue());
				dismiss();
			}
		});
		((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
		updateHeightView(contentView, behavior);
	}

	private void initPicker() {
		String[] data = new String[120];
		for (int i = 0; i < data.length; i++) {
			data[i] = String.valueOf(i + 1);
		}
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(data.length);
		numberPicker.setDisplayedValues(data);
		numberPicker.setValue(downloadLimit);
		numberPicker.setWrapSelectorWheel(true);
	}

	private void updateHeightView(final View contentView, CoordinatorLayout.Behavior behavior) {
		if (behavior != null && behavior instanceof BottomSheetBehavior) {
			mBottomSheetBehavior = (BottomSheetBehavior) behavior;
			mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
			contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					int height = contentView.getMeasuredHeight();
					mBottomSheetBehavior.setPeekHeight(height);
				}
			});
		}
	}
}
