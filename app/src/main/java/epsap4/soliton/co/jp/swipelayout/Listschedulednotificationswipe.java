package epsap4.soliton.co.jp.swipelayout;
/**
 * Created by daoanhtung on 1/5/2017.
 */


import android.content.Context;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;
import epsap4.soliton.co.jp.dbalias.Itemschedulednotification;
import epsap4.soliton.co.jp.notification.SKMNotification;

public class Listschedulednotificationswipe extends ArrayAdapter<Itemschedulednotification> {
    private Context mContext;
    List<Itemschedulednotification> itemschedulednotificationList;
    private LayoutInflater layoutInflater = null;
    private ViewHolder viewHolder;
    private DatabaseHandler databaseHandler;


    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView textViewTileDate;
        public TextView textViewContentDate;
        public ImageView trashEvent;
        public SwipeRevealLayout swipeRevealLayout;

    }

    /**
     * This method contructor Listschedulednotificationswipe
     *
     * @param context
     * @param list_event
     */
    public Listschedulednotificationswipe(Context context, List<Itemschedulednotification> list_event) {
        super(context, 0);
        this.mContext = context;
        this.itemschedulednotificationList = list_event;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewHolder = new ViewHolder();
        databaseHandler = new DatabaseHandler(mContext);
    }

    @Override
    public int getCount() {
        if (itemschedulednotificationList != null) {
            return itemschedulednotificationList.size();
        } else {
            return 0;
        }
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_scheduled_setting, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textViewTileDate = (TextView) convertView.findViewById(R.id.textView_Tile_Date);
            viewHolder.textViewContentDate = (TextView) convertView.findViewById(R.id.textview_Content_Date);
            viewHolder.trashEvent = (ImageView) convertView.findViewById(R.id.trash_event);
            viewHolder.swipeRevealLayout = (SwipeRevealLayout) convertView.findViewById(R.id.swipe_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.swipeRevealLayout.setLockDrag(false);
        viewHolder.textViewTileDate.setText(itemschedulednotificationList.get(position).getTitleDate());
        viewHolder.textViewContentDate.setText(itemschedulednotificationList.get(position).getContentDate());
        viewHolder.trashEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemschedulednotificationList.get(position).getContentDate().compareTo(mContext.getString(R.string.label_date_final)) == 0) {
                    ItemAlias itemAlias = databaseHandler.getAlias(itemschedulednotificationList.get(position).getId());
                    displayAlertConfirmDelete(itemAlias, position, true);
                } else {
                    DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
                    ItemAlias itemAlias = databaseHandler.getAlias(itemschedulednotificationList.get(position).getId());
                    displayAlertConfirmDelete(itemAlias, position, false);
                }

            }
        });
        return convertView;
    }

    /**
     * This method view alert confirm delete notification
     *
     * @param itemAlias
     * @param position
     * @param flagNotification
     */
    private void displayAlertConfirmDelete(final ItemAlias itemAlias, final int position, final Boolean flagNotification) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(mContext.getString(R.string.label_msg_delete_confirm));
        alertDialogBuilder.setPositiveButton(mContext.getString(R.string.label_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (flagNotification) {
                            itemAlias.setStatusNotificationFinalDate(0);
                            databaseHandler.updateAlias(itemAlias);
                        } else {
                            itemAlias.setStatusNotificationBeforeFinalDate(0);
                            databaseHandler.updateAlias(itemAlias);
                        }
                        SKMNotification.updateStatusNotification(mContext, itemAlias);
                        itemschedulednotificationList.remove(position);
                        notifyDataSetChanged();
                    }
                });

        alertDialogBuilder.setNegativeButton(mContext.getString(R.string.label_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}

