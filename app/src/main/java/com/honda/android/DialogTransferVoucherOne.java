package com.honda.android;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogTransferVoucherOne extends DialogFragment {

    private FrameLayout mlinear_name;
    private TextView mJudul, mSerialNumber, mSerialNumber2, mTanggalValid, mTanggalValid2, mAmount, mAmount2;
    private Button mSelectContact, mProceed, mCancel;
    private EditText mInputByonchatId;
    private String id, judul, serial, tglvalid, nominal, bcid, bgcolor, textcolor,background;
    private ImageView mBackground;

    public static DialogTransferVoucherOne newInstance(String id, String judul, String serial, String tglvalid, String nominal, String bgcolor, String textcolor, String background) {
        DialogTransferVoucherOne f = new DialogTransferVoucherOne();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        args.putString("pbackground", background);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_voucher_transfer_1, container, false);
        mlinear_name = (FrameLayout) dialog.findViewById(R.id.linear_name);
        mJudul = (TextView) dialog.findViewById(R.id.name) ;
        mBackground = (ImageView) dialog.findViewById(R.id.background);
        mSerialNumber = (TextView) dialog.findViewById(R.id.serial_number);
        mSerialNumber2 = (TextView) dialog.findViewById(R.id.serial_number2);
        mTanggalValid = (TextView) dialog.findViewById(R.id.tanggal_valid);
        mTanggalValid2 = (TextView) dialog.findViewById(R.id.tanggal_valid2);
        mAmount = (TextView) dialog.findViewById(R.id.amount);
        mAmount2 = (TextView) dialog.findViewById(R.id.amount2);
        mSelectContact = (Button) dialog.findViewById(R.id.btn_select_contact);
        mInputByonchatId = (EditText) dialog.findViewById(R.id.input_byonchat_id);
        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel = (Button) dialog.findViewById(R.id.btn_cancel);

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
        mSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogTransferVoucher1");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    DialogFragment newFragment = DialogVoucherSelectContacts.newInstance(id,judul,serial,tglvalid,nominal,bgcolor,textcolor,background);
                    newFragment.show(ft, "DialogVoucherSelectContact");
                }
            }
        });

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bcid = mInputByonchatId.getText().toString();
                if(bcid.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Byonchat ID is required", Toast.LENGTH_SHORT).show();
                }else{
                    if (getDialog() != null) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("DialogTransferVoucher1");
                        if (prev != null) {
//                        ft.remove(prev);
                            DialogFragment df = (DialogFragment) prev;
                            df.dismiss();
                        }
                        ft.addToBackStack(null);

                        DialogFragment newFragment = DialogTransferVoucherTwo.newInstance(id,judul,serial,tglvalid,nominal,bcid,"", bgcolor,textcolor,background);
                        newFragment.show(ft, "DialogTransferVoucher2");
                    }
                }

            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
//        mlinear_name.setBackground(new ColorDrawable(getResources().getColor(R.color.color_primary_red_dark)));

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
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.getWindow().setLayout(500, 750);
//            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
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
        bgcolor = getArguments().getString("pbgcolor");
        textcolor = getArguments().getString("ptextcolor");
        background = getArguments().getString("pbackground");
    }
}
