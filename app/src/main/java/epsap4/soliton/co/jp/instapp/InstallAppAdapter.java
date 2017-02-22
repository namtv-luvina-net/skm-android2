package epsap4.soliton.co.jp.instapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import epsap4.soliton.co.jp.ListItem;
import epsap4.soliton.co.jp.R;

public class InstallAppAdapter extends ArrayAdapter<ListItem> {

	private LayoutInflater inflater; 	/* XMLからViewを生成する */
	private int uniqueViewId;			/* リストアイテムのレイアウト */
	private List<ListItem> listItems;	/* 表示するアイテム */
	
	public InstallAppAdapter(Context context, int id, List<ListItem> items) {
		super(context, id, items);
		// TODO 自動生成されたコンストラクター・スタブ
		
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
			Log.i("InstallAppAdapter::getView", "convertView != null");
			view = convertView;
		} else {
			Log.i("InstallAppAdapter::getView", "convertView == null");
			view = inflater.inflate(uniqueViewId, null);
		}
	
		// 対象のアイテムを取得
		ListItem uniqueitem = listItems.get(position);
	
		// アイコンを設定
		ImageView iconImage = (ImageView)view.findViewById(R.id.FileImageView);
		byte[] icon_byte = uniqueitem.getIcon();
		Bitmap theBitmap =
				BitmapFactory.decodeByteArray(icon_byte, 0, icon_byte.length);
		iconImage.setImageBitmap(theBitmap);
	
		// テキストを設定
		TextView filename = (TextView)view.findViewById(R.id.TextViewByFileName);
		filename.setText(uniqueitem.getText());
	
		return view;
	}

}
