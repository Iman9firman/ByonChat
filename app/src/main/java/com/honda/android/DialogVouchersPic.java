package com.honda.android;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honda.android.list.utilLoadImage.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogVouchersPic extends DialogFragment {

    private FrameLayout mlinear_name;
    private TextView mVoucherValue, mTglValid, mSerialNumber;
    private Button mUserVoucher, mTransferVoucher, mParticipantOutlets, mTermsAndConditions;
    private EditText mInput_outlet_id;
    String id, judul, serial, tglvalid, nominal, bgcolor, textcolor, strbackground;
    Bitmap background;
    private com.joooonho.SelectableRoundedImageView mBgvouchers;
    private ImageView mBtnBack;
    LinearLayout topPanelLeft, topPanelcenter, topPanelRight;
    private Context mContext;
    private Resources mResources;
    public ImageLoader imgBackground;

    public static DialogVouchersPic newInstance(String id, String judul, String serial, String tglvalid, String nominal, String bgcolor, String textcolor, String strbackground, Bitmap background) {
       DialogVouchersPic f = new DialogVouchersPic();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        args.putString("pstrbackground", strbackground);
        args.putParcelable("pbackground", background);
        f.setArguments(args);
      return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.activity_vouchers_pic, container, false);
        mBgvouchers = (com.joooonho.SelectableRoundedImageView) dialog.findViewById(R.id.bg_vouchers);
        mUserVoucher = (Button) dialog.findViewById(R.id.use_voucher);
        mTransferVoucher = (Button) dialog.findViewById(R.id.transfer_voucher);
        mParticipantOutlets = (Button) dialog.findViewById(R.id.participant_outlets);
        mTermsAndConditions = (Button) dialog.findViewById(R.id.terms_and_conditions);
        mVoucherValue = (TextView) dialog.findViewById(R.id.voucher_value);
        mTglValid = (TextView) dialog.findViewById(R.id.tgl_valid);
        mSerialNumber = (TextView) dialog.findViewById(R.id.serial_number);
        mBtnBack = (ImageView) dialog.findViewById(R.id.btn_back);

        mContext = getContext().getApplicationContext();
        mResources = getResources();


        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
        mVoucherValue.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));
        mSerialNumber.setText("SN : " + serial);
        mTglValid.setText("Valid until " + tglvalid);


        mUserVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogVouchersPic");
//                    if(prev != null){
//                        prev.isRemoving();
//                    }
                    ft.addToBackStack(null);

                    DialogFragment testDialog = DialogUseVoucherOne.newInstance(id, judul, serial, tglvalid, nominal, bgcolor, textcolor, strbackground);
                    testDialog.setRetainInstance(true);
                    testDialog.show(ft, "DialogUseVoucher1");
                }
            }
        });

        mTransferVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogVouchersPic");
                    ft.addToBackStack(null);

                    DialogFragment testDialog = DialogTransferVoucherOne.newInstance(id, judul, serial, tglvalid, nominal, bgcolor, textcolor, strbackground);
                    testDialog.setRetainInstance(true);
                    testDialog.show(ft, "DialogTransferVoucher1");
                }
            }
        });

        mParticipantOutlets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogVouchersPic");
                    ft.addToBackStack(null);

                    DialogFragment testDialog = DialogVoucherParticipantOutlets.newInstance(id, judul, serial, tglvalid, nominal, bgcolor, textcolor);
                    testDialog.setRetainInstance(true);
                    testDialog.show(ft, "DialogVoucherParticipantOutlets");
                }
            }
        });

        mTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogVouchersPic");
                    ft.addToBackStack(null);

                    DialogFragment testDialog = DialogVoucherTermsAndConditions.newInstance(id, judul, serial, tglvalid, nominal, bgcolor, textcolor);
                    testDialog.setRetainInstance(true);
                    testDialog.show(ft, "DialogVoucherTermsAndConditions");
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        mBgvouchers.setImageBitmap(background);

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
        bgcolor = getArguments().getString("pbgcolor");
        textcolor = getArguments().getString("ptextcolor");
        strbackground = getArguments().getString("pstrbackground");
        background = getArguments().getParcelable("pbackground");

    }

    public interface FileDownloadListener{
        public void onFileDownload(String path);

    }
    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                URL url = new URL("");
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotated = Bitmap.createBitmap(bitMap, 0, 0, bitMap.getWidth(), bitMap.getHeight(),
                        matrix, true);

                return rotated;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mBgvouchers.setImageBitmap(result);
        }
    }
}
