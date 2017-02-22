package epsap4.soliton.co.jp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.support.v7.app.NotificationCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.cert.*;
import java.util.ArrayList;
import java.util.List;

import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.SKMPreferences;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.CommonCertificate;
import epsap4.soliton.co.jp.adapter.AdapterCertificates;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;
import epsap4.soliton.co.jp.notification.SKMNotification;

/**
 * Created by daoanhtung on 12/22/2016.
 */

public class ListCertActivity extends Activity implements KeyChainAliasCallback {

    //UI param
    private TextView textAddCert;
    private ImageView imageViewInformation;
    private ListView listViewCertificate;
    private AdapterCertificates adapterCertificate;
    private Button buttonAddListCA;
    //DB
    private DatabaseHandler databaseHandler;
    private List<X509Certificate> listCetificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlistcert);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View customView = mInflater.inflate(R.layout.custom_actionbar, null);
        textAddCert = (TextView) customView.findViewById(R.id.textAddCert);
        imageViewInformation = (ImageView) customView.findViewById(R.id.imageViewInformation);
        listCetificate = new ArrayList<X509Certificate>();
        listViewCertificate = (ListView) findViewById(R.id.listviewCertInstallFinish);
        buttonAddListCA = (Button) findViewById(R.id.button_addlistca);
        databaseHandler = new DatabaseHandler(this);
        setOnClickListener();
        new listCetificateTask().execute();
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    /**
     * Update List Certificate, Certificate delete screen DetailCertActivity
     */
    @Override
    protected void onResume() {
        new listCetificateTask().execute();
        super.onResume();
    }

    /**
     * This method check alias have in DB, if alias no have in DB, alias add in DB
     *
     * @param alias
     */
    @Override
    public void alias(String alias) {
        if (alias != null) {
            List<ItemAlias> itemAliasList = databaseHandler.getAllAlias();
            Boolean flagadd = true;
            for (int i = 0; i < itemAliasList.size(); i++) {
                if (alias.compareTo(itemAliasList.get(i).getAlias()) == 0) {
                    flagadd = false;
                    break;
                }
            }
            if (flagadd) {
                addAlias(alias);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ListCertActivity.this, getString(R.string.msg_cert_already_in_list), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            LogCtrl.Logger(LogCtrl.m_strError, "User hit Disallow", ListCertActivity.this);
        }
    }

    /**
     * This method returns onclick buttonAddListCA, textAddCert, imageViewInformation
     */
    private void setOnClickListener() {
        buttonAddListCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCert();
            }
        });
        textAddCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCertActivity.this, EPS_ap400Activity.class);
                startActivity(intent);
                //finish();

            }
        });
        imageViewInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCertActivity.this, SettingActivity.class);
                startActivity(intent);
                // finish();
            }
        });
    }

    /**
     * Update List Certificate, display list certificate on screen
     */
    private class listCetificateTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            listCetificate.clear();
            List<ItemAlias> itemAliasList = databaseHandler.getAllAlias();
            ItemAlias itemAlias = new ItemAlias();
            for (int i = 0; i < itemAliasList.size(); i++) {
                itemAlias = itemAliasList.get(i);
                if (itemAlias.getFinalDate() <System.currentTimeMillis()) {
                    itemAlias.setStatusNotificationBeforeFinalDate(0);
                    itemAlias.setStatusNotificationFinalDate(0);
                    databaseHandler.updateAlias(itemAlias);
                }
                X509Certificate[] x509Certificates = CommonCertificate.getCertificateChain(ListCertActivity.this, itemAliasList.get(i).getAlias());
                if (x509Certificates != null) {
                    listCetificate.add(x509Certificates[0]);
                } else {
                    //Certificate was remove in Setting of deveice
                    //Cancle notification
                    SKMNotification.cancleAlarm(ListCertActivity.this, itemAliasList.get(i));
                    // Delete Certificate was remove in Setting of deveice in DB
                    databaseHandler.deleteAlias(itemAliasList.get(i));
                }

            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                adapterCertificate = new AdapterCertificates(ListCertActivity.this, listCetificate);
                listViewCertificate.setAdapter(adapterCertificate);
                listViewCertificate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ListCertActivity.this, DetailCertActivity.class);
                        intent.putExtra(StringList.m_str_InformCtrl, listCetificate.get(position));
                        startActivity(intent);
                        //finish();
                    }
                });
            }
        }
    }

    /**
     * This method returns dialog contain list certificate
     */
    private void chooseCert() {
        KeyChain.choosePrivateKeyAlias(this, this,
                new String[]{},
                null,
                "localhost",
                -1,
                "");
    }

    /**
     * This method add ItemAlias in DB
     *
     * @param alias
     */
    private void addAlias(String alias) {
        ItemAlias itemAlias = new ItemAlias();
        itemAlias.setAlias(alias);
        X509Certificate[] x509Certificates = CommonCertificate.getCertificateChain(ListCertActivity.this, alias);
        long timeInMilliseconds = x509Certificates[0].getNotAfter().getTime();
        if (timeInMilliseconds > System.currentTimeMillis()) {
            itemAlias.setFinalDate(timeInMilliseconds);
            Long before_timeInMilliseconds = Long.valueOf(SKMPreferences.getNumberBeforeFinalDate(ListCertActivity.this) * SKMNotification.DAY_MILLIS);
            itemAlias.setBeforeFinalDate(before_timeInMilliseconds);
            itemAlias.setNameSubjectDN(CommonCertificate.getPartName(x509Certificates[0].getSubjectDN().getName(), StringList.m_str_common_name_certificate));
            Boolean statusAllNotificationFinalDate = SKMPreferences.getStatusAllNotificationFinalDate(ListCertActivity.this);
            Boolean statusAllNotificationBeforeFinalDate = SKMPreferences.getStatusAllNotificationBeforeFinalDate(ListCertActivity.this);
            if (statusAllNotificationFinalDate) {
                itemAlias.setStatusNotificationFinalDate(1);
            } else {
                itemAlias.setStatusNotificationFinalDate(0);
            }
            if (statusAllNotificationBeforeFinalDate) {
                itemAlias.setStatusNotificationBeforeFinalDate(1);
            } else {
                itemAlias.setStatusNotificationBeforeFinalDate(0);
            }
            databaseHandler.addAlias(itemAlias);
            if (statusAllNotificationBeforeFinalDate) {
                itemAlias = databaseHandler.getAlias(timeInMilliseconds);
                SKMNotification.setRepeatingAlarm(ListCertActivity.this, itemAlias, timeInMilliseconds - before_timeInMilliseconds);
            } else {
                if (statusAllNotificationFinalDate) {
                    itemAlias = databaseHandler.getAlias(timeInMilliseconds);
                    SKMNotification.setRepeatingAlarm(ListCertActivity.this, itemAlias, timeInMilliseconds);
                }
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ListCertActivity.this, getString(R.string.label_date_final), Toast.LENGTH_SHORT).show();
                }
            });
        }
        new listCetificateTask().execute();
    }

}
