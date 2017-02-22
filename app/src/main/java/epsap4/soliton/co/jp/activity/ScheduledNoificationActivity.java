package epsap4.soliton.co.jp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import epsap4.soliton.co.jp.CommonCertificate;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;
import epsap4.soliton.co.jp.dbalias.Itemschedulednotification;
import epsap4.soliton.co.jp.notification.SKMNotification;
import epsap4.soliton.co.jp.swipelayout.Listschedulednotificationswipe;

/**
 * Created by daoanhtung on 1/4/2017.
 */

public class ScheduledNoificationActivity extends AppCompatActivity {

    //UI param
    private TextView textViewScheduledSetting;
    private TextView textviewOKDialog;
    private ListView listviewScheduled;
    private Listschedulednotificationswipe listschedulednotificationswipe;
    private LinearLayout lineLayoutScheduledNotification;
    private List<X509Certificate> listCetificate;
    //DB
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_notification);
        lineLayoutScheduledNotification = (LinearLayout) findViewById(R.id.lineLayoutScheduledNotification);
        textViewScheduledSetting = (TextView) findViewById(R.id.textViewScheduledSetting);
        textviewOKDialog = (TextView) findViewById(R.id.textviewOKDialog);
        textViewScheduledSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduledNoificationActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        listviewScheduled = (ListView) findViewById(R.id.listviewScheduled);
        listCetificate = new ArrayList<X509Certificate>();
        databaseHandler = new DatabaseHandler(this);
        new listCetificateTask().execute();

    }

    /**
     * Update List Certificate, display list certificate on screen
     */
    private class listCetificateTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            listCetificate.clear();
            List<ItemAlias> itemAliasList = databaseHandler.getAllAlias();
            // Delete Certificate was remove in Setting of deveice in DB
            itemAliasList = databaseHandler.getAllAlias();
            ItemAlias itemAlias = new ItemAlias();
            for (int i = 0; i < itemAliasList.size(); i++) {
                itemAlias = itemAliasList.get(i);
                if (itemAlias.getFinalDate() < System.currentTimeMillis()) {
                    itemAlias.setStatusNotificationBeforeFinalDate(0);
                    itemAlias.setStatusNotificationFinalDate(0);
                    databaseHandler.updateAlias(itemAlias);
                }
                X509Certificate[] x509Certificates = CommonCertificate.getCertificateChain(ScheduledNoificationActivity.this, itemAliasList.get(i).getAlias());
                if (x509Certificates != null) {
                    listCetificate.add(x509Certificates[0]);
                } else {
                    //Certificate was remove in Setting of deveice
                    //Cancle notification
                    SKMNotification.cancleAlarm(ScheduledNoificationActivity.this, itemAliasList.get(i));
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

                List<ItemAlias> itemAliasList = databaseHandler.getAllAlias();
                List<Itemschedulednotification> itemschedulednotificationList = new ArrayList<Itemschedulednotification>();
                for (int i = 0; i < itemAliasList.size(); i++) {
                    if (itemAliasList.get(i).getStatusNotificationFinalDate() == 1) {
                        Itemschedulednotification itemschedulednotification = new Itemschedulednotification();
                        itemschedulednotification.setId(itemAliasList.get(i).getiD());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(itemAliasList.get(i).getFinalDate());
                        String datefinal = SKMNotification.format.format(calendar.getTime());
                        itemschedulednotification.setTitleDate(datefinal);
                        itemschedulednotification.setContentDate(getString(R.string.label_date_final));
                        itemschedulednotificationList.add(itemschedulednotification);
                    }
                    if (itemAliasList.get(i).getStatusNotificationBeforeFinalDate() == 1) {
                        Itemschedulednotification itemschedulednotification = new Itemschedulednotification();
                        itemschedulednotification.setId(itemAliasList.get(i).getiD());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(itemAliasList.get(i).getFinalDate() - itemAliasList.get(i).getBeforeFinalDate());
                        String datefinal = SKMNotification.format.format(calendar.getTime());
                        itemschedulednotification.setTitleDate(datefinal);
                        int numberday = (int) (itemAliasList.get(i).getBeforeFinalDate() / (SKMNotification.DAY_MILLIS));
                        itemschedulednotification.setContentDate(getString(R.string.label_certificate_will_experied) + " " + String.valueOf(numberday) + " " + getString(R.string.label_days));
                        itemschedulednotificationList.add(itemschedulednotification);
                    }

                }
                if (itemschedulednotificationList.size() > 0) {
                    lineLayoutScheduledNotification.setVisibility(View.GONE);
                    textViewScheduledSetting.setEnabled(true);
                    listschedulednotificationswipe = new Listschedulednotificationswipe(ScheduledNoificationActivity.this, itemschedulednotificationList);
                    listviewScheduled.setAdapter(listschedulednotificationswipe);
                } else {
                    lineLayoutScheduledNotification.setVisibility(View.VISIBLE);
                    textViewScheduledSetting.setEnabled(false);
                    lineLayoutScheduledNotification.setAlpha(1.0f);
                    textviewOKDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        }
    }
}
