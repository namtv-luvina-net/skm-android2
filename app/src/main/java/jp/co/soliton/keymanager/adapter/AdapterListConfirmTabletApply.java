package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class AdapterListConfirmTabletApply extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listElementApply;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView titleHost;
        public TextView tvHostValue;
        public TextView titleID;
        public TextView tvIdValue;
        public TextView tvStatus;
        public TextView tvUpdateDate;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listElementApply
     */
    public AdapterListConfirmTabletApply(Context context, List<ElementApply> listElementApply) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_confirm_apply_tablet, parent, false);
            viewHolder.titleHost = (TextView) convertView.findViewById(R.id.titleHost);
            viewHolder.tvHostValue = (TextView) convertView.findViewById(R.id.tvHostValue);
            viewHolder.titleID = (TextView) convertView.findViewById(R.id.titleID);
            viewHolder.tvIdValue = (TextView) convertView.findViewById(R.id.tvIdValue);
            viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
            viewHolder.tvUpdateDate = (TextView) convertView.findViewById(R.id.tvUpdateDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data ElementApply
	    ElementApply elementApply = listElementApply.get(position);
        if (elementApply.getHost() != null) {
            viewHolder.tvHostValue.setText(elementApply.getHost());
        }
        if (elementApply.getUserId() != null) {
            viewHolder.tvIdValue.setText(elementApply.getUserId());
        }
        if (elementApply.getStatus() == ElementApply.STATUS_APPLY_CANCEL) {
            viewHolder.tvStatus.setText(getContext().getText(R.string.stt_cancel));
        } else if (elementApply.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
            String stt = getContext().getText(R.string.stt_waiting_approval).toString();
            viewHolder.tvStatus.setText(stt.replace(" ", "\n"));
        } else if (elementApply.getStatus() == ElementApply.STATUS_APPLY_REJECT) {
            viewHolder.tvStatus.setText(getContext().getText(R.string.stt_rejected));
        }
	    String updateDate = elementApply.getUpdateDate().split(" ")[0];
	    updateDate = getContext().getString(R.string.title_apply_date) + " " + updateDate;
	    viewHolder.tvUpdateDate.setText(updateDate.replace("-", "/"));
        return convertView;
    }
}
