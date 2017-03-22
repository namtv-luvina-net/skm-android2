package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

/**
 * Created by vinhlx on 2/16/2017.
 */

public class AlarmReapplyActivity extends Activity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_reapply);
        id = getIntent().getStringExtra(ViewPagerReapplyActivity.ELEMENT_APPLY_ID);
    }

    public void clickStart(View v) {
        InputApplyInfo.deletePref(this);
        Intent intent = new Intent(this, ViewPagerReapplyActivity.class);
        intent.putExtra(ViewPagerReapplyActivity.ELEMENT_APPLY_ID, id);
        this.startActivity(intent);
    }

}
