package com.byonchat.android.personalRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.utils.ShareFileFromAPI;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.EndlessRecyclerViewScrollListener;
import com.byonchat.android.utils.PermissionsUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;


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
        View view = inflater.inflate(R.layout.room_fragment_idcard, container, false);


        namecardMain = view.findViewById(R.id.namecard_main);
        imageLogo = view.findViewById(R.id.logo_ncl);
        imageLogo2 = view.findViewById(R.id.logo_2_ncl);
        textName = view.findViewById(R.id.tv_nama_ncl);
        textPhone = view.findViewById(R.id.tv_hp_ncl);
        textOutlet = view.findViewById(R.id.tv_outlet_ncl);
        textAddress = view.findViewById(R.id.tv_alamat_ncl);
        textWarn = view.findViewById(R.id.tv_warn_ncl);

        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }

        Cursor cur = db.getSingleRoom(username);
        if (cur.getCount() > 0) {
            final String officer = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "d");
            Log.w("alamak", officer);

            JSONObject jsonOfficer = null;
            try {
                jsonOfficer = new JSONObject(officer);
                //{"bc_user":"628589122112","name":"Iman","nik":"23432","divisi":"MARKETING","jabatan":"SALES CONSULTANT","lokasi":"HONDA PONDOK INDAH"}

                Picasso.with(mContext).load("https://bb.byonchat.com/mediafiles/profile_photo_special_rooms/icon_honda.png").into(imageLogo);

                textName.setText(Html.fromHtml("<b>" + jsonOfficer.getString("jabatan") + "</b><br>" + jsonOfficer.getString("name")));
                textPhone.setText("Hp. 0" + jsonOfficer.getString("bc_user").substring(2, jsonOfficer.getString("bc_user").length()));
                textOutlet.setText(jsonOfficer.getString("lokasi"));
                if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA PONDOK INDAH")) {
                    textAddress.setText(Html.fromHtml("Jalan Sultan Iskandar Muda No.kav 8, RT.1/RW.5\n" + "Telp.(021) 7223366\n"));
                    Picasso.with(mContext).load("https://i0.wp.com/www.honda-ikb.com/baru/wp-content/uploads/elementor/thumbs/Page-BgTexture-nqj4cccw6nbm654ntwowspt9pau9kujusoc9pb241s.jpg?zoom=2&w=1170").into(imageLogo2);
                    textWarn.setText("Honda Pondok Indah tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA FATMAWATI")) {
                    Picasso.with(mContext).load("https://i0.wp.com/www.honda-ikb.com/baru/wp-content/uploads/elementor/thumbs/Page-BgTexture-nqj4cccw6nbm654ntwowspt9pau9kujusoc9pb241s.jpg?zoom=2&w=1170").into(imageLogo2);
                    textAddress.setText(Html.fromHtml("Jl. RS. Fatmawati No. 21 Jakarta Selatan, 12410\n" + "Telp. 021 - 7656456 (SR) 021 - 7508895 (Bengkel)\n" + "021 - 7502678"));
                    textWarn.setText("Honda Fatmawati tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                } else if (jsonOfficer.getString("lokasi").equalsIgnoreCase("HONDA PRADANA SAWANGAN")) {
                    imageLogo2.setVisibility(View.GONE);
                    textAddress.setText(Html.fromHtml("Jl.Raya Cinangka No.9 Serua Bojong Sari\n" + "Depok-Jawa Barat 16517\n" + "Telp. (021) 3049 8889,(021) 3049 9990\n" + "Fax (021) 3042 8889"));
                    textWarn.setText("Honda Pradana Sawangan tidak bertanggung jawab apabila customer melakukan pembayaran apapun melalui sales baik secara tunai maupun transfer ke rekening pribadi sales.");
                }


                //


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
/*

        // TODO: 27/02/19 bisa download dan share  & UBAH JADI IMAGEVIEW
        try {

            com.hendrix.pdfmyxml.PdfDocument doc = new com.hendrix.pdfmyxml.PdfDocument(getContext());
            doc.addPage(createBitmapFromView(namecardMain));
            doc.setRenderWidth(630);
            doc.setRenderHeight(360);
            doc.setOrientation(com.hendrix.pdfmyxml.PdfDocument.A4_MODE.LANDSCAPE);
            doc.setProgressTitle(R.string.crop__saving);
            doc.setProgressMessage(R.string.crop__wait);
            doc.setFileName("namecardnewbyon");
            File pdf = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            doc.setSaveDirectory(pdf);
            doc.setInflateOnMainThread(false);
            doc.setListener(new com.hendrix.pdfmyxml.PdfDocument.Callback() {
                @Override
                public void onComplete(File file) {
                    Log.w(TAG, "onComplete: "+file.getAbsolutePath());
                }

                @Override
                public void onError(Exception e) {
                    Log.w(TAG, "onError: "+e.getLocalizedMessage());
                }
            });

            doc.createPdf(getContext());

            Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"Err : "+e,Toast.LENGTH_LONG).show();
        }

//        imCard = (ImageView) sss.findViewById(R.id.id_cards);
        big_share = (FloatingActionButton) view.findViewById(R.id.main_share);
//        card_share = (FloatingActionButton) sss.findViewById(R.id.card_share);
//        merge_share = (FloatingActionButton) sss.findViewById(R.id.all_share);
//
        big_share.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (card_share.getVisibility() == View.GONE) {
                    card_share.setVisibility(View.VISIBLE);
                    merge_share.setVisibility(View.VISIBLE);
                } else {
                    card_share.setVisibility(View.GONE);
                    merge_share.setVisibility(View.GONE);
                }


            }
        });

        merge_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFile();
            }
        });

        card_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentd = new Intent(mContext, ShareFileFromAPI.class);
                intentd.putExtra("path", file_kartu);
                intentd.putExtra("nama_file", title);
                mContext.startActivity(intentd);
            }
        });
*/


        return view;
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


    protected void addFile() {
        if (PermissionsUtil.hasPermissions(getActivity(), FILE_PERMISSION)) {
            Intent intentd = new Intent(mContext, ShareFileFromAPI.class);
            intentd.putExtra("path", CardLink.get(0));
            intentd.putExtra("card", CardLink.get(0));
            intentd.putExtra("nama_file", CardLink.get(1));
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