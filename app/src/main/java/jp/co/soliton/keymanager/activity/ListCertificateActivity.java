package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterListCertificate;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

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
        title.setText(getString(R.string.list_application));
        list = (ListView)findViewById(R.id.listConfirm);
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
        listCertificate = elementMgr.getAllElementApply();
        if(listCertificate.size() == 1) {
            Intent intent = new Intent(ListCertificateActivity.this, DetailConfirmActivity.class);
            intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listCertificate.get(0).getId()));
            finish();
            startActivity(intent);
        } else if(listCertificate.size() == 0) {
            finish();
        }
        adapterListCertificate = new AdapterListCertificate(this, listCertificate);
        list.setAdapter(adapterListCertificate);

    }
}
