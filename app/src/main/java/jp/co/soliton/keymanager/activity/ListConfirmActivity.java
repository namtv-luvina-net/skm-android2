package jp.co.soliton.keymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.adapter.AdapterListConfirmApply;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class ListConfirmActivity extends FragmentActivity {

    private ElementApplyManager elementMgr;
	private ListView list;
	private AdapterListConfirmApply adapterListConfirmApply;
	private List<ElementApply> listElementApply;
	private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_confirm);
	    title = (TextView) findViewById(R.id.tvTitleHeader);
	    title.setText(getString(R.string.list_application));
	    list = (ListView) findViewById(R.id.listConfirm);
	    adapterListConfirmApply = new AdapterListConfirmApply(this, listElementApply);
	    list.setAdapter(adapterListConfirmApply);
	    elementMgr = new ElementApplyManager(getApplicationContext());
    }

	@Override
	public void onBackPressed() {
		btnBackClick(null);
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
	    ElementApply.sortListConfirmApply(listElementApply);
	    int sizeList = listElementApply.size();
	    if(sizeList == 1) {
            Intent intent = new Intent(ListConfirmActivity.this, DetailConfirmActivity.class);
            intent.putExtra(StringList.ELEMENT_APPLY_ID, String.valueOf(listElementApply.get(0).getId()));
            finish();
            startActivity(intent);
        } else if(sizeList == 0) {
            finish();
        }
		adapterListConfirmApply.setListElementApply(listElementApply);
	    adapterListConfirmApply.notifyDataSetChanged();
	    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    Intent intent = new Intent(ListConfirmActivity.this, DetailConfirmActivity.class);
		    intent.putExtra(StringList.ELEMENT_APPLY_ID, String.valueOf(listElementApply.get(position).getId()));
		    startActivity(intent);
	    }
    });
    }
}
