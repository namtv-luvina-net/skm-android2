package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

import java.util.List;

public class AdapterMDM extends ArrayAdapter<AdapterMDM.ItemMDM> {
    // Param in AdapterListConfirmApply
    private List<ItemMDM> listItemMDM;
    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView txtName;
        public TextView txtValue;
        public TextView line;
    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param listItemMDM
     */
    public AdapterMDM(Context context, List<ItemMDM> listItemMDM) {
        super(context, 0);
        setListItemMDM(listItemMDM);
    }

	public void setListItemMDM(List<ItemMDM> listItemMDM) {
		this.listItemMDM = listItemMDM;
	}

	/**
     * This method get size listItemMDM
     *
     * @return
     */
    @Override
    public int getCount() {
        if (listItemMDM != null) {
            return listItemMDM.size();
        } else {
            return 0;
        }
    }

    public ItemMDM getItem(int position) {
        return listItemMDM.get(position);
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
	        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mdm, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txtName);
            viewHolder.txtValue = convertView.findViewById(R.id.txtValue);
            viewHolder.line = convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(getItem(position).getName());
	    if (getItem(position).getValue()) {
		    viewHolder.txtValue.setText(getContext().getString(R.string.enable));
	    } else {
		    viewHolder.txtValue.setText(getContext().getString(R.string.disable));
	    }
	    if (position == getCount()-1) {
		    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.line.getLayoutParams();
		    p.setMargins(0,0,0,0);
		    viewHolder.line.requestLayout();
	    }
	    return convertView;
    }

    public static class ItemMDM {
	    private String name;
	    private boolean value;

	    public ItemMDM(String name, boolean value) {
		    this.name = name;
		    this.value = value;
	    }

	    public String getName() {
		    return name;
	    }

	    public void setName(String name) {
		    this.name = name;
	    }

	    public boolean getValue() {
		    return value;
	    }

	    public void setValue(boolean value) {
		    this.value = value;
	    }
    }
}
