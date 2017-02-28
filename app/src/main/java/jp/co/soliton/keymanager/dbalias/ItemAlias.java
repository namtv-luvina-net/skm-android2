package jp.co.soliton.keymanager.dbalias;

import java.io.Serializable;

/**
 * Created by daoanhtung on 12/30/2016.
 */

public class ItemAlias implements Serializable {
    // Param in ItemAlias
    private int iD;
    private String alias;
    private String nameSubjectDN;
    private Long finalDate;
    private Long beforeFinalDate;
    private int statusNotificationFinalDate = 1;
    private int statusNotificationBeforeFinalDate = 1;
    public ItemAlias() {

    }

    /**
     * This method contructor ItemAlias
     * @param ID
     * @param alias
     * @param nameSubjectDN
     * @param finalDate
     * @param beforeFinalDate
     * @param statusNotificationFinalDate
     * @param statusNotificationBeforeFinalDate
     */
    public ItemAlias(int ID, String alias, String nameSubjectDN, Long finalDate, Long beforeFinalDate, int statusNotificationFinalDate, int statusNotificationBeforeFinalDate) {

        this.iD = ID;
        this.alias = alias;
        this.nameSubjectDN = nameSubjectDN;
        this.finalDate = finalDate;
        this.beforeFinalDate = beforeFinalDate;
        this.statusNotificationFinalDate = statusNotificationFinalDate;
        this.statusNotificationBeforeFinalDate = statusNotificationBeforeFinalDate;

    }

    public int getiD() {
        return iD;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setNameSubjectDN(String nameSubjectDN) {
        this.nameSubjectDN = nameSubjectDN;
    }

    public String getNameSubjectDN() {
        return nameSubjectDN;
    }

    public void setFinalDate(Long finalDate) {
        this.finalDate = finalDate;
    }

    public Long getFinalDate() {
        return finalDate;
    }

    public void setBeforeFinalDate(Long beforeFinalDate) {
        this.beforeFinalDate = beforeFinalDate;
    }

    public Long getBeforeFinalDate() {
        return beforeFinalDate;
    }

    public void setStatusNotificationFinalDate(int statusNotificationFinalDate) {
        this.statusNotificationFinalDate = statusNotificationFinalDate;
    }

    public int getStatusNotificationFinalDate() {
        return statusNotificationFinalDate;
    }

    public void setStatusNotificationBeforeFinalDate(int statusNotificationBeforeFinalDate) {
        this.statusNotificationBeforeFinalDate = statusNotificationBeforeFinalDate;
    }

    public int getStatusNotificationBeforeFinalDate() {
        return statusNotificationBeforeFinalDate;
    }


}
