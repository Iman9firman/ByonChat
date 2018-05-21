package com.byonchat.android.personalRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.adapter.VideoAdapter;
import com.byonchat.android.personalRoom.adapter.VideoAdapterMine;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.personalRoom.model.VideoModel;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.EndlessRecyclerViewScrollListener;
import com.byonchat.android.utils.ImageFilePath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by lukma on 3/17/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentMyVideo extends Fragment {

    public static String myContact;
    public static String title;
    public static String urlTembak;
    public static String username;
    public static String idRoomTab;
    public static String color;
    public static Boolean personal;

    private Activity mContext ;
    VideoAdapter mAdapter;
    SwipeRefreshLayout mswipeRefreshLayout;
    RecyclerView mRecyclerView;
    public static String myuserid = "myid";
    public static String userid = "id";
    public static String uname = "name";
    private static final String TAG = FragmentMyVideo.class.getSimpleName();
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int LOAD_VIDEO_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
    private static final String URL_VIEW_VIDEO = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/video.php";
    //    private static final String URL_VIEW_VIDEO = "https://"+ MessengerConnectionService.HTTP_SERVER+"/personal_room/webservice/view/photo.php";
    ArrayList<PictureModel> videoModels = new ArrayList<>();
    private VideoAdapter adapter;
    private VideoAdapterMine adaptermine;
    private EndlessRecyclerViewScrollListener scrollListener;
    private GridLayoutManager gridLayoutManager;
    private MessengerDatabaseHelper messengerHelper;
    private static String URL_DELETE_VIDEOS = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/delete_video.php";
    public static int IMGS[] = {
            R.drawable.bt_add_movie,
    };

    public FragmentMyVideo(Activity ctx) {
        mContext = ctx;

    }

    public static FragmentMyVideo newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag,Activity act) {
        FragmentMyVideo fragmentRoomTask = new FragmentMyVideo(act);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        args.putBoolean("fla", flag);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
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
        personal = getArguments().getBoolean("fla");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.fragment_my_video, container, false);

        if (container == null) {
            return null;
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        mswipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);


        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(mContext.getApplicationContext(), 4);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(mContext.getApplicationContext(), 6);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        return sss;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (videoModels == null) {
            videoModels = new ArrayList<PictureModel>();
        }

        if (personal) {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                adaptermine = new VideoAdapterMine(mContext.getApplicationContext(), videoModels);
                mRecyclerView.setAdapter(adaptermine);

                adaptermine.setOnLongClickListener(new VideoAdapterMine.MyClickListenerLongClick() {

                    @Override
                    public void onLongClick(final int position, View v) {
                        final Dialog dialog;
                        dialog = DialogUtil.customDialogConversation(mContext);
                        dialog.show();

                        FrameLayout btnDeleteRounded = (FrameLayout) dialog.findViewById(R.id.btnDeleteRounded);
                        btnDeleteRounded.setVisibility(View.VISIBLE);

                        btnDeleteRounded.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                final Dialog dialogConfirmation;
                                dialogConfirmation = DialogUtil.customDialogConversationConfirmation(mContext);
                                dialogConfirmation.show();

                                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                                txtConfirmation.setText("Confirm Delete");
                                descConfirmation.setVisibility(View.VISIBLE);
                                descConfirmation.setText("Do you want to delete this video?");

                                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);
                                btnNo.setText("Cancel");
                                btnNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogConfirmation.dismiss();
                                    }
                                });

                                btnYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogConfirmation.dismiss();
                                        new deleteVideo().execute(URL_DELETE_VIDEOS, videoModels.get(position).getUserid(), videoModels.get(position).getId_photo());
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }

