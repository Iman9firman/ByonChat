package com.byonchat.android.personalRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.adapter.ProfileAdapter;
import com.byonchat.android.personalRoom.alertDialog.AlertDialogManager;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.database.DBPersonal;
import com.byonchat.android.personalRoom.database.Profile;
import com.byonchat.android.personalRoom.model.ProfileModel;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.ValidationsKey;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukma on 4/7/2016.
 */
public class FragmentMyProfileMine extends Fragment {

    LinearLayout layoutRoot;
    AlertDialog alertD;
    EditText statusEdt;
    ImageView btSaveProfile, btEditProfile;
    TextView hashtag, statusTxt;
    FrameLayout frameEdit, frameSave;
    private static String url_save_deskripsi = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/my_profile.php";
    private static String URL_SAVE_TAG = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/add_tags.php";
    private static String URL_GET_DATA_PROFILE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/api_view_profile.php";
    Button btnEditHashtag, btnSave, btnCancel;
    DBPersonal db;
    EditText hashtag1, hashtag2, hashtag3, hashtag4, hashtag5, hashtag6, hashtag7, hashtag8;
    Button saveHashtag;
    String tag1 = "", tag2 = "", tag3 = "", tag4 = "", tag5 = "", tag6 = "", tag7 = "", tag8 = "";
    String dtag1 = "", dtag2 = "", dtag3 = "", dtag4 = "", dtag5 = "", dtag6 = "", dtag7 = "", dtag8 = "";
    String stag1 = "", stag2 = "", stag3 = "", stag4 = "", stag5 = "", stag6 = "", stag7 = "", stag8 = "";
    InputMethodManager inputManager;
    RequestPersonal requestPersonal;
    private String is_cover_upload;
    private ArrayList<Profile> profile;
    private ArrayList<Profile> profileItems;

    public String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private Boolean personal;

    public static FragmentMyProfileMine newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag) {
        FragmentMyProfileMine fragmentRoomTask = new FragmentMyProfileMine();
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        args.putBoolean("fl", flag);
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
        personal = getArguments().getBoolean("fl");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_profile_self, container, false);

        statusEdt = (EditText) view.findViewById(R.id.profileStatus);
        btSaveProfile = (ImageView) view.findViewById(R.id.btSaveProfile);
        btEditProfile = (ImageView) view.findViewById(R.id.btEditProfile);
        btnEditHashtag = (Button) view.findViewById(R.id.btnedit);
        frameEdit = (FrameLayout) view.findViewById(R.id.frameEdit);
        frameSave = (FrameLayout) view.findViewById(R.id.frameSave);
        statusTxt = (TextView) view.findViewById(R.id.statusTxt);
        hashtag = (TextView) view.findViewById(R.id.hashtag);
        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (db == null) {
            db = new DBPersonal(getActivity());
        }

        if (profile == null) {
            profile = new ArrayList<Profile>();
        }

        refreshPersonal();

        if (NetworkInternetConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline(getActivity().getApplicationContext())) {
            requestPersonal = new RequestPersonal(getActivity());
            requestPersonal.execute(myContact);
        } else {
            refreshPersonal();
        }

