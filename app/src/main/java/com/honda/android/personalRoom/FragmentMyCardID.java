package com.honda.android.personalRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.utils.ShareFileFromAPI;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.EndlessRecyclerViewScrollListener;
import com.honda.android.utils.PermissionsUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.GONE;


@SuppressLint("ValidFragment")
public class FragmentMyCardID extends Fragment {
    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private Boolean personal;
    private static final String TAG = FragmentMyCardID.class.getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1000;
    private static final int RESULT_LOAD_IMAGE_REQUEST_CODE = 2000;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;
    private static final String IMAGE_DIRECTORY_NAME = "ByonChat";
    private ArrayList<String> CardLink = new ArrayList<>();
    private BotListDB db;
    private MessengerDatabaseHelper messengerHelper;
    private EndlessRecyclerViewScrollListener scrollListener;
    private static String URL_DELETE_PICTURE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/delete_photo.php";
    private Activity mContext;
    private ImageView imCard;
    private FloatingActionButton big_share, card_share, merge_share;
    public static int IMGS[] = {
            R.drawable.bt_add_image,
    };
    private static final String[] FILE_PERMISSION = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    CardView namecardMain;
    ImageView imageLogo;
    ImageView imageLogo2;
    TextView textName;
    TextView textPhone;
    TextView textOutlet;
    TextView textAddress;
    TextView textWarn;
    RelativeLayout vFrameCard;
    View view;

    public FragmentMyCardID(Activity ctx) {
        mContext = ctx;
    }

