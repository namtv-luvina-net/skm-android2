package jp.co.soliton.keymanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 4/5/2017.
 */

public class DialogMenuCertDetail extends Dialog {

    private Button btnCancel;
    private Button btnNotification;
    private Button btnDelete;

    public DialogMenuCertDetail(Context context) {

        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_menu_cert_detail);

        btnCancel = findViewById(R.id.btnDlgCertDetailCancel);
        btnDelete = findViewById(R.id.btnDlgCertDetailDel);
        btnNotification = findViewById(R.id.btnDlgCertDetailNotif);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
    }

    public void setOnDeleteCert(View.OnClickListener listener) {
        btnDelete.setOnClickListener(listener);
    }

    public void setOnNotificationSetting(View.OnClickListener listener) {
        btnNotification.setOnClickListener(listener);
    }

}
