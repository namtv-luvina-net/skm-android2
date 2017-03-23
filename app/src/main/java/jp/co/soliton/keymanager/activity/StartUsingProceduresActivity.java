package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
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

import jp.co.soliton.keymanager.EpsapAdminReceiver;
import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.mdm.MDMControl;
import jp.co.soliton.keymanager.mdm.MDMFlgs;
import jp.co.soliton.keymanager.scep.Requester;
import jp.co.soliton.keymanager.scep.RequesterException;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.scep.cert.CertificateUtility;
import jp.co.soliton.keymanager.scep.pkimessage.CertRep;
import jp.co.soliton.keymanager.scep.pkimessage.PkiStatus;
import jp.co.soliton.keymanager.wifi.WifiControl;
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
    private XmlPullParserAided m_p_aided_profile = null;
    private static InformCtrl m_InformCtrl;
    private int m_nErroType;
    private ElementApply element;
    private String m_strCertArias;
    private DevicePolicyManager m_DPM;
    private MDMControl mdmctrl;
    private ComponentName m_DeviceAdmin;

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

            SetScepItem();

            m_p_aided_profile = m_p_aided;
            ret = m_p_aided_profile.TakeApartProfile();
            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  "+ "TakeApartProfile false", getApplicationContext());
                //	m_ErrorMessage.setText(R.string.EnrollErrorMessage);
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
            // 2. GetMDMDictionary
            XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();
            if (mdm_dict == null) {
                Log.d("CertLoginActivity", "SetMDM() No profile");
                SetScepWifi();
                new CertificateEnrollTask().execute(scepRequester);
            } else {
                Log.d("CertLoginActivity", "SetMDM() Has profile");
                CallMDMCheckIn();
            }

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
                StringList.backToList = "1";
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                    element.setExpirationDate(dateFormat.format(certRep.getCertificate().getNotAfter()));
//                    element.setExpirationDate("2017/03/20");
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
            m_strCertArias = strData;
        } else if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_keytype)) {	// Key Type
            m_strKeyType = strData;
        } else if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_rfc822Name)) {	// rfc822Name
            // チケット #8907 メールアドレスをSubjectAltNameとして設定しておく
            m_strSubjectAltName = strData;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCtrl.Logger(LogCtrl.m_strInfo, "onActivityResult start " + "REC CODE = " + Integer.toString(requestCode), this);
        if (requestCode == m_nEnrollRtnCode) {
            // After CertificateEnrollTask
            Log.i("CertLoginActivity","REC CODE = " + Integer.toString(resultCode));
            if (resultCode != 0) {
                ElementApplyManager mgr = new ElementApplyManager(getApplicationContext());
                mgr.updateElementCertificate(element);
                AlarmReceiver alarm = new AlarmReceiver();
                alarm.setOnetimeTimer(getApplicationContext(), String.valueOf(element.getId()));
                Intent intent = new Intent(getApplicationContext(), CompleteUsingProceduresActivity.class);
                intent.putExtra("ELEMENT_APPLY", element);
                StringList.backToList = "1";
                finish();
                startActivity(intent);
            } else {
                StringList.backToList = "1";
                finish();
            }
        } else if (requestCode == m_nMDM_RequestCode) {
            if (resultCode == RESULT_OK) {
                SetMDM();
                SetScepItem();
                SetScepWifi();
                new CertificateEnrollTask().execute(scepRequester);
            } else {
                finish();
            }
        }
    }

    private void SetScepWifi() {
        // 1. WifiControlインスタンス取得
        WifiControl wifi = new WifiControl(this);

        // 2. GetWifiDictList取得
        List<XmlDictionary> wifi_list = m_p_aided_profile.GetWifiDictList();

        // 3. ループしてWifiControl::SetWifiListを実行
        if(!wifi_list.isEmpty()) {
            for(int i = 0; wifi_list.size() > i; i++){
                XmlDictionary one_piece = wifi_list.get(i);
                wifi.SetWifiList(one_piece);
            }
        }

        // 3.2 CA_CertとUser_Certを設定する.
        wifi.SetCaCert(m_strCertArias);
        wifi.SetUserCert(m_InformCtrl.GetUserID());

        // 4. WifiControl::PublicConnect(WifiControl.SCEP_WIFI)を実行
        if(wifi.PublicConnect(WifiControl.SCEP_WIFI) == false) {
            return;
        }
    }

    // MDMのチェックインの呼び出し
    private void CallMDMCheckIn() {
        LogCtrl.Logger(LogCtrl.m_strDebug, "CertLoginActivity "+ "CallMDMActivity()", this);

        // 古い情報をチェックアウト (この段階では設定ファイルは古い情報のまま)
        OldMdmCheckOut();

        m_DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        m_DeviceAdmin = new ComponentName(StartUsingProceduresActivity.this, EpsapAdminReceiver.class);
        mdmctrl = new MDMControl(this, m_InformCtrl.GetAPID());

        if (isDeviceAdmin() == false) {
            addDeviceAdmin();
        }
    }

    private void OldMdmCheckOut() {
        String filedir = "/data/data/" + getPackageName() + "/files/";

        java.io.File filename_mdm = new java.io.File(filedir + StringList.m_strMdmOutputFile);
        if(filename_mdm.exists()) {
            LogCtrl.Logger(LogCtrl.m_strInfo, "MDMCheckinActivity "+ "OldMdmCheckOut()", this);
            MDMFlgs mdm = new MDMFlgs();
            boolean bRet = mdm.ReadAndSetScepMdmInfo(this);
            if(mdm.GetCheckOut() == true) {
                MDMControl.CheckOut(mdm, this);
            }

            MDMControl mdmctrl = new MDMControl(this, mdm.GetUDID());	// この時点でサービスを止める
            filename_mdm.delete();
        }
    }

    // MDMのチェックインおよび、定期通信サービススレッドの起動
    // HTTP通信を行うため、スレッドから呼び出されること
    private void SetMDM() {
        Log.d("MDMCheckinActivity", "SetMDM()");
        // 2. GetMDMDictionary
        XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();
        if (mdm_dict == null) {
            Log.d("CertLoginActivity", "SetMDM() No profile");
            return;
        }

        // 3. MDMFlgsにセット(MDMControlにMDMFlgs変数を持たせてそちらにやってもらう
        mdmctrl.SetMDMmember(mdm_dict);

        // 4. チェックイン(HTTP(S)) (新しいMDM設定情報もここでファイル保存する)
        boolean bret = mdmctrl.CheckIn(true);

        // 5. OKならスレッド起動...定期通信
        if(bret == false) {
            //	mdmctrl.SrartService();
            Log.e("MDMCheckinActivity::SetMDM", "Checkin err");
            return;
        }

        bret = mdmctrl.TokenUpdate();
        if(bret == false) {
            //	mdmctrl.SrartService();
            Log.e("MDMCheckinActivity::SetMDM", "TokenUpdate err");
            return;
        }
    }

    private void addDeviceAdmin() {
        Log.i("ProfileActivity", "addDeviceAdmin");
        // Launch the activity to have the user enable our admin.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, m_DeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Additional text explaining why this needs to be added.");
        startActivityForResult(intent, m_nMDM_RequestCode);
    }

    private boolean isDeviceAdmin() {
        return m_DPM.isAdminActive(m_DeviceAdmin);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
