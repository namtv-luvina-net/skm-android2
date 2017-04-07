package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterSettingListCertificate;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by luongdolong on 4/3/2017.
 */

public class SettingListCertificateActivity extends Activity {
    private ListView list;
    private AdapterSettingListCertificate adapterListCertificate;
    private ElementApplyManager elementMgr;
    private List<ElementApply> listCertificate;
    private TextView title;
    private TextView tvNoCertInstalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list_certificate);
        title = (TextView) findViewById(R.id.tvTitleHeader);
        tvNoCertInstalled = (TextView) findViewById(R.id.tvNoCertInstalled);
        title.setText(getString(R.string.list_cert));
        list = (ListView)findViewById(R.id.listSettingCert);
        elementMgr = new ElementApplyManager(getApplicationContext());
    }

    public void btnBackClick(View v) {
        finish();
    }

    /**
     * Update List Certificate
     */
    @Override
    protected void onResume() {
        super.onResume();
        listCertificate = elementMgr.getAllCertificate();
        if (listCertificate == null || listCertificate.isEmpty()) {
            tvNoCertInstalled.setVisibility(View.VISIBLE);
        } else {
            tvNoCertInstalled.setVisibility(View.GONE);
        }
        adapterListCertificate = new AdapterSettingListCertificate(this, listCertificate);
        list.setAdapter(adapterListCertificate);
    }
}
