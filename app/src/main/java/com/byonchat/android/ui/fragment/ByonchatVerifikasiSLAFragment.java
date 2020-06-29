package com.byonchat.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.DialogFormChildMainNew;
import com.byonchat.android.DialogFormChildRequestDoc;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.data.model.File;
import com.byonchat.android.data.model.Status;
import com.byonchat.android.data.model.Video;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.contact;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.ui.activity.ByonchatPDFPreviewActivity;
import com.byonchat.android.ui.activity.DialogApproveRequestDocument;
import com.byonchat.android.ui.activity.DialogRejectRequest;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.ui.activity.PushRepairReportActivity;
import com.byonchat.android.ui.activity.PushSLAVerificationActivity;
import com.byonchat.android.ui.activity.PustSLAFollowUpActivity;
import com.byonchat.android.ui.adapter.ByonchatApprovalDocAdapter;
import com.byonchat.android.ui.adapter.ByonchatRepairReportAdapter;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.ClientSSLSocketFactory;
import com.google.android.gms.vision.L;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ByonchatVerifikasiSLAFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected List<File> files = new ArrayList<>();

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;

    private ArrayList<String> kodeJJt = new ArrayList<>();

    protected LinearLayoutManager chatLayoutManager;
    protected ByonchatRepairReportAdapter mAdapter;
    MessengerDatabaseHelper databaseHelper;

    protected boolean isChanged = true;
    protected MainByonchatRoomBaseActivity activity;

    @NonNull
    protected RelativeLayout vFrameError;
    @NonNull
    protected RelativeLayout vFrameEmpty;
    @NonNull
    protected ByonchatRecyclerView vListVideoTube;
    @NonNull
    protected SwipeRefreshLayout vRefreshList;
    @NonNull
    protected FloatingActionButton vFab;
    @NonNull
    protected TextView vTextError;
    @NonNull
    protected TextView vTextContentError;

    public ByonchatVerifikasiSLAFragment() {

    }

    public ByonchatVerifikasiSLAFragment(MainByonchatRoomBaseActivity activity) {
        this.activity = activity;
    }

    public static ByonchatVerifikasiSLAFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatVerifikasiSLAFragment fragment = new ByonchatVerifikasiSLAFragment(activity);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = MessengerDatabaseHelper.getInstance((FragmentActivity) getContext());
        title = getArguments().getString("aa");
        urlTembak = "https://"+ MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/report_verifikasi_sla_new.php";
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        myContact = getArguments().getString("ee");
        color = getArguments().getString("col");

        UserDB userDB = UserDB.getInstance(getActivity());

        Cursor cursorJJT = userDB.getSingle();
        if (cursorJJT.getCount() > 0) {
            String contentJJT = cursorJJT.getString(cursorJJT.getColumnIndexOrThrow(UserDB.EMPLOYEE_MULTICOST));
            try {
                JSONArray arr = new JSONArray(contentJJT);

                for (int as = 0; as < arr.length(); as++) {
                    kodeJJt.add(arr.getString(as).substring(arr.getString(as).indexOf("[") + 1, arr.getString(as).indexOf("]")));
                }

            } catch (Exception e) { }
        }
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.b_video_fragment, container, false);

        vFrameError = getFrameError(view);
        vFrameEmpty = getFrameEmpty(view);
        vListVideoTube = getListFrequently(view);
        vRefreshList = getRefreshList(view);
        vFab = getFabView(view);
        vTextError = getTextError(view);
        vTextContentError = getTextContentError(view);

        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        resolveConnectionProblem();
        resolveListFile();
        resolveRefreshList();

        vFab.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        onResume();
    }

    @Override
    public void onResume() {

        vRefreshList.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
            Map<String, String> params = new HashMap<>();
            params.put("username_room", username);
            params.put("bc_user", databaseHelper.getMyContact().getJabberId());
            params.put("id_rooms_tab", idRoomTab);
            for(int i = 0; i < kodeJJt.size(); i++){
                params.put("kode_jjt["+i+"]", kodeJJt.get(i));
                Log.w("dilog paramser", "jjt."+i+": "+kodeJJt.get(i));
            }
            getDetail(/*"https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/category_tab/report_tobe_repair.php"*/urlTembak, params, true);
        } else {
            vRefreshList.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }

    protected void resolveConnectionProblem() {
        vTextError.setText("Home isn't responding");
        vTextContentError.setText("Thats why we can't show videos right now\nPlease check back later.");
    }

    protected void resolveListFile() {
        files = new ArrayList<>();
        vListVideoTube.setUpAsList();
        vListVideoTube.setNestedScrollingEnabled(false);
        chatLayoutManager = (LinearLayoutManager) vListVideoTube.getLayoutManager();
        mAdapter = new ByonchatRepairReportAdapter(getContext(), files, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, String position, File item, String type,String idts) {
                Map<String, String> params = new HashMap<>();
                params.put("username_room", username);
                params.put("bc_user", databaseHelper.getMyContact().getJabberId());
                params.put("kode_jjt", item.kode_jjt + "");
                params.put("tanggal_submit",item.timestamp);
                getMoreDetail("https://"+ MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/push_verifikasi_sla_new.php", params, true,item.title,item.timestamp);
            }
        }, new OnRequestItemClickListener() {
            @Override
            public void onItemClick(View view, int position, File item) {
                Intent intent = new Intent(getContext(), ByonchatPDFPreviewActivity.class);
                intent.putExtra(Constants.EXTRA_URL, item.url);
                startActivity(intent);
            }
        });

        vListVideoTube.setAdapter(mAdapter);
    }


    protected void adapterSelected(File file) {
        file.isSelected = !file.isSelected();
        mAdapter.notifyDataSetChanged();
        onContactSelected(mAdapter.getSelectedComments());
    }

    public void onContactSelected(List<File> selectedContacts) {
        int total = selectedContacts.size();
        boolean hasCheckedItems = total > 0;
        if (hasCheckedItems) {
            isChanged = false;
        } else {
            isChanged = true;
        }
    }

    protected void resolveRefreshList() {
        vRefreshList.setOnRefreshListener(this);
    }

    protected void showPopup(View view, final Video video) {
        View menuItemView = view.findViewById(R.id.more);
        final PopupMenu popup = new PopupMenu(view.getContext(), menuItemView);
        MenuInflater inflate = popup.getMenuInflater();

        inflate.inflate(R.menu.byonchat_menu_delete_file, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    Byonchat.getVideoTubeDataStore().delete(video);
                    onResume();
                    break;
                default:
                    return false;
            }
            return false;
        });
        popup.show();
    }

    protected RelativeLayout getFrameError(View view) {
        return (RelativeLayout) view.findViewById(R.id.frame_error);
    }

    protected RelativeLayout getFrameEmpty(View view) {
        return (RelativeLayout) view.findViewById(R.id.frame_empty);
    }

    protected ByonchatRecyclerView getListFrequently(View view) {
        return (ByonchatRecyclerView) view.findViewById(R.id.list_videotube);
    }

    protected SwipeRefreshLayout getRefreshList(View view) {
        return (SwipeRefreshLayout) view.findViewById(R.id.refresh_videotube);
    }

    protected FloatingActionButton getFabView(View view) {
        return (FloatingActionButton) view.findViewById(R.id.fab);
    }

    protected TextView getTextError(View view) {
        return (TextView) view.findViewById(R.id.text_coba_lagi);
    }

    protected TextView getTextContentError(View view) {
        return (TextView) view.findViewById(R.id.text_content_error);
    }

    protected void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    vRefreshList.setRefreshing(false);
                    if (hide) {
                        files.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("value_detail"));
                            if (jsonArray.length() > 0) {
                                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                    JSONObject jObj = jsonArray.getJSONObject(i);
                                    String timestamp = jObj.getString("tanggal_submit");
                                    String nama_file = jObj.getString("title");
                                    String kode_jjt = jObj.getString("kode_jjt");
                                    File file = new File();
                                    file.id = /*Long.valueOf(id)*/i;
                                    file.title = nama_file;
                                    file.timestamp = timestamp;
                                    file.kode_jjt = kode_jjt;
                                    file.url = "";
                                    file.type = "text";
                                    file.id_history = "";
                                    file.description = "";
                                    file.nama_requester = "";

                                    files.add(file);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.setItems(files);
                        mAdapter.notifyDataSetChanged();
                        vRefreshList.setRefreshing(false);
                    }

                },
                error -> {
                    vRefreshList.setRefreshing(false);
                    rdialog.dismiss();
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

    private void getMoreDetail(String Url, Map<String, String> params2, Boolean hide, String toTitle, String toSubtitle) {
        ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    vRefreshList.setRefreshing(false);
                    rdialog.dismiss();
                    if (hide) {
                        Intent iii = new Intent(getContext(), PushSLAVerificationActivity.class);
                        iii.putExtra("data", response);
                        iii.putExtra("username_room", username);
                        iii.putExtra("bc_user", databaseHelper.getMyContact().getJabberId());
                        iii.putExtra("id_rooms_tab", idRoomTab);
                        iii.putExtra("title",toTitle);
                        iii.putExtra("subtitle",toSubtitle);
                        startActivity(iii);
                    }

                },
                error -> {
                    vRefreshList.setRefreshing(false);
                    rdialog.dismiss();
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



