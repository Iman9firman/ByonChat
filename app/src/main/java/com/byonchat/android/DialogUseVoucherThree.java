package com.byonchat.android;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogUseVoucherThree extends DialogFragment {

    FrameLayout mlinear_name;
    TextView mSerialNumber, mSerialNumber2, mTanggalValid, mTanggalValid2, mAmount, mAmount2, mOutlet_id, mJudul;
    private Button mProceed;
    String id, judul, serial, tglvalid, nominal, outletID, bgcolor, textcolor,background;
    private ImageView mBackground;

    public static DialogUseVoucherThree newInstance(String id, String judul, String serial, String tglvalid, String nominal, String outletID, String bgcolor, String textcolor, String background) {
        DialogUseVoucherThree f = new DialogUseVoucherThree();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("poutletID", outletID);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        args.putString("pbackground", background);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_voucher_use_3, container, false);
        mlinear_name = (FrameLayout) dialog.findViewById(R.id.linear_name);
        mJudul = (TextView) dialog.findViewById(R.id.name);
        mBackground = (ImageView) dialog.findViewById(R.id.background);
        mSerialNumber = (TextView) dialog.findViewById(R.id.serial_number);
        mSerialNumber2 = (TextView) dialog.findViewById(R.id.serial_number2);
        mTanggalValid = (TextView) dialog.findViewById(R.id.tanggal_valid);
        mTanggalValid2 = (TextView) dialog.findViewById(R.id.tanggal_valid2);
        mAmount = (TextView) dialog.findViewById(R.id.amount);
        mAmount2 = (TextView) dialog.findViewById(R.id.amount2);
        mOutlet_id = (TextView) dialog.findViewById(R.id.outlet_id);
        mProceed = (Button) dialog.findViewById(R.id.btn_continue);

        Glide.with(getContext()).load(background).into(mBackground);
        String color = "";
        if(bgcolor.equalsIgnoreCase("") || bgcolor.equalsIgnoreCase("null")){
            color = "1e8cc4";
        }else{
            color = bgcolor;
        }


        GradientDrawable drawable = (GradientDrawable) mlinear_name.getBackground();
        drawable.setColor(Color.parseColor("#"+color));

        String txtcolor = "";
        if(textcolor.equalsIgnoreCase("") || textcolor.equalsIgnoreCase("null")){
            txtcolor = "ffffff";
        }else{
            txtcolor = textcolor;
        }

        mJudul.setText(judul);
        mJudul.setTextColor(Color.parseColor("#"+txtcolor));
        mSerialNumber.setText(serial);
        mSerialNumber2.setText(serial);
        mTanggalValid.setText("Valid until "+tglvalid);
        mTanggalValid2.setText(tglvalid);
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
        mAmount2.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));
        mAmount.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));
        mOutlet_id.setText(outletID);

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("pid");
        judul = getArguments().getString("pjudul");
        serial = getArguments().getString("pserial");
        tglvalid = getArguments().getString("ptglvalid");
        nominal = getArguments().getString("pnominal");
        outletID = getArguments().getString("poutletID");
        bgcolor = getArguments().getString("pbgcolor");
        textcolor = getArguments().getString("ptextcolor");
        background = getArguments().getString("pbackground");
    }
}
