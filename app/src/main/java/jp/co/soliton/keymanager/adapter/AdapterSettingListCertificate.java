package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.activity.SettingDetailCertificateActivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by lexuanvinh on 03/04/2017.
 */

public class AdapterSettingListCertificate extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listElementApply;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView txtStatus;
        public TextView txtName;
        public ImageView icCertificate;
        public LinearLayout llSettingCert;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listElementApply
     */
    public AdapterSettingListCertificate(Context context, List<ElementApply> listElementApply) {
        super(context, 0);
        this.listElementApply = listElementApply;
    }

    /**
     * This method get size listElementApply
     *
     * @return
     */
    @Override
    public int getCount() {
        if (listElementApply != null) {
            return listElementApply.size();
        } else {
            return 0;
        }
    }

    public ElementApply getItem(int position) {
        return listElementApply.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * This method View item at position
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting_certificate, parent, false);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusCert);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtNameCert);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icSettingCertificate);
            viewHolder.llSettingCert = (LinearLayout) convertView.findViewById(R.id.llSettingCert);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            Date expirationDate = formatter.parse(listElementApply.get(position).getExpirationDate());
            Date date = new Date();

            //Comparing dates
            long difference = expirationDate.getTime() - date.getTime();
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            if (difference > 0){
                differenceDates++;
            }

            if (differenceDates > 0 && differenceDates <= listElementApply.get(position).getNotiEnableBefore()) {
                if (ValidateParams.isJPLanguage()) {
                    viewHolder.txtStatus.setText("残り" + differenceDates + "日");
                } else {
                    if (differenceDates == 1) {
                        viewHolder.txtStatus.setText(differenceDates + " day remaining");
                    } else {
                        viewHolder.txtStatus.setText(differenceDates + " days remaining");
                    }
                }
            } else if (differenceDates > listElementApply.get(position).getNotiEnableBefore()){
                if (ValidateParams.isJPLanguage()) {
                    viewHolder.txtStatus.setText("有効期限：" + formatter.format(expirationDate).split(" ")[0]);
                } else {
                    viewHolder.txtStatus.setText("Expiration: " + formatter.format(expirationDate).split(" ")[0]);
                }
            } else {
                if (ValidateParams.isJPLanguage()) {
                    viewHolder.txtStatus.setText("有効期限切れ");
                } else {
                    viewHolder.txtStatus.setText("Expired");
                }
                viewHolder.icCertificate.setImageResource(R.drawable.ic_expired);
            }
            final int id = listElementApply.get(position).getId();
            viewHolder.llSettingCert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SettingDetailCertificateActivity.class);
                    intent.putExtra(StringList.ELEMENT_APPLY_ID, String.valueOf(id));
                    getContext().startActivity(intent);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (listElementApply.get(position).getUserId() != null) {
            viewHolder.txtName.setText(listElementApply.get(position).getUserId());
        }
        return convertView;
    }
}
