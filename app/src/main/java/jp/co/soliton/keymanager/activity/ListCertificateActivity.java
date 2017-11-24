package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterListCertificate;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class ListCertificateActivity extends Activity {

    private ListView list;
    private AdapterListCertificate adapterListCertificate;
    private ElementApplyManager elementMgr;
    private List<ElementApply> listCertificate;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_certificate);
        title = (TextView) findViewById(R.id.tvTitleHeader);
        title.setText(getString(R.string.title_list_certificate));
        list = (ListView)findViewById(R.id.listConfirm);
        list.setSelector(android.R.color.transparent);
        elementMgr = ElementApplyManager.getInstance(getApplicationContext());
    }

    public void btnBackClick(View v) {
        finish();
    }

    public void btnNewClick(View v) {
        InputApplyInfo.deletePref(ListCertificateActivity.this);
        Intent intent = new Intent(ListCertificateActivity.this, ViewPagerInputActivity.class);
        startActivity(intent);
    }

    /**
     * Update List Certificate
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!elementMgr.hasCertificate()) {
            finish();
        }
        listCertificate = elementMgr.getAllCertificate();
        adapterListCertificate = new AdapterListCertificate(this, listCertificate);
        list.setAdapter(adapterListCertificate);
    }
}
