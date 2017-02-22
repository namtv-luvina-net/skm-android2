package epsap4.soliton.co.jp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import epsap4.soliton.co.jp.R;

/**
 * Created by daoanhtung on 12/28/2016.
 */

public class AdapterDetailCertificate extends ArrayAdapter<ItemDetalCert> {
    // Param in AdapterDetailCertificate
    private List<ItemDetalCert> detalCertList = new ArrayList<ItemDetalCert>();
    private LayoutInflater layoutInflater = null;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView tileItem;
        public TextView contentItem;
    }

    /**
     * This method contructor AdapterDetailCertificate
     *
     * @param context
     * @param objects
     */
    public AdapterDetailCertificate(Context context, List<ItemDetalCert> objects) {
        super(context, 0);
        this.detalCertList = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * This method get size detalCertList
     *
     * @return int
     */
    @Override
    public int getCount() {
        if (detalCertList != null) {
            return detalCertList.size();
        } else {
            return 0;
        }
    }

    /**
     * This method get count type
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * This method get stype of item have at postion
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return detalCertList.get(position).getStyle();
    }

    /**
     * This method View item have at postion
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (type == 0) {
                convertView = layoutInflater.inflate(R.layout.item_group_cert, null);
                viewHolder.tileItem = (TextView) convertView.findViewById(R.id.textView_Tile_Group);
            } else if (type == 1) {
                convertView = layoutInflater.inflate(R.layout.item_subject_cert, null);
                viewHolder.tileItem = (TextView) convertView.findViewById(R.id.textView_Tile);
                viewHolder.contentItem = (TextView) convertView.findViewById(R.id.textview_Content_Subject);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (type == 0) {
            viewHolder.tileItem.setHint(detalCertList.get(position).getTileItem());
        } else if (type == 1) {
            viewHolder.tileItem.setText(detalCertList.get(position).getTileItem());
            if (detalCertList.get(position).getContentItem() != null) {
                viewHolder.contentItem.setText(detalCertList.get(position).getContentItem());
            }
        }
        // Return the completed view to render on screen
        return convertView;

    }
}
