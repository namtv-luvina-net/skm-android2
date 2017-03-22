package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.adapter.ViewPagerReaaplyAdapter;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.ReapplyBasePageFragment;
import jp.co.soliton.keymanager.swipelayout.InputApplyViewPager;

/**
 * Created by luongdolong on 2/3/2017.
 * Activity for input screen apply
 */

public class ViewPagerReapplyActivity extends FragmentActivity {
    public static int REQUEST_CODE_APPLY_COMPLETE = 4953;
    public static String ELEMENT_APPLY_ID = "ELEMENT_APPLY_ID";

    private InputApplyViewPager mViewPager;
    private ViewPagerReaaplyAdapter adapter;
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
        setContentView(R.layout.activity_pager_reapply);
        setUpView();
        setTab();
        inputApplyInfo = InputApplyInfo.getPref(this);
        m_InformCtrl = new InformCtrl();
        elementMgr = new ElementApplyManager(this);
        String idConfirmApply = getIntent().getStringExtra(ViewPagerReapplyActivity.ELEMENT_APPLY_ID);

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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setChangePage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        adapter = new ViewPagerReaaplyAdapter(getApplicationContext(),getSupportFragmentManager());
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
        System.out.println(adapter.getCount());
        if (pageIndex >= 0 && pageIndex < adapter.getCount()) {
            mViewPager.setCurrentItem(pageIndex, true);
            btnCircleAction(pageIndex + 3);
        }
    }

    /**
     * Go to confirm apply screen
     */
    public void gotoConfirmApply() {
        Intent intent = new Intent(ViewPagerReapplyActivity.this, ConfirmApplyActivity.class);
        // ビューのリストを新しいintentに引き渡す.HTTP通信もそちらで行う。
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
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
}
