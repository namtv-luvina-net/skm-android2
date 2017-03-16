package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class AdapterListCertificate extends ArrayAdapter<ElementApply> {
    // Param in AdapterListConfirmApply
    private List<ElementApply> listElementApply;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView txtStatus;
        public TextView txtName;
        public Button btnUpdate;
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
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.btnUpdate = (Button) convertView.findViewById(R.id.btnUpdate);
            viewHolder.icCertificate = (ImageView) convertView.findViewById(R.id.icCertificate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data ElementApply
        if (listElementApply.get(position).getHost() != null) {
            viewHolder.txtStatus.setText(listElementApply.get(position).getHost());
        }
        if (listElementApply.get(position).getUserId() != null) {
            viewHolder.txtName.setText(listElementApply.get(position).getUserId());
        }
        viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;

    }
}
