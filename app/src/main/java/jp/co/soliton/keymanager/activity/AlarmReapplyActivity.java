package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;

/**
 * Created by vinhlx on 2/16/2017.
 */

public class AlarmReapplyActivity extends Activity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_reapply);
        id = getIntent().getStringExtra(StringList.ELEMENT_APPLY_ID);
    }

    public void clickStart(View v) {
        InputApplyInfo.deletePref(this);
        Intent intent = new Intent(this, ViewPagerUpdateActivity.class);
        intent.putExtra(StringList.ELEMENT_APPLY_ID, id);
        this.startActivity(intent);
    }

}
