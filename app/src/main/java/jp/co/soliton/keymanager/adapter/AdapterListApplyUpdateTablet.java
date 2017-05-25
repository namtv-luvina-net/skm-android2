package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AdapterListApplyUpdateTablet extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listElementApply;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView titleDateInfo;
        public TextView titleUserId;
        public TextView btnApplyUpdate;
	    public ImageView icCertificate;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listElementApply
     */
    public AdapterListApplyUpdateTablet(Context context, List<ElementApply> listElementApply) {
        super(context, 0);
	    setListElementApply(listElementApply);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_apply_update_tablet, parent, false);
            viewHolder.titleDateInfo = (TextView) convertView.findViewById(R.id.titleDateInfo);
            viewHolder.titleUserId = (TextView) convertView.findViewById(R.id.titleUserId);
            viewHolder.btnApplyUpdate = (TextView) convertView.findViewById(R.id.btnApplyUpdate);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icCertificate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data ElementApply
	    ElementApply elementApply = listElementApply.get(position);
        viewHolder.titleDateInfo.setText(getContext().getString(R.string.title_expiration_date));
        if (elementApply.getUserId() != null) {
            viewHolder.titleUserId.setText(elementApply.getUserId());
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
			    viewHolder.icCertificate.setImageDrawable(getContext().getDrawable(R.drawable.ic_certificate));
			    if (ValidateParams.isJPLanguage()) {
				    viewHolder.titleDateInfo.setText("残り" + differenceDates + "日");
			    } else {
				    if (differenceDates == 1) {
					    viewHolder.titleDateInfo.setText(differenceDates + " day remaining");
				    } else {
					    viewHolder.titleDateInfo.setText(differenceDates + " days remaining");
				    }
			    }
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_active);
		    } else if (differenceDates > listElementApply.get(position).getNotiEnableBefore()){
			    if (ValidateParams.isJPLanguage()) {
				    viewHolder.titleDateInfo.setText("有効期限：" + formatter.format(expirationDate).split(" ")[0]);
			    } else {
				    viewHolder.titleDateInfo.setText("Expiration: " + formatter.format(expirationDate).split(" ")[0]);
			    }
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_inactive));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_inactive);
		    } else {
			    if (ValidateParams.isJPLanguage()) {
				    viewHolder.titleDateInfo.setText("有効期限切れ");
			    } else {
				    viewHolder.titleDateInfo.setText("Expired");
			    }
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_active);
		    }
		    final int id = listElementApply.get(position).getId();
		    final String userId = listElementApply.get(position).getUserId();
		    viewHolder.btnApplyUpdate.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
				    LogCtrl logCtrl = LogCtrl.getInstance(getContext());
				    logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate id: " + id);
				    logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate userId: " + userId);
				    InputApplyInfo.deletePref(getContext());

			    }
		    });

	    } catch (Exception ex) {
		    ex.printStackTrace();
	    }
        return convertView;
    }

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
