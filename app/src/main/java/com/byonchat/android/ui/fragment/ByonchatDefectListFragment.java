package com.byonchat.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.DialogFormChildMainNew;
import com.byonchat.android.DialogFormChildRequestDoc;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.TagTrending.Tag;
import com.byonchat.android.TagTrending.TagView;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.data.model.File;
import com.byonchat.android.data.model.Video;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.ItemListTrending;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.ui.activity.ByonchatDetailDefectActivity;
import com.byonchat.android.ui.activity.ByonchatPDFPreviewActivity;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.ui.adapter.ByonchatDefectListAdapter;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.ValidationsKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ByonchatDefectListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected List<File> files = new ArrayList<>();

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;

    protected LinearLayoutManager chatLayoutManager;
    protected ByonchatDefectListAdapter mAdapter;
    private MessengerDatabaseHelper messengerHelper;
    private UserDB dbHelper;
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

    public ByonchatDefectListFragment() {

    }

    public ByonchatDefectListFragment(MainByonchatRoomBaseActivity activity) {
        this.activity = activity;
    }

    public static ByonchatDefectListFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatDefectListFragment fragment = new ByonchatDefectListFragment(activity);
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
        urlTembak = getArguments().getString("bb");
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
        resolveRefresh();

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
        resolveRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void resolveRefresh(){

        vRefreshList.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
            Map<String, String> params = new HashMap<>();
            params.put("bc_user",  databaseHelper.getMyContact().getJabberId());
            params.put("username_room",username);
            params.put("id_rooms_tab",idRoomTab);
            getDetail(urlTembak,params,true);
        } else {
            vRefreshList.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void resolveConnectionProblem() {
        vTextError.setText("Home isn't responding");
        vTextContentError.setText("Thats why we can't show videos right now\nPlease check back later.");
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getContext());
        }
        dbHelper = new UserDB(getContext());
    }

    protected void resolveListFile() {
        files = new ArrayList<>();
        vListVideoTube.setUpAsList();
        vListVideoTube.setNestedScrollingEnabled(false);
        chatLayoutManager = (LinearLayoutManager) vListVideoTube.getLayoutManager();
        mAdapter = new ByonchatDefectListAdapter(getContext(), files, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, File item) {
//                Intent intent = new Intent(getContext(), DinamicRoomTaskActivity.class);
//                intent.putExtra("tt", "Detail Defect List");
//                intent.putExtra("uu", username);
//                intent.putExtra("ii", idRoomTab);
//                intent.putExtra("idTask", item.id+"|"+item._id);
//                intent.putExtra("col", color);
//                intent.putExtra("ll", "");
//                intent.putExtra("from", "defect");
//
//                startActivity(intent);
                Map<String, String> params = new HashMap<>();
                params.put("bc_user",  databaseHelper.getMyContact().getJabberId());
                params.put("username_room",username);
                params.put("id_rooms_tab",idRoomTab);
                params.put("parent_id",item._id+"");
                params.put("id_list_push",item.id+"");

                getMoreDetail(new ValidationsKey().getInstance(getContext()).getTargetUrl()+"/bc_voucher_client/webservice/category_tab/list_task_defect_multiple.php",params,true, item.id+"|"+item._id);
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

    private void traceView(File file){
        Map<String, String> params = new HashMap<>();
        params.put("nama_file",  file.title);
        params.put("desc_file", file.subtitle);
        params.put("nik", dbHelper.getColValue(UserDB.EMPLOYEE_NIK));
        params.put("nama", dbHelper.getColValue(UserDB.EMPLOYEE_NAME));
        params.put("bc_user", messengerHelper.getMyContact().getJabberId());

        getDetail("https://bb.byonchat.com/ApiDocumentControl/index.php/View_history", params, true);
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
                    resolveRefresh();
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

    public void onActionSearch(String args) {
        mAdapter.getFilter().filter(args);
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    try {
                        Log.w("HSsial yoap ad",response);
                        JSONObject jsonObject = new JSONObject(response);
                        String id_room_tab = jsonObject.getString("id_rooms_tab");
                        String message = jsonObject.getString("list_pull");

                        Log.w("joa 1 yoaaw","ok");
                        files.clear();
                        JSONArray jsonArray = new JSONArray(message);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                String id = jObj.getString("id");
                                String parent_id = jObj.getString("parent_id");
                                Log.w("joa 2 yoaaw","ok");
                                JSONArray bvalue = jObj.getJSONArray("value_detail");
                                for (int ii = 0; ii < bvalue.length(); ii++) {
                                    JSONObject jObj2 = bvalue.getJSONObject(ii);
                                    String label = jObj2.getString("label");
                                    String type = jObj2.getString("type");
                                    String value = jObj2.getString("value");
                                    Log.w("joa 3 yoaaw","ok");

                                    File file = new File();
                                    file.id = Long.valueOf(id);
                                    file._id = Integer.parseInt(parent_id);
                                    file.title = label;
                                    file.subtitle = value;
                                    file.type = type;
                                    file.timestamp = "";
                                    file.url = "";
                                    file.thumbnail = "";
                                    file.description = "";

                                    files.add(file);
                                }
                            }

                            mAdapter.setItems(files);
                        } else {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.w("joa ERRE yoaaw",e.getMessage());
                    }
                    vRefreshList.setRefreshing(false);

                },
                error -> {
                    vRefreshList.setRefreshing(false);
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e("HttpClient", "error: " + error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }

    private void getMoreDetail(String Url, Map<String, String> params2, Boolean hide, String idTask) {
        ProgressDialog rdialog = new ProgressDialog((FragmentActivity) getActivity());
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) getActivity());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();

                Intent intent = new Intent(getContext(), ByonchatDetailDefectActivity.class);
                intent.putExtra("data", response);
                intent.putExtra("tt", "Detail Defect List");
                intent.putExtra("col", color);
                intent.putExtra("ii", idRoomTab);
                intent.putExtra("idTask", idTask);

                startActivity(intent);

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
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e("HttpClient", "error: " + error.toString());
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }
}


