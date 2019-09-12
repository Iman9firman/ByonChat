package com.honda.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.honda.android.DialogFormChildMainNew;
import com.honda.android.DialogFormChildRequestDoc;
import com.honda.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.honda.android.ISSActivity.LoginDB.UserDB;
import com.honda.android.R;
import com.honda.android.TagTrending.Tag;
import com.honda.android.TagTrending.TagView;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.data.model.File;
import com.honda.android.data.model.Video;
import com.honda.android.helpers.Constants;
import com.honda.android.list.ItemListTrending;
import com.honda.android.local.Byonchat;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.ui.activity.ByonchatPDFPreviewActivity;
import com.honda.android.ui.activity.MainByonchatRoomBaseActivity;
import com.honda.android.ui.adapter.ByonchatPDFAdapter;
import com.honda.android.ui.adapter.OnPreviewItemClickListener;
import com.honda.android.ui.adapter.OnRequestItemClickListener;
import com.honda.android.ui.view.ByonchatRecyclerView;

import org.apache.http.Consts;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ByonchatPDFFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected List<File> files = new ArrayList<>();
//    protected List<File> folders = new ArrayList<>();

    private String myContact;
    private String title;
    private String urlTembak;
    private ArrayList<String> urlBack = new ArrayList<>();
    private String username;
    private String idRoomTab;
    private String color;

    protected LinearLayoutManager chatLayoutManager;
    protected ByonchatPDFAdapter mAdapter;
    private MessengerDatabaseHelper messengerHelper;
    private UserDB dbHelper;

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
    protected TextView vTextEmpty;
    @NonNull
    protected TextView vTextContentError;
//    @NonNull
//    protected TagView vTagGroup;
    @NonNull
    protected LinearLayout vTagLayout;
    @NonNull
    protected TextView vTag1;
    @NonNull
    protected TextView vTag2;
    @NonNull
    protected TextView vTag3;
    @NonNull
    protected TextView vTag4;

    public ByonchatPDFFragment() {

    }

    public ByonchatPDFFragment(MainByonchatRoomBaseActivity activity) {
        this.activity = activity;
    }

    public static ByonchatPDFFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatPDFFragment fragment = new ByonchatPDFFragment(activity);
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
        vTextEmpty = getTextEmpty(view);
        vTextContentError = getTextContentError(view);
//        vTagGroup = getTagContent(view);
        vTagLayout = getTagContent(view);
        vTag1 = getTagSatu(view);
        vTag2 = getTagDua(view);
        vTag3 = getTagTiga(view);
        vTag4 = getTagEmpat(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        resolveConnectionProblem();
        resolveListFile();
        resolveRefreshList();
        resolesTagGroup();
        onRefresh();

        vTextEmpty.setText("No file in this directory");
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
//        onResume();
        vRefreshList.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
            urlBack.add("OnBack");
            new getFile().execute("https://iss.byonchat.com/index.php/Api/Files");
            Log.w("Apa ini harusee",urlTembak);
        } else {
            vRefreshList.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        urlBack = urlTembak;
        /*vRefreshList.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
            new getFile().execute(urlTembak);
            Log.w("Apa ini harusee",urlTembak);
        } else {
            vRefreshList.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }*/
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
//        folders = new ArrayList<>();
        vListVideoTube.setUpAsList();
        vListVideoTube.setNestedScrollingEnabled(false);
        chatLayoutManager = (LinearLayoutManager) vListVideoTube.getLayoutManager();
        mAdapter = new ByonchatPDFAdapter(getContext(), files, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, File item, String type) {
                if(type.equalsIgnoreCase("folder")){
                    urlBack.add(item._id+"");
                    new getFile().execute("https://iss.byonchat.com/index.php/Api/Files/"+item.id);
                }else {
                    Intent intent = new Intent(getContext(), ByonchatPDFPreviewActivity.class);
                    intent.putExtra(Constants.EXTRA_URL, item.url);
                    startActivity(intent);
                    traceView(item);
                }
            }
        }, new OnRequestItemClickListener() {
            @Override
            public void onItemClick(View view, int position, File item) {
                FragmentManager fm = activity.getSupportFragmentManager();
                DialogFormChildRequestDoc testDialog = DialogFormChildRequestDoc.newInstance(username, idRoomTab, item.id, item.title, item.subtitle, item.url);
                testDialog.setRetainInstance(true);
                testDialog.show(fm, "Dialog");
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

    protected void resolesTagGroup(){
        vTagLayout.setVisibility(View.VISIBLE);

        vTag1.setText("Kebijakan");
        vTag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.getFilter().filter(vTag1.getText().toString().toLowerCase());
            }
        });
        vTag2.setText("SOP");
        vTag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.getFilter().filter(vTag2.getText().toString());
            }
        });
        vTag3.setText("SWI");
        vTag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.getFilter().filter(vTag3.getText().toString());
            }
        });
        vTag4.setText("Operational");
        vTag4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.getFilter().filter(vTag4.getText().toString());
            }
        });
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

    protected TextView getTextEmpty(View view) {
        return (TextView) view.findViewById(R.id.text_empty);
    }

    protected TextView getTextContentError(View view) {
        return (TextView) view.findViewById(R.id.text_content_error);
    }

    protected LinearLayout getTagContent(View view) {
        return (LinearLayout) view.findViewById(R.id.tag_filter);
    }

    protected TextView getTagSatu(View view) {
        return (TextView) view.findViewById(R.id.tag_satu);
    }
    protected TextView getTagDua(View view) {
        return (TextView) view.findViewById(R.id.tag_dua);
    }
    protected TextView getTagTiga(View view) {
        return (TextView) view.findViewById(R.id.tag_tiga);
    }
    protected TextView getTagEmpat(View view) {
        return (TextView) view.findViewById(R.id.tag_empat);
    }
    protected void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private class getFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.w("Killing speri",params[0]);
            urlTembak = params[0];
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                files.clear();
                JSONArray folderArray = new JSONArray(jsonObject.getString("folder"));
                if (folderArray.length() > 0) {
                    for (int i = 0; i < folderArray.length(); i++) {
                        JSONObject jObj = folderArray.getJSONObject(i);
                        String id = jObj.getString("id");
                        String idparent = jObj.getString("parent_id");
                        String title = jObj.getString("nama_folder");
                        String timestamp = jObj.getString("create_at");

                        File file = new File();
                        file.id = Long.valueOf(id);
                        file._id = Integer.parseInt(idparent);
                        file.title = title;
                        file.type = "folder";
                        file.timestamp = timestamp;
                        file.url = "https://iss.byonchat.com/files/"+id;

                        files.add(file);
                    }
                }
                JSONArray jsonArray = new JSONArray(jsonObject.getString("files"));
                Log.w("isi dari holdyui", jsonArray+"");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        String id = jObj.getString("id");
                        String title = jObj.getString("nama_file");
                        String subtitle = jObj.getString("desc");
                        String timestamp = jObj.getString("create_at");
                        String url = jObj.getString("base_url");
                        String type = jObj.getString("type_file");
