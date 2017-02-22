package epsap4.soliton.co.jp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileAdapter extends ArrayAdapter<ListItem>
	implements AdapterView.OnItemClickListener {	// ListItemクラスを配列に持つ
	
	private LayoutInflater inflater; 	/* XMLからViewを生成する */
	private int uniqueViewId;			/* リストアイテムのレイアウト */
	private List<ListItem> listItems;	/* 表示するアイテム */
	
	public FileAdapter(Context context, int id
			, List<ListItem> items/*, ArrayList array*/){
		super(context, id, items);
		
		// リストアイテムリソースIDと表示アイテムの割り当て
		this.uniqueViewId = id;
		this.listItems = items;
		
		// ContextからLayoutInflaterを取得
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/////////////////////////////
	// 1アイテム分のビューを取得
	// ※コレを設定することで、unique.xmlの内容をListViewに表示できる.
	//   (設定しないと、アプリケーションが落ちてしまう)
	/////////////////////////////
	// @Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view;
		
		// convertViewに設定されていた場合、それを使用する
		if(convertView != null) {
			Log.i("FileAdapter::getView", "convertView != null");
			view = convertView;
		} else {
			Log.i("FileAdapter::getView", "convertView == null");
			view = inflater.inflate(uniqueViewId, null);
		}
		
		// 対象のアイテムを取得
		ListItem uniqueitem = listItems.get(position);
		
		// アイコンを設定
		ImageView iconImage = (ImageView)view.findViewById(R.id.FileImageView);
		iconImage.setImageResource(uniqueitem.getIconResource());
		
		// テキストを設定
		TextView filename = (TextView)view.findViewById(R.id.TextViewByFileName);
		filename.setText(uniqueitem.getText());
		
		return view;
	}
	
	public void onClick(View view) {
		Log.i("FileAdapter::onClick", "NULL");
	
	}

	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 Log.i("FileAdapter::onClick", "NULL");
	 }
}
