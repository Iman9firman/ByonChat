package com.byonchat.android.personalRoom.utils;

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

import com.byonchat.android.R;
import com.byonchat.android.provider.DataBaseDropDown;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;


public class ShareFileFromAPI extends AppCompatActivity {
    private static final String SD_CARD_FOLDER = "ByonChatDoc";
    private ProgressDialog mProgressDialog;
    private String DOWNLOAD_PATH;
    private String CARD_PATH;
    private String NAME_FILE;

    private class DownloadFile extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ShareFileFromAPI.this);
            mProgressDialog.setTitle("Preparing file...");
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
                File dbDownloadPath = new File(oldFolder, NAME_FILE);
                if (!oldFolder.exists()) {
                    oldFolder.mkdirs();
                }
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                DefaultHttpClient client = new DefaultHttpClient(httpParameters);
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
                File oldFile = new File(oldFolder, NAME_FILE);
                String extension = NAME_FILE.substring(NAME_FILE.lastIndexOf("."));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uri = FileProvider.getUriForFile(ShareFileFromAPI.this, getPackageName() + ".provider", oldFile);
                    if(CARD_PATH == null) {
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
//                    share.setType("application/pdf");
                        if (extension.equals(".pdf")) {
                            share.setType("application/pdf");
                        } else if (extension.equals(".mp4")) {
                            share.setType("video/mp4");
                        } else {
                            share.setType("image/jpg");
                        }
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        share.setPackage("com.whatsapp"); //UNTUK SHARE KE WHATSAPP ONLY
                        startActivity(share);
                    }else{
                        prepareMerging(uri);
                    }
                } else {

                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String ext = MimeTypeMap.getFileExtensionFromUrl(oldFile.getName());
                    String type = map.getMimeTypeFromExtension(ext);
                    if (type == null)
                        type = "*/*";
                    if(CARD_PATH == null) {
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        Uri data = Uri.fromFile(oldFile);
                        share.setDataAndType(data, type);
                        startActivity(share);
                    }else{
                        Uri data = Uri.fromFile(oldFile);
                        prepareMerging(data);
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "file can't accessed, plese try again later", Toast.LENGTH_LONG).show();
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
        CARD_PATH = getIntent().getStringExtra("card");

        File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);

        NAME_FILE = DOWNLOAD_PATH.toString().substring(DOWNLOAD_PATH.toString().lastIndexOf('/'), DOWNLOAD_PATH.toString().length());

        File oldFile = new File(oldFolder, NAME_FILE);
        String extension = NAME_FILE.substring(NAME_FILE.lastIndexOf("."));
        if (oldFile.exists()) {
            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = FileProvider.getUriForFile(ShareFileFromAPI.this, getPackageName() + ".provider", oldFile);
//                intent.setData(uri);
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                startActivity(intent);
                if(CARD_PATH == null) {
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    if (extension.equals(".pdf")) {
                        share.setType("application/pdf");
                    } else if (extension.equals(".mp4")) {
                        share.setType("video/mp4");
                    } else {
                        share.setType("image/jpg");
                    }
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setPackage("com.whatsapp"); //UNTUK SHARE KE WHATSAPP ONLY
                    startActivity(share);
                }else{
                    prepareMerging(uri);
                }
            } else {

                MimeTypeMap map = MimeTypeMap.getSingleton();
                String ext = MimeTypeMap.getFileExtensionFromUrl(oldFile.getName());
                String type = map.getMimeTypeFromExtension(ext);
                if (type == null)
                    type = "*/*";
                if(CARD_PATH == null) {
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    Uri data = Uri.fromFile(oldFile);
                    share.setDataAndType(data, type);
                    startActivity(share);
                }else {
                    Uri data = Uri.fromFile(oldFile);
                    prepareMerging(data);
                }
            }

        } else {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(getApplicationContext(), "Please insert memory card", Toast.LENGTH_LONG).show();
                finish();
            }
            DownloadFile mDatabaseOpenTask = new DownloadFile();
            mDatabaseOpenTask.execute(new Context[]{this});
        }
    }

    private void prepareMerging(Uri file){
        File filer = new File(file.getPath());//create path from uri
        Log.w("APAPAAAANNNN NOOH",file.getPath() +" ---- "+CARD_PATH+" -0-=0-=0-=- "+filer.getPath());

        String fileOne = file.getPath().replace("/external_files","/storage/emulated/0");
        /*String fileTwo = CARD_PATH;
        File hasil = new File(Environment.getExternalStorageDirectory(),"hasil_merge.pdf");
        hasil.getParentFile().mkdirs();
        String fileHasil = hasil.getAbsolutePath();

        try {
            FileInputStream fisOne = new FileInputStream(fileOne);
            FileInputStream fisTwo = new FileInputStream(fileTwo);
            FileOutputStream fosHasil = new FileOutputStream(fileHasil);

            mergePdfFiles(fisOne,fisTwo,fosHasil);
        } catch (Exception e){
            e.printStackTrace();
            Log.w("sini", "Exc : "+ e.getMessage());
        }

        File outputFile = hasil;
        Log.w("FILE HASIL MERGE",outputFile.getPath());

//        Uri pdfUri = FileProvider.getUriForFile(getBaseContext(), getApplication().getApplicationContext().getPackageName() +
//                ".my.package.name.provider", outputFile);

        Uri pdfUri = FileProvider.getUriForFile(ShareFileFromAPI.this, getPackageName() + ".provider", outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, pdfUri);
//        share.setPackage("*");
        startActivity(share);

        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(outputFile.getName());
        String type = map.getMimeTypeFromExtension(ext);
        Log.w("INI NIHHHHHH TYPE NYEEE",type);*/
        Intent cloud = new Intent(this, CloudStorageActivity.class);
        cloud.putExtra("card", fileOne);
        startActivity(cloud);
    }
/*
    private void mergePdfFiles(FileInputStream isOne, FileInputStream isTwo,FileOutputStream hasil) throws Exception {
        PdfReader one = new PdfReader(isOne);
        PdfReader two = new PdfReader(isTwo);
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, hasil);
        document.open();
        copy.addDocument(one);
        copy.addDocument(two);
        document.close();
        one.close();
        two.close();
    }*/
}
