package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.ViewPagerTabletAdapter;
import jp.co.soliton.keymanager.common.ControlPagesInput;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.swipelayout.InputApplyViewPager;

/**
 * Created by nguyenducdat on 5/4/2017.
 */

public abstract class TabletAbtractInputFragment extends Fragment implements SoftKeyboardCtrl.DetectsListenner{

	protected InputApplyInfo inputApplyInfo;
	protected Activity activity;
	protected int sdk_int_version;
	protected Button btnSkip;
	protected Button btnNext;
	protected Button btnBack;
	protected InputApplyViewPager viewPager;
	protected ViewPagerTabletAdapter adapter;
	protected InformCtrl m_InformCtrl;
	protected int m_nErroType;
	protected DialogApplyProgressBar progressDialog;
	protected ControlPagesInput controlPagesInput;
	protected String hostName;
	protected String portName;
	protected boolean isShowingKeyboard = false;
	protected String idConfirmApply;
	private View viewFragment;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = (Activity) context;
	}

	public DialogApplyProgressBar getProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new DialogApplyProgressBar(getActivity());
		}
		return progressDialog;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_base_input_tablet, container, false);
		sdk_int_version = Build.VERSION.SDK_INT;
		viewFragment.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return SoftKeyboardCtrl.hideKeyboardIfTouchOutEditText(getActivity(), event);
			}
		});
		SoftKeyboardCtrl.addListenner(viewFragment, this);
		return viewFragment;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		btnSkip = (Button) view.findViewById(R.id.btnSkip);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		viewPager = (InputApplyViewPager) view.findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter);
		viewPager.setPagingEnabled(false);
		viewPager.setCurrentItem(0);
		setTab();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputApplyInfo = InputApplyInfo.getPref(activity);
		m_InformCtrl = new InformCtrl();
		controlPagesInput = new ControlPagesInput(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		setChangePage();
		setStatusBackNext(getCurrentPage());
	}

	private void setChangePage() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickButtonBack();
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickButtonNext();
			}
		});

		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickButtonSkip();
			}
		});
	}

	/**
	 * Show message
	 *
	 * @param message
	 */
	protected void showMessage(String message) {
		DialogMessageTablet dlgMessage = new DialogMessageTablet(getContext(), message);
		dlgMessage.show();
	}

	/**
	 * Show message
	 *
	 * @param message
	 */
	protected void showMessage(String message, DialogMessageTablet.OnOkDismissMessageListener listener) {
		DialogMessageTablet dlgMessage = new DialogMessageTablet(getContext(), message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.show();
	}

	/**
	 * Action change tab page
	 */
	private void setTab(){
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int state) {}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageSelected(int position) {
				updateButtonFooterStatus(position);
				updateLeftSide();
			}
		});
		updateButtonFooterStatus(getCurrentPage());
		updateLeftSide();
	}

	/**
	 * Switch page to index page
	 * @param positionPage
	 */
	public void gotoPage(final int positionPage) {
		viewPager.postDelayed(new Runnable() {

			@Override
			public void run() {
				int pageIndex = positionPage;
				goneSkip();
				if (sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2 && pageIndex == 2){
					pageIndex++;
				}
				if (pageIndex >= 0 && pageIndex < adapter.getCount()) {
					viewPager.setCurrentItem(pageIndex, true);
					setStatusBackNext(pageIndex);
				}
			}
		}, 100);
	}

	public abstract void gotoNextPage();

	/**
	 * Get current page index
	 * @return
	 */
	public int getCurrentPage() {
		return viewPager.getCurrentItem();
	}

	public InformCtrl getInformCtrl() {
		return m_InformCtrl;
	}

	public void setInformCtrl(InformCtrl m_InformCtrl) {
		this.m_InformCtrl = m_InformCtrl;
	}

	public int getErroType() {
		return m_nErroType;
	}

	public void setErroType(int m_nErroType) {
		this.m_nErroType = m_nErroType;
	}

	public InputApplyInfo getInputApplyInfo() {
		return inputApplyInfo;
	}

	public void gotoCompleteApply() {
		((MenuAcivity)getActivity()).goApplyCompleted();
	}

	public void gotoCompleteApply(InformCtrl m_InformCtrl, ElementApply element) {
		((MenuAcivity)getActivity()).goApplyCompleted(m_InformCtrl, element);
	}

	public void gotoCompleteConfirmApplyFragment(int status, ElementApply element, InformCtrl m_InformCtrl){
		((MenuAcivity)getActivity()).gotoCompleteConfirmApplyFragment(status, element, m_InformCtrl);
	}

	protected abstract void updateLeftSide();
	protected abstract void updateButtonFooterStatus(int currentPage);
	public abstract void hideInputPort(boolean isHide);
	public abstract void setStatusBackNext(int current);
	public abstract void clickButtonSkip();
	public abstract void clickButtonBack();
	public abstract void clickButtonNext();

	public void goneSkip() {
		btnSkip.setVisibility(View.GONE);
	}
	public void visibleSkip() {
		btnSkip.setVisibility(View.VISIBLE);
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getHostName() {
		return hostName;
	}
	public String getPortName() {
		return portName;
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

	@Override
	public void onSoftKeyboardShown(boolean isShowing) {
		if (!isShowing) {
			if (isShowingKeyboard) {
				View v = activity.getCurrentFocus();
				if (v != null && v instanceof EditText) {
					v.clearFocus();
				}
				isShowingKeyboard = false;
			}
		} else {
			isShowingKeyboard = true;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
