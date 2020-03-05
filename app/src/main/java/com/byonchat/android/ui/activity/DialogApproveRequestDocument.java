package com.byonchat.android.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.text.method.ScrollingMovementMethod;
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

public class DialogApproveRequestDocument extends DialogFragment {

    String username = "";
    String idTab = "";
    String nama_file = "";
    String date_file = "";
    String reason_file = "";
    String reqs_file = "";
    String id_history = "";

    long id = 0;
    String description = "";
    LinearLayout linearLayout;
    Button mCancel, mOkay;
    MessengerDatabaseHelper databaseHelper;
    UserDB dbHelper;

    DialogRefreshListener listener;

    public static DialogApproveRequestDocument newInstance(String username, String idTab, long idFile, String title, String description, String requester, String date, String reason, String id_history) {
        DialogApproveRequestDocument f = new DialogApproveRequestDocument();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("requester", requester);
        args.putString("idTab", idTab);
        args.putLong("id",idFile);
        args.putString("title", title);
        args.putString("description", description);
        args.putString("date",date);
        args.putString("alasan", reason);
        args.putString("id_hist", id_history);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            idTab = getArguments().getString("idTab");
            username = getArguments().getString("username");
            id = getArguments().getLong("id");
            nama_file = getArguments().getString("title");
            description = getArguments().getString("description");
            date_file = getArguments().getString("date");
            reason_file = getArguments().getString("alasan");
            reqs_file = getArguments().getString("requester");
            id_history = getArguments().getString("id_hist");
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);

        try {
            dbHelper = new UserDB(getContext());
            databaseHelper = MessengerDatabaseHelper.getInstance((FragmentActivity) getContext());

            linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);
            linearLayout.setPadding(16, 16, 16, 16);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            //Field 1
//        LinearLayout layout1 = new LinearLayout(getContext());
//        layout1.setOrientation(LinearLayout.HORIZONTAL);

            TextView fildNama = new TextView(getContext());
            fildNama.setText("Title : ");
            fildNama.setTextSize(15);

            TextView entryNama = new TextView(getContext());
            entryNama.setText(nama_file);
            entryNama.setTypeface(Typeface.DEFAULT_BOLD);
            entryNama.setTextSize(15);
            linearLayout.addView(fildNama, params0);
            linearLayout.addView(entryNama, params0);

            //Field 4
//        LinearLayout layout4 = new LinearLayout(getContext());
//        layout4.setOrientation(LinearLayout.HORIZONTAL);

            TextView fildReqs = new TextView(getContext());
            fildReqs.setText("Request by : ");
            fildReqs.setTextSize(15);

            TextView entryReqs = new TextView(getContext());
            entryReqs.setText(reqs_file);
            entryReqs.setTypeface(Typeface.DEFAULT_BOLD);
            entryReqs.setTextSize(15);
            TextView entryReqs2 = new TextView(getContext());
            entryReqs2.setText(reqs_file);
            entryReqs2.setTypeface(Typeface.DEFAULT_BOLD);
            entryReqs2.setTextSize(15);
            linearLayout.addView(fildReqs, params0);
            linearLayout.addView(entryReqs, params0);

            //Field 3
//        LinearLayout layout3 = new LinearLayout(getContext());
//        layout3.setOrientation(LinearLayout.HORIZONTAL);

            TextView fildDate = new TextView(getContext());
            fildDate.setText("Request Date : ");
            fildDate.setTextSize(15);

            TextView entryDate = new TextView(getContext());
            entryDate.setText(date_file);
            entryDate.setTypeface(Typeface.DEFAULT_BOLD);
            entryDate.setTextSize(15);
            linearLayout.addView(fildDate, params0);
            linearLayout.addView(entryDate, params0);

            //Field 5
//        LinearLayout layout5 = new LinearLayout(getContext());
//        layout5.setOrientation(LinearLayout.HORIZONTAL);

            TextView fildReason = new TextView(getContext());
            fildReason.setText("Information : ");
            fildReason.setTextSize(15);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 250);
            params1.setMargins(16, 16, 16, 16);

            TextView entryReason = new TextView(getContext());
            entryReason.setText(reason_file);
            entryReason.setTypeface(Typeface.DEFAULT_BOLD);
//        entryReason.setTextSize(15);
//        entryReason.setLines(6);
            entryReason.setPadding(10, 10, 10, 10);
//        entryReason.setMaxLines(8);
//        entryReason.setGravity(Gravity.TOP);
            entryReason.setBackground(getActivity().getResources().getDrawable(R.drawable.rounder_editext));
            entryReason.canScrollVertically(View.SCROLL_AXIS_VERTICAL);
            entryReason.setMovementMethod(new ScrollingMovementMethod());
            linearLayout.addView(fildReason, params0);
            linearLayout.addView(entryReason, params1);


//        linearLayout.addView(layout1, params1);
//        linearLayout.addView(layout3, params1);
//        linearLayout.addView(layout4, params1);
//        linearLayout.addView(layout5, params2);

            mCancel = (Button) dialog.findViewById(R.id.btn_proceed);
            mCancel.setText("Reject");
            mOkay = (Button) dialog.findViewById(R.id.btn_cancel);
            mOkay.setText("Approve");

            mOkay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_history", id_history);
                    params.put("status", 1 + "");

                    getDialog().dismiss();
                    getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/Approval/update", params, true);
                    Toast.makeText(getActivity(), "Request Approved", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onRefreshUp();
                    }
                }
            });

            Log.w("Parking lot prestice AA", id_history);

            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_history", id_history);
                    params.put("status", 2 + "");

                    getDialog().dismiss();
                    getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/Approval/update", params, true);
                    Toast.makeText(getActivity(), "Request Rejected", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onRefreshUp();
                    }
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
    
    public DialogRefreshListener getListener() {
        return listener;
    }

    public void setListener(DialogRefreshListener listener) {
        this.listener = listener;
    }

    public interface DialogRefreshListener{
        void onRefreshUp();
    }
    
}