//        if (NetworkInternetConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline(getActivity().getApplicationContext())) {
//            new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
//        }

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                new AmbilGambarNext(totalItemsCount).execute(urlTembak, myContact, username, idRoomTab);
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);
//        getListVideo(userid);

       /* if (myuserid.equalsIgnoreCase(userid)) {
            getListVideoMine(userid);
        } else {
            new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
        }*/

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (personal) {
                    Toast.makeText(mContext.getApplicationContext(), "tes", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                        refreshItems();
                        scrollListener.resetState();
                    } else {
                        onItemsLoadComplete();
                    }
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext.getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        if (personal) {
                            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                                if (position == 0) {
                                    ShowAlertDialogWithListview();
                                } else if (position > 0) {
                                    Intent intent = new Intent(mContext.getApplicationContext(), VideoDetailActivity.class);
                                    intent.putParcelableArrayListExtra("data", videoModels);
                                    intent.putExtra("pos", position);
                                    startActivity(intent);
                                }
                            } else {
                                Intent intent = new Intent(mContext.getApplicationContext(), VideoDetailActivity.class);
                                intent.putParcelableArrayListExtra("data", videoModels);
                                intent.putExtra("pos", position);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(mContext.getApplicationContext(), VideoDetailActivity.class);
                            intent.putParcelableArrayListExtra("data", videoModels);
                            intent.putExtra("pos", position);
                            startActivity(intent);
                        }
                    }
                }));


    }

    @Override
    public void onResume() {
        mswipeRefreshLayout.setRefreshing(true);
        if (personal) {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                videoModels.clear();
                videoModels.add(null);
                new AmbilVideoPersonalMine().execute(URL_VIEW_VIDEO, myContact);
                adaptermine = new VideoAdapterMine(mContext.getApplicationContext(), videoModels);
                mRecyclerView.setAdapter(adaptermine);
            } else {
                new AmbilVideoPersonal().execute(URL_VIEW_VIDEO, myContact);
            }
        } else {
            if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
            }
        }
        super.onResume();
    }

    void refreshItems() {

        if (personal) {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                videoModels.clear();
                videoModels.add(null);
                new AmbilVideoPersonalMine().execute(URL_VIEW_VIDEO, myContact);
                adaptermine = new VideoAdapterMine(mContext.getApplicationContext(), videoModels);
                mRecyclerView.setAdapter(adaptermine);
            } else {
                new AmbilVideoPersonal().execute(URL_VIEW_VIDEO, myContact);
            }
        } else {
            new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
        }
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
    }

    public void ShowAlertDialogWithListview() {
        List<String> mAnimals = new ArrayList<String>();
        mAnimals.add("Take a video");
        mAnimals.add("Choose from gallery");
        //Create sequence of items
        final CharSequence[] Animals = mAnimals.toArray(new String[mAnimals.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext.getApplicationContext());
//        dialogBuilder.setTitle("Upload photo");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    captureVideo();
                } else if (item == 1) {
                    loadVideo();
                }
                String selectedText = Animals[item].toString();  //Selected item in listview
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    private void loadVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, LOAD_VIDEO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == mContext.RESULT_OK) {
                launchUploadActivity();
            } else if (resultCode == mContext.RESULT_CANCELED) {

//                Toast.makeText(getActivity(),
//                        "User cancelled video recording", Toast.LENGTH_SHORT)
//                        .show();
            } else {
                Toast.makeText(mContext.getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == LOAD_VIDEO_REQUEST_CODE) {
            if (resultCode == mContext.RESULT_OK) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(mContext.getApplicationContext(), selectedUri);

                launchUploadActivityFromVidSelected(selectedImagePath);

            } else if (resultCode == mContext.RESULT_CANCELED) {
                /*Toast.makeText(getActivity().getApplicationContext(),
                        "User cancelled to load image", Toast.LENGTH_SHORT)
                        .show();*/
            } else {
                Toast.makeText(mContext.getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity() {
        if (personal) {
            DialogFragmentVideo d = new DialogFragmentVideo();
            d.DialogFragmentVideo(fileUri.getPath(), myContact);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        } else {
            DialogFragmentVideo d = new DialogFragmentVideo();
            d.DialogFragmentVideo(fileUri.getPath(), userid);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        }
    }

    private void launchUploadActivityFromVidSelected(String sourceFileUri) {
        int day, month, year;
        int second, minute, hour;
        GregorianCalendar date = new GregorianCalendar();

        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);

        second = date.get(Calendar.SECOND);
        minute = date.get(Calendar.MINUTE);
        hour = date.get(Calendar.HOUR);

        String name = (hour + "" + minute + "" + second + "" + day + "" + (month + 1) + "" + year);
        String tag = name + ".jpg";
        String fileName = sourceFileUri.replace(sourceFileUri, tag);

        if (personal) {
            DialogFragmentVideo d = new DialogFragmentVideo();
            d.DialogFragmentVideo(sourceFileUri, myContact);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        } else {
            DialogFragmentVideo d = new DialogFragmentVideo();
            d.DialogFragmentVideo(sourceFileUri, userid);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    class deleteVideo extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            data.put("id_video", params[2]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            if (s.equalsIgnoreCase("1")) {
                Toast.makeText(mContext.getApplicationContext(), "Video has been deleted", Toast.LENGTH_SHORT).show();
                onResume();
            } else {
                Toast.makeText(mContext.getApplicationContext(), "Failed to delete video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AmbilGambar extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username_room", params[2]);
            data.put("id_rooms_tab", params[3]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            JSONArray dataJsonArr = null;
            String data = "";

            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                videoModels.clear();
                try {
                    //e{"id_rooms_tab":"371","username_room":"628158888933","data":[{"id":"18","title":"Video BMW","description":"video bmw","file_gallery":"https:\/\/bb.byonchat.com\/bc_voucher_client\/images\/videos\/VID_26_371_14092016_142209_dRpa4gHtr4.mp4","thumbnail":"https:\/\/bb.byonchat.com\/bc_voucher_client\/images\/videos\/thumbs\/VID_26_371_14092016_142209_dRpa4gHtr4.mp4","add_date":"2016-09-14 14:22:09"}]}
                    JSONObject json = new JSONObject(s);
            /*   String json_userid = json.getString("id_rooms_tab");
                    String json_userid = json.getString("username_room");*/

                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {

                        JSONObject c = dataJsonArr.getJSONObject(i);

                        PictureModel item2 = new PictureModel();
                        String id_video = c.getString("id");
                        String title = c.getString("title");
                        String description = c.getString("description");
                        String url = c.getString("file_gallery");
                        String thumbnail = c.getString("thumbnail");
                        String tgl_upload = c.getString("add_date");
                        String duration = /*c.getString("durasi")*/"";

                        Log.e(TAG, "id_video: " + id_video
                                + ", title: " + title
                                + ", description: " + description
                                + ", url: " + url
                                + ", thumbnail: " + thumbnail
                                + ", tgl_upload: " + tgl_upload
                                + ", duration: " + duration);

//                            PictureModel item = new PictureModel();

                        item2.setUserid(myuserid);
                        item2.setUserid(userid);
                        item2.setId_photo(id_video);
                        item2.setTitle(title);
                        item2.setDescription(description);

                        String vidDecode = Uri.decode(Uri.decode(c.getString("file_gallery")));
//                            String vid = c.isNull("file_video") ? null : vidDecode;
                        String vid = c.isNull("file_gallery") ? null : c.getString("file_gallery");
                        item2.setUrl(vid);

                        String thumb = c.isNull("thumbnail") ? null : c.getString("thumbnail");
                        item2.setUrl_thumb(thumb);

                        item2.setTgl_upload(tgl_upload);
                        item2.setDuration(duration);
                        item2.setDrawable(0);
                        item2.setFlag("false");
                        item2.setColor(color);

                        videoModels.add(item2);
                    }
//                      adapter.notifyDataSetChanged();
                    adaptermine = new VideoAdapterMine(getContext(), videoModels);
                    mRecyclerView.setAdapter(adaptermine);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class AmbilGambarNext extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        int totalItem;

        public AmbilGambarNext(int totalItem) {
            this.totalItem = totalItem;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username_room", params[2]);
            data.put("id_rooms_tab", params[3]);
            data.put("last_page", totalItem + "");
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            JSONArray dataJsonArr = null;
            String data = "";

            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    //e{"id_rooms_tab":"371","username_room":"628158888933","data":[{"id":"18","title":"Video BMW","description":"video bmw","file_gallery":"https:\/\/bb.byonchat.com\/bc_voucher_client\/images\/videos\/VID_26_371_14092016_142209_dRpa4gHtr4.mp4","thumbnail":"https:\/\/bb.byonchat.com\/bc_voucher_client\/images\/videos\/thumbs\/VID_26_371_14092016_142209_dRpa4gHtr4.mp4","add_date":"2016-09-14 14:22:09"}]}
                    JSONObject json = new JSONObject(s);
            /*   String json_userid = json.getString("id_rooms_tab");
                    String json_userid = json.getString("username_room");*/

                    dataJsonArr = json.getJSONArray("data");
                    if (dataJsonArr != null) {
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            PictureModel item2 = new PictureModel();
                            String id_video = c.getString("id");
                            String title = c.getString("title");
                            String description = c.getString("description");
                            String url = c.getString("file_gallery");
                            String thumbnail = c.getString("thumbnail");
                            String tgl_upload = c.getString("add_date");
                            String duration = /*c.getString("durasi")*/"";

                            Log.e(TAG, "id_video: " + id_video
                                    + ", title: " + title
                                    + ", description: " + description
                                    + ", url: " + url
                                    + ", thumbnail: " + thumbnail
                                    + ", tgl_upload: " + tgl_upload
                                    + ", duration: " + duration);

//                            PictureModel item = new PictureModel();

                            item2.setUserid(myuserid);
                            item2.setUserid(userid);
                            item2.setId_photo(id_video);
                            item2.setTitle(title);
                            item2.setDescription(description);

                            String vidDecode = Uri.decode(Uri.decode(c.getString("file_gallery")));
//                            String vid = c.isNull("file_video") ? null : vidDecode;
                            String vid = c.isNull("file_gallery") ? null : c.getString("file_gallery");
                            item2.setUrl(vid);

                            String thumb = c.isNull("thumbnail") ? null : c.getString("thumbnail");
                            item2.setUrl_thumb(thumb);

                            item2.setTgl_upload(tgl_upload);
                            item2.setDuration(duration);
                            item2.setDrawable(0);
                            item2.setFlag("false");
                            item2.setColor(color);

                            videoModels.add(item2);
                        }
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class AmbilVideoPersonalMine extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mswipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            JSONArray dataJsonArr = null;

            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userid = json.getString("userid");

                    dataJsonArr = json.getJSONArray("data");
                    if (dataJsonArr.length() > 0) {
                        videoModels.clear();
                        for (int i = 0; i < dataJsonArr.length() + 1; i++) {

                            if (i == 0) {
                                PictureModel item = new PictureModel();
                                item.setUrl("");
                                item.setUrl_thumb("");
                                item.setDescription("");
                                item.setDrawable(IMGS[i]);
                                videoModels.add(item);
                            } else {
                                JSONObject c = dataJsonArr.getJSONObject(i - 1);
                                PictureModel item2 = new PictureModel();
                                String id_video = c.getString("id_video");
                                String title = c.getString("title");
                                String description = c.getString("description");
                                String url = c.getString("file_video");
                                String thumbnail = c.getString("cover_video");
                                String tgl_upload = c.getString("tgl_upload");
                                String duration = c.getString("durasi");
                                item2.setMyuserid(messengerHelper.getMyContact().getJabberId());
                                item2.setUserid(myContact);
                                item2.setId_photo(id_video);
                                item2.setTitle(title);
                                item2.setDescription(description);
                                item2.setUrl(url);
                                item2.setUrl_thumb(thumbnail);
                                item2.setTgl_upload(tgl_upload);
                                item2.setDuration(duration);
                                item2.setDrawable(0);
                                item2.setFlag("false");
                                item2.setColor(color);

                                videoModels.add(item2);
                            }
                        }
                    }
                    adaptermine = new VideoAdapterMine(getContext(), videoModels);
                    mRecyclerView.setAdapter(adaptermine);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class AmbilVideoPersonal extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mswipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            JSONArray dataJsonArr = null;

            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userid = json.getString("userid");

                    dataJsonArr = json.getJSONArray("data");
                    if (dataJsonArr.length() > 0) {
                        videoModels.clear();
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);
                            PictureModel item2 = new PictureModel();
                            String id_video = c.getString("id_video");
                            String title = c.getString("title");
                            String description = c.getString("description");
                            String url = c.getString("file_video");
                            String thumbnail = c.getString("cover_video");
                            String tgl_upload = c.getString("tgl_upload");
                            String duration = c.getString("durasi");
                            item2.setMyuserid(messengerHelper.getMyContact().getJabberId());
                            item2.setUserid(myContact);
                            item2.setId_photo(id_video);
                            item2.setTitle(title);
                            item2.setDescription(description);
                            item2.setUrl(url);
                            item2.setUrl_thumb(thumbnail);
                            item2.setTgl_upload(tgl_upload);
                            item2.setDuration(duration);
                            item2.setDrawable(0);
                            item2.setFlag("false");
                            item2.setColor(color);

                            videoModels.add(item2);
                        }
                    }
                    adapter = new VideoAdapter(mContext.getApplicationContext(), videoModels);
                    mRecyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}