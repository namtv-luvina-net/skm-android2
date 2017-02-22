package epsap4.soliton.co.jp.dbalias;

/**
 * Created by luongdolong on 2/22/2017.
 */

public class ElementApply {
    public static int STATUS_APPLY_CANCEL  = 0;
    public static int STATUS_APPLY_REJECT  = 1;
    public static int STATUS_APPLY_PENDING = 2;

    private String host;
    private String userId;
    private String password;
    private String email;
    private String reason;
    private String targer;
    private int status;
    private boolean challenge;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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
}
