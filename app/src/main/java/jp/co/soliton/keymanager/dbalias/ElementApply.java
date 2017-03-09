package jp.co.soliton.keymanager.dbalias;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by luongdolong on 2/22/2017.
 */

public class ElementApply implements Serializable {
    public static int STATUS_APPLY_CANCEL  = 0;
    public static int STATUS_APPLY_REJECT  = 1;
    public static int STATUS_APPLY_PENDING = 2;
    public static int STATUS_APPLY_APPROVED = 99;

    private int id;
    private String host;
    private String portSSL;
    private String port;
    private String userId;
    private String password;
    private String email;
    private String reason;
    private String targer;
    private int status;
    private boolean challenge;
    private String updateDate;
    private boolean notiEnableFlag;
    private int notiEnableBefore;
    private String expirationDate;
    private String cNValue;
    private String sNValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPortSSL() {
        return portSSL;
    }

    public void setPortSSL(String portSSL) {
        this.portSSL = portSSL;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTarger() {
        return targer;
    }

    public void setTarger(String targer) {
        this.targer = targer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isChallenge() {
        return challenge;
    }

    public void setChallenge(boolean challenge) {
        this.challenge = challenge;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isNotiEnableFlag() {
        return notiEnableFlag;
    }

    public void setNotiEnableFlag(boolean notiEnableFlag) {
        this.notiEnableFlag = notiEnableFlag;
    }

    public int getNotiEnableBefore() {
        return notiEnableBefore;
    }

    public void setNotiEnableBefore(int notiEnableBefore) {
        this.notiEnableBefore = notiEnableBefore;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getcNValue() {
        return cNValue;
    }

    public void setcNValue(String cNValue) {
        this.cNValue = cNValue;
    }

    public String getsNValue() {
        return sNValue;
    }

    public void setsNValue(String sNValue) {
        this.sNValue = sNValue;
    }
}
