package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.customview.DialogMenuCertDetail;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.ContentListApplyUpdateTabletFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class AdapterListApplyUpdateTablet extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listCertificate;

	public interface ItemListener{
		void clickApplyButton(String id);
	}

	private ItemListener listener;
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
     * @param listCertificate
     */
    public AdapterListApplyUpdateTablet(Context context, List<ElementApply> listCertificate, ItemListener listener) {
        super(context, 0);
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
            viewHolder.titleUserId = (TextView) convertView.findViewById(R.id.titleUserId);
            viewHolder.btnApplyUpdate = (TextView) convertView.findViewById(R.id.btnApplyUpdate);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icCertificate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data ElementApply
	    try {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    Log.d("AdapterListApplyUpdateTablet:datnd", "getView: = " + listCertificate.get(position).getUserId()
				    + " - " + listCertificate.get(position).getExpirationDate());
		    Date expirationDate = formatter.parse(listCertificate.get(position).getExpirationDate());
		    Date date = new Date();

		    //Comparing dates
		    long difference = expirationDate.getTime() - date.getTime();
		    long differenceDates = difference / (24 * 60 * 60 * 1000);
		    if (difference > 0){
			    differenceDates++;
		    }

		    if (differenceDates > 0 && differenceDates <= listCertificate.get(position).getNotiEnableBefore()) {
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
			    viewHolder.icCertificate.setImageResource(R.drawable.certificate_image);
		    } else if (differenceDates > listCertificate.get(position).getNotiEnableBefore()){
			    if (ValidateParams.isJPLanguage()) {
				    viewHolder.titleDateInfo.setText("有効期限：" + formatter.format(expirationDate).split(" ")[0]);
			    } else {
				    viewHolder.titleDateInfo.setText("Expiration: " + formatter.format(expirationDate).split(" ")[0]);
			    }
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_inactive));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_inactive);
			    viewHolder.icCertificate.setImageResource(R.drawable.certificate_image);
		    } else {
			    if (ValidateParams.isJPLanguage()) {
				    viewHolder.titleDateInfo.setText("有効期限切れ");
			    } else {
				    viewHolder.titleDateInfo.setText("Expired");
			    }
			    viewHolder.btnApplyUpdate.setTextColor(getContext().getResources().getColor(R.color.text_color_active));
			    viewHolder.btnApplyUpdate.setBackgroundResource(R.drawable.border_button_active);
			    viewHolder.icCertificate.setImageResource(R.drawable.ic_expired);
		    }
		    ElementApply elementApply = listCertificate.get(position);
		    final int id = elementApply.getId();
		    final String userId = elementApply.getUserId();
		    if (userId != null) {
			    viewHolder.titleUserId.setText(userId);
		    }
		    viewHolder.btnApplyUpdate.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
				    LogCtrl logCtrl = LogCtrl.getInstance(getContext());
				    logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate id: " + id);
				    logCtrl.loggerInfo("AdapterListCertificate::click btnUpdate userId: " + userId);
				    Log.d("AdapterListApplyUpdateTablet:datnd", "onClick: " + id + " - " + userId);
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
