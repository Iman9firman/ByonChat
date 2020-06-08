package com.byonchat.android.personalRoom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.database.DBPersonal;
import com.byonchat.android.personalRoom.database.Profile;
import com.byonchat.android.utils.HttpHelper;
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
import java.util.List;

/**
 * Created by lukma on 3/4/2016.
 */
public class FragmentMyProfile extends Fragment {

    private static String URL_GET_DATA_PROFILE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/api_view_profile.php";
    TextView mProfileStatus, mHashtag;
    DBPersonal db;
    ArrayList<Profile> profile;
    ArrayList<Profile> profileItems;
    String tag1 = "", tag2 = "", tag3 = "", tag4 = "", tag5 = "", tag6 = "", tag7 = "", tag8 = "";

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private String personal;

    RequestPersonal requestPersonal;
    private String is_cover_upload;

    public static FragmentMyProfile newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag) {
        FragmentMyProfile fragmentRoomTask = new FragmentMyProfile();
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
        personal = getArguments().getString("fl");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mProfileStatus = (TextView) profileView.findViewById(R.id.profileStatus);
        mHashtag = (TextView) profileView.findViewById(R.id.hashtag);

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

        return profileView;
    }

    private void refreshPersonal() {
        db.open();
        Cursor c = db.getStatusPersonal(myContact);
        if (c.getCount() > 0) {
            String status = c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_STATUS));
            if (status.equalsIgnoreCase(null) || status.equalsIgnoreCase("") || status.equalsIgnoreCase("null")) {
                mProfileStatus.setText("I Love Byonchat");
            } else {
                mProfileStatus.setText(status);
            }
        }

        Cursor cc = db.getHashtagPersonal(myContact);
        if (cc.getCount() > 0) {
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG1)).equalsIgnoreCase("")) {
                tag1 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG1));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG2)).equalsIgnoreCase("")) {
                tag2 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG2));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG3)).equalsIgnoreCase("")) {
                tag3 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG3));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG4)).equalsIgnoreCase("")) {
                tag4 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG4));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG5)).equalsIgnoreCase("")) {
                tag5 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG5));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG6)).equalsIgnoreCase("")) {
                tag6 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG6));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG7)).equalsIgnoreCase("")) {
                tag7 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG7));
            }
            if (!cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG8)).equalsIgnoreCase("")) {
                tag8 = " #" + cc.getString(cc.getColumnIndexOrThrow(DBPersonal.PERSONAL_HASHTAG8));
            }
            String tag = tag1 + tag2 + tag3 + tag4 + tag5 + tag6 + tag7 + tag8;
            mHashtag.setText(tag);
        }
        db.close();
    }

    class RequestPersonal extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestPersonal(Context context) {
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
                        Profile hashtag = new com.byonchat.android.personalRoom.database.Profile(myContact, "", "", "", "", "", "", "", "");
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
}
