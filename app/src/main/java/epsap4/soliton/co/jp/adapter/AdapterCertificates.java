package epsap4.soliton.co.jp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;;
import android.widget.TextView;

import java.security.cert.X509Certificate;

import java.util.List;

import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.CommonCertificate;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.notification.SKMNotification;

/**
 * Created by daoanhtung on 12/23/2016.
 */

public class AdapterCertificates extends ArrayAdapter<X509Certificate> {
    // Param in AdapterCertificates
    private List<X509Certificate> x509Certificates;

    /**
     * This Item View
     */
    public class ViewHolder {
        public TextView subjectDNCN;
        public TextView issuerDNCN;
        public TextView finalDateCertificate;

    }

    /**
     * This method contructor AdapterCertificates
     *
     * @param context
     * @param x509Certificates
     */
    public AdapterCertificates(Context context, List<X509Certificate> x509Certificates) {
        super(context, 0);
        this.x509Certificates = x509Certificates;
    }

    /**
     * This method get size x509Certificates
     *
     * @return
     */
    @Override
    public int getCount() {
        if (x509Certificates != null) {
            return x509Certificates.size();
        } else {
            return 0;
        }
    }

    /**
     * This method View item at position
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_certinstall_finish, parent, false);
            viewHolder.subjectDNCN = (TextView) convertView.findViewById(R.id.textviewSubjectDN);
            viewHolder.issuerDNCN = (TextView) convertView.findViewById(R.id.textviewIssuerDN);
            viewHolder.finalDateCertificate = (TextView) convertView.findViewById(R.id.textviewFinalDateCA);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Lookup view for data Certificate
        if (x509Certificates.get(position).getSubjectDN().getName() != null) {
            if (CommonCertificate.getPartName(x509Certificates.get(position).getSubjectDN().getName(), StringList.m_str_common_name_certificate) != null) {
                viewHolder.subjectDNCN.setText(CommonCertificate.getPartName(x509Certificates.get(position).getSubjectDN().getName(), StringList.m_str_common_name_certificate));
            }
        }
        if (x509Certificates.get(position).getIssuerDN().toString() != null) {
            if (CommonCertificate.getPartName(x509Certificates.get(position).getIssuerDN().getName(), StringList.m_str_common_name_certificate) != null) {
                viewHolder.issuerDNCN.setText(CommonCertificate.getPartName(x509Certificates.get(position).getIssuerDN().getName(), StringList.m_str_common_name_certificate));
            }
        }
        if (x509Certificates.get(position).getNotAfter().toString() != null) {

            String dateAfter = SKMNotification.format.format(x509Certificates.get(position).getNotAfter());
            if (x509Certificates.get(position).getNotAfter().getTime() > System.currentTimeMillis()) {
                viewHolder.finalDateCertificate.setText(dateAfter);
            } else {
                viewHolder.finalDateCertificate.setText(dateAfter + " " + getContext().getString(R.string.label_expired) + " !");
            }
        }
        // Return the completed view to render on screen
        return convertView;

    }
}
