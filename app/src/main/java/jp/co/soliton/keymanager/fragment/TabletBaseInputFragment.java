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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.ViewPagerTabletAdapter;
import jp.co.soliton.keymanager.common.ControlPagesInput;
import jp.co.soliton.keymanager.common.DetectsSoftKeyboard;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.swipelayout.InputApplyViewPager;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by nguyenducdat on 5/4/2017.
 */

public class TabletBaseInputFragment extends Fragment implements DetectsSoftKeyboard.DetectsListenner{
	public final static int ERR_FORBIDDEN    = 20;
	public final static int ERR_UNAUTHORIZED = 21;
	public final static int SUCCESSFUL       = 22;
	public final static int ERR_NETWORK      = 23;
	public final static int ERR_COLON        = 24;
	public final static int NOT_INSTALL_CA   = 25;
	public final static int ERR_LOGIN_FAIL = 27;

	public final static String TARGET_VPN  = "0";
	public final static String TARGET_WiFi = "1";

	InputApplyViewPager viewPager;
	ViewPagerTabletAdapter adapter;
	private Button btnSkip;
	private Button btnNext;
	private Button btnBack;
	private InformCtrl m_InformCtrl;
	private InputApplyInfo inputApplyInfo;
	private ElementApplyManager elementMgr;
	private int m_nErroType;
	public int sdk_int_version;
	protected DialogApplyProgressBar progressDialog;
	protected ControlPagesInput controlPagesInput;
	private String hostName;
	private String portName;
	private boolean isShowingKeyboard = false;
	private Activity activity;

	public static Fragment newInstance() {
		TabletBaseInputFragment f = new TabletBaseInputFragment();
		return f;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = (Activity) context;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.adapter = new ViewPagerTabletAdapter(activity, getChildFragmentManager(), this);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		btnSkip = (Button) view.findViewById(R.id.btnSkip);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		viewPager = (InputApplyViewPager) view.findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter);
		viewPager.setPagingEnabled(false);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setCurrentItem(0);
		setTab();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_base_input_tablet, container, false);
		sdk_int_version = Build.VERSION.SDK_INT;
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return dispatchTouchEvent(v, event);
			}
		});
		DetectsSoftKeyboard.addListenner(view, this);
		return view;
	}

	public boolean dispatchTouchEvent(View view, MotionEvent ev) {
		View v = activity.getCurrentFocus();
		if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
				v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
			int scrcoords[] = new int[2];
			v.getLocationOnScreen(scrcoords);
			float x = ev.getRawX() + v.getLeft() - scrcoords[0];
			float y = ev.getRawY() + v.getTop() - scrcoords[1];

			if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
				hideKeyboard(activity);
				v.clearFocus();
			}
		}
		return true;
	}

	private void hideKeyboard(Activity activity) {
		if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputApplyInfo = InputApplyInfo.getPref(activity);
		m_InformCtrl = new InformCtrl();
		elementMgr = new ElementApplyManager(activity);
		controlPagesInput = new ControlPagesInput(activity);
	}

	public void finishInstallCertificate(int resultCode) {
		((TabletInputPortFragment)adapter.getItem(viewPager.getCurrentItem())).finishInstallCertificate(resultCode);
	}

	public void gotoCompleteApply() {
		((MenuAcivity)getActivity()).goApplyCompleted();
	}

	public void gotoCompleteApply(InformCtrl m_InformCtrl, ElementApply element) {
		((MenuAcivity)getActivity()).goApplyCompleted(m_InformCtrl, element);
	}

	/**
	 * Get current page index
	 * @return
	 */
	public int getCurrentPage() {
		return viewPager.getCurrentItem();
	}

	public InputApplyInfo getInputApplyInfo() {
		return inputApplyInfo;
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

	public void updateLeftSide() {
		((MenuAcivity)getActivity()).updateLeftSideInput(getCurrentPage());
	}

	private void updateButtonFooterStatus(int position) {
		btnBack.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
		btnNext.setVisibility(position == 2 ? View.INVISIBLE : View.VISIBLE);
		if (position != 4 && position != 5) {
			btnSkip.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setChangePage();
		setStatusBackNext(getCurrentPage());
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

	public void clickBackButton(){
		btnBack.performClick();
	}
	/**
	 * Action next back page input
	 */

	private void setChangePage() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int current;
				if (sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2 && viewPager.getCurrentItem() == 3){
					viewPager.setCurrentItem(2, true);
				}
				if (viewPager.getCurrentItem() == 2) {
					hideInputPort(true);
					current = viewPager.getCurrentItem() - 2;
				} else {
					current = viewPager.getCurrentItem() - 1;
				}
				if (current < 0) {
					InputApplyInfo.deletePref(getActivity());
					((MenuAcivity)getActivity()).gotoMenu();
				} else {
					viewPager.setCurrentItem(current, true);
				}
				setStatusBackNext(current);
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int current = viewPager.getCurrentItem();
				if (current < adapter.getCount()) {
					((TabletInputFragment)adapter.getItem(current)).nextAction();
				}
			}
		});

		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TabletInputFragment)adapter.getItem(viewPager.getCurrentItem())).clickSkipButton();
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
	 * Set status next back button
	 * @param current
	 */
	public void setStatusBackNext(int current) {
		if (current == 1) {
			btnNext.setText(R.string.download);
		} else if (current == 6) {
			btnNext.setText(R.string.apply);
		} else {
			btnNext.setText(R.string.next);
		}
	}

	public void hideInputPort(boolean hide) {
		((TabletInputPortFragment) adapter.getItem(1)).hideScreen(hide);
	}

	public void goneSkip() {
		btnSkip.setVisibility(View.GONE);
	}
	public void invisibleSkip() {
		btnSkip.setVisibility(View.INVISIBLE);
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
}
