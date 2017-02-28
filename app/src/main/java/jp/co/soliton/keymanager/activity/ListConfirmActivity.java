package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterListConfirmApply;
import jp.co.soliton.keymanager.dbalias.DatabaseHandler;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class ListConfirmActivity extends Activity {

    private ListView list;
    private AdapterListConfirmApply adapterListConfirmApply;
    private DatabaseHandler databaseHandler;
    private List<ElementApply> listElementApply;
    private TextView textViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_confirm);
        list = (ListView)findViewById(R.id.listConfirm);
        databaseHandler = new DatabaseHandler(getApplicationContext());
        listElementApply = databaseHandler.getAllElementApply();
        adapterListConfirmApply = new AdapterListConfirmApply(this, listElementApply);
        list.setAdapter(adapterListConfirmApply);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        textViewBack = (TextView)findViewById(R.id.textViewBack);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Update List Certificate, Certificate delete screen DetailCertActivity
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
}
