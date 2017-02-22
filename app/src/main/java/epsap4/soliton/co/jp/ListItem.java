package epsap4.soliton.co.jp;

import android.util.Base64;

import java.io.Serializable;

/**  
  * アイコンとテキストを表示するためのリストアイテム.  
  */ 

public class ListItem implements Serializable {
	/**
	 * serialversion Uid
	 */
	private static final long serialVersionUID = 1L;
	/** 表示するアイコンのリソースID. */ 
	private int iconResource;  
	/** 表示するアイコンのバイナリデータ. */ 
	private byte[] m_iconbytes;
	/** 表示するテキスト. */ 
	private String text;
	/** 表示しない隠しパラメータ **/
	private String id_num;		// インスタンスプロファイルリストの番号
	private String cmd_uuid;	// CommandUUID
	private String apkname;		// Apk名称
	
	public ListItem() {
		
	}
	
	public ListItem(int iconResource, String text){
		this.iconResource = iconResource;  
		this.text = text;  
	}  
   
	public ListItem(int iconResource, String text, String id){
		this.iconResource = iconResource;  
		this.text = text;  
		this.id_num = id;
	} 
	
	public int getIconResource() {  
		return iconResource;  
	}  

	
	public void setIconResource(int iconResource) {  
		this.iconResource = iconResource;  
	}  
	
	public void setIcon(String str_icon) {
		m_iconbytes = Base64.decode(str_icon, 0);
	}
	
	public byte[] getIcon() { 
		return m_iconbytes; 
	}

	public String getText() {
		return text;  
	}  
	
	public void setText(String text) {
		this.text = text;  
	}  
	
	public String getIDText() {
		return id_num;  
	}  
	
	public void setIDText(String id) {
		this.id_num = id;  
	}  
	
	public String getUUIDText() {
		return cmd_uuid;  
	}  
	
	public void setUUIDIDText(String uuid) {
		this.cmd_uuid = uuid;  
	}  
	
	public String getApk() {
		return apkname;
	}
	
	public void setApk(String apk) {
		this.apkname = apk;
	}

} 
