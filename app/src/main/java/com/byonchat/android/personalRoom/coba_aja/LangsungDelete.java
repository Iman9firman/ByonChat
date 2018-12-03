package com.byonchat.android.personalRoom.coba_aja;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.guna.ocrlibrary.OCRCapture;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.guna.ocrlibrary.OcrCaptureActivity.TextBlockObject;

public class LangsungDelete extends AppCompatActivity {

    private EditText setNIK;
    private ImageView scan,eKTP;
    private TextView realBaca;
    private LinearLayout hasil;
    private final int CAMERA_SCAN_TEXT = 0;
    private TextView satu,dua,tiga,empat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aaa_langsung_delete);
        setNIK = (EditText) findViewById(R.id.hasil_NIK);
        scan = (ImageView) findViewById(R.id.imageScan);
        eKTP = (ImageView) findViewById(R.id.eKTP);
        realBaca = (TextView) findViewById(R.id.realBaca);
        hasil = (LinearLayout) findViewById(R.id.hasil);
        hasil.setVisibility(View.GONE);

        satu = (TextView) findViewById(R.id.nama);
        dua = (TextView) findViewById(R.id.alamat);
        tiga = (TextView) findViewById(R.id.tglahir);
        empat = (TextView) findViewById(R.id.jenisk);

        scan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                OCRCapture.Builder(LangsungDelete.this)
                        .setUseFlash(false)
                        .setAutoFocus(true)
                        .buildWithRequestCode(CAMERA_SCAN_TEXT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CAMERA_SCAN_TEXT) {
                if (resultCode == CommonStatusCodes.SUCCESS) {
//                    textView.setText(data.getStringExtra(TextBlockObject));
                    vhiuo(data.getStringExtra(TextBlockObject));
                }
            }
        }
    }

    public void vhiuo(String text){
        String[] array =  text.split("\n");
        boolean error = true;
        Log.w("ALL",text);
        for (int oi = 0; oi<array.length;oi++){
            if(array[oi].matches(": (.*)")){
                array[oi] = array[oi].replace(": ","");
            }
            if(array[oi].matches(":(.*)")){
                array[oi] = array[oi].replace(":","");
            }
            if(array[oi].matches("[0-9](.*)[0-9]")){
                Log.w("Here",array[oi] + " whats -> "+array[oi].length());
                if(!array[oi].contains("-")){
                    if(array[oi].length() >10) {
                        String NIK = array[oi];
                        error = false;
                        hasil.setVisibility(View.VISIBLE);
                        Log.w("error 4",error+"");
                        if(NIK.contains(",")){  NIK = NIK.replace(",","");    }
                        if(NIK.contains(".")){  NIK = NIK.replace(".","");    }
                        if(NIK.contains(" ")){  NIK = NIK.replace(" ","");    }
                        if(NIK.contains("S")){  NIK = NIK.replace("S","5");   }
                        if(NIK.contains("L")){  NIK = NIK.replace("L","6");   }
                        if(NIK.contains("v")){  NIK = NIK.replace("v","4");   }
                        if(NIK.contains("O")){  NIK = NIK.replace("O","0");   }
                        if(NIK.contains("b")){  NIK = NIK.replace("b","6");   }
                        if(NIK.contains("G")){  NIK = NIK.replace("G","6");   }
                        if(NIK.contains("T")){  NIK = NIK.replace("T","9");   }
                        if(NIK.contains("o")){  NIK = NIK.replace("o","0");   }
                        if(NIK.contains("?")){  NIK = NIK.replace("?","7");   }
                        if(NIK.contains("%")){  NIK = NIK.replace("%","6");   }
                        if(NIK.contains("D")){  NIK = NIK.replace("D","0");   }
                        setNIK.setText( NIK );
                        realBaca.setText(array[oi]);
                        getNama(NIK);
                        Log.w("cek lewat 3",NIK);
                    }
                }
            }
        }
        if(error == true){
            showDialog(this,"NIK Error","We Can't found Your NIK, Please retry scanning process or Input your NIK manually");
        }
    }

    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OCRCapture.Builder(LangsungDelete.this)
                        .setUseFlash(false)
                        .setAutoFocus(true)
                        .buildWithRequestCode(CAMERA_SCAN_TEXT);
            }
        });
        builder.setNegativeButton("INPUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hasil.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    public void getNama(String nik){
//        String nikpalsu = "3307071806960004";
        String urlString = "https://infopemilu.kpu.go.id/pilkada2018/pemilih/dps/1/hasil-cari/resultDps.json?nik="+ nik
                +"&nama=&namaPropinsi=&namaKabKota=&namaKecamatan=&namaKelurahan=&notificationType=";
        if(nik.length()==16){
            new getJSONeKtp(urlString).execute();
        }
    }


    private class getJSONeKtp extends AsyncTask<String, Void, String> {
        private String vug;

        private getJSONeKtp(String text) {
            this.vug = text;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(vug);
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.w("GUNS and Roses",result);
            setData(result);
        }
    }

    public void setData(String result) {
        try {
            JSONObject start = new JSONObject(result);
            JSONArray data = start.getJSONArray("aaData");
            JSONObject jsonObject = data.getJSONObject(0);

            String nik =  jsonObject.getString("nik");
            String tanggal = nik.substring(6,8);
            String bulan = nik.substring(8,10);
            String tahun = nik.substring(10,12);

            Log.w("uhifewgfu",tanggal+"-"+bulan+"-"+tahun);

            satu.setText("Nama : "+ jsonObject.getString("nama"));
            dua.setText("Jenis Kelamin : "+ jsonObject.getString("jenisKelamin"));
            tiga.setText("Alamat : "+ jsonObject.getString("namaKelurahan")+", "+ jsonObject.getString("namaKecamatan")+", "
                    + jsonObject.getString("namaKabKota"));
            empat.setText("Tanggal Lahir : "+ jsonObject.getString("namaKabKota")+ ", "+tanggal+"-"+bulan+"-"+tahun);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = httpResponse.getStatusLine();
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
//            Log.d("TAG" + " GET", result);
//            Log.w("TAG" + " GET", result);
        } catch (Exception e) {
            Log.w("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
