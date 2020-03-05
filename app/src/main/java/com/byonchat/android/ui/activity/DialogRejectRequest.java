package com.byonchat.android.ui.activity;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.provider.MessengerDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.byonchat.android.utils.Utility.reportCatch;

public class DialogRejectRequest extends DialogFragment {

    String id_hps = "";
    long id = 0;
    String description = "";
    LinearLayout linearLayout;
    Button mCancel, mOkay;
    MessengerDatabaseHelper databaseHelper;
    UserDB dbHelper;
    DialogRejectListener listener;

    public static DialogRejectRequest newInstance(String id) {
        DialogRejectRequest f = new DialogRejectRequest();
        Bundle args = new Bundle();
        args.putString("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_hps = getArguments().getString("id");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);

        try {
            dbHelper = new UserDB(getContext());
            databaseHelper = MessengerDatabaseHelper.getInstance((FragmentActivity) getContext());

            linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);
            linearLayout.setPadding(16, 16, 16, 16);

            TextView textView = new TextView(getContext());
            textView.setText("Are you sure to delete your request?");
            textView.setTextSize(15);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.setMargins(30, 10, 30, 0);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 200);
            params2.setMargins(30, 10, 30, 0);

            linearLayout.addView(textView, params1);

            mCancel = (Button) dialog.findViewById(R.id.btn_proceed);
            mCancel.setText("Cancel");
            mOkay = (Button) dialog.findViewById(R.id.btn_cancel);
            mOkay.setText("Delete");

            mOkay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", id_hps);
                    getDialog().dismiss();
                    getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/Request/delete", params, true);
                    Toast.makeText(getActivity(), "Request Deleted", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onReject();
                    }
                }
            });

            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }

        return dialog;
    }

    private String jsonData(){

        JSONArray datas = new JSONArray();
        JSONObject approver = new JSONObject();
        try {
            approver.put("bc_user_approval",databaseHelper.getMyContact().getJabberId());
            approver.put("nama",dbHelper.getColValue(UserDB.EMPLOYEE_NAME));
            approver.put("nik",dbHelper.getColValue(UserDB.EMPLOYEE_NIK));
            approver.put("order","1");
            datas.put(approver);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas.toString();
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        try {
            ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
            rdialog.setMessage("Loading...");
            rdialog.show();

            RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity());

            StringRequest sr = new StringRequest(Request.Method.POST, Url,
                    response -> {
                        rdialog.dismiss();
                        if (hide) {
                            Log.w("sukses harusee", response);
                        }

                    },
                    error -> {
                        rdialog.dismiss();
                    }
            ) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    return params2;
                }
            };
            queue.add(sr);
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public DialogRejectListener getListener() {
        return listener;
    }

    public void setListener(DialogRejectListener listener) {
        this.listener = listener;
    }

    public interface DialogRejectListener{
        void onReject();
    }

}
