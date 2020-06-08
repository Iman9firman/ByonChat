package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.DownloadUtilsActivity;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.DatabaseKodePos;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.HttpHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentRoomAPIAPura extends Fragment {

    public static String myContact;
    public static String title;
    public static String urlTembak;
    public static String username;
    public static String idRoomTab;
    BotListDB db;
    LinearLayout linearLayout;
    private static final int REQ_LOCATION_SETTING = 1200;
    private static final int REQ_CAMERA = 1201;
    private static final int REQ_GALLERY = 1202;
    private static final int REQ_VIDEO = 1203;
    private final int PLACE_PICKER_REQUEST = 1209;
    private static final int REQ_GALLERY_VIDEO = 1204;
    private static final int SIGNATURE_ACTIVITY = 1205;
    private static final int PICK_FILE_REQUEST = 1206;
    private static final int PICK_ESTIMATION = 1208;
    private static final int OCR_REQUEST = 1211;
    private static final String MENU_GALLERY_TITLE = "Gallery";
    ProgressDialog progressDialog;
    private ArrayList<AttachmentAdapter.AttachmentMenuItem> curAttItems;
    String cameraFileOutput;
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy", Locale.getDefault());
    double latitude, longitude;
    Bitmap result = null;
    ImageView imageView[];
    LinearLayout linearEstimasi[];
    RatingBar rat[];
    EditText et[];
    TextView tp[];
    Map<Integer, List<String>> hashMap = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapOcr = new HashMap<Integer, List<String>>();
    Integer count ;
    boolean showButton = true;
    GPSTracker gps;
    EditText langlot;
    int mYear, mMonth, mDay;
    int dummyIdDate;
    private int attCurReq = 0;
    Boolean showDialog = true;
    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 2;
    private static ArrayList<AttachmentAdapter.AttachmentMenuItem> attCameraItems;
    private static ArrayList<AttachmentAdapter.AttachmentMenuItem> attVideoItems;


    public static FragmentRoomAPIAPura newInstance(String myc, String tit, String utm, String usr, String idrtab) {
        FragmentRoomAPIAPura fragmentRoomTask = new FragmentRoomAPIAPura();
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_api, container, false);
        linearLayout = (LinearLayout) sss.findViewById(R.id.linear);

        return sss;
    }


    private class posTask extends AsyncTask<String, String, String> {

        String error = "";

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
            //dialog.dismiss();
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, final String usr, final String idr) {
            // Create a new HttpClient and Post Header

            try {

                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpPost httppost = new HttpPost(valueIWantToSend);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("nama_barang","beras"));
                nameValuePairs.add(new BasicNameValuePair("tahun", "2016"));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();


                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                } else {
                    Toast.makeText(getActivity(), "Upload Gagal.", Toast.LENGTH_SHORT).show();
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Upload Gagal.", Toast.LENGTH_SHORT).show();
            } catch (ClientProtocolException e) {
                Toast.makeText(getActivity(), "Upload Gagal.", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Upload Gagal.", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated catch block
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
