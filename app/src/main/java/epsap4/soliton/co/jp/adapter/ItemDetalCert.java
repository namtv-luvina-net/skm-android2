package epsap4.soliton.co.jp.adapter;

/**
 * Created by daoanhtung on 12/28/2016.
 */

public class ItemDetalCert {
    private String tileItem = "";
    private String contentItem = "";
    private int style = 0;
    //style =0, display title group of detail Certificate
    //style =1, display title item and content item of detail Certificate

    /**
     * This method contructor ItemDetalCert
     * @param tileItem
     * @param style
     */
    public ItemDetalCert(String tileItem, int style) {
        this.tileItem = tileItem;
        this.style = style;
    }

    /**
     *
     * @param tileItem
     * @param contentItem
     * @param style
     */
    public ItemDetalCert(String tileItem, String contentItem, int style) {
        this.tileItem = tileItem;
        this.contentItem = contentItem;
        this.style = style;
    }

    public String getContentItem() {
        return contentItem;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getTileItem() {
        return tileItem;
    }
}