//        statusEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    hideKeyboard(v);
//                }
//            }
//        });

        frameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameEdit.setVisibility(View.GONE);
                frameSave.setVisibility(View.VISIBLE);
                showKeyboard(view);
                statusEdt.setFocusable(true);

                ((PersonalRoomActivity)getActivity()).toolbarCollapse();
            }
        });

        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameEdit.setVisibility(View.GONE);
                frameSave.setVisibility(View.VISIBLE);
                showKeyboard(view);
                statusEdt.setFocusable(true);

                ((PersonalRoomActivity)getActivity()).toolbarCollapse();
            }
        });

        btSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PersonalRoomActivity)getActivity()).toolbarOpen();
                if (NetworkInternetConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline(getActivity().getApplicationContext())) {
                    hideKeyboard(getView());
                    new saveStatus(statusEdt.getText().toString()).execute(url_save_deskripsi, myContact);
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEditHashtag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.dialog_edit_hashtag, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                layoutRoot = (LinearLayout) promptView.findViewById(R.id.layout_root);
                btnSave = (Button) promptView.findViewById(R.id.btnSAVE);
                btnCancel = (Button) promptView.findViewById(R.id.btnCANCEL);

                btnCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (alertD != null) {
                            alertD.cancel();
                        }
                    }
                });

                alertDialogBuilder.setView(promptView);
                alertD = alertDialogBuilder.create();
                alertD.show();

                hashtag1 = (EditText) promptView.findViewById(R.id.hashtag1);
                hashtag2 = (EditText) promptView.findViewById(R.id.hashtag2);
                hashtag3 = (EditText) promptView.findViewById(R.id.hashtag3);
                hashtag4 = (EditText) promptView.findViewById(R.id.hashtag4);
                hashtag5 = (EditText) promptView.findViewById(R.id.hashtag5);
                hashtag6 = (EditText) promptView.findViewById(R.id.hashtag6);
                hashtag7 = (EditText) promptView.findViewById(R.id.hashtag7);
                hashtag8 = (EditText) promptView.findViewById(R.id.hashtag8);

                db.open();
                Cursor cc = db.getHashtagPersonal(myContact);
                if (cc.getCount() > 0) {
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG1)).equalsIgnoreCase("")) {
                        hashtag1.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG1)));
                    } else {
                        hashtag1.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG2)).equalsIgnoreCase("")) {
                        hashtag2.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG2)));
                    } else {
                        hashtag2.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG3)).equalsIgnoreCase("")) {
                        hashtag3.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG3)));
                    } else {
                        hashtag3.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG4)).equalsIgnoreCase("")) {
                        hashtag4.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG4)));
                    } else {
                        hashtag4.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG5)).equalsIgnoreCase("")) {
                        hashtag5.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG5)));
                    } else {
                        hashtag5.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG6)).equalsIgnoreCase("")) {
                        hashtag6.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG6)));
                    } else {
                        hashtag6.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG7)).equalsIgnoreCase("")) {
                        hashtag7.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG7)));
                    } else {
                        hashtag7.setText("");
                    }
                    if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG8)).equalsIgnoreCase("")) {
                        hashtag8.setText(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG8)));
                    } else {
                        hashtag8.setText("");
                    }
                }
                db.close();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        db.open();
                        if ((hashtag1.getText().toString().trim().length() > 0)) {
                            tag1 = hashtag1.getText().toString().trim();
                            dtag1 = " #" + hashtag1.getText().toString().trim();
                            stag1 = hashtag1.getText().toString().trim();
//                            db.updateHashtag1(myContact, tag1);
                        } else {
//                            db.updateHashtag1(myContact, "");
                        }
                        if ((hashtag2.getText().toString().trim().length() > 0)) {
                            tag2 = hashtag2.getText().toString().trim();
                            dtag2 = " #" + hashtag2.getText().toString().trim();
                            stag2 = "|" + hashtag2.getText().toString().trim();
//                            db.updateHashtag2(myContact, tag2);
                        } else {
//                            db.updateHashtag2(myContact, "");
                        }
                        if ((hashtag3.getText().toString().trim().length() > 0)) {
                            tag3 = hashtag3.getText().toString().trim();
                            dtag3 = " #" + hashtag3.getText().toString().trim();
                            stag3 = "|" + hashtag3.getText().toString().trim();
//                            db.updateHashtag3(myContact, tag3);
                        } else {
//                            db.updateHashtag3(myContact, "");
                        }
                        if ((hashtag4.getText().toString().trim().length() > 0)) {
                            tag4 = hashtag4.getText().toString().trim();
                            dtag4 = " #" + hashtag4.getText().toString().trim();
                            stag4 = "|" + hashtag4.getText().toString().trim();
//                            db.updateHashtag4(myContact, tag4);
                        } else {
//                            db.updateHashtag4(myContact, "");
                        }
                        if ((hashtag5.getText().toString().trim().length() > 0)) {
                            tag5 = hashtag5.getText().toString().trim();
                            dtag5 = " #" + hashtag5.getText().toString().trim();
                            stag5 = "|" + hashtag5.getText().toString().trim();
//                            db.updateHashtag5(myContact, tag5);
                        } else {
//                            db.updateHashtag5(myContact, "");
                        }
                        if ((hashtag6.getText().toString().trim().length() > 0)) {
                            tag6 = hashtag6.getText().toString().trim();
                            dtag6 = " #" + hashtag6.getText().toString().trim();
                            stag6 = "|" + hashtag6.getText().toString().trim();
//                            db.updateHashtag6(myContact, tag6);
                        } else {
//                            db.updateHashtag6(myContact, "");
                        }
                        if ((hashtag7.getText().toString().trim().length() > 0)) {
                            tag7 = hashtag7.getText().toString().trim();
                            dtag7 = " #" + hashtag7.getText().toString().trim();
                            stag7 = "|" + hashtag7.getText().toString().trim();
//                            db.updateHashtag7(myContact, tag7);
                        } else {
//                            db.updateHashtag7(myContact, "");
                        }
                        if ((hashtag8.getText().toString().trim().length() > 0)) {
                            tag8 = hashtag8.getText().toString().trim();
                            dtag8 = " #" + hashtag8.getText().toString().trim();
                            stag8 = "|" + hashtag8.getText().toString().trim();
//                            db.updateHashtag8(myContact, tag8);
                        } else {
//                            db.updateHashtag8(myContact, "");
                        }
                        db.close();

                        String dtag = dtag1 + dtag2 + dtag3 + dtag4 + dtag5 + dtag6 + dtag7 + dtag8;
                        String stag = stag1 + stag2 + stag3 + stag4 + stag5 + stag6 + stag7 + stag8;