    public static FragmentMyCardID newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag, Activity act) {
        FragmentMyCardID fragmentRoomTask = new FragmentMyCardID(act);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        args.putBoolean("fla", flag);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("aa");
        urlTembak = getArguments().getString("bb");
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        myContact = getArguments().getString("ee");
        color = getArguments().getString("col");
        personal = getArguments().getBoolean("fla");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("aa", title);
        outState.putString("bb", urlTembak);
        outState.putString("cc", username);
        outState.putString("dd", idRoomTab);
        outState.putString("ee", myContact);
        outState.putString("col", color);
        outState.putBoolean("fla", personal);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        view = inflater.inflate(R.layout.room_fragment_idcard, container, false);


        ProgressDialog rdialog = new ProgressDialog(mContext);
        rdialog.setMessage("Loading your id card...");
        rdialog.setTitle("Please Wait");
        rdialog.show();

        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }

        Cursor cur = db.getSingleRoom(username);
        if (cur.getCount() > 0) {
            final String officer = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "d");
            if (officer.contains("HONDA SONIC BANDUNG") || officer.contains("HONDA SONIC BANDUNG")) {
                view = inflater.inflate(R.layout.room_fragment_idcard_bandung, container, false);
            }


            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (officer.contains("HONDA SONIC BANDUNG") || officer.contains("HONDA SONIC BANDUNG")) {
                        Log.w("kamhire", "sni1");
                        // TODO Share Button
                        //view = inflater.inflate(R.layout.room_fragment_idcard_bandung, container, false);

                        big_share = (FloatingActionButton) view.findViewById(R.id.main_share);
                        card_share = (FloatingActionButton) view.findViewById(R.id.card_share);
                        merge_share = (FloatingActionButton) view.findViewById(R.id.all_share);

                        namecardMain = view.findViewById(R.id.namecard_main);
                        imageLogo = view.findViewById(R.id.logo_ncl);
                        imageLogo2 = view.findViewById(R.id.logo_2_ncl);
                        textName = view.findViewById(R.id.tv_nama_ncl);
                        textPhone = view.findViewById(R.id.tv_hp_ncl);

                        textOutlet = view.findViewById(R.id.tv_outlet_ncl);
                        textAddress = view.findViewById(R.id.tv_alamat_ncl);
                        textWarn = view.findViewById(R.id.tv_warn_ncl);
                        vFrameCard = view.findViewById(R.id.frame_card);

                        Display display = getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width - 50, ViewGroup.LayoutParams.WRAP_CONTENT);
                        vFrameCard.setLayoutParams(params);

                        Picasso.with(mContext)
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
                                                rdialog.dismiss();
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
                        // TODO Share Button
                        big_share = (FloatingActionButton) view.findViewById(R.id.main_share);
                        card_share = (FloatingActionButton) view.findViewById(R.id.card_share);
                        merge_share = (FloatingActionButton) view.findViewById(R.id.all_share);

                        namecardMain = view.findViewById(R.id.namecard_main);
                        imageLogo = view.findViewById(R.id.logo_ncl);
                        imageLogo2 = view.findViewById(R.id.logo_2_ncl);
                        textName = view.findViewById(R.id.tv_nama_ncl);
                        textPhone = view.findViewById(R.id.tv_hp_ncl);
                        textOutlet = view.findViewById(R.id.tv_outlet_ncl);
                        textAddress = view.findViewById(R.id.tv_alamat_ncl);
                        textWarn = view.findViewById(R.id.tv_warn_ncl);
                        vFrameCard = view.findViewById(R.id.frame_card);

                        Display display = getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width - 50, ViewGroup.LayoutParams.WRAP_CONTENT);
                        vFrameCard.setLayoutParams(params);

                        Picasso.with(mContext)
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
                                                    Picasso.with(mContext).load("https://bb.byonchat.com/bc_voucher_client/public/list_task/document_preview/honda_iso.jpg").into(imageLogo2);
                                                    textWarn.setText("Honda Pondok Indah tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA FATMAWATI")) {
                                                    textOutlet.setText(jsonOfficer.getString("lokasi") + "\n" + "PT. Istana Kebayoran Raya Motor");
                                                    Picasso.with(mContext).load("https://bb.byonchat.com/bc_voucher_client/public/list_task/document_preview/honda_iso.jpg").into(imageLogo2);
                                                    textAddress.setText("Jl. RS. Fatmawati No. 21 Jakarta Selatan, 12410\n" + "Telp. Showroom : (021) 7656456\n" + "Bengkel : (021) 7656437\n" + "Fax : (021) 7502678");
                                                    textWarn.setText("Honda Fatmawati tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA PRADANA SAWANGAN")) {
                                                    textOutlet.setText(jsonOfficer.getString("lokasi") + "\n" + "PT. Ambara Karya Pradana");
                                                    imageLogo2.setVisibility(GONE);
                                                    textAddress.setText(Html.fromHtml("Jl.Raya Cinangka No.9<br>Serua, Bojongsari<br>" + "Depok-Jawa Barat 16517<br>" + "Telp. (021) 3049 8889<br> <font color='#FFFFFF'>Telp.</font> (021) 3049 9990<br>" + "<font color='#FFFFFF'>Telp.</font> (021) 3042 8889"));
                                                    textWarn.setText("Honda Pradana Sawangan tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                                                }


                                                rdialog.dismiss();

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
            handler.postDelayed(runnable, 800);
        }


        return view;
    }

    private void shareCardID(String name) {
        Log.w("si jreng lewwat", vFrameCard.getHeight() + "::" + vFrameCard.getWidth());
        final String[] path_file = new String[1];
        // TODO: 27/02/19 bisa download dan share  & UBAH JADI IMAGEVIEW
        try {
//417::1030
            com.hendrix.pdfmyxml.PdfDocument doc = new com.hendrix.pdfmyxml.PdfDocument(getContext());
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
                    Log.w(TAG, "onComplete: " + file.getAbsolutePath());
                }

                @Override
                public void onError(Exception e) {
                    Log.w(TAG, "onError: " + e.getLocalizedMessage());
                }
            });

            doc.createPdf(getContext());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Err : " + e, Toast.LENGTH_LONG).show();
        }

        big_share.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (card_share.getVisibility() == GONE) {
                    card_share.setVisibility(View.VISIBLE);
                    merge_share.setVisibility(View.VISIBLE);
                } else {
                    card_share.setVisibility(GONE);
                    merge_share.setVisibility(GONE);
                }
            }
        });

        merge_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFile(path_file[0], "idcard_" + name + "_honda");
            }
        });

        card_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentd = new Intent(mContext, ShareFileFromAPI.class);
                intentd.putExtra("path", path_file[0]);
                intentd.putExtra("nama_file", "idcard_" + name + "_honda");
                mContext.startActivity(intentd);
            }
        });
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


    protected void addFile(String card, String name) {
        if (PermissionsUtil.hasPermissions(getActivity(), FILE_PERMISSION)) {
            Intent intentd = new Intent(mContext, ShareFileFromAPI.class);
            intentd.putExtra("path", card);
            intentd.putExtra("card", card);
            intentd.putExtra("nama_file", name);
            mContext.startActivity(intentd);
        } else {
            requestAddFilePermission();
        }
    }

    protected void requestAddFilePermission() {
        if (!PermissionsUtil.hasPermissions(getActivity(), FILE_PERMISSION)) {
            PermissionsUtil.requestPermissions(this, "Please grant this file import permission first!",
                    130, FILE_PERMISSION);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }
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


}