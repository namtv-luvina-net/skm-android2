package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.ViewPagerReapplyActivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class AdapterListCertificate extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listElementApply;
	private Context context;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView txtStatus;
        public TextView txtCN;
        public TextView btnUpdate;
        public ImageView icCertificate;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listElementApply
     */
    public AdapterListCertificate(Context context, List<ElementApply> listElementApply) {
        super(context, 0);
	    this.context = context;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_certificate, parent, false);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            viewHolder.txtCN = (TextView) convertView.findViewById(R.id.txtCN);
            viewHolder.btnUpdate = (TextView) convertView.findViewById(R.id.btnUpdate);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icCertificate);
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
	            String status;
	            if (differenceDates == 1) {
		            status = context.getResources().getString(R.string.one_day_remaining);
                } else {
		            status = String.format(context.getResources().getString(R.string.many_days_remaining), String.valueOf
				            (differenceDates));
                }
                viewHolder.txtStatus.setText(status);
                viewHolder.btnUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
                viewHolder.btnUpdate.setBackgroundResource(R.drawable.border_button_active);
            } else if (differenceDates > listElementApply.get(position).getNotiEnableBefore()){
                viewHolder.txtStatus.setText(context.getResources().getString(R.string.expiration_date) + formatter
		                .format(expirationDate).split(" ")[0]);

	            viewHolder.btnUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_inactive));
                viewHolder.btnUpdate.setBackgroundResource(R.drawable.border_button_inactive);
            } else {
	            viewHolder.txtStatus.setText(context.getResources().getString(R.string.label_expired));
                viewHolder.btnUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
                viewHolder.btnUpdate.setBackgroundResource(R.drawable.border_button_active);
                viewHolder.icCertificate.setImageResource(R.drawable.ic_expired);
            }
            final int id = listElementApply.get(position).getId();
	        final String userId = listElementApply.get(position).getUserId();
            viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
	                LogCtrl logCtrl = LogCtrl.getInstance(context);
	                logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate id: " + id);
	                logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate userId: " + userId);
                    InputApplyInfo.deletePref(getContext());
	                Intent intent = new Intent(getContext(), ViewPagerReapplyActivity.class);
	                intent.putExtra(StringList.ELEMENT_APPLY_ID, String.valueOf(id));
	                getContext().startActivity(intent);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (listElementApply.get(position).getUserId() != null) {
            viewHolder.txtCN.setText(listElementApply.get(position).getcNValue());
        }
        return convertView;
    }
}
