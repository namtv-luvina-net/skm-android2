package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;

/**
 * Created by vinhlx on 2/16/2017.
 */

public class APIDActivity extends Activity implements View.OnClickListener {

    private LinearLayout layoutShareAPID;
    private TextView tvVPNID;
    private TextView tvWIFIID;
	String strUDID = "";
	String strVpnID = "";
	private StringBuilder builderAPID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apid);
	    this.layoutShareAPID = (LinearLayout)findViewById(R.id.layoutShareAPID);
        layoutShareAPID.setOnClickListener(this);
		Bundle extras = getIntent().getExtras();
	    if (extras != null) {   
		    strVpnID = extras.getString("m_strAPIDVPN");
		    strUDID = extras.getString("m_strAPIDWifi");
	    }
	    builderAPID = new StringBuilder();
	    builderAPID.append(getResources().getString(R.string.main_apid_vpn) + "\n");
	    builderAPID.append(strVpnID +"\n\n");
	    builderAPID.append(getResources().getString(R.string.main_apid_wifi) + "\n");
	    builderAPID.append(strUDID);

        this.tvVPNID = (TextView)findViewById(R.id.tvVPNID);
        this.tvWIFIID = (TextView)findViewById(R.id.tvWIFIID);
        tvVPNID.setText(strVpnID);
        tvWIFIID.setText(strUDID);
    }


    private void setClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = android.content.ClipData.newPlainText("Text", builderAPID.toString());
        clipboard.setPrimaryClip(clip);

        LogCtrl.getInstance().info("APID: Copied");
    }

    private void sendMail() {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
        email.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.apid_subject_email).toString());
        email.putExtra(Intent.EXTRA_TEXT, builderAPID.toString());
        try {
            LogCtrl.getInstance().info("APID: Show email apps chooser");
            startActivity(Intent.createChooser(email, ""));
        } catch (android.content.ActivityNotFoundException ex) {
            LogCtrl.getInstance().warn("APID: No email apps");
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnBackClick(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        CharSequence menus[] = new CharSequence[] {getText(R.string.copy_apid).toString(), getText(R.string.send_apid).toString()};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.select_apid).toString());
        builder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setClipboard();
                } else {
                    sendMail();
                }
            }
        });
        builder.show();
    }
}
