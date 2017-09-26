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
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.SettingDetailCertificateActivity;
import jp.co.soliton.keymanager.activity.SettingTabletActivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_LEFT;
import static jp.co.soliton.keymanager.manager.APIDManager.PREFIX_APID_WIFI;

/**
 * Created by lexuanvinh on 03/04/2017.
 */

public class AdapterSettingListCertificate extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listElementApply;
	private boolean isTablet;
    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView txtStore;
        public TextView txtStatus;
        public TextView txtCN;
        public ImageView icCertificate;
        public LinearLayout llSettingCert;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listElementApply
     */
    public AdapterSettingListCertificate(Context context, List<ElementApply> listElementApply, boolean isTablet) {
        super(context, 0);
        setListElementApply(listElementApply);
	    this.isTablet = isTablet;
    }

	public void setListElementApply(List<ElementApply> listElementApply) {
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
	        if (isTablet) {
		        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting_certificate_tablet, parent, false);
	        }else {
		        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting_certificate, parent, false);
	        }
            viewHolder.txtStore = (TextView) convertView.findViewById(R.id.txtStore);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusCert);
            viewHolder.txtCN = (TextView) convertView.findViewById(R.id.txtCommonNameCert);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icSettingCertificate);
            viewHolder.llSettingCert = (LinearLayout) convertView.findViewById(R.id.llSettingCert);
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
		            status = getContext().getResources().getString(R.string.one_day_remaining);
	            } else {
		            status = String.format(getContext().getResources().getString(R.string.many_days_remaining), String.valueOf
				            (differenceDates));
	            }
	            viewHolder.txtStatus.setText(status);
            } else if (differenceDates > elementApply.getNotiEnableBefore()){
	            viewHolder.txtStatus.setText(getContext().getResources().getString(R.string.expiration_date) + formatter
			            .format(expirationDate).split(" ")[0]);
            } else {
	            viewHolder.txtStatus.setText(getContext().getResources().getString(R.string.label_expired_item));
                viewHolder.icCertificate.setImageResource(R.drawable.ic_expired);
            }
            final int id = elementApply.getId();
            viewHolder.llSettingCert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
	                if (isTablet) {
		                ((SettingTabletActivity)getContext()).gotoDetailCertificatesSetting(String.valueOf(id), SCROLL_TO_LEFT);
	                }else {
		                Intent intent = new Intent(getContext(), SettingDetailCertificateActivity.class);
		                intent.putExtra(StringList.ELEMENT_APPLY_ID, String.valueOf(id));
		                getContext().startActivity(intent);
	                }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (elementApply.getUserId() != null) {
            viewHolder.txtCN.setText(elementApply.getcNValue());
        }
	    if (elementApply.getTarget() != null) {
		    if (elementApply.getTarget().startsWith(PREFIX_APID_WIFI)) {
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
