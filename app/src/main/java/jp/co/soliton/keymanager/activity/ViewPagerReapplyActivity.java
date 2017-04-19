package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.adapter.ViewPagerReaaplyAdapter;
import jp.co.soliton.keymanager.common.DetectsSoftKeyboard;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.ReapplyBasePageFragment;
import jp.co.soliton.keymanager.swipelayout.InputApplyViewPager;

/**
 * Created by luongdolong on 2/3/2017.
 * Activity for input screen apply
 */

public class ViewPagerReapplyActivity extends FragmentActivity implements DetectsSoftKeyboard.DetectsListenner {
    public static int REQUEST_CODE_APPLY_COMPLETE = 4953;

    private InputApplyViewPager mViewPager;
    private ViewPagerReaaplyAdapter adapter;
    private ArrayList<Button> listButtonCircle = new ArrayList<>();
    private Button backButton;
    private Button nextButton;
    private InformCtrl m_InformCtrl;
    private InputApplyInfo inputApplyInfo;
    private ElementApplyManager elementMgr;
    private RelativeLayout groupCircle;
    public String idConfirmApply;
	boolean isShowingKeyboard = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_reapply);
        setUpView();
        setTab();
        inputApplyInfo = InputApplyInfo.getPref(this);
        m_InformCtrl = new InformCtrl();
        elementMgr = new ElementApplyManager(this);
        idConfirmApply = getIntent().getStringExtra(StringList.ELEMENT_APPLY_ID);

        if(!ValidateParams.nullOrEmpty(idConfirmApply)) {
            ElementApply detail = elementMgr.getElementApply(idConfirmApply);
            getInputApplyInfo().setHost(detail.getHost());
            getInputApplyInfo().setPort(detail.getPort());
            getInputApplyInfo().setSecurePort(detail.getPortSSL());
            if (detail.getTarger().startsWith("WIFI")) {
                getInputApplyInfo().setPlace(ReapplyBasePageFragment.TARGET_WiFi);
            } else {
                getInputApplyInfo().setPlace(ReapplyBasePageFragment.TARGET_VPN);
            }
            getInputApplyInfo().setUserId(detail.getUserId());
            getInputApplyInfo().savePref(this);
        } else {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setChangePage();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onBackPressed() {
        int current;
        current = mViewPager.getCurrentItem() - 1;
        if (current < 0) {
            InputApplyInfo.deletePref(ViewPagerReapplyActivity.this);
            finish();
        } else {
            mViewPager.setCurrentItem(current, true);
            btnCircleAction(current);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
	            hideKeyboard(this);
	            v.clearFocus();
	            ((ReapplyBasePageFragment) adapter.getItem(mViewPager.getCurrentItem())).clearFocusEditText();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    LogCtrl.getInstance(this).loggerInfo("ViewPagerReapplyActivity:onActivityResult requestCode = " + requestCode + ". " +
			    "resultCode = " + requestCode);
        if (requestCode == REQUEST_CODE_APPLY_COMPLETE && resultCode != Activity.RESULT_OK) {
            finish();
        }
    }

    /**
     * Init control common
     */
    private void setUpView(){
        mViewPager = (InputApplyViewPager) findViewById(R.id.viewPager);
        backButton = (Button) findViewById(R.id.btnInputBack);
        nextButton = (Button) findViewById(R.id.btnInputNext);
        groupCircle = (RelativeLayout) findViewById(R.id.groupCircle);
        adapter = new ViewPagerReaaplyAdapter(getApplicationContext(),getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.setCurrentItem(0);
        initButtonCircle();
	    DetectsSoftKeyboard.addListenner(findViewById(R.id.activityRoot), this);
    }

    /**
     * Action change tab page
     */
    private void setTab(){
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrollStateChanged(int position) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageSelected(int position) {
                btnCircleAction(position);
            }
        });
    }

    /**
     * Action next back page input
     */
    private void setChangePage() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current;
                current = mViewPager.getCurrentItem() - 1;
                if (current < 0) {
                    InputApplyInfo.deletePref(ViewPagerReapplyActivity.this);
                    finish();
                } else {
                    mViewPager.setCurrentItem(current, true);
                    btnCircleAction(current);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mViewPager.getCurrentItem();
                if (current < (adapter.getCount())) {
                    ((ReapplyBasePageFragment) adapter.getItem(current)).nextAction();
                }
            }
        });
    }

    /**
     * Set status mark page
     * @param action
     */
    private void btnCircleAction(int action){
        action+=3;
        Drawable bgCircle;
        for (int i = 0; i < listButtonCircle.size(); i ++) {
            if (i == action) {
                bgCircle = ContextCompat.getDrawable(this, R.drawable.rounded_cell_active);
                listButtonCircle.get(i).setBackgroundDrawable(bgCircle);
            } else {
                bgCircle = ContextCompat.getDrawable(this, R.drawable.rounded_cell_inactive);
                listButtonCircle.get(i).setBackgroundDrawable(bgCircle);
            }
        }
        if(action <= 3) {
            groupCircle.setVisibility(View.INVISIBLE);
        } else {
            groupCircle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Init button mark page
     */
    private void initButtonCircle(){
        listButtonCircle.add((Button)findViewById(R.id.btnOne));
        listButtonCircle.add((Button)findViewById(R.id.btnTwo));
        listButtonCircle.add((Button)findViewById(R.id.btnThree));
        listButtonCircle.add((Button)findViewById(R.id.btnFour));
        listButtonCircle.add((Button)findViewById(R.id.btnFive));
        listButtonCircle.add((Button)findViewById(R.id.btnSix));
    }

    /**
     * Set active status next back button
     * @param activeBack
     * @param activeNext
     */
    public void setActiveBackNext(boolean activeBack, boolean activeNext) {
        backButton.setEnabled(activeBack);
        nextButton.setEnabled(activeNext);
        if (activeBack) {
            backButton.setTextColor(getResources().getColor(R.color.text_color_active));
        } else {
            backButton.setTextColor(getResources().getColor(R.color.text_button_inactive));
        }
        if (activeNext) {
            nextButton.setTextColor(getResources().getColor(R.color.text_color_active));
        } else {
            nextButton.setTextColor(getResources().getColor(R.color.text_button_inactive));
        }
    }

    /**
     * Switch page to index page
     * @param pageIndex
     */
    public void gotoPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < adapter.getCount()) {
            mViewPager.setCurrentItem(pageIndex, true);
            btnCircleAction(pageIndex);
        }
    }

    /**
     * Go to confirm apply screen
     */
    public void gotoConfirmApply() {
        Intent intent = new Intent(ViewPagerReapplyActivity.this, ConfirmApplyActivity.class);
        // ビューのリストを新しいintentに引き渡す.HTTP通信もそちらで行う。
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        intent.putExtra(StringList.UPDATE_APPLY, idConfirmApply);
        startActivityForResult(intent, REQUEST_CODE_APPLY_COMPLETE);
    }

    /**
     * Get current page index
     * @return
     */
    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    public InputApplyInfo getInputApplyInfo() {
        return inputApplyInfo;
    }

    public InformCtrl getInformCtrl() {
        return m_InformCtrl;
    }

	@Override
	public void onSoftKeyboardShown(boolean isShowing) {
		if (!isShowing) {
			if (isShowingKeyboard) {
				((ReapplyBasePageFragment) adapter.getItem(mViewPager.getCurrentItem())).clearFocusEditText();
				isShowingKeyboard = false;
			}
		} else {
			isShowingKeyboard = true;
		}
	}
}
