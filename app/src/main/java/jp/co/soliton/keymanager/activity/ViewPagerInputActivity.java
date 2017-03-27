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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.adapter.ViewPagerAdapter;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import jp.co.soliton.keymanager.swipelayout.InputApplyViewPager;

/**
 * Created by luongdolong on 2/3/2017.
 * Activity for input screen apply
 */

public class ViewPagerInputActivity extends FragmentActivity {
    public static int REQUEST_CODE_APPLY_COMPLETE        = 4953;
    public static int REQUEST_CODE_INSTALL_CERTIFICATION = 4954;

    private InputApplyViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private ArrayList<Button> listButtonCircle = new ArrayList<>();
    private Button backButton;
    private Button nextButton;
    private InformCtrl m_InformCtrl;
    private InputApplyInfo inputApplyInfo;
    private ElementApplyManager elementMgr;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_input);
        setUpView();
        setTab();
        inputApplyInfo = InputApplyInfo.getPref(this);
        m_InformCtrl = new InformCtrl();
        elementMgr = new ElementApplyManager(this);
        String idConfirmApply = getIntent().getStringExtra("ELEMENT_APPLY_ID");

        if(!ValidateParams.nullOrEmpty(idConfirmApply)) {
            ElementApply detail = elementMgr.getElementApply(idConfirmApply);
            getInputApplyInfo().setHost(detail.getHost());
            getInputApplyInfo().setPort(detail.getPort());
            getInputApplyInfo().setSecurePort(detail.getPortSSL());
            if (detail.getTarger().startsWith("WIFI")) {
                getInputApplyInfo().setPlace(InputBasePageFragment.TARGET_WiFi);
            } else {
                getInputApplyInfo().setPlace(InputBasePageFragment.TARGET_VPN);
            }
            getInputApplyInfo().setUserId(detail.getUserId());
            gotoPage(3);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setChangePage();
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

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
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
    public void onBackPressed() {
        int current;
        if (mViewPager.getCurrentItem() == 2) {
            hideInputPort(true);
            current = mViewPager.getCurrentItem() - 2;
        } else {
            current = mViewPager.getCurrentItem() - 1;
        }
        if (current < 0) {
            InputApplyInfo.deletePref(ViewPagerInputActivity.this);
            finish();
        } else {
            mViewPager.setCurrentItem(current, true);
            btnCircleAction(current);
        }
        setStatusBackNext(current);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_APPLY_COMPLETE && resultCode != Activity.RESULT_OK) {
            finish();
        } else if (requestCode == REQUEST_CODE_INSTALL_CERTIFICATION) {
            ((InputPortPageFragment)adapter.getItem(mViewPager.getCurrentItem())).finishInstallCertificate(resultCode);
        }
    }

    /**
     * Init control common
     */
    private void setUpView(){
        mViewPager = (InputApplyViewPager) findViewById(R.id.viewPager);
        backButton = (Button) findViewById(R.id.btnInputBack);
        nextButton = (Button) findViewById(R.id.btnInputNext);
        adapter = new ViewPagerAdapter(getApplicationContext(),getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.setCurrentItem(0);
        initButtonCircle();
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
                if (mViewPager.getCurrentItem() == 2) {
                    hideInputPort(true);
                    current = mViewPager.getCurrentItem() - 2;
                } else {
                    current = mViewPager.getCurrentItem() - 1;
                }
                if (current < 0) {
                    InputApplyInfo.deletePref(ViewPagerInputActivity.this);
                    finish();
                } else {
                    mViewPager.setCurrentItem(current, true);
                    btnCircleAction(current);
                }
                setStatusBackNext(current);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mViewPager.getCurrentItem();
                if (current < (adapter.getCount())) {
                    ((InputBasePageFragment) adapter.getItem(current)).nextAction();
                }
            }
        });
    }

    /**
     * Set status next back button
     * @param current
     */
    public void setStatusBackNext(int current) {
        nextButton.setVisibility(current == 2 ? View.INVISIBLE : View.VISIBLE);
        if (current == 1) {
            nextButton.setText(R.string.download);
        } else {
            nextButton.setText(R.string.next);
        }
    }

    /**
     * Set status mark page
     * @param action
     */
    private void btnCircleAction(int action){
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
            setStatusBackNext(pageIndex);
        }
    }

    /**
     * Go to confirm apply screen
     */
    public void gotoConfirmApply() {
        Intent intent = new Intent(ViewPagerInputActivity.this, ConfirmApplyActivity.class);
        // ビューのリストを新しいintentに引き渡す.HTTP通信もそちらで行う。
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        startActivityForResult(intent, REQUEST_CODE_APPLY_COMPLETE);
    }

    public void hideInputPort(boolean hide) {
        ((InputPortPageFragment) adapter.getItem(1)).hideScreen(hide);
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
}
