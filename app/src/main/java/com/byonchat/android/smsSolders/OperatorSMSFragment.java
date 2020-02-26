package com.byonchat.android.smsSolders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;

import org.json.JSONObject;

/**
 * Created by byonc on 12/29/2017.
 */

@SuppressLint("ValidFragment")
public class OperatorSMSFragment extends Fragment {

    private Activity activity;
    private RelativeLayout vLinearKuota1;
    private TextView vTxtKuota1, vTxtKuota1Jumlah, vTxtEndPeriod1, vTxtEndPeriod1Angka;
    int vQuote = 0, vSms = 0, vPoint = 0;
    String vOperator = "No", vSchedule = "No", vStatus = "Not Active", vTglSetKuota, vTglMulai, vTglSelesai, vJamMulai, vJamSelesai;
    String data;

    public OperatorSMSFragment(Activity activity, String data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_fragment_sms, container, false);

        getData();
        setupView(view);

        return view;
    }

    void getData() {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(data);

            if (jObject.has("code")) {
                return;
            } else {
                String opId;
                opId = jObject.getString("opr_id");
                vQuote = Integer.valueOf(jObject.getString("kuota_sesama"));
                vSms = Integer.valueOf(jObject.getString("kuota_all"));
                vTglSetKuota = jObject.getString("tgl_set_kuota");
                vTglMulai = jObject.getString("tgl_mulai");
                vTglSelesai = jObject.getString("tgl_selesai");
                vJamMulai = jObject.getString("jam_mulai");
                vJamSelesai = jObject.getString("jam_selesai");
                vPoint = jObject.getInt("point");

                if (vQuote != 0 && vSms != 0)
                    vOperator = "All";
                else
                    vOperator = opId;

                vSchedule = "Yes";
                vStatus = "Active";
            }
        } catch (Exception e) {

        }
    }

    void setupView(View view) {
        vLinearKuota1 = (RelativeLayout) view.findViewById(R.id.linear_SMS_quote1);
        vTxtKuota1 = (TextView) view.findViewById(R.id.txtKuota1);
        vTxtKuota1Jumlah = (TextView) view.findViewById(R.id.txtKuota1Jumlah);
        vTxtEndPeriod1 = (TextView) view.findViewById(R.id.txtEndPeriod1);
        vTxtEndPeriod1Angka = (TextView) view.findViewById(R.id.txtEndPeriod1Angka);

        vLinearKuota1.setVisibility(View.VISIBLE);
        vTxtKuota1.setText("Operator");
        vTxtEndPeriod1.setVisibility(View.GONE);
        vTxtKuota1Jumlah.setText(vOperator);
        vTxtEndPeriod1Angka.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
