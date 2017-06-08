package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.ItemChildDetailCertSetting;
import jp.co.soliton.keymanager.R;

import java.util.List;

public class AdapterSettingDetailCertificate extends BaseExpandableListAdapter {

	Context context;
	List<String> listDataHeader;
	List<List<ItemChildDetailCertSetting>> listDataChild;
	public static final int ONE_ROW = 0;
	public static final int TWO_ROWS = 1;

	public AdapterSettingDetailCertificate(Context context, List<String> listDataHeader, List<List<ItemChildDetailCertSetting>>
			listDataChild) {
		this.context = context;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listDataChild;
	}

	@Override
	public int getGroupCount() {
		return listDataHeader.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.listDataChild.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.listDataHeader.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.listDataChild.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		HeaderHolder headerHolder = new HeaderHolder();
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.item_header_detail_cert_setting, null);
			headerHolder.title = (TextView) convertView.findViewById(R.id.header_item);
			headerHolder.lineTop = (TextView) convertView.findViewById(R.id.lineTopHeader);
			convertView.setTag(headerHolder);
		}else {
			headerHolder = (HeaderHolder) convertView.getTag();
		}
		if (groupPosition == 0) {
			headerHolder.lineTop.setVisibility(View.GONE);
		}else {
			headerHolder.lineTop.setVisibility(View.VISIBLE);
		}
		headerHolder.title.setText(headerTitle);
		((ExpandableListView) parent).expandGroup(groupPosition);
		return convertView;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		if (listDataChild.get(groupPosition).get(childPosition).isOneRow()) {
			return ONE_ROW;
		}else {
			return TWO_ROWS;
		}
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ItemChildDetailCertSetting itemChild = listDataChild.get(groupPosition).get(childPosition);
		ChildHolder childHolder = new ChildHolder();
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int itemType = getChildType(groupPosition, childPosition);
			if (itemType == ONE_ROW) {
				convertView = infalInflater.inflate(R.layout.item_child_one_detail_cert_setting, null);
			}else {
				convertView = infalInflater.inflate(R.layout.item_child_two_detail_cert_setting, null);
			}
			childHolder.title = (TextView) convertView.findViewById(R.id.tvTitle);
			childHolder.detail = (TextView) convertView.findViewById(R.id.tvDetail);
			childHolder.lineBottom = (TextView) convertView.findViewById(R.id.lineBottom);
			convertView.setTag(childHolder);
		}else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		childHolder.title.setText(itemChild.getTitle());
		childHolder.detail.setText(itemChild.getDetail());
		if (childPosition == listDataChild.get(groupPosition).size() - 1) {
			childHolder.lineBottom.setVisibility(View.INVISIBLE);
		}else {
			childHolder.lineBottom.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public class ChildHolder {
		public TextView title;
		public TextView detail;
		public TextView lineBottom;
	}
	public class HeaderHolder {
		public TextView lineTop;
		public TextView title;
	}
}
