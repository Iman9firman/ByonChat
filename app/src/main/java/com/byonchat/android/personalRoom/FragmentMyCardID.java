package com.byonchat.android.personalRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    ImageView imageLogo;
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


        imageLogo = view.findViewById(R.id.logo_ncl);
        textName = view.findViewById(R.id.tv_nama_ncl);
        textPhone = view.findViewById(R.id.tv_hp_ncl);
        textOutlet = view.findViewById(R.id.tv_outlet_ncl);
        textAddress = view.findViewById(R.id.tv_alamat_ncl);
        textWarn = view.findViewById(R.id.tv_warn_ncl);

        // TODO: 27/02/19 bisa download dan share  & UBAH JADI IMAGEVIEW

        /*imCard = (ImageView) sss.findViewById(R.id.id_cards);
        big_share = (FloatingActionButton) sss.findViewById(R.id.main_share);
        card_share = (FloatingActionButton) sss.findViewById(R.id.card_share);
        merge_share = (FloatingActionButton) sss.findViewById(R.id.all_share);

        big_share.setOnClickListener(new View.OnClickListener() {
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
        });*/

        return view;
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
        if (personal) {

        } else {
            if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
            } else {
                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
                if (allRoomDetailFormWithFlag.size() > 0) {
                } else {
                    if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                        new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
                    }
                }
            }
        }
    }

    private void launchUploadActivity(boolean isImage) {
        if (personal) {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(fileUri.getPath(), myContact);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        } else {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(fileUri.getPath(), username);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        }
    }

    private void launchUploadActivityFromImgSelected(String sourceFileUri) {
        int day, month, year;
        int second, minute, hour;
        GregorianCalendar date = new GregorianCalendar();

        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);

        second = date.get(Calendar.SECOND);
        minute = date.get(Calendar.MINUTE);
        hour = date.get(Calendar.HOUR);

        String name = (hour + "" + minute + "" + second + "" + day + "" + (month + 1) + "" + year);
        String tag = name + ".jpg";
        String fileName = sourceFileUri.replace(sourceFileUri, tag);

        if (personal) {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(sourceFileUri, myContact);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        } else {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(sourceFileUri, username);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        }
    }

    class AmbilGambar extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        String idTab;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("bc_user", params[1]);
            data.put("username_room", params[2]);
            data.put("id_rooms_tab", params[3]);

//            idTab = params[3];
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            Log.w("Lewat --------------- 1", "iya");
            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                JSONArray dataJsonArr = null;
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userId = json.getString("id_rooms_tab");
                    String json_userRoom = json.getString("username_room");
                    db.deleteRoomsDetailPtabPRoom(idRoomTab, username);
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        String id = c.getString("id");
                        RoomsDetail orderModel = new RoomsDetail(id, json_userId, json_userRoom, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        db.insertRoomsDetail(orderModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");

                if (allRoomDetailFormWithFlag != null) {
                    try {

                        for (RoomsDetail ss : allRoomDetailFormWithFlag) {

                            JSONObject c = new JSONObject(ss.getContent());
                            String id = c.getString("id");
                            String title = c.getString("title");
                            String description = c.getString("description");
                            String file_kartu = c.getString("file");
                            String thump = c.getString("thumbnail");
                            String tgl_upload = c.getString("add_date");

                            /*CardLink.add(file_kartu);
                            CardLink.add(title);*/
                            Log.w("kambing", description);

                            JSONObject desc = new JSONObject(description);

                            Picasso.with(mContext).load(desc.getString("imageLogo")).into(imageLogo);
                            textName.setText(Html.fromHtml(desc.getString("textName")));
                            textPhone.setText("Hp. "+desc.getString("textPhone"));
                            textOutlet.setText(desc.getString("textOutlet"));
                            textAddress.setText(Html.fromHtml(desc.getString("textAddress")));
                            textWarn.setText(desc.getString("textWarn"));


                            card_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intentd = new Intent(mContext, ShareFileFromAPI.class);
                                    intentd.putExtra("path", file_kartu);
                                    intentd.putExtra("nama_file", title);
                                    mContext.startActivity(intentd);
                                }
                            });

                            imCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, DownloadFileByonchat.class);
                                    intent.putExtra("path", file_kartu);
                                    intent.putExtra("nama_file", title + "_idcard");
                                    mContext.startActivity(intent);
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //refresh(allRoomDetailFormWithFlag, true);
                }
            }
        }
    }

    /*public void refresh(ArrayList<RoomsDetail> s, boolean refresh) {
        try {

            for (RoomsDetail ss : s) {

                JSONObject c = new JSONObject(ss.getContent());
                String id = c.getString("id");
                String title = c.getString("title");
                String description = c.getString("description");
                String file_kartu = c.getString("file");
                String thump = c.getString("thumbnail");
//                String type = c.getString("type");
                String tgl_upload = c.getString("add_date");
                CardLink.add(file_kartu);
                CardLink.add(title);

                Log.w("SAYA yeyeye 1", c + "");
//                Log.w("SAYA yeyeye 2",item2+"");
                Log.w("SAYA yeyeye 3", file_kartu + "");
//                Log.w("SAYA yeyeye 4",c+"");

                Picasso.with(mContext).load(thump).into(imCard);

                card_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentd = new Intent(mContext, ShareFileFromAPI.class);
                        intentd.putExtra("path", file_kartu);
                        intentd.putExtra("nama_file", title);
                        mContext.startActivity(intentd);
                    }
                });

                imCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DownloadFileByonchat.class);
                        intent.putExtra("path", file_kartu);
                        intent.putExtra("nama_file", title + "_idcard");
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}