package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class AdapterListCertificateTablet extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listCertificate;
	private Context context;

	public interface ItemListener{
		void clickApplyButton(String id);
	}

	private ItemListener listener;
    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView titleDateInfo;
        public TextView titleCN;
        public TextView btnApplyUpdate;
	    public ImageView icCertificate;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listCertificate
     */
    public AdapterListCertificateTablet(Context context, List<ElementApply> listCertificate, ItemListener listener) {
        super(context, 0);
	    this.context = context;
	    setListCertificate(listCertificate);
	    this.listener = listener;
    }

	public void setListCertificate(List<ElementApply> listCertificate) {
		this.listCertificate = listCertificate;
	}

    /**
     * This method get size listCertificate
     *
     * @return
     */
    @Override
    public int getCount() {
        if (listCertificate != null) {
            return listCertificate.size();
        } else {
            return 0;
        }
    }

    public ElementApply getItem(int position) {
        return listCertificate.get(position);
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
            viewHolder.titleCN = (TextView) convertView.findViewById(R.id.titleCN);
            viewHolder.btnApplyUpdate = (TextView) convertView.findViewById(R.id.btnApplyUpdate);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icCertificate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data ElementApply
	    try {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    Date expirationDate = formatter.parse(listCertificate.get(position).getExpirationDate());
		    Date date = new Date();

		    //Comparing dates
		    long difference = expirationDate.getTime() - date.getTime();
		    long differenceDates = difference / (24 * 60 * 60 * 1000);
		    if (difference > 0){
			    differenceDates++;
		    }

		    if (differenceDates > 0 && differenceDates <= listCertificate.get(position).getNotiEnableBefore()) {
			    String status;
			    if (differenceDates == 1) {
				    status = context.getResources().getString(R.string.one_day_remaining);
			    } else {
				    status = String.format(context.getResources().getString(R.string.many_days_remaining), String.valueOf(differenceDates));
			    }
			    viewHolder.titleDateInfo.setText(status);
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_active);
			    viewHolder.icCertificate.setImageResource(R.drawable.certificate_image);
		    } else if (differenceDates > listCertificate.get(position).getNotiEnableBefore()){
			    viewHolder.titleDateInfo.setText(context.getResources().getString(R.string.expiration_date) + formatter
					    .format(expirationDate).split(" ")[0]);
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_inactive));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_inactive);
			    viewHolder.icCertificate.setImageResource(R.drawable.certificate_image);
		    } else {
			    viewHolder.titleDateInfo.setText(context.getResources().getString(R.string.label_expired));
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_active);
			    viewHolder.icCertificate.setImageResource(R.drawable.ic_expired);
		    }
		    ElementApply elementApply = listCertificate.get(position);
		    final int id = elementApply.getId();
		    final String strCN = elementApply.getcNValue();
		    if (strCN != null) {
			    viewHolder.titleCN.setText(strCN);
		    }
		    viewHolder.btnApplyUpdate.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
				    LogCtrl logCtrl = LogCtrl.getInstance(getContext());
				    logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate id: " + id);
				    logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate userId: " + strCN);
				    listener.clickApplyButton(String.valueOf(id));
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
