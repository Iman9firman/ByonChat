package com.byonchat.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.data.model.Status;
import com.byonchat.android.data.model.Video;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.ui.activity.DialogRejectRequest;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.ui.adapter.ByonchatStatusRequestAdapter;
import com.byonchat.android.ui.view.ByonchatRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ByonchatStatusRequestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected List<Status> files = new ArrayList<>();

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;

    protected LinearLayoutManager chatLayoutManager;
    protected ByonchatStatusRequestAdapter mAdapter;
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

    public ByonchatStatusRequestFragment() {

    }

    public ByonchatStatusRequestFragment(MainByonchatRoomBaseActivity activity) {
        this.activity = activity;
    }

    public static ByonchatStatusRequestFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatStatusRequestFragment fragment = new ByonchatStatusRequestFragment(activity);
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
        urlTembak = "https://bb.byonchat.com/ApiDocumentControl/index.php/Request/list";
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        myContact = getArguments().getString("ee");
        color = getArguments().getString("col");
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
            params.put("bc_user",  databaseHelper.getMyContact().getJabberId());
            getDetail(urlTembak,params,true);
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
        mAdapter = new ByonchatStatusRequestAdapter(getContext(), files);

        mAdapter.setOnLongItemClickListener((view, position) -> {
            Status item = files.get(position);

            if(ValidateHistory(position, files)) {
                FragmentManager fm = activity.getSupportFragmentManager();
                DialogRejectRequest testDialog = DialogRejectRequest.newInstance(item.id_request);
                testDialog.setRetainInstance(true);
                testDialog.setListener(new DialogRejectRequest.DialogRejectListener() {
                    @Override
                    public void onReject() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onRefresh();
                            }
                        }, 300);
                    }
                });
                testDialog.show(fm, "Dialog");
            }
        });

        vListVideoTube.setAdapter(mAdapter);
    }



    private boolean ValidateHistory(int position,  List<Status> items) {
        Status item = items.get(position);
        boolean valid = false;

        JSONArray history = null;
        try {
            history = new JSONArray(item.history);

            for (int ii = 0; ii < history.length(); ii++){
                JSONObject jsonObject = history.getJSONObject(ii);
                String status = jsonObject.getString("status");

                if(status.equalsIgnoreCase("2")){
                    valid = true;
                }

                if(history.length() == 1){
                    valid = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valid;
    }


    protected void adapterSelected(Status file) {
        file.isSelected = !file.isSelected();
        mAdapter.notifyDataSetChanged();
        onContactSelected(mAdapter.getSelectedComments());
    }

    public void onContactSelected(List<Status> selectedContacts) {
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

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    if (hide) {
                        try {
                            JSONArray jsonArray0 = new JSONArray(response);
                            JSONObject jsonObject = jsonArray0.getJSONObject(0);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            files.clear();
                            if (message.equalsIgnoreCase("succes")) {
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));


                            if (jsonArray.length() > 0) {
                                for (int i = jsonArray.length() -1 ; i >= 0; i--) {
                                    JSONObject jObj = jsonArray.getJSONObject(i);
                                    String id = jObj.getString("id");
                                    String id_file = jObj.getString("id_file");
                                    String link_file = jObj.getString("link_file");
                                    String timestamp = jObj.getString("create_at");
                                    String bc_user_requester = jObj.getString("bc_user_requester");
                                    String nama_file = jObj.getString("nama_file");
                                    String history = jObj.getString("history");

                                    String id_request = new JSONArray(history).getJSONObject(0).getString("id_request");
                                    String id_history = new JSONArray(history).getJSONObject(new JSONArray(history).length()-1).getString("id");

                                    Status file = new Status();
                                    file.id = Long.valueOf(id_file);
                                    file.title = nama_file;
                                    file.url = link_file;
                                    file.timestamp = timestamp;
                                    file.no_requester = bc_user_requester;
                                    file.type = "text";
                                    file.history = history;
                                    file.id_request = id_request;
                                    file.id_history = id_history;

                                    files.add(file);
                                }

                                mAdapter.setItems(files);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    vRefreshList.setRefreshing(false);
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
    }
}



