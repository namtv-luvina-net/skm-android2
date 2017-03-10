package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import org.bouncycastle.jce.PKCS10CertificationRequest;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.scep.Requester;
import jp.co.soliton.keymanager.scep.RequesterException;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.scep.cert.CertificateUtility;
import jp.co.soliton.keymanager.scep.pkimessage.CertRep;
import jp.co.soliton.keymanager.scep.pkimessage.PkiStatus;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class StartUsingProceduresActivity extends Activity implements KeyChainAliasCallback {
    private int m_nApplicationList = 75;
    private int m_nMDM_RequestCode = 70;
    private int m_nGuidePageRequestCode = 65;
    private int m_nCertReq_RequestCode = 60;
    private int m_nEnrollRtnCode = 55;

    private String m_strKeyType = "";
    private String m_strSubject = "";
    private String m_strChallenge = "";
    private String m_strSubjectAltName = "";
    private String m_strServerURL = "";
    private CertStore cACertificateStore = null;
    private Requester scepRequester = null;
    private XmlPullParserAided m_p_aided = null;
    private static InformCtrl m_InformCtrl;
    private int m_nErroType;
    private ElementApply element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_using_procedures);
        Intent intent = getIntent();
        m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
        element = (ElementApply)intent.getSerializableExtra("ELEMENT_APPLY");
        scepRequester = getScepRequester();
        new GetDeviceCertTask().execute();
    }

    public Requester getScepRequester() {
        if (scepRequester == null) {
            setScepRequester(new Requester());
        }
        return scepRequester;
    }

    public void setScepRequester(Requester scepRequester) {
        this.scepRequester = scepRequester;
    }

    /**
     * Task processing GetDeviceCertTask
     */
    private class GetDeviceCertTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... params) {
            ////////////////////////////////////////////////////////////////////////////
            // 大項目1. ログイン開始 <=========
            ////////////////////////////////////////////////////////////////////////////
            HttpConnectionCtrl conn = new HttpConnectionCtrl(getApplicationContext());
            boolean ret = conn.RunHttpDeviceCertUrlConnection(m_InformCtrl);

            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask " + "Network error", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_NETWORK;
                return false;
            }
            // ログイン結果
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + " Forbidden.", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_FORBIDDEN;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "Unauthorized.", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_UNAUTHORIZED;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "ERR:", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_COLON;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith("NG")) {
                m_nErroType = InputBasePageFragment.ERR_LOGIN_FAIL;
                return false;
            }
            // 取得したCookieをログイン時のCookieとして保持する.
            m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
            ///////////////////////////////////////////////////
            // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
            ///////////////////////////////////////////////////
            // 取得XMLのパーサー
            m_p_aided = new XmlPullParserAided(getApplicationContext(), m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

            ret = m_p_aided.TakeApartScepInfoResponse(m_InformCtrl);
            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask-- " + "TakeApartDevice false", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_NETWORK;
                return false;
            }
            ////////////////////////////////////////////////////////////////////////////
            // 大項目1. ログイン終了 =========>
            ////////////////////////////////////////////////////////////////////////////
            m_nErroType = InputBasePageFragment.SUCCESSFUL;
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            endConnection(result);
        }
    }

    /**
     * Processing result from server return back
     *
     * @param result
     */
    private void endConnection(boolean result) {
        if (result) {
            SetScepItem();
            new CertificateEnrollTask().execute(scepRequester);
        } else {
            //show error message
            if (m_nErroType == InputBasePageFragment.ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                showMessage(m_InformCtrl.GetRtn().substring(str_forbidden.length()));
            } else if (m_nErroType == InputBasePageFragment.ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
            } else if (m_nErroType == InputBasePageFragment.ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
            } else if (m_nErroType == InputBasePageFragment.ERR_LOGIN_FAIL) {
                showMessage(getString(R.string.login_failed));
            } else {
                showMessage(getString(R.string.connect_failed));
            }
        }
    }

    /**
     * Show message
     *
     * @param message
     */
    protected void showMessage(String message) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.setOnOkDismissMessageListener(new DialogApplyMessage.OnOkDismissMessageListener() {
            @Override
            public void onOkDismissMessage() {
                DetailConfirmActivity.backToList = "1";
                finish();
            }
        });
        dlgMessage.show();
    }

    @Override
    public void alias(String alias) {
        Log.d("CertLoginActivity", "printAlias():: " + alias);
    }

    private class CertificateEnrollTask extends AsyncTask<Requester, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Requester... params) {
            try {
                Requester requester = params[0];
                // Generate Key Pair
                KeyPairGenerator rSAKeyPairGenerator = KeyPairGenerator.getInstance(m_strKeyType/*"RSA"*/, "BC");
                rSAKeyPairGenerator.initialize(2048, new SecureRandom());
                KeyPair rSAKeyPair = rSAKeyPairGenerator.generateKeyPair();

                // Generate self-signed certificate
                X509Certificate selfSignedCertificate;
                final Date notBefore = new Date(System.currentTimeMillis() - (5 * 60000));
                final Date notAfter = new Date(System.currentTimeMillis() + (1 * 3600000));
                final BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

                Log.d("CertificateEnrollTask", "privatekey - " + rSAKeyPair.getPrivate());

                selfSignedCertificate =
                        CertificateUtility.generateSelfSignedCertificate(
                                m_strSubject,//"CN=NetAttest EPS Root CA,OU=RDD,O=Soliton Systems K.K.,L=Shinjuku,ST=Tokyo,C=JP",
                                serial,
                                notBefore,
                                notAfter,
                                rSAKeyPair,
                                "SHA1WithRSA");

                // Make PKCS #10
                PKCS10CertificationRequest certificateSigningRequest;
                certificateSigningRequest =
                        CertificateUtility.generateCertificateSigningRequest(
                                m_strSubject,//"CN=NetAttest EPS Root CA,OU=RDD,O=Soliton Systems K.K.,L=Shinjuku,ST=Tokyo,C=JP",
                                m_strChallenge,//"Challenge",
                                rSAKeyPair,
                                "SHA1WithRSA",
                                m_strSubjectAltName);
                cACertificateStore =
                        requester.getCACertificate(m_strServerURL/*"http://10.30.127.44/ca/NaScepEPSap.cgi"*/);
                CertRep certRep = requester.certificateEnrollment(
                        m_strServerURL/*"https://10.30.127.44/ca/NaScepEPSap.cgi"*/,
                        certificateSigningRequest,
                        selfSignedCertificate,
                        rSAKeyPair.getPrivate(),
                        cACertificateStore);
                if (certRep.getPkiStatus().getStatus() == PkiStatus.Status.SUCCESS) {
                    CertificateUtility.keyPairToKeyChain(
                            StartUsingProceduresActivity.this,
                            rSAKeyPair);
                    CertificateUtility.certificateToKeyChain(
                            StartUsingProceduresActivity.this,
                            certRep.getCertificate(),
                            m_InformCtrl.GetUserID()/*"epsap"m_strCertArias*/, m_nEnrollRtnCode/*0*/);

                    element.setsNValue(certRep.getCertificate().getSerialNumber().toString());
                    String str = certRep.getCertificate().getSubjectDN().toString();
                    String[] arr = str.split(",");
                    for(int i = 0; i < arr.length; i++) {
                        if(arr[i].toString().startsWith("CN=")) {
                            element.setcNValue(arr[i].toString().replace("CN=","").trim());
                        }
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    element.setExpirationDate(dateFormat.format(certRep.getCertificate().getNotAfter()));
                } else {
                    return false;
                }
            } catch (RequesterException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask RequesterException::" + e.toString(), StartUsingProceduresActivity.this);
                //	e.printStackTrace();
                return false;
            } catch (NoSuchAlgorithmException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask NoSuchAlgorithmException::" + e.toString(), StartUsingProceduresActivity.this);
                //	e.printStackTrace();
                return false;
            } catch (NoSuchProviderException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask NoSuchProviderException::" + e.toString(), StartUsingProceduresActivity.this);
                //	e.printStackTrace();
                return false;
            } catch (Exception e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask Exception::" + e.toString(), StartUsingProceduresActivity.this);
                //	e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        // プログレス処理
        protected void onProgressUpdate(Integer... values) {
            Log.d("CertificateEnrollTask", "onProgressUpdate - " + "values");
        }

        @Override
        // メインスレッドに反映させる処理
        protected void onPostExecute(Boolean result) {
            Log.d("CertificateEnrollTask", "onPostExecute - " + "result");
            if(!result) {
                new DropCertTask().execute();
            }
        }
    }

    private boolean SetScepItem() {
        // URL
        m_strServerURL = m_InformCtrl.GetSituationURL();
        // Subject
        List<String> list = m_p_aided.GetSubjectList();
        String subject_string = "";
        System.out.println("_________________---------____________________");
        for(int n = 0; list.size() > n; n++) {
            System.out.println(list.get(n));
            if(subject_string.length() == 0) {
                subject_string = list.get(n);
                subject_string += "=";
            } else if ((n%2) == 0) {
                subject_string += ", ";
                subject_string += list.get(n);
                subject_string += "=";
            } else if ((n%2) == 1) {
                subject_string += "\"";
                subject_string += list.get(n);
                subject_string += "\"";
            }
        }
        m_strSubject = subject_string;
        // v1.2.1以降---SubjectにmailAddressを追加する場合があるので確認は最後に...

        // Challenge, Name, KeyType
        XmlDictionary dict = m_p_aided.GetDictionary();
        List<XmlStringData> str_list;
        if(dict != null) {
            str_list = dict.GetArrayString();
            for(int i = 0; str_list.size() > i; i++){
                // config情報に従って、処理を行う.
                XmlStringData p_data = str_list.get(i);
                SetEditMemberChild(p_data);
            }
        }

        LogCtrl.Logger(LogCtrl.m_strDebug, "CertLoginAcrivity::SetScepItem "+ "Subject: " + m_strSubject, this);

        return true;
    }

    private void SetEditMemberChild(XmlStringData data) {
        String strKeyName = data.GetKeyName();	// キー名
        int    i_type = data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
        String strData = data.GetData();		// 要素
        LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity::SetEditMemberChild "
                + "Key= " +  strKeyName + " , Data= " + strData , this);

        // Chalenge
        if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_challenge)) {	// Challenge
            m_strChallenge = strData;
        } else if(strKeyName.equalsIgnoreCase(StringList.m_str_CaIdent)) {	// Name(Arias)
//            m_strCertArias = strData;
        } else if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_keytype)) {	// Key Type
            m_strKeyType = strData;
        } else if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_rfc822Name)) {	// rfc822Name
            // チケット #8907 メールアドレスをSubjectAltNameとして設定しておく
            m_strSubjectAltName = strData;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCtrl.Logger(LogCtrl.m_strInfo, "onActivityResult start " + "REC CODE = " + Integer.toString(requestCode), this);
        if (requestCode == m_nCertReq_RequestCode) {
            // 申請アクティビティ終了後
            Log.i("onActivityResult","REC CODE = " + Integer.toString(requestCode));
        } else if (requestCode == m_nEnrollRtnCode) {
            // After CertificateEnrollTask
            Log.i("CertLoginActivity","REC CODE = " + Integer.toString(resultCode));
            if (resultCode != 0) {
                ElementApplyManager mgr = new ElementApplyManager(getApplicationContext());
                mgr.updateElementCertificate(element);
                Intent intent = new Intent(getApplicationContext(), CompleteUsingProceduresActivity.class);
                intent.putExtra("ELEMENT_APPLY", element);
                DetailConfirmActivity.backToList = "1";
                finish();
                startActivity(intent);
            } else {
                DetailConfirmActivity.backToList = "1";
                finish();
            }
        } else if (requestCode == m_nGuidePageRequestCode) {
            if (resultCode == StringList.RESULT_GUIDE_CLOSE) finish();
        } else if (requestCode == m_nApplicationList) {
        }
    }

    /**
     * Task processing DropCertTask
     */
    private class DropCertTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... params) {
            m_InformCtrl.SetMessage("Action=drop");
            // 申請
            HttpConnectionCtrl conn = new HttpConnectionCtrl(getApplicationContext());
            boolean ret = conn.RunHttpApplyUrlConnection(m_InformCtrl);		// 専用のRunHttpを作成する
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            StartUsingProceduresActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
