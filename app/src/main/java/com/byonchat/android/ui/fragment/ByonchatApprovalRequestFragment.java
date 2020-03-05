package com.byonchat.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.util.Log;
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
import com.byonchat.android.data.model.File;
import com.byonchat.android.data.model.Video;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.ui.activity.ByonchatPDFPreviewActivity;
import com.byonchat.android.ui.activity.DialogApproveRequestDocument;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.ui.adapter.ByonchatApprovalDocAdapter;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.byonchat.android.utils.Utility.reportCatch;

@SuppressLint("ValidFragment")
public class ByonchatApprovalRequestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected List<File> files = new ArrayList<>();

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    MessengerDatabaseHelper databaseHelper;

    protected LinearLayoutManager chatLayoutManager;
    protected ByonchatApprovalDocAdapter mAdapter;

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

    public ByonchatApprovalRequestFragment() {

    }

    public ByonchatApprovalRequestFragment(MainByonchatRoomBaseActivity activity) {
        this.activity = activity;
    }

    public static ByonchatApprovalRequestFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatApprovalRequestFragment fragment = new ByonchatApprovalRequestFragment(activity);
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
        urlTembak = "https://bb.byonchat.com/ApiDocumentControl/index.php/Approval/list";
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        resolveConnectionProblem();
        resolveListFile();
        resolveRefreshList();

        vFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = ByonchatVideoBeforeDownloadActivity.generateIntent(getContext());
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();

        vRefreshList.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
            Map<String, String> params = new HashMap<>();
            params.put("bc_user_approval",  databaseHelper.getMyContact().getJabberId());
            getDetail(urlTembak,params,true);
        } else {
            vRefreshList.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void resolveConnectionProblem() {
        try {
            vTextError.setText("Home isn't responding");
            vTextContentError.setText("Thats why we can't show videos right now\nPlease check back later.");
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    protected void resolveListFile() {
        try {
            files = new ArrayList<>();
            vListVideoTube.setUpAsList();
            vListVideoTube.setNestedScrollingEnabled(false);
            chatLayoutManager = (LinearLayoutManager) vListVideoTube.getLayoutManager();
            mAdapter = new ByonchatApprovalDocAdapter(getContext(), files, new OnPreviewItemClickListener() {
                @Override
                public void onItemClick(View view, int position, File item, String type) {
                    FragmentManager fm = activity.getSupportFragmentManager();
                    DialogApproveRequestDocument testDialog = DialogApproveRequestDocument.newInstance(username, idRoomTab, item.id, item.title, "nulll", item.nama_requester, item.timestamp, item.description, item.id_history);
                    testDialog.setRetainInstance(true);
                    testDialog.show(fm, "Dialog");
                    testDialog.setListener(new DialogApproveRequestDocument.DialogRefreshListener() {
                        @Override
                        public void onRefreshUp() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onRefresh();
                                }
                            }, 300);
                        }
                    });
                }
            }, new OnRequestItemClickListener() {
                @Override
                public void onItemClick(View view, int position, File item) {
                    Intent intent = new Intent(getContext(), ByonchatPDFPreviewActivity.class);
                    intent.putExtra(Constants.EXTRA_URL, item.url);
                    startActivity(intent);
                }
            });

            mAdapter.setOnItemClickListener((view, position) -> {
                if (isChanged) {
//                Intent intent = ByonchatDetailVideoTubeActivity.generateIntent(getContext(), mAdapter.getData().get(position));
//                startActivity(intent);
                } else
                    adapterSelected((File) mAdapter.getData().get(position));
            });

            mAdapter.setOnLongItemClickListener((view, position) -> {
                /*adapterSelected((Video) mAdapter.getData().get(position));*/
            });

            vListVideoTube.setAdapter(mAdapter);
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    protected void adapterSelected(File file) {
        try {
            file.isSelected = !file.isSelected();
            mAdapter.notifyDataSetChanged();
            onContactSelected(mAdapter.getSelectedComments());
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void onContactSelected(List<File> selectedContacts) {
        try {
            int total = selectedContacts.size();
            boolean hasCheckedItems = total > 0;
            if (hasCheckedItems) {
                isChanged = false;
            } else {
                isChanged = true;
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    protected void resolveRefreshList() {
        vRefreshList.setOnRefreshListener(this);
    }

    protected void showPopup(View view, final Video video) {
        try {
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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
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
        try {
            ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
            rdialog.setMessage("Loading...");
            rdialog.show();

            RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity());

            StringRequest sr = new StringRequest(Request.Method.POST, Url,
                    response -> {
                        rdialog.dismiss();
                        if (hide) {
                            Log.w("INI approvere harusee", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");

                                files.clear();
                                if (message.equalsIgnoreCase("succes")) {
                                    JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));

                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jObj = jsonArray.getJSONObject(i);
                                            String _id = jObj.getString("id_request");
                                            String id = jObj.getString("id_file");
                                            String id_history = jObj.getString("id_request_history");
                                            String timestamp = jObj.getString("create_at");
                                            String bc_user_requester = jObj.getString("bc_user_requester");
                                            String keterangan = jObj.getString("keterangan");
                                            String url = jObj.getString("link_file");
                                            String nik = jObj.getString("nik");
                                            String nama_requester = jObj.getString("nama_user_requester");
                                            String nama_file = jObj.getString("nama_file");

                                            File file = new File();
                                            file.id = Long.valueOf(id);
                                            file.title = nama_file;
                                            file.timestamp = timestamp;
                                            file.url = url;
                                            file.type = "text";
                                            file.description = keterangan;
                                            file.nama_requester = nama_requester;
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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }
}


