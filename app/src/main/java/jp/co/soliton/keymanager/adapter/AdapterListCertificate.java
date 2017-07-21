package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.ViewPagerUpdateActivity;
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
        public TextView txtStore;
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
	        viewHolder.txtStore = (TextView) convertView.findViewById(R.id.txtStore);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            viewHolder.txtCN = (TextView) convertView.findViewById(R.id.txtCN);
            viewHolder.btnUpdate = (TextView) convertView.findViewById(R.id.btnUpdate);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icCertificate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ElementApply elementApply = listElementApply.get(position);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            Date expirationDate = formatter.parse(elementApply.getExpirationDate());
            Date date = new Date();

            //Comparing dates
            long difference = expirationDate.getTime() - date.getTime();
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            if (difference > 0){
                differenceDates++;
            }

            if (differenceDates > 0 && differenceDates <= elementApply.getNotiEnableBefore()) {
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
            } else if (differenceDates > elementApply.getNotiEnableBefore()){
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
            final int id = elementApply.getId();
	        final String userId = elementApply.getUserId();
            viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputApplyInfo.deletePref(getContext());
	                Intent intent = new Intent(getContext(), ViewPagerUpdateActivity.class);
	                intent.putExtra(StringList.ELEMENT_APPLY_ID, String.valueOf(id));
	                getContext().startActivity(intent);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (elementApply.getUserId() != null) {
            viewHolder.txtCN.setText(elementApply.getcNValue());
        }
	    if (elementApply.getTarger() != null) {
		    if (elementApply.getTarger().startsWith("WIFI")) {
			    viewHolder.txtStore.setText(getContext().getString(R.string.title_place) + getContext().getString(R.string
					    .main_apid_wifi));
		    } else {
			    viewHolder.txtStore.setText(getContext().getString(R.string.title_place) + getContext().getString(R.string
					    .main_apid_vpn));
		    }
	    }else {
		    viewHolder.txtStore.setVisibility(View.GONE);
	    }
        return convertView;
    }
}
