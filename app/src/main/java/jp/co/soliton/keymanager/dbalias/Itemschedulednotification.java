package jp.co.soliton.keymanager.dbalias;

/**
 * Created by daoanhtung on 1/5/2017.
 */

public class Itemschedulednotification {
    // Param in Itemschedulednotification
    private int id;
    private String titleDate;
    private String contentDate;

    public String getContentDate() {
        return contentDate;
    }

    public void setContentDate(String contentDate) {
        this.contentDate = contentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleDate() {
        return titleDate;
    }

    public void setTitleDate(String titleDate) {
        this.titleDate = titleDate;
    }
}