//                        hashtag.setText(dtag);
                        new saveTag().execute(URL_SAVE_TAG, myContact, stag);

                    }
                });
                db.close();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkInternetConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline(getActivity().getApplicationContext())) {
            requestPersonal = new RequestPersonal(getActivity());
            requestPersonal.execute(myContact);
        } else {
            refreshPersonal();
        }
    }

    private void refreshPersonal() {
        db.open();
        Cursor c = db.getStatusPersonal(myContact);
        if (c.getCount() > 0) {
            String status = c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_STATUS));
            if (status.equalsIgnoreCase(null) || status.equalsIgnoreCase("") || status.equalsIgnoreCase("null")) {
                statusEdt.setHint("Write your status...");
                statusTxt.setText("I love byonchat");
            } else {
                statusEdt.setText(status);
                statusTxt.setText(status);
            }
        }
        db.close();

        db.open();
        Cursor cc = db.getHashtagPersonal(myContact);
        if (cc.getCount() > 0) {
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG1)).equalsIgnoreCase(""))) {
                tag1 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG1));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG2)).equalsIgnoreCase(""))) {
                tag2 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG2));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG3)).equalsIgnoreCase(""))) {
                tag3 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG3));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG4)).equalsIgnoreCase(""))) {
                tag4 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG4));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG5)).equalsIgnoreCase(""))) {
                tag5 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG5));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG6)).equalsIgnoreCase(""))) {
                tag6 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG6));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG7)).equalsIgnoreCase(""))) {
                tag7 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG7));
            }
            if (!(cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG8)).equalsIgnoreCase(""))) {
                tag8 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG8));
            }
            String tag = tag1 + tag2 + tag3 + tag4 + tag5 + tag6 + tag7 + tag8;
            hashtag.setText(tag);
        }
        db.close();
    }

    private void setStatus(String status) {
        db.open();
        db.updateStatus(myContact, status);
        db.close();
    }

    class RequestPersonal extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestPersonal(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            JSONArray json_tag_room = null;
            JSONObject json_tag_name = null;
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("userid", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_DATA_PROFILE);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpClient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Check the Http Request for success
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();

                    Log.w("oneokrock", content);
                    db.open();
                    profile = db.retrieveProfile(myContact);
                    if (profile.size() > 0) {
                        db.deletePersonal(myContact);
                    }
                    db.close();

                    JSONObject json = new JSONObject(content);
                    JSONObject json_data = json.getJSONObject("data");
                    json_tag_room = json_data.getJSONArray("tag_room");

                    is_cover_upload = json_data.getString("is_cover_upload");

                    Profile profile = new com.byonchat.android.personalRoom.database.Profile(json.getString("userid"), username, json_data.getString("desc_profile"), json_data.getString("cover_photo"), "");
                    db.open();
                    db.insertProfile(profile);
                    db.close();

                    db.open();
                    profileItems = db.retrieveHashtag(myContact);
                    if (profileItems.size() > 0) {
                        if (json_tag_room.length() > 0) {
                            for (int i = 0; i < json_tag_room.length(); i++) {
                                json_tag_name = json_tag_room.getJSONObject(i);
                                String tagname = json_tag_name.getString("nm_tag");
                                String json_tag_room_array = json_tag_room.getString(i);
                                if ((json_tag_room_array.length() == 1 || json_tag_room_array.length() > 1) && i == 0) {
                                    db.updateHashtag1(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 2 || json_tag_room_array.length() > 2) && i == 1) {
                                    db.updateHashtag2(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 3 || json_tag_room_array.length() > 3) && i == 2) {
                                    db.updateHashtag3(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 4 || json_tag_room_array.length() > 4) && i == 3) {
                                    db.updateHashtag4(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 5 || json_tag_room_array.length() > 5) && i == 4) {
                                    db.updateHashtag5(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 6 || json_tag_room_array.length() > 6) && i == 5) {
                                    db.updateHashtag6(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 7 || json_tag_room_array.length() > 7) && i == 6) {
                                    db.updateHashtag7(json.getString("userid"), tagname);
                                } else if (json_tag_room_array.length() <= 8 && i == 7) {
                                    db.updateHashtag8(json.getString("userid"), tagname);
                                }
                            }
                        }
                    } else {
                        Profile hashtag = new Profile(myContact, "", "", "", "", "", "", "", "");
                        db.insertHashtag(hashtag);

                        if (json_tag_room.length() > 0) {
                            for (int i = 0; i < json_tag_room.length(); i++) {
                                json_tag_name = json_tag_room.getJSONObject(i);
                                String tagname = json_tag_name.getString("nm_tag");
                                String json_tag_room_array = json_tag_room.getString(i);
                                if ((json_tag_room_array.length() == 1 || json_tag_room_array.length() > 1) && i == 0) {
                                    db.updateHashtag1(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 2 || json_tag_room_array.length() > 2) && i == 1) {
                                    db.updateHashtag2(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 3 || json_tag_room_array.length() > 3) && i == 2) {
                                    db.updateHashtag3(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 4 || json_tag_room_array.length() > 4) && i == 3) {
                                    db.updateHashtag4(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 5 || json_tag_room_array.length() > 5) && i == 4) {
                                    db.updateHashtag5(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 6 || json_tag_room_array.length() > 6) && i == 5) {
                                    db.updateHashtag6(json.getString("userid"), tagname);
                                } else if ((json_tag_room_array.length() <= 7 || json_tag_room_array.length() > 7) && i == 6) {
                                    db.updateHashtag7(json.getString("userid"), tagname);
                                } else if (json_tag_room_array.length() <= 8 && i == 7) {
                                    db.updateHashtag8(json.getString("userid"), tagname);
                                }
                            }
                        }
                    }
                    db.close();
                } else {
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestPersonal = new RequestPersonal(getActivity());
                            requestPersonal.execute(key);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_LONG).show();
                }
            } else {
                refreshPersonal();
            }
        }
    }

    class saveStatus extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        String status;
        private ProgressDialog progressDialog;

        public saveStatus(String status) {
            super();
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(getActivity());
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            data.put("deskripsi", status);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals("1")) {
                setStatus(status);
                statusTxt.setText(status);
                frameSave.setVisibility(View.GONE);
                frameEdit.setVisibility(View.VISIBLE);
                return;
            } else {
                Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class saveTag extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        private ProgressDialog progressDialog;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(getActivity());
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            data.put("tag_name", params[2]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals("1")) {
                db.open();
                if ((hashtag1.getText().toString().trim().length() > 0)) {
                    tag1 = hashtag1.getText().toString().trim();
                    dtag1 = " #" + hashtag1.getText().toString().trim();
                    stag1 = hashtag1.getText().toString().trim();
                    db.updateHashtag1(myContact, tag1);
                } else {
                    db.updateHashtag1(myContact, "");
                }
                if ((hashtag2.getText().toString().trim().length() > 0)) {
                    tag2 = hashtag2.getText().toString().trim();
                    dtag2 = " #" + hashtag2.getText().toString().trim();
                    stag2 = "|" + hashtag2.getText().toString().trim();
                    db.updateHashtag2(myContact, tag2);
                } else {
                    db.updateHashtag2(myContact, "");
                }
                if ((hashtag3.getText().toString().trim().length() > 0)) {
                    tag3 = hashtag3.getText().toString().trim();
                    dtag3 = " #" + hashtag3.getText().toString().trim();
                    stag3 = "|" + hashtag3.getText().toString().trim();
                    db.updateHashtag3(myContact, tag3);
                } else {
                    db.updateHashtag3(myContact, "");
                }
                if ((hashtag4.getText().toString().trim().length() > 0)) {
                    tag4 = hashtag4.getText().toString().trim();
                    dtag4 = " #" + hashtag4.getText().toString().trim();
                    stag4 = "|" + hashtag4.getText().toString().trim();
                    db.updateHashtag4(myContact, tag4);
                } else {
                    db.updateHashtag4(myContact, "");
                }
                if ((hashtag5.getText().toString().trim().length() > 0)) {
                    tag5 = hashtag5.getText().toString().trim();
                    dtag5 = " #" + hashtag5.getText().toString().trim();
                    stag5 = "|" + hashtag5.getText().toString().trim();
                    db.updateHashtag5(myContact, tag5);
                } else {
                    db.updateHashtag5(myContact, "");
                }
                if ((hashtag6.getText().toString().trim().length() > 0)) {
                    tag6 = hashtag6.getText().toString().trim();
                    dtag6 = " #" + hashtag6.getText().toString().trim();
                    stag6 = "|" + hashtag6.getText().toString().trim();
                    db.updateHashtag6(myContact, tag6);
                } else {
                    db.updateHashtag6(myContact, "");
                }
                if ((hashtag7.getText().toString().trim().length() > 0)) {
                    tag7 = hashtag7.getText().toString().trim();
                    dtag7 = " #" + hashtag7.getText().toString().trim();
                    stag7 = "|" + hashtag7.getText().toString().trim();
                    db.updateHashtag7(myContact, tag7);
                } else {
                    db.updateHashtag7(myContact, "");
                }
                if ((hashtag8.getText().toString().trim().length() > 0)) {
                    tag8 = hashtag8.getText().toString().trim();
                    dtag8 = " #" + hashtag8.getText().toString().trim();
                    stag8 = "|" + hashtag8.getText().toString().trim();
                    db.updateHashtag8(myContact, tag8);
                } else {
                    db.updateHashtag8(myContact, "");
                }
                db.close();

                String dtag = dtag1 + dtag2 + dtag3 + dtag4 + dtag5 + dtag6 + dtag7 + dtag8;
                String stag = stag1 + stag2 + stag3 + stag4 + stag5 + stag6 + stag7 + stag8;
                hashtag.setText(dtag);

                alertD.cancel();
            } else {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(statusEdt, InputMethodManager.SHOW_IMPLICIT);
            statusEdt.setFocusable(true);
            statusEdt.requestFocus();
            statusEdt.setSelection(statusTxt.length());
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
