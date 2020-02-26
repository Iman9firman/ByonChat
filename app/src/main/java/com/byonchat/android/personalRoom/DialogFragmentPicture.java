package com.byonchat.android.personalRoom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

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

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.ImageCompress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by lukma on 3/17/2016.
 */
public class DialogFragmentPicture extends DialogFragment implements DialogInterface.OnDismissListener {
    private static final String TAG = FragmentMyPicture.class.getSimpleName();

    private ProgressBar progressBar;
    private String path = null, userid;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private EditText mTitle, mDescription;
    private Button btnUpload, btnCancel;
    private DialogInterface.OnDismissListener onDismissListener;
    public static final String FILE_UPLOAD_URL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/upload_photo.php";
    private ImageCompress imageCompress;

    public static DialogFragmentPicture newInstance(String str, String uid) {
        DialogFragmentPicture f = new DialogFragmentPicture();
        Bundle args = new Bundle();
        args.putString("filePath", str);
        args.putString("userid", uid);
        f.setArguments(args);

        return f;
    }

    public void DialogFragmentPicture(String url, String uid) {
        this.path = url;
        this.userid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_detail_picture, container, false);
        imageCompress = new ImageCompress();
        mTitle = (EditText) dialog.findViewById(R.id.title);
        mDescription = (EditText) dialog.findViewById(R.id.description);
        imgPreview = (ImageView) dialog.findViewById(R.id.image_preview);
        txtPercentage = (TextView) dialog.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        btnUpload = (Button) dialog.findViewById(R.id.btnSUBMIT);
        btnCancel = (Button) dialog.findViewById(R.id.btnCANCEL);

        if (path != null) {
            previewMedia();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

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
                if (mTitle.getText().toString().trim().length() == 0) {
                    mTitle.setError("Title is required!");
                } else if (mDescription.getText().toString().trim().length() == 0) {
                    mDescription.setError("Description is required!");
                } else {
                    new UploadFileToServer().execute();
                }
            }
        });

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
//        filePath = getArguments().getString("filePath");
//        userid = getArguments().getString("userid");
    }

    public void previewMedia() {
        imgPreview.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        final Bitmap bitmap = BitmapFactory.decodeFile(
                imageCompress.compressImage(getContext(), path), options);
        imgPreview.setImageBitmap(bitmap);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private ProgressDialog Dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("uploading...");
            Dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            if (result.equalsIgnoreCase("1")) {
                dismiss();
            } else if (result.equalsIgnoreCase("0")) {
                Toast.makeText(getActivity(), "Failed to upload picture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }
    }

    private Bitmap resize(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bp = null;
        int orignalHeight = opts.outHeight;
        int orignalWidth = opts.outWidth;
        float maxHeight = (float) 600.0;
        float maxWidth = (float) 800.0;
        int resizeScale = 1;
        if (orignalWidth > maxWidth || orignalHeight > maxHeight) {
            final int heightRatio = Math.round((float) orignalHeight / maxHeight);
            final int widthRatio = Math.round((float) orignalWidth / maxWidth);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        opts.inSampleSize = resizeScale;
        opts.inJustDecodeBounds = false;
        int bmSize = (orignalWidth / resizeScale) * (orignalHeight / resizeScale) * 4;
        if (Runtime.getRuntime().freeMemory() > bmSize) {
            bp = BitmapFactory.decodeFile(path, opts);
        } else
            return null;
        return bp;
    }
}
