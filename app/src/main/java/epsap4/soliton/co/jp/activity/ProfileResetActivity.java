package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.RestrictionsControl;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.shortcut.CreateShortcutLink;
import epsap4.soliton.co.jp.wifi.WifiControl;

////////////////////////////////
//Profileアクティビティ
////////////////////////////////
public class ProfileResetActivity extends Activity
	implements View.OnClickListener  {

	private Button m_ButtonDeleteProfile;
	private Button m_ButtonResetProfile;
	private static InformCtrl m_InformCtrl;
	
	// Debug用
	private Button m_ButtonDeleteData;
	
//	private String m_str_FilesDir = "/data/data/" + getPackageName() + "/files/";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.setTitle(R.string.ApplicationTitle);
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.reset);
    	
    	setUItoMember();
    	
    }
    
    private void setUItoMember() {
    	
    	// 情報管理クラスの取得
    	Intent intent = getIntent();
    	m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
		
    	
    	m_ButtonDeleteProfile = (Button)findViewById(R.id.Button_delete_pro);
    	m_ButtonDeleteProfile.setOnClickListener(this);
    	m_ButtonResetProfile = (Button)findViewById(R.id.Button_reset_pro);
    	m_ButtonResetProfile.setOnClickListener(this);
    	
 //   	m_ButtonDeleteData = (Button)findViewById(R.id.Button_date_delete);
 //   	m_ButtonDeleteData.setOnClickListener(this);
    		
    }
	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Log.i("ProfileResetActivity::onClick", "start");
		
		// TODO 自動生成されたメソッド・スタブ
		if(v == m_ButtonDeleteProfile) {
			DeleteProfile();
			// 機能制限の削除 ===>
			
		} else if (v == m_ButtonResetProfile) {
			Intent AppIntent = new Intent(this, ProfileListActivity.class);
			
			AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
			
			startActivityForResult(AppIntent, 0);
		} else if (v == m_ButtonDeleteData) {
			Cursor cursor = null;
			ContentResolver cr = getContentResolver();
			String filename = "/sdcard/Download/" + "07c3d5d1.jpg";	// 正しいファイル名を指定しよう
			
			cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new String[] {MediaStore.Images.Media._ID},
					MediaStore.Images.Media.DATA + " = ?",
					new String[]{filename},
					null); 
			
			 if(cursor.getCount() != 0) { 
			cursor.moveToFirst(); 
			 Uri uri = ContentUris.appendId(
					 MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon(),
					 cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))).build() ;
					 cr.delete(uri, null, null); 
			 } 
			
			 cr.delete(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					 7), null, null); 
	//		cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
	//				MediaStore.Images.Media.DATA + "=?", new String[]{filename}); 
			
		//	File filename_test = new File(filename);
		//	if(filename_test.exists()) {
		//		Log.i("ProfileResetActivity::onClick", "Filename = " + filename + "あるよ");
		//		filename_test.delete();
		//	}
		}
	}
	
	public void DeleteProfile() {
		String filedir = "/data/data/" + getPackageName() + "/files/";
		
		//<=== ショートカットの削除
		File filename = new File(filedir + StringList.m_strShortcutOutputFile);
		Log.i("ProfileResetActivity::onClick", "Filename = " + filename);
		
		if(filename.exists()) {
			CreateShortcutLink c_link = new CreateShortcutLink(this);
			c_link.ReadAndSetShortcutInfo();
			c_link.RemoveRun();
			filename.delete();
		}
		// ショートカットの削除 ===>
		
		//<=== Wifiの削除
		// Wi-Fi
		File filename2 = new File(filedir + StringList.m_strWifiOutputFile);
		
		if(filename2.exists()) {
			WifiControl wifi = new WifiControl(this);
			wifi.ReadAndSetWifiInfo(StringList.m_strWifiOutputFile);
			wifi.deleteConfig();
			filename2.delete();
		}
		// Wifiの削除 ===>
		
		//<=== 機能制限の削除
		File filename3 = new File(filedir + StringList.m_strRestrictionFileName);
		if(filename3.exists()) {
			RestrictionsControl m_resriction = new RestrictionsControl(this);	// この時点でサービスを止める
			
			filename3.delete();
			
		}
	}
	
	// 子Activityからアプリを終了する方法
	// 参照:http://ymgcsng.blogspot.jp/2010/12/activity.html
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == StringList.RESULT_CLOSE) {
	        setResult(StringList.RESULT_CLOSE);
	        finish();
	    }
	}
	
}