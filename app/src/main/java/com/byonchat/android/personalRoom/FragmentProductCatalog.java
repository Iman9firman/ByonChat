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
import android.provider.MediaStore;
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

import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.adapter.PictureAdapterMine;
import com.byonchat.android.personalRoom.adapter.ProductCatalogAdapter;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.EndlessRecyclerViewScrollListener;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.UtilsPD;

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
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentProductCatalog extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private Boolean personal;
    private static final String TAG = FragmentProductCatalog.class.getSimpleName();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1000;
    private static final int RESULT_LOAD_IMAGE_REQUEST_CODE = 2000;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;
    private static final String IMAGE_DIRECTORY_NAME = "ByonChat";
    //private static final String URL_DATA_PICTURE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/category_tab/product_catalog.php";
    private ArrayList<PictureModel> pictureModels;
    private ProductCatalogAdapter adapter;
    private PictureAdapterMine adapterMine;
    private BotListDB db;
    private MessengerDatabaseHelper messengerHelper;
    private EndlessRecyclerViewScrollListener scrollListener;
    private GridLayoutManager gridLayoutManager;
    private PictureDetailActivity pictureActivity;
    private static String URL_DELETE_PICTURE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/delete_photo.php";
    private Activity mContext ;
    public static int IMGS[] = {
            R.drawable.bt_add_image,
    };

    public FragmentProductCatalog(Activity ctx) {
        mContext = ctx;
    }
    public static FragmentProductCatalog newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag, Activity act) {
        FragmentProductCatalog fragmentRoomTask = new FragmentProductCatalog(act);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("aa", title);
        outState.putString("bb", urlTembak);
        outState.putString("cc", username);
        outState.putString("dd", idRoomTab);
        outState.putString("ee", myContact);
        outState.putString("col", color);
        outState.putBoolean("fla", personal);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        View sss = inflater.inflate(R.layout.room_fragment_task_pull, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getContext(), 4);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(getContext(), 6);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        return sss;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (pictureModels == null) {
            pictureModels = new ArrayList<PictureModel>();
        }

        pictureActivity = new PictureDetailActivity();

        if (!personal) {
            adapter = new ProductCatalogAdapter(mContext.getApplicationContext(), pictureModels);
            mRecyclerView.setAdapter(adapter);
        } else {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                adapterMine = new PictureAdapterMine(mContext.getApplicationContext(), pictureModels);
                mRecyclerView.setAdapter(adapterMine);

                adapterMine.setOnLongClickListener(new PictureAdapterMine.MyClickListenerLongClick() {

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
                                descConfirmation.setText("Do you want to delete this picture?");

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
                                        new deletePicture().execute(URL_DELETE_PICTURE, pictureModels.get(position).getUserid(), pictureModels.get(position).getId_photo());
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                new AmbilGambarNext(totalItemsCount).execute(urlTembak, myContact, username, idRoomTab);
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (personal) {

                } else {
                    if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                        new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
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
            final PictureModel item = pictureModels.get(position);
                Intent intent = new Intent(mContext, DownloadFileByonchat.class);
                intent.putExtra("path", item.getUrl());
                intent.putExtra("nama_file",item.getTitle());
                mContext.startActivity(intent);
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        if (personal) {

        } else {
            if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
            } else {
                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
                if (allRoomDetailFormWithFlag.size() > 0) {
                    refresh(allRoomDetailFormWithFlag, false);
                } else {
                    if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                        new AmbilGambar().execute(urlTembak, myContact, username, idRoomTab);
                    }
                }
            }
        }
    }

    void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void ShowAlertDialogWithListview() {
        List<String> mAnimals = new ArrayList<String>();
        mAnimals.add("Take a photo");
        mAnimals.add("Choose from gallery");
        final CharSequence[] Animals = mAnimals.toArray(new String[mAnimals.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    captureImage();
                } else if (item == 1) {
                    loadImage();
                }
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void loadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                launchUploadActivity(true);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == RESULT_LOAD_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(getActivity(), selectedUri);
                launchUploadActivityFromImgSelected(selectedImagePath);

            } else if (resultCode == getActivity().RESULT_CANCELED) {
            } else {
                Toast.makeText(mContext.getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity(boolean isImage) {
        if (personal) {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(fileUri.getPath(), myContact);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        } else {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(fileUri.getPath(), username);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        }
    }

    private void launchUploadActivityFromImgSelected(String sourceFileUri) {
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
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(sourceFileUri, myContact);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onResume();
                }
            });
            d.show(getFragmentManager(), "dialog");
        } else {
            DialogFragmentPicture d = new DialogFragmentPicture();
            d.DialogFragmentPicture(sourceFileUri, username);
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

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    class deletePicture extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(mContext.getApplicationContext());
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            data.put("id_foto", params[2]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {
                Toast.makeText(mContext.getApplicationContext(), "Picture has been deleted", Toast.LENGTH_SHORT).show();
                onResume();
            } else {
                Toast.makeText(mContext.getApplicationContext(), "Failed to delete picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AmbilGambar extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        String idTab;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username_room", params[2]);
            data.put("id_rooms_tab", params[3]);
//            idTab = params[3];
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            onItemsLoadComplete();
            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {

                JSONArray dataJsonArr = null;
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userId = json.getString("id_rooms_tab");
                    String json_userRoom = json.getString("username_room");
                    db.deleteRoomsDetailPtabPRoom(idRoomTab, username);
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        String id = c.getString("id");
                        RoomsDetail orderModel = new RoomsDetail(id, json_userId, json_userRoom, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        db.insertRoomsDetail(orderModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
                if (allRoomDetailFormWithFlag != null) {

                    refresh(allRoomDetailFormWithFlag, true);
                }
            }
        }
    }

    public void refresh(ArrayList<RoomsDetail> s, boolean refresh) {
        try {

            if (refresh) {
                pictureModels = new ArrayList<>();
            }

            for (RoomsDetail ss : s) {

                JSONObject c = new JSONObject(ss.getContent());
                PictureModel item2 = new PictureModel();
                String id = c.getString("id");
                String title = c.getString("title");
                String description = c.getString("description");
                String file_foto = c.getString("file");
                String thump = c.getString("thumbnail");
//                String file_foto = "https://bb.byonchat.com/bc_voucher_client/public/product_catalog/FILE_173_2392_07112018_145057_BiOAITMoLG.png";
                String type = c.getString("type");
                String tgl_upload = c.getString("add_date");
                item2.setMyuserid(myContact);
                item2.setUserid(username);
                item2.setId_photo(id);
                item2.setTitle(title);
                item2.setDescription(description);
                item2.setUrl(file_foto);
                item2.setTgl_upload(tgl_upload);
                item2.setDrawable(0);
                item2.setFlag("true");
                item2.setColor(color);
                item2.setUrl_thumb(thump);
                item2.setType(type);
                pictureModels.add(item2);

            }

            adapter = new ProductCatalogAdapter(mContext.getApplicationContext(), pictureModels);
            mRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class AmbilGambarNext extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        String idTab;
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
            idTab = params[3];
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            onItemsLoadComplete();
            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                JSONArray dataJsonArr = null;
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userId = json.getString("id_rooms_tab");
                    String json_userRoom = json.getString("username_room");
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        String id = c.getString("id");
                        RoomsDetail orderModel = new RoomsDetail(id, json_userId, json_userRoom, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        db.insertRoomsDetail(orderModel);
                        PictureModel item2 = new PictureModel();
                        String title = c.getString("title");
                        String description = c.getString("description");
                        String file_foto = c.getString("file");
                        String thump = c.getString("thumbnail");
//                        String file_foto = "https://bb.byonchat.com/bc_voucher_client/public/product_catalog/FILE_173_2392_07112018_145057_BiOAITMoLG.png";
                        String type = c.getString("type");
                        String tgl_upload = c.getString("add_date");
                        item2.setMyuserid(myContact);
                        item2.setUserid(username);
                        item2.setId_photo(id);
                        item2.setTitle(title);
                        item2.setDescription(description);
                        item2.setUrl(file_foto);
                        item2.setTgl_upload(tgl_upload);
                        item2.setDrawable(0);
                        item2.setFlag("true");
                        item2.setType(type);
                        item2.setUrl_thumb(thump);
                        item2.setColor(color);
                        pictureModels.add(item2);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
