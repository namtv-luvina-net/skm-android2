package jp.co.soliton.keymanager.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.ContentCompleteUsingProceduresFragment;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class CompleteUsingProceduresActivity extends FragmentActivity {
    private ElementApply elementApply;
	FragmentManager fragmentManager;
	Fragment fragmentContent;
	boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_using_procedures);
	    fragmentManager = getSupportFragmentManager();
	    setOrientation();
        Intent intent = getIntent();
        elementApply = (ElementApply)intent.getSerializableExtra("ELEMENT_APPLY");
    }

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (!isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			fragmentContent= new ContentCompleteUsingProceduresFragment();
			fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
			fragmentTransaction.commit();
		}
	}

	public ElementApply getElementApply() {
		return elementApply;
	}

    @Override
    public void onBackPressed() {
        backToTop(null);
    }

    public void backToTop(View v) {
	    final Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    if (isTablet) {
		    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		    fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
		    fragmentTransaction.remove(fragmentContent);
		    fragmentTransaction.commit();
		    final Handler handler = new Handler();
		    handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
				    startActivity(intent);
				    overridePendingTransition(0, 0);
			    }
		    }, getResources().getInteger(android.R.integer.config_shortAnimTime));
	    } else {
		    startActivity(intent);
	    }
    }
}
