package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.utils.HttpHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class DownloadFileByonchat extends AppCompatActivity {
    private static final String SD_CARD_FOLDER = "ByonChatDoc";
    private ProgressDialog mProgressDialog;
    private String DOWNLOAD_PATH;
    private String NAME_FILE;
    private String NEW_NAME_FILE;

    private class DownloadFile extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(DownloadFileByonchat.this);
            mProgressDialog.setTitle("downloading...");
            mProgressDialog.setMessage("Please wait a moment");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            try {
                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);
                File dbDownloadPath = new File(oldFolder, NEW_NAME_FILE);
                if (!oldFolder.exists()) {
                    oldFolder.mkdirs();
                }
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient client = HttpHelper.createHttpClient();
                HttpGet httpGet = new HttpGet(DOWNLOAD_PATH);
                InputStream content = null;
                try {
                    HttpResponse execute = client.execute(httpGet);
                    if (execute.getStatusLine().getStatusCode() != 200) {
                        return null;
                    }
                    content = execute.getEntity().getContent();
                    long downloadSize = execute.getEntity().getContentLength();
                    FileOutputStream fos = new FileOutputStream(dbDownloadPath.getAbsolutePath());
                    byte[] buffer = new byte[256];
                    int read;
                    long downloadedAlready = 0;
                    while ((read = content.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedAlready += read;
                        publishProgress((int) (downloadedAlready * 100 / downloadSize));
                    }
                    fos.flush();
                    fos.close();
                    content.close();
                    return true;
                } catch (Exception e) {
                    if (content != null) {
                        try {
                            content.close();
                        } catch (IOException e1) {
                        }
                    }
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(values[0]);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            if (result.equals(Boolean.TRUE)) {
                finish();
                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);
                File oldFile = new File(oldFolder, NEW_NAME_FILE);

                if(getIntent().getStringExtra("download") != null ){
                    try {
                        createPdfFooter(oldFile+"", oldFolder +"/ISS_"+NEW_NAME_FILE,getIntent().getStringExtra("download"));

                        Toast.makeText(getApplicationContext(),"Successfully Downloaded",Toast.LENGTH_SHORT).show();

                        if(oldFile.exists()){
                            try {
                                oldFile.delete();

                                if(getIntent().getStringExtra("approving") != null) {
                                    String apa = getIntent().getStringExtra("approving");
                                    Log.w("Parking lot prestice",apa);
                                    Map<String, String> params = new HashMap<>();
                                    params.put("id_history", apa);
                                    params.put("status", 1 + "");

                                    getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/Approval/update", params, true);
                                }
                            }catch (Exception e){
                                Log.e("Error hereee","get "+e.getMessage());
                            }
                        }

                    }catch (Exception e){
                        Log.e("Error hereee","get "+e.getMessage());
                    }
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = FileProvider.getUriForFile(DownloadFileByonchat.this, getPackageName() + ".provider", oldFile);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                    } else {

                        MimeTypeMap map = MimeTypeMap.getSingleton();
                        String ext = MimeTypeMap.getFileExtensionFromUrl(oldFile.getName());
                        String type = map.getMimeTypeFromExtension(ext);
                        if (type == null)
                            type = "*/*";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.fromFile(oldFile);
                        intent.setDataAndType(data, type);
                        startActivity(intent);
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "failed download, plese try again...", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_pnumber);
        LinearLayout linearLayoutContent = (LinearLayout) findViewById(R.id.linearLayoutContent);
        linearLayoutContent.setVisibility(View.GONE);

        DOWNLOAD_PATH = getIntent().getStringExtra("path");

        File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);

        NAME_FILE = DOWNLOAD_PATH.substring(DOWNLOAD_PATH.lastIndexOf('/'), DOWNLOAD_PATH.length());

        String extension = NAME_FILE.substring(NAME_FILE.lastIndexOf("."));

        if (getIntent().getStringExtra("nama_file") != null) {
            NEW_NAME_FILE = getIntent().getStringExtra("nama_file") + extension;
        } else {
            NEW_NAME_FILE = NAME_FILE;
        }


        File oldFile = new File(oldFolder, NEW_NAME_FILE);
        if (oldFile.exists()) {
            if (getIntent().getStringExtra("remove") == null) {
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Uri uri = FileProvider.getUriForFile(DownloadFileByonchat.this, getPackageName() + ".provider", oldFile);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                } else {

                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String ext = MimeTypeMap.getFileExtensionFromUrl(oldFile.getName());
                    String type = map.getMimeTypeFromExtension(ext);
                    if (type == null)
                        type = "*/*";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(oldFile);
                    intent.setDataAndType(data, type);
                    startActivity(intent);
                }
            } else {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getApplicationContext(), "Please insert memory card", Toast.LENGTH_LONG).show();
                    finish();
                }
                DownloadFile mDatabaseOpenTask = new DownloadFile();
                mDatabaseOpenTask.execute(new Context[]{this});
            }
        } else {

            if(getIntent().getStringExtra("download") != null ){

                File baru = new File(oldFolder, "ISS_"+NEW_NAME_FILE);

                if(baru.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = FileProvider.getUriForFile(DownloadFileByonchat.this, getPackageName() + ".provider", baru);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                    } else {

                        MimeTypeMap map = MimeTypeMap.getSingleton();
                        String ext = MimeTypeMap.getFileExtensionFromUrl(baru.getName());
                        String type = map.getMimeTypeFromExtension(ext);
                        if (type == null)
                            type = "*/*";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.fromFile(baru);
                        intent.setDataAndType(data, type);
                        startActivity(intent);
                    }
                }else {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(getApplicationContext(), "Please insert memory card", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    DownloadFile mDatabaseOpenTask = new DownloadFile();
                    mDatabaseOpenTask.execute(new Context[]{this});
                }
            }else {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getApplicationContext(), "Please insert memory card", Toast.LENGTH_LONG).show();
                    finish();
                }
                DownloadFile mDatabaseOpenTask = new DownloadFile();
                mDatabaseOpenTask.execute(new Context[]{this});
            }
        }
    }

    public void createPdfFooter(String originPath, String destPath, String text) throws IOException, DocumentException {

        PdfReader reader = new PdfReader(originPath);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destPath));
        Phrase footer = new Phrase(text, new Font(Font.FontFamily.UNDEFINED, 10,0,BaseColor.RED));
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            float x1 = 10;
            float x2 = reader.getPageSize(i).getWidth()-10;
            float y1 = reader.getPageSize(i).getBottom(10);
            float y2 = reader.getPageSize(i).getBottom(60);
            ColumnText ct = new ColumnText(stamper.getOverContent(i));
            ct.setSimpleColumn(footer,x1,y1,x2,y2,15,Element.ALIGN_RIGHT);
            ct.go();
        }
        stamper.close();
        reader.close();
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        Log.w("start prestice","FYTSX");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    Log.w("sukses prestice",response);
                    if (hide) {
                    }

                },
                error -> {
                    Log.w("error prestice",error);
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

}
