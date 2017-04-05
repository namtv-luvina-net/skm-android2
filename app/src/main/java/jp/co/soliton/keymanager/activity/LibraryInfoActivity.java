package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class LibraryInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void btnBackClick(View v) {
        finish();
    }
}
