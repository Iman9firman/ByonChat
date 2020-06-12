package com.byonchat.android.ISSActivity.Requester;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.R;
import com.byonchat.android.utils.ClientSSLSocketFactory;

import java.util.HashMap;
import java.util.Map;

public class DialogCancelReliever extends Dialog {
    private RadioButton rb1, rb2, rb3, rb4, rb5, rb6;
    private Activity activity;
    private RadioGroup radioGroup;
    private  RadioButton radioButton;
    private Button ya, tidak;
    private TextView tvTitle;
    private String dataId;

    public DialogCancelReliever(Activity activity, String dataId) {
        super(activity);
        this.activity = activity;
        this.dataId = dataId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_order);

        initViews();
        initSetViews();

        ya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButton(v);
            }
        });

        tidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initViews() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGrop);

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);
        rb5 = (RadioButton) findViewById(R.id.rb5);
        rb6 = (RadioButton) findViewById(R.id.rb6);

        ya = (Button) findViewById(R.id.btn_proceed);
        tidak = (Button) findViewById(R.id.btn_cancel);

        tvTitle = (TextView) findViewById(R.id.head_title);
    }

    private void initSetViews(){
        tvTitle.setText(activity.getResources().getString(R.string.reason_title));

        rb1.setText(activity.getResources().getString(R.string.reason1));
        rb2.setText(activity.getResources().getString(R.string.reason2));
        rb3.setText(activity.getResources().getString(R.string.reason3));
        rb4.setText(activity.getResources().getString(R.string.reason4));
        rb5.setText(activity.getResources().getString(R.string.reason5));
        rb6.setText(activity.getResources().getString(R.string.reason6));
    }

    private void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

//        Toast.makeText(activity,radioButton.getText().toString(),Toast.LENGTH_SHORT).show();
        Map<String, String> params = new HashMap<>();
        params.put("id", dataId);
        params.put("status", "5");
        if(radioButton.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.reason4))){
            params.put("opsi", "hapus");
        }if(radioButton.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.reason5))){
            params.put("opsi", "hapus");
        }if(radioButton.getText().toString().equalsIgnoreCase(activity.getResources().getString(R.string.reason6))){
            params.put("opsi", "hapus");
        }
        params.put("reason", radioButton.getText().toString());
        getDetail("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, true);
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        ProgressDialog rdialog = new ProgressDialog(activity);
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue(activity, new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    if (hide) {
                        Toast.makeText(activity, "sukses", Toast.LENGTH_SHORT).show();
                        ByonchatBaseMallKelapaGadingActivity ss = (ByonchatBaseMallKelapaGadingActivity) activity;
                        ss.finish();
                    }

                },
                error -> rdialog.dismiss()
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
