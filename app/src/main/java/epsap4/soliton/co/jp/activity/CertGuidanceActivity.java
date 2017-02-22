package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;

public class CertGuidanceActivity extends Activity implements View.OnClickListener{

	private Button m_ButtonGuideNext;
	private Button m_ButtonGuideBack;
	private TextView m_TextGuide;
	private ImageView m_ImageGuide;
	private Button m_ButtonOpenCert;
	
	int m_nPageCount = 4;			// WiFiプロパティの総数
    int m_nPageCurrentNum = 0;		// 現在表示中のWiFiプロパティの番号
    String[] m_StrListComment = {"証明書取得ページで<開始>ボタンをクリックして証明書インストールを開始します。",
    		"CA証明書の画面では証明書名を変更せずに<OK>をクリックしてください。",
    		"ユーザー証明書の画面でも証明書名を変更せずに<OK>をクリックしてください。",
    		"インストール終了後、「設定」でWi-Fiなどの設定を行って下さい。"};
    
    int[] m_nListComment = {R.string.guidance_msg01,
    		R.string.guidance_msg02,
    		R.string.guidance_msg03,
    		R.string.guidance_msg04
    };
    int[] m_Image = {0, R.drawable.ca_cert, R.drawable.user_cert, 0};
    
    private static InformCtrl m_InformCtrl;
    
	public CertGuidanceActivity() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	this.setTitle(R.string.ApplicationTitle);
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.cert_gundance);
    	
    	// 情報管理クラスの取得
    	Intent intent = getIntent();
    	m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
    	
    	setUItoMember();

    	
    }
	
	private void setUItoMember() {
		m_ButtonGuideNext = (Button)findViewById(R.id.Button_GuideNext);
		m_ButtonGuideNext.setOnClickListener(this);
		m_ButtonGuideBack = (Button)findViewById(R.id.Button_GuideBack);
		m_ButtonGuideBack.setOnClickListener(this);
		
		m_TextGuide = (TextView)findViewById(R.id.certguidance_comment);
		
		m_ImageGuide = (ImageView)findViewById(R.id.imageView_cert);
	    	
		m_ButtonOpenCert =  (Button)findViewById(R.id.button_open_cert);
		m_ButtonOpenCert.setOnClickListener(this);


		SetPageScreen(m_nPageCurrentNum);
		updateUi();
   }

	// UIのグレーアウト状況
	private void updateUi() {

		// Wi-Fiプロパティボタン
		if(m_nPageCurrentNum <= 0) {
			m_ButtonGuideBack.setEnabled(false);
		} else {
			m_ButtonGuideBack.setEnabled(true);
		}
		
		if(m_nPageCurrentNum >= m_nPageCount - 1) {
			m_ButtonGuideNext.setEnabled(false);
		} else {
			m_ButtonGuideNext.setEnabled(true);
		}
		   	

	}
		
	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		if(v == m_ButtonGuideNext) {
			m_nPageCurrentNum++;
		    if(m_nPageCurrentNum < m_nPageCount) {
		    	SetPageScreen(m_nPageCurrentNum);
		    } else m_nPageCurrentNum--;
		    updateUi();		
		} else if(v == m_ButtonGuideBack) {
			m_nPageCurrentNum--;
			if(m_nPageCurrentNum > -1) {
				SetPageScreen(m_nPageCurrentNum);
		    } else m_nPageCurrentNum++;
			updateUi();
		} else if(v == m_ButtonOpenCert) {
		//	Intent AppIntent = new Intent(this, CertLoginActivity.class);
		//	AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
		//	startActivityForResult(AppIntent, 0);
			
		//	setResult(StringList.RESULT_GUIDE_CLOSE);
			// 20140507
			// わざわざCertLoginActivityを開きなおす必要なしと判断したので、削除
			finish();
		}
	}
	
	private void SetPageScreen(int currentpage) {
		
		// コメント
		m_TextGuide.setText(m_nListComment[currentpage]/*m_StrListComment[currentpage]*/);
		
		// 画像
		if(m_Image[currentpage] == 0) {
			m_ImageGuide.setVisibility(View.GONE);
		} else {
			m_ImageGuide.setVisibility(View.VISIBLE);
			m_ImageGuide.setImageResource(m_Image[currentpage]);
		}
		
		// 証明書取得ボタン
		if(currentpage == (m_nPageCount - 1)) {
			m_ButtonOpenCert.setVisibility(View.VISIBLE);
		} else m_ButtonOpenCert.setVisibility(View.GONE);
	}
}
