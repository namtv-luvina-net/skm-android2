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
import jp.co.soliton.keymanager.adapter.AdapterListConfirmApply;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class ListConfirmActivity extends Activity {

    private ListView list;
    private AdapterListConfirmApply adapterListConfirmApply;
    private ElementApplyManager elementMgr;
    private List<ElementApply> listElementApply;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_confirm);
        title = (TextView) findViewById(R.id.tvTitleHeader);
        title.setText(getString(R.string.list_application));
        list = (ListView)findViewById(R.id.listConfirm);
        elementMgr = new ElementApplyManager(getApplicationContext());
    }

    public void btnBackClick(View v) {
        finish();
    }

    /**
     * Update List Certificate, Certificate delete screen DetailCertActivity
     */
    @Override
    protected void onResume() {
        super.onResume();
        listElementApply = elementMgr.getAllElementApply();
        if(listElementApply.size() == 1) {
            Intent intent = new Intent(ListConfirmActivity.this, DetailConfirmActivity.class);
            intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(0).getId()));
            finish();
            startActivity(intent);
        } else if(listElementApply.size() == 0) {
            finish();
        }
        adapterListConfirmApply = new AdapterListConfirmApply(this, listElementApply);
        list.setAdapter(adapterListConfirmApply);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListConfirmActivity.this, DetailConfirmActivity.class);
                intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(position).getId()));
                startActivity(intent);
            }
        });
    }
}