//                        String thumbnail = jObj.getString("thumbnail");
                        String descriptionTag = jObj.getString("tag");

                        File file = new File();
                        file.id = Long.valueOf(id);
                        file.title = title;
                        file.subtitle = subtitle;
                        file.timestamp = timestamp;
                        file.url = url + title + type;
                        file.type = type;
//                        file.thumbnail = thumbnail;
                        file.description = descriptionTag;

                        files.add(file);
                    }
                }


                if(jsonArray.length() == 0 && folderArray.length() == 0){
                    vFrameEmpty.setVisibility(View.VISIBLE);
                }else{
                    vFrameEmpty.setVisibility(View.GONE);
                }

                mAdapter.setItems(files);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            vRefreshList.setRefreshing(false);
        }

    }

    private class sercFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.w("Killing speri",params[0]);
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                files.clear();
                JSONArray folderArray = new JSONArray(jsonObject.getString("folder"));
                if (folderArray.length() > 0) {
                    for (int i = 0; i < folderArray.length(); i++) {
                        JSONObject jObj = folderArray.getJSONObject(i);
                        String id = jObj.getString("id");
                        String idparent = jObj.getString("parent_id");
                        String title = jObj.getString("nama_folder");
                        String timestamp = jObj.getString("create_at");

                        File file = new File();
                        file.id = Long.valueOf(id);
                        file.title = title;
                        file.type = "folder";
                        file.timestamp = timestamp;
                        file.url = "https://iss.byonchat.com/files/"+id;

                        files.add(file);
                    }
                }
                JSONArray jsonArray = new JSONArray(jsonObject.getString("files"));
                Log.w("isi dari holdyui", jsonArray+"");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        String id = jObj.getString("id");
                        String title = jObj.getString("nama_file");
                        String subtitle = jObj.getString("desc");
                        String timestamp = jObj.getString("create_at");
                        String url = jObj.getString("base_url");
                        String type = jObj.getString("type_file");
//                        String thumbnail = jObj.getString("thumbnail");
                        String descriptionTag = jObj.getString("tag");

                        File file = new File();
                        file.id = Long.valueOf(id);
                        file.title = title;
                        file.subtitle = subtitle;
                        file.timestamp = timestamp;
                        file.url = url + title + type;
                        file.type = type;
//                        file.thumbnail = thumbnail;
                        file.description = descriptionTag;

                        files.add(file);
                    }
                }

                mAdapter.setItems(files);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            vRefreshList.setRefreshing(false);
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

            Log.w("fdsafdsa", statusLine + " -- " + result);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
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

    public void onActionSearch(String args) {
        vRefreshList.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
            new sercFile().execute(urlTembak+"/"+args);
        } else {
            vRefreshList.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackClick(){
        if(urlBack.get(urlBack.size()-1).equalsIgnoreCase("OnBack")){
            getActivity().onBackPressed();
        }else {
            vRefreshList.setRefreshing(true);
            if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
                new getFile().execute("https://iss.byonchat.com/index.php/Api/Files/" + urlBack.get(urlBack.size() - 1));
                urlBack.remove(urlBack.size() - 1);
                Log.w("Apa ini harusee", urlTembak);
            } else {
                vRefreshList.setRefreshing(false);
                Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
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
                        Log.w("sukses harusee",response);
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

