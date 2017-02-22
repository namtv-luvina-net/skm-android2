package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.SessionInfo;
//import android.util.Log;


/////////////////////////////////////////////
// Restrictionブロックが発動したときに
// 表示されるアクティビティ
/////////////////////////////////////////////
public class BlockActivity extends Activity {
	private boolean unlock = false;
	private boolean unlockBack = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.setTitle(R.string.ApplicationTitle);
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.blockmsg);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    //	EditText passEdit = (EditText)findViewById(R.id.pass);
    //	passEdit.setText("");
    	
    	SessionInfo session = SessionInfo.getInstance();
    	if (session.isBlock()) {
    		Toast.makeText(this, "Block.", Toast.LENGTH_SHORT).show();
    		if (!session.getExtraType().equals("TYPE_URI")) {
    			unlockBack = true;
    		}
    	}
    /*	else {
    		Intent main = new Intent(this, EPS_ap300Activity.class);
			startActivity(main);
    	}*/
    }
    
    @Override
    public void onPause() {
    	Log.i("MDM Client BlockActivity", "onPause");
    	super.onPause();
    	finish();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if ((unlock) && (unlockBack)) {
    			unlock = false;	
    		}
    		else {
    			return false;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    public void onClick_unlock(View view) {
    /*	EditText passEdit = (EditText)findViewById(R.id.pass);
    	String pass;
    	
    	// get text
    	pass = passEdit.getText().toString();	
    	if (pass.equals("5555") == true) {
    		Toast.makeText(this, "Unlock success.", Toast.LENGTH_SHORT).show();
    		unLock();
    	}
    	else {
    		Toast.makeText(this, "Unlock failed.", Toast.LENGTH_SHORT).show();
    		passEdit.setText("");
    	}*/
    }
    
    private void unLock() {
    	SessionInfo session = SessionInfo.getInstance();
    	session.setBlock(false);
    	unlock = true;
    	if (unlockBack) {
    		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        	dispatchKeyEvent(event);
    	}
    	else {
	    	// start interrupt activity!
			Intent Views = new Intent();
			Views.setAction("local.soliton.mdm.appcontrol.unlock.ViewAction.VIEW");
			sendBroadcast(Views);
    	}
    }
}