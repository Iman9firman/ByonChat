package com.honda.android.personalRoom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.utils.UtilsPD;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by lukma on 3/17/2016.
 */
public class DialogFragmentEditPicture extends DialogFragment {
    private static final String TAG = FragmentMyPicture.class.getSimpleName();

    private ProgressBar progressBar;
    private String purl, purlthumb, ptitle, ptimestamp, pdesc, pmyuserid, puserid, pid, pflag, url;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private EditText mTitle, mDescription;
    private Button btnUpload, btnCancel;
    //    private DialogInterface.OnDismissListener onDismissListener;
    public static final String URL_UPDATE_PICTURE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/edit_photo.php";
    public static final String URL_UPDATE_VIDEO = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/edit_video.php";

    public static DialogFragmentEditPicture newInstance(String url, String urlthumb, String title, String timestamp, String descrip, String myuserid, String userid, String id, String flag) {
        DialogFragmentEditPicture f = new DialogFragmentEditPicture();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("urlthumb", urlthumb);
        args.putString("title", title);
        args.putString("timestamp", timestamp);
        args.putString("desc", descrip);
        args.putString("myuserid", myuserid);
        args.putString("userid", userid);
        args.putString("id", id);
        args.putString("flag", flag);
        f.setArguments(args);

        return f;
    }

    public void DialogFragmentEditPicture(String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String pmyuserid, String puserid, String pid, String pflag) {
        this.purl = purl;
        this.purlthumb = purlthumb;
        this.ptitle = ptitle;
        this.ptimestamp = ptimestamp;
        this.pdesc = pdesc;
        this.pmyuserid = pmyuserid;
        this.puserid = puserid;
        this.pid = pid;
        this.pflag = pflag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_detail_picture, container, false);
        mTitle = (EditText) dialog.findViewById(R.id.title);
        mDescription = (EditText) dialog.findViewById(R.id.description);
        imgPreview = (ImageView) dialog.findViewById(R.id.image_preview);
        txtPercentage = (TextView) dialog.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        btnUpload = (Button) dialog.findViewById(R.id.btnSUBMIT);
        btnCancel = (Button) dialog.findViewById(R.id.btnCANCEL);

        if (pflag.equalsIgnoreCase("true")) {
            url = purl;
        } else {
            url = purlthumb;
        }

        previewMedia();

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    dismiss();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {

                                         @Override
                                         public void onClick(View v) {
                                             if (pflag.equals("true")) {
                                                 if (mTitle.getText().toString().trim().length() == 0) {
                                                     mTitle.setError("Title is required!");
                                                 } else if (mDescription.getText().toString().trim().length() == 0) {
                                                     mDescription.setError("Description is required!");
                                                 } else {
                                                     new editPicture().execute(URL_UPDATE_PICTURE, puserid, mTitle.getText().toString().trim(), mDescription.getText().toString().trim(), pid);
                                                 }
                                             } else {
                                                 if (mTitle.getText().toString().trim().length() == 0) {
                                                     mTitle.setError("Title is required!");
                                                 } else if (mDescription.getText().toString().trim().length() == 0) {
                                                     mDescription.setError("Description is required!");
                                                 } else {
                                                     new editVideo().execute(URL_UPDATE_VIDEO, puserid, mTitle.getText().toString().trim(), mDescription.getText().toString().trim(), pid);

                                                 }
                                             }
                                         }
                                     }

        );

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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        purl = getArguments().getString("url");
//        purlthumb = getArguments().getString("urlthumb");
//        ptitle = getArguments().getString("title");
//        ptimestamp = getArguments().getString("timestamp");
//        pdesc = getArguments().getString("desc");
//        pmyuserid = getArguments().getString("myuserid");
//        puserid = getArguments().getString("userid");
//        pid = getArguments().getString("id");
//        pflag = getArguments().getString("flag");

    }

    public void previewMedia() {
        imgPreview.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        mTitle.setText(Html.fromHtml(ptitle).toString());
        mDescription.setText(Html.fromHtml(pdesc).toString());
        Glide.with(getActivity()).load(url).thumbnail(0.1f).into(imgPreview);
    }

//    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
//        this.onDismissListener = onDismissListener;
//    }

//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        if (onDismissListener != null) {
//            onDismissListener.onDismiss(dialog);
//        }
//    }

    class editPicture extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

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
            data.put("title", params[2]);
            data.put("description", params[3]);
            data.put("id_foto", params[4]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);

            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);
                String status = json.getString("status");
                String waktu = json.getString("updated_at");

                if (status.equalsIgnoreCase("1")) {
                    PictureDetailActivity pd = (PictureDetailActivity) getActivity();
                    pd.onUserSelectValue(mTitle.getText().toString(), waktu, mDescription.getText().toString());
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Failed to edit picture information", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.w("EditPicture", e.toString());
            }
        }
    }

    class editVideo extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

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
            data.put("title", params[2]);
            data.put("description", params[3]);
            data.put("id_video", params[4]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);

            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);
                String status = json.getString("status");
                String waktu = json.getString("updated_at");

                if (status.equalsIgnoreCase("1")) {
                    VideoDetailActivity pd = (VideoDetailActivity) getActivity();
                    pd.onUserSelectValue(mTitle.getText().toString(), waktu, mDescription.getText().toString());
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Failed to edit video information", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.w("EditVideo", e.toString());
            }
        }
    }
}
