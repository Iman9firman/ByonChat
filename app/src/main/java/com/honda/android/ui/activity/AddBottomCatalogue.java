package com.honda.android.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.honda.android.DownloadFileByonchat;
import com.honda.android.R;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.view.View.GONE;

public class AddBottomCatalogue extends Activity {
    private BotListDB db;
    private MessengerDatabaseHelper messengerHelper;
    private FloatingActionButton big_share;

    CardView namecardMain;
    ImageView imageLogo;
    ImageView imageLogo2;
    TextView textName;
    TextView textPhone;
    TextView textOutlet;
    TextView textAddress;
    TextView textWarn;
    RelativeLayout hidden;
    RelativeLayout vFrameCard;
    View view;
    ProgressDialog rdialog;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_fragment_idcard);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }

        rdialog = new ProgressDialog(AddBottomCatalogue.this);
        rdialog.setMessage("Loading your id card...");
        rdialog.setTitle("Please Wait");
        rdialog.show();

        big_share = (FloatingActionButton) findViewById(R.id.main_share);
        hidden = (RelativeLayout) findViewById(R.id.hidenned);
        hidden.setVisibility(View.VISIBLE);
        big_share.setVisibility(GONE);

        if (db == null) {
            db = BotListDB.getInstance(AddBottomCatalogue.this.getApplicationContext());
        }

        Cursor cur = db.getSingleRoom(getIntent().getStringExtra("username"));
        if (cur.getCount() > 0) {
            final String officer = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "d");

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (officer.contains("HONDA SONIC BANDUNG") || officer.contains("HONDA SONIC BANDUNG")) {

                        namecardMain = findViewById(R.id.namecard_main);
                        imageLogo = findViewById(R.id.logo_ncl);
                        imageLogo2 = findViewById(R.id.logo_2_ncl);
                        textName = findViewById(R.id.tv_nama_ncl);
                        textPhone = findViewById(R.id.tv_hp_ncl);

                        textOutlet = findViewById(R.id.tv_outlet_ncl);
                        textAddress = findViewById(R.id.tv_alamat_ncl);
                        textWarn = findViewById(R.id.tv_warn_ncl);
                        vFrameCard = findViewById(R.id.frame_card);

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width - 50, ViewGroup.LayoutParams.WRAP_CONTENT);
                        vFrameCard.setLayoutParams(params);

                        Picasso.with(AddBottomCatalogue.this)
                                .load("https://bb.byonchat.com/mediafiles/profile_photo_special_rooms/icon_honda.png")
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        if (bitmap != null) {
                                            imageLogo.setImageBitmap(bitmap);
                                            JSONObject jsonOfficer = null;
                                            try {
                                                jsonOfficer = new JSONObject(officer);
                                                textName.setText(jsonOfficer.getString("jabatan"));
                                                textPhone.setText(jsonOfficer.getString("name").toUpperCase());
                                                if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA SONIC BANDUNG")) {
                                                    textOutlet.setText("Honda Sonic");
                                                    textAddress.setText("Sales - Service - Spare Parts - Body & Paint" + "\nJl. Soekarno Hatta No. 368 Bandung, 40235\n" + "Telp. : (022) 730 3333, Fax : (022) 521 2000\n" + "Hp  :" + " 0" + jsonOfficer.getString("bc_user").substring(2, jsonOfficer.getString("bc_user").length()));
                                                    textWarn.setText("Honda Sonic tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA AUTOBEST")) {
                                                    textOutlet.setText("Honda Autobest");
                                                    textAddress.setText("Sales - Service - Spare Parts - Body & Paint" + "\nJl. Soekarno Hatta No. 517 Bandung, 40235\n" + "Telp. : (022) 523 0000\n" + "Hp  :" + " 0" + jsonOfficer.getString("bc_user").substring(2, jsonOfficer.getString("bc_user").length()));
                                                    textWarn.setText("Honda Autobest tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                }

                                                shareCardID(jsonOfficer.getString("name"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                rdialog.dismiss();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });


                    } else {
                        namecardMain = findViewById(R.id.namecard_main);
                        imageLogo = findViewById(R.id.logo_ncl);
                        imageLogo2 = findViewById(R.id.logo_2_ncl);
                        textName = findViewById(R.id.tv_nama_ncl);
                        textPhone = findViewById(R.id.tv_hp_ncl);
                        textOutlet = findViewById(R.id.tv_outlet_ncl);
                        textAddress = findViewById(R.id.tv_alamat_ncl);
                        textWarn = findViewById(R.id.tv_warn_ncl);
                        vFrameCard = findViewById(R.id.frame_card);

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width - 50, ViewGroup.LayoutParams.WRAP_CONTENT);
                        vFrameCard.setLayoutParams(params);

                        Picasso.with(AddBottomCatalogue.this)
                                .load("https://bb.byonchat.com/mediafiles/profile_photo_special_rooms/icon_honda.png")
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        Log.w("si jreng lewwat", "bitmap grees");
                                        if (bitmap != null) {
                                            imageLogo.setImageBitmap(bitmap);

                                            JSONObject jsonOfficer = null;
                                            try {
                                                jsonOfficer = new JSONObject(officer);

                                                textName.setText(Html.fromHtml("<b>" + jsonOfficer.getString("jabatan") + "</b><br><br>" + jsonOfficer.getString("name")));
                                                textPhone.setText("Hp. 0" + jsonOfficer.getString("bc_user").substring(2, jsonOfficer.getString("bc_user").length()));
//                                    textOutlet.setText(jsonOfficer.getString("lokasi") +"\n");
                                                if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA PONDOK INDAH")) {
                                                    textOutlet.setText(jsonOfficer.getString("lokasi") + "\n" + "PT. Istana Kebayoran Raya Motor");
                                                    textAddress.setText("Jalan Sultan Iskandar Muda No.kav 8, RT.1/RW.5\n" + "Telp. Showroom : (021) 7223366\n" + "Bengkel : (021) 7223377\n" + "Fax : (021) 7223747");
                                                    Picasso.with(AddBottomCatalogue.this).load("https://bb.byonchat.com/bc_voucher_client/public/list_task/document_preview/honda_iso.jpg").into(imageLogo2);
                                                    textWarn.setText("Honda Pondok Indah tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA FATMAWATI")) {
                                                    textOutlet.setText(jsonOfficer.getString("lokasi") + "\n" + "PT. Istana Kebayoran Raya Motor");
                                                    Picasso.with(AddBottomCatalogue.this).load("https://bb.byonchat.com/bc_voucher_client/public/list_task/document_preview/honda_iso.jpg").into(imageLogo2);
                                                    textAddress.setText("Jl. RS. Fatmawati No. 21 Jakarta Selatan, 12410\n" + "Telp. Showroom : (021) 7656456\n" + "Bengkel : (021) 7656437\n" + "Fax : (021) 7502678");
                                                    textWarn.setText("Honda Fatmawati tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA PRADANA SAWANGAN")) {
                                                    textOutlet.setText(jsonOfficer.getString("lokasi") + "\n" + "PT. Ambara Karya Pradana");
                                                    imageLogo2.setVisibility(GONE);
                                                    textAddress.setText(Html.fromHtml("Jl.Raya Cinangka No.9<br>Serua, Bojongsari<br>" + "Depok-Jawa Barat 16517<br>" + "Telp. (021) 3049 8889<br> <font color='#FFFFFF'>Telp.</font> (021) 3049 9990<br>" + "<font color='#FFFFFF'>Telp.</font> (021) 3042 8889"));
                                                    textWarn.setText("Honda Pradana Sawangan tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                }

                                                shareCardID(jsonOfficer.getString("name"));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                rdialog.dismiss();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                    }
                }
            };
            handler.postDelayed(runnable, 2000);
        }
    }

    private void shareCardID(String name) {
        final String[] path_file = new String[1];
        // TODO: 27/02/19 bisa download dan share  & UBAH JADI IMAGEVIEW
        try {
            com.hendrix.pdfmyxml.PdfDocument doc = new com.hendrix.pdfmyxml.PdfDocument(this);
            doc.addPage(createBitmapFromView(namecardMain));
            doc.setRenderWidth(vFrameCard.getWidth());
            doc.setRenderHeight(vFrameCard.getHeight());
            doc.setOrientation(com.hendrix.pdfmyxml.PdfDocument.A4_MODE.LANDSCAPE);
            doc.setProgressTitle(R.string.crop__saving);
            doc.setProgressMessage(R.string.crop__wait);
            doc.setFileName("idcard_" + name + "_honda");

            File pdf = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ByonChatDoc");
            if (!pdf.exists()) {
                pdf.mkdir();
            }
            doc.setSaveDirectory(pdf);
            doc.setInflateOnMainThread(false);
            doc.setListener(new com.hendrix.pdfmyxml.PdfDocument.Callback() {
                @Override
                public void onComplete(File file) {
                    path_file[0] = file.getAbsolutePath();
                    nextToDownload(file.getAbsolutePath());
                }

                @Override
                public void onError(Exception e) {
                }
            });

            doc.createPdf(this);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Err : " + e, Toast.LENGTH_LONG).show();
        }
    }

    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public Bitmap createBitmapFromView(View v) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        v.setLayoutParams(params);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }

    private void nextToDownload(String path_card){
        rdialog.dismiss();
        Intent intent = new Intent(this, DownloadFileByonchat.class);
        intent.putExtra("add_merge",path_card);
        intent.putExtra("path", getIntent().getStringExtra("path"));
        intent.putExtra("nama_file", getIntent().getStringExtra("nama_file"));
        startActivity(intent);
        finish();
    }
}
