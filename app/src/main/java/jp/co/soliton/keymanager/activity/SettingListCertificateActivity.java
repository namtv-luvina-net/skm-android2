package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterSettingListCertificate;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by luongdolong on 4/3/2017.
 */

public class SettingListCertificateActivity extends Activity {
    private ListView list;
    private AdapterSettingListCertificate adapterListCertificate;
    private ElementApplyManager elementMgr;
    private List<ElementApply> listCertificate;
    private TextView textViewBack;
    private TextView title;
    private TextView tvNoCertInstalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list_certificate);
        title = (TextView) findViewById(R.id.tvTitleHeader);
	    title.setText(getString(R.string.list_cert));
	    textViewBack = (TextView) findViewById(R.id.textViewBack);
	    textViewBack.setText(getString(R.string.label_setting));
	    tvNoCertInstalled = (TextView) findViewById(R.id.tvNoCertInstalled);
        list = (ListView)findViewById(R.id.listSettingCert);
        elementMgr = new ElementApplyManager(getApplicationContext());
	    listCertificate = elementMgr.getAllCertificate();
	    adapterListCertificate = new AdapterSettingListCertificate(this, listCertificate, false);
	    list.setAdapter(adapterListCertificate);
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
	    adapterListCertificate.setListElementApply(listCertificate);
	    adapterListCertificate.notifyDataSetChanged();
    }
}
