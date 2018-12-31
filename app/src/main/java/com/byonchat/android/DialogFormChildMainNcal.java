package com.byonchat.android;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Validations;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.byonchat.android.FragmentDinamicRoom.FragmentRoomAPI.function;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
@SuppressLint("ValidFragment")
public class DialogFormChildMainNcal extends DialogFragment {

    LinearLayout linearLayout;
    String content;
    String title;
    Boolean update = false;
    Bitmap result = null;
    ImageView imageView[];
    LinearLayout linearEstimasi[];
    RatingBar rat[];
    EditText et[];
    TextView tp[];
    Map<Integer, List<String>> hashMap = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapOcr = new HashMap<Integer, List<String>>();
    Integer count;
    TextView nameTitle;
    BotListDB botListDB;
    String dbMaster = "";
    String idDetail = "";
    String idListTaskMaster = "";
    String username = "";
    String idTab = "";
    String idchildDetail = "";
    String FormulaMaster = "";
    //sementara
    String customersId = "";
    private static final int REQ_CAMERA = 12011;
    private static final int REQ_GALLERY = 12022;
    int dummyIdDate;
    private BroadcastReceiver localBroadcastReceiver;
    Button mProceed, mCancel;
    Activity mContext;
    private static final String MENU_GALLERY_TITLE = "Gallery";
    private int attCurReq = 0;

    private ArrayList<AttachmentAdapter.AttachmentMenuItem> curAttItems;
    private static final ArrayList<AttachmentAdapter.AttachmentMenuItem> attCameraItems;

    static {
        attCameraItems = new ArrayList<AttachmentAdapter.AttachmentMenuItem>();
        attCameraItems.add(new AttachmentAdapter.AttachmentMenuItem(R.drawable.ic_att_photo,
                "Camera"));
        attCameraItems.add(new AttachmentAdapter.AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));


    }

    public static DialogFormChildMainNcal newInstance(String content, String title, String dbMaster, String idDetail, String username, String idTab, String idListTaskMaster, String idChildDetail, String customersId, Activity activity) {
        DialogFormChildMainNcal f = new DialogFormChildMainNcal(activity);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putString("dbMaster", dbMaster);
        args.putString("idDetail", idDetail);
        args.putString("username", username);
        args.putString("idListTaskMaster", idListTaskMaster);
        args.putString("idTab", idTab);
        args.putString("idChildDetail", idChildDetail);
        args.putString("customersId", customersId);
        f.setArguments(args);

        return f;
    }


    public DialogFormChildMainNcal(Activity ctx) {
        mContext = ctx;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);
        linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);
        nameTitle = (TextView) dialog.findViewById(R.id.name);
        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel = (Button) dialog.findViewById(R.id.btn_cancel);


        if (botListDB == null) {
            botListDB = BotListDB.getInstance(getContext());
        }

        return dialog;
    }

    public void delete() {


        List<RoomsDetail> list = botListDB.getAllRoomDetailFormWithFlagContent(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail");

        if (list.size() > 0) {
            for (RoomsDetail orderModel : list) {
                botListDB.deleteDetailRoomWithFlagContent(orderModel);
            }
        }


    }

    public void captureGalery(String idDetail, String username, String idTab, String idListTask, String type, String name, String flag, int facing, String idii) {

        Log.w("wow", flag);

        if (localBroadcastReceiver == null) {
            localBroadcastReceiver = new LocalBroadcastReceiver();
            LocalBroadcastManager.getInstance(mContext).registerReceiver(
                    localBroadcastReceiver,
                    new IntentFilter("SOME_ACTION"));
        }

        Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(idii)));

        if (cEdit.getCount() == 0) {
            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "", jsonCreateType(idListTask, type, String.valueOf(idii)), name, "child_detail");
            botListDB.insertRoomsDetail(orderModel);
        }

        dummyIdDate = Integer.parseInt(idListTask);

        if (flag.equalsIgnoreCase("live_camera")) {
            if (facing == 1) {
                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                CameraActivity.Builder start = new CameraActivity.Builder(mContext, REQ_CAMERA);
                start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                start.setCameraFace(CameraActivity.CAMERA_FRONT);
                start.setFlashMode(CameraActivity.FLASH_OFF);
                start.setQuality(CameraActivity.MEDIUM);
                start.setRatio(CameraActivity.RATIO_4_3);
                start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                new Camera(start.build()).lauchCamera();

            } else if (facing == 0) {
                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                CameraActivity.Builder start = new CameraActivity.Builder(mContext, REQ_CAMERA);
                start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                start.setCameraFace(CameraActivity.CAMERA_REAR);
                start.setFlashMode(CameraActivity.FLASH_OFF);
                start.setQuality(CameraActivity.MEDIUM);
                start.setRatio(CameraActivity.RATIO_4_3);
                start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                new Camera(start.build()).lauchCamera();
            }
        } else if (flag.equalsIgnoreCase("gallery_camera")) {
            showAttachmentDialog(REQ_CAMERA);
        } else if (flag.equalsIgnoreCase("gallery")) {
            showAttachmentDialog(REQ_CAMERA);
        }
    }

    private void showAttachmentDialog(int req) {
        if (req == REQ_CAMERA) {
            curAttItems = attCameraItems;
        }
        attCurReq = req;

        AttachmentAdapter adapter = new AttachmentAdapter(mContext,
                R.layout.menu_item, R.id.textMenu, curAttItems);

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.attachment_gridview);
        GridView gridview = (GridView) dialog.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String iTitle = curAttItems.get(pos).getTitle();
                String action = Intent.ACTION_GET_CONTENT;
                int req;
                Intent i;
                if (R.drawable.ic_att_video == curAttItems.get(0)
                        .getResourceIcon()) {

                } else {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        Log.w("hallo", "33");
                        if (Build.VERSION.SDK_INT < 19) {
                            i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("image/*");
                        } else {
                            i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                        }
                        req = REQ_GALLERY;
                        Log.w("hallo", REQ_GALLERY + "--");
                        i.setAction(action);
                        startActivityForResult(i, 12022);

                        attCurReq = 0;
                    } else {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        CameraActivity.Builder start = new CameraActivity.Builder(mContext, REQ_CAMERA);
                        start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                        start.setCameraFace(CameraActivity.CAMERA_REAR);
                        start.setFlashMode(CameraActivity.FLASH_OFF);
                        start.setQuality(CameraActivity.MEDIUM);
                        start.setRatio(CameraActivity.RATIO_4_3);
                        start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                        new Camera(start.build()).lauchCamera();

                    }
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCancel.getText().toString().equalsIgnoreCase("Cancel")) {
            String titleUntuk = "";
            String decsUntuk = "";
            String priceUntuk = "";

            List<RoomsDetail> list = botListDB.getAllRoomDetailFormWithFlagContent(idchildDetail, DialogFormChildMainNcal.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMainNcal.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail");

            for (int u = 0; u < list.size(); u++) {
                JSONArray jsA = null;
                String contentS = "";

                String cc = list.get(u).getContent();
                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                    cc = jsoncreateC(list.get(u).getContent());
                }

                try {
                    if (cc.startsWith("{")) {
                        if (!cc.startsWith("[")) {
                            cc = "[" + cc + "]";
                        }
                        jsA = new JSONArray(cc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsA != null) {
                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                        titleUntuk = jsonResultType(list.get(u).getContent(), "Name Detail");
                        if (titleUntuk.equalsIgnoreCase("")) {
                            titleUntuk = jsonResultType(list.get(u).getContent(), "SKU");
                        }
                    } else {
                        try {
                            for (int ic = 0; ic < jsA.length(); ic++) {
                                final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                contentS += icC + "|";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("number") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("currency")) {
                        decsUntuk = list.get(u).getContent();
                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("formula")) {
                        priceUntuk = list.get(u).getContent();
                    }

                }
            }

            boolean next = true;
            if (titleUntuk.equalsIgnoreCase("")) {
                next = false;
            } else if (priceUntuk.equalsIgnoreCase("")) {
                next = false;
            } else if (decsUntuk.equalsIgnoreCase("")) {
                next = false;
            }
            if (!next) {
                delete();
            }
        }
    }

    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }

    private String jsoncreateC(String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private String jsonCreateType(String id, String title, String desc) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", id);
            obj.put("b", title);
            obj.put("c", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    private String jsonCreateTypeTitle(String id, String name, String qty, String price) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", id);
            obj.put("b", name);
            obj.put("c", qty);
            obj.put("d", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static String jsonCreateIdTabNUsrName(String idTab, String username) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("idt", idTab);
            obj.put("usr", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static String jsonCreateIdDetailNIdListTaskOld(String iddtil, String idtsk) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("iddtl", iddtil);
            obj.put("idtsk", idtsk);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
        }
    }


    @Override
    public void onDestroy() {
        if (localBroadcastReceiver != null){
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(localBroadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (localBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(
                    localBroadcastReceiver);
        }*/

        if (idchildDetail.equalsIgnoreCase("")) {
            idchildDetail = getRandomString();
        } else {
            update = true;
        }

        if (update) {
            mProceed.setText("Save");
            mCancel.setText("Delete");
        }

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titleUntuk = "";
                String decsUntuk = "";
                String priceUntuk = "";

                List<RoomsDetail> list = botListDB.getAllRoomDetailFormWithFlagContent(idchildDetail, DialogFormChildMainNcal.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMainNcal.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail");
                Log.w("asd", list.size() + "");
                for (int u = 0; u < list.size(); u++) {
                    JSONArray jsA = null;
                    String contentS = "";

                    String cc = list.get(u).getContent();
                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                        cc = jsoncreateC(list.get(u).getContent());
                    }

                    try {
                        if (cc.startsWith("{")) {
                            if (!cc.startsWith("[")) {
                                cc = "[" + cc + "]";
                            }
                            jsA = new JSONArray(cc);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsA != null) {
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                            Log.w("ll223", list.get(u).getContent() + "::" + jsonResultType(list.get(u).getFlag_content(), "b"));
                        } else {
                            try {
                                for (int ic = 0; ic < jsA.length(); ic++) {
                                    final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                    content += icC + "|";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.w("ll2", list.get(u).getContent() + "::" + jsonResultType(list.get(u).getFlag_content(), "b"));
                        }
                    } else {
                        Log.w("ll", list.get(u).getContent() + "::" + jsonResultType(list.get(u).getFlag_content(), "b"));
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera") || (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("front_camera"))) {
                            titleUntuk = list.get(u).getContent();
                        } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("textarea") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("number") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("currency")) {
                            decsUntuk = list.get(u).getContent();
                        } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown")) {
                            titleUntuk = list.get(u).getContent();
                        }

                    }
                }
                Log.w("lolo", "cinta");
                boolean next = true;
                if (titleUntuk.equalsIgnoreCase("")) {
                    AlertDialog.Builder builder = DialogUtil.generateAlertDialog(mContext, "Required", "Please input field (*)");
                    builder.show();
                    next = false;
                } else if (decsUntuk.equalsIgnoreCase("")) {
                    /*     Log.w("soso", et.length + "");*/

                   /* if (et.length > 1) {
                        et[1].setError("required");
                        next = false;
                    } else {
                        et[0].setError("required");
                        next = false;
                    }*/

                }

                Log.w("lolo", next + "");


                if (next) {
                    Log.w("lolo1", idListTaskMaster);
                    Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idListTaskMaster, username, idTab, "titleChild", idDetail);

                    if (cEdit.getCount() > 0) {
                        Log.w("lolo2", "AA");
                        RoomsDetail orderModel = new RoomsDetail(idListTaskMaster, idTab, username, jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, ""), idDetail, "", "titleChild");
                        botListDB.updateDetailRoomWithFlagContent(orderModel);
                    } else {
                        Log.w("lolo3", idListTaskMaster + "::" + idTab + "::" + username + "::" + jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, "") + "::" + idDetail + "::" + "" + "::" + "titleChild");
                        RoomsDetail orderModel = new RoomsDetail(idListTaskMaster, idTab, username, jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, ""), idDetail, "", "titleChild");
                        botListDB.insertRoomsDetail(orderModel);
                    }


                    Cursor cEdit2 = botListDB.getSingleRoomDetailFormWithFlagContentChild(idListTaskMaster, username, idTab, "titleChild", idDetail);
                    Log.w("sss", cEdit2.getCount() + "");

                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                    Intent newIntent = new Intent("refreshForm");
                    mContext.sendBroadcast(newIntent);

                }

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getContext());
                alert.setTitle("Confirmation!!");
                if (mCancel.getText().toString().equalsIgnoreCase("Cancel")) {
                    alert.setMessage("Are you sure to cancel ?");
                } else {
                    alert.setMessage("Are you sure to delete ?");
                }

                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                        Intent newIntent = new Intent("refreshForm");
                        mContext.sendBroadcast(newIntent);
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();

            }
        });

        try {
            linearLayout.removeAllViews();
            count = null;
            JSONArray jsonArrayCild = new JSONArray(content);
            et = new EditText[jsonArrayCild.length()];
            tp = new TextView[jsonArrayCild.length()];
            rat = new RatingBar[jsonArrayCild.length()];
            linearEstimasi = new LinearLayout[jsonArrayCild.length()];
            imageView = new ImageView[jsonArrayCild.length()];
            for (int i = 0; i < jsonArrayCild.length(); i++) {

                final String idListTask = jsonArrayCild.getJSONObject(i).getString("order").toString();
                String label = jsonArrayCild.getJSONObject(i).getString("label").toString();
                final String name = jsonArrayCild.getJSONObject(i).getString("name").toString();
                String placeHolder = jsonArrayCild.getJSONObject(i).getString("placeholder").toString();
                String maxlength = jsonArrayCild.getJSONObject(i).getString("maxlength").toString();
                if (maxlength.equalsIgnoreCase("")) maxlength = "99";
                String required = jsonArrayCild.getJSONObject(i).getString("required").toString();
                final String value = jsonArrayCild.getJSONObject(i).getString("value").toString();
                final String type = jsonArrayCild.getJSONObject(i).getString("type").toString();
                final String flag = jsonArrayCild.getJSONObject(i).getString("flag").toString();


                if (type.equalsIgnoreCase("form_child")) {

                } else if (type.equalsIgnoreCase("text")) {

                    TextView textView = new TextView(getContext());
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }
                    et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    et[count].setId(Integer.parseInt(idListTask));
                    et[count].setHint(placeHolder);


                    et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(30, 10, 30, 40);


                    linearLayout.addView(textView, params1);
                    linearLayout.addView(et[count], params2);

                } else if (type.equalsIgnoreCase("formula")) {
                    FormulaMaster = jsonArrayCild.getJSONObject(i).getString("formula").toString();

                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }
                    et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    et[count].setId(Integer.parseInt(idListTask));
                    et[count].setHint(placeHolder);
                    et[count].setEnabled(false);

                    et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});
                    Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cEdit.getCount() > 0) {
                        et[count].setText(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                    } else {
                        if (!value.equalsIgnoreCase("")) {
                            et[count].setText(value);
                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(value), jsonCreateType(idListTask, type, String.valueOf(i)), name, "child_detail");
                            botListDB.insertRoomsDetail(orderModel);
                        }
                    }

                    final int finalI = i;
                    et[count].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "child_detail");
                                botListDB.updateDetailRoomWithFlagContent(orderModel);
                            } else {
                                if (String.valueOf(s).length() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "child_detail");
                                    botListDB.insertRoomsDetail(orderModel);
                                } else {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "child_detail");
                                    botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                }
                            }
                        }
                    });


                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(30, 10, 30, 40);


                    linearLayout.addView(textView, params1);
                    linearLayout.addView(et[count], params2);


                } else if (type.equalsIgnoreCase("textarea")) {

                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);
                    textView.setLayoutParams(new TableRow.LayoutParams(0));

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }
                    et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    et[count].setId(Integer.parseInt(idListTask));
                    et[count].setHint(placeHolder);
                    et[count].setMinLines(4);
                    et[count].setMaxLines(8);
                    et[count].setScroller(new Scroller(mContext));
                    et[count].setVerticalScrollBarEnabled(true);
                    et[count].setSingleLine(false);
                    et[count].setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                    et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});
                    Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cEdit.getCount() > 0) {
                        et[count].setText(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                    } else {
                        if (!value.equalsIgnoreCase("")) {
                            et[count].setText(value);
                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(value), jsonCreateType(idListTask, type, String.valueOf(i)), name, "child_detail");
                            botListDB.insertRoomsDetail(orderModel);
                        }
                    }

                    final int finalI = i;
                    et[count].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "child_detail");
                                botListDB.updateDetailRoomWithFlagContent(orderModel);
                            } else {
                                if (String.valueOf(s).length() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "child_detail");
                                    botListDB.insertRoomsDetail(orderModel);
                                } else {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "child_detail");
                                    botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                }
                            }
                        }
                    });
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(30, 10, 30, 40);


                    linearLayout.addView(textView, params1);
                    linearLayout.addView(et[count], params2);
                } else if (type.equalsIgnoreCase("text_info")) {

                } else if (type.equalsIgnoreCase("date")) {

                } else if (type.equalsIgnoreCase("time")) {

                } else if (type.equalsIgnoreCase("currency")) {

                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    et[count].setId(Integer.parseInt(idListTask));
                    et[count].setHint(placeHolder);
                    et[count].setInputType(InputType.TYPE_CLASS_NUMBER);
                    et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                    Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cursorCild.getCount() > 0) {
                        et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).replace(".", ""));
                    } else {
                        if (!value.equalsIgnoreCase("")) {
                            et[count].setText(value);
                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(value).replace(".", ""), jsonCreateType(idListTask, type, String.valueOf(i)), name, "child_detail");
                            botListDB.insertRoomsDetail(orderModel);
                        }
                    }
                    final int finalI3 = i;
                    et[count].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            List value = (List) hashMap.get(Integer.parseInt(idListTask));
                            et[Integer.valueOf(value.get(0).toString())].removeTextChangedListener(this);

                            try {
                                String originalString = s.toString();

                                Long longval;
                                if (originalString.contains(",")) {
                                    originalString = originalString.replaceAll(",", "");
                                }
                                longval = Long.parseLong(originalString);

                                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                formatter.applyPattern("#,###,###,###");
                                String formattedString = formatter.format(longval);

                                //setting text after format to EditText
                                et[Integer.valueOf(value.get(0).toString())].setText(formattedString);
                                et[Integer.valueOf(value.get(0).toString())].setSelection(et[Integer.valueOf(value.get(0).toString())].getText().length());
                                Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                                if (cEdit.getCount() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(formattedString), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "child_detail");
                                    botListDB.updateDetailRoomWithFlagContent(orderModel);
                                } else {
                                    if (String.valueOf(s).length() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(formattedString), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "child_detail");
                                        botListDB.insertRoomsDetail(orderModel);
                                    } else {
                                        RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(formattedString), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "child_detail");
                                        botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                    }
                                }

                            } catch (NumberFormatException nfe) {
                                nfe.printStackTrace();
                            }

                            et[Integer.valueOf(value.get(0).toString())].addTextChangedListener(this);
                        }
                    });

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(30, 10, 30, 40);


                    linearLayout.addView(textView, params1);
                    linearLayout.addView(et[count], params2);

                } else if (type.equalsIgnoreCase("number")) {

                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    et[count].setId(Integer.parseInt(idListTask));
                    et[count].setHint(placeHolder);
                    et[count].setInputType(InputType.TYPE_CLASS_NUMBER);
                    et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                    Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cursorCild.getCount() > 0) {
                        et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).replace(".", ""));
                    } else {
                        if (!value.equalsIgnoreCase("")) {
                            et[count].setText(value);
                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(value).replace(".", ""), jsonCreateType(idListTask, type, String.valueOf(i)), name, "child_detail");
                            botListDB.insertRoomsDetail(orderModel);
                        }
                    }
                    final int finalI3 = i;
                    et[count].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "child_detail");
                                botListDB.updateDetailRoomWithFlagContent(orderModel);
                            } else {
                                if (String.valueOf(s).length() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "child_detail");
                                    botListDB.insertRoomsDetail(orderModel);

                                } else {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "child_detail");
                                    botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                }
                            }
                        }
                    });

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(30, 10, 30, 40);


                    linearLayout.addView(textView, params1);
                    linearLayout.addView(et[count], params2);

                } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                    Log.w("masuk", "siniWow" + "::" + type);
                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(30, 10, 30, 0);
                    textView.setLayoutParams(params2);
                    linearLayout.addView(textView);

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    imageView[count] = (ImageView) mContext.getLayoutInflater().inflate(R.layout.image_view_frame, null);
                    int width = mContext.getWindowManager().getDefaultDisplay().getWidth();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                    params.setMargins(5, 15, 0, 0);
                    imageView[count].setLayoutParams(params);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    linearLayout.addView(imageView[count], params);
                    final Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cursorCild.getCount() > 0) {
                        Log.w("lasdl", cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).toString());
                        String aa[] = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).toString().split(";");
                        if (aa.length == 2) {
                            Uri imgUri = Uri.parse(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).toString().split(";")[1]);
                            imageView[count].setImageURI(imgUri);
                        }
                    }

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    final int finalI4 = i;
                    imageView[count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cursorCild.getCount() > 0) {
                                String[] uti = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).toString().split(";");
                                if (uti.length > 1) {
                                    Uri imgUri = Uri.parse(uti[1]);
                                    Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, imgUri.toString());
                                    startActivity(intent);
                                } else {
                                    int facing = 0;
                                    if (type.equalsIgnoreCase("front_camera")) {
                                        facing = 1;
                                    }
                                    captureGalery(idDetail, username, idTab, idListTask, type, name, flag, facing, String.valueOf(finalI4));
                                }
                            } else {
                                int facing = 0;
                                if (type.equalsIgnoreCase("front_camera")) {
                                    facing = 1;
                                }
                                captureGalery(idDetail, username, idTab, idListTask, type, name, flag, facing, String.valueOf(finalI4));
                            }
                        }
                    });

                    final String finalLabel = label;
                    final int finalI5 = i;
                    imageView[count].setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    mContext);
                            builderSingle.setTitle("Select an action " + Html.fromHtml(finalLabel));
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    mContext,
                                    android.R.layout.simple_list_item_1);


                            if (cursorCild.getCount() > 0) {
                                arrayAdapter.add("Retake");
                                arrayAdapter.add("View");
                            } else {
                                arrayAdapter.add("Capture");
                            }
                            builderSingle.setAdapter(arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String listName = arrayAdapter.getItem(which);
                                            if (listName.equalsIgnoreCase("Retake") || listName.equalsIgnoreCase("Capture")) {
                                                int facing = 0;
                                                if (type.equalsIgnoreCase("front_camera")) {
                                                    facing = 1;
                                                }
                                                captureGalery(idDetail, username, idTab, idListTask, type, name, flag, facing, String.valueOf(finalI4));
                                            } else if (listName.equalsIgnoreCase("View")) {
                                                Uri imgUri = Uri.parse(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                                                intent.putExtra(ZoomImageViewActivity.KEY_FILE, imgUri);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                            builderSingle.show();
                            return true;
                        }
                    });
                } else if (type.equalsIgnoreCase("ocr")) {

                } else if (type.equalsIgnoreCase("upload_document")) {

                } else if (type.equalsIgnoreCase("signature")) {

                } else if (type.equalsIgnoreCase("distance_estimation")) {

                } else if (type.equalsIgnoreCase("rate")) {

                } else if (type.equalsIgnoreCase("map")) {

                } else if (type.equalsIgnoreCase("video")) {

                } else if (type.equalsIgnoreCase("dropdown_dinamis")) {

                } else if (type.equalsIgnoreCase("new_dropdown_dinamis")) {

                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);
                    textView.setLayoutParams(new TableRow.LayoutParams(0));

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);
                    linearLayout.addView(textView, params1);

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(String.valueOf(count));
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    linearEstimasi[count] = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.layout_child, null);
                    //[{"url":"https:\/\/bb.byonchat.com\/bc_voucher_client\/public\/list_task\/dropdown_dinamis\/","data":[{"title":"Model","value":"model"},{"title":"Group","value":"item_group"},{"title":"Name Detail","value":"item_name_detail"},{"title":"Code Number","value":"item_number"}],"value":""}]
                    String valueTo = value;
                    JSONArray jjj = new JSONArray(value);
                    if (jjj.length() == 1) {
                        valueTo = jjj.getString(0).toString();
                    }
                    JSONObject jObject = new JSONObject(valueTo);
                    String url = jObject.getString("url");
                    String table = jsonArrayCild.getJSONObject(i).getString("formula").toString();
                    final ArrayList<String> kolom = new ArrayList<>();
                    ArrayList<String> title = new ArrayList<>();

                    JSONArray jsonData = jObject.getJSONArray("data");
                    try {
                        for (int ii = 0; ii < jsonData.length(); ii++) {
                            JSONObject oContent = new JSONObject(jsonData.getString(ii));
                            kolom.add(oContent.getString("value").toString());
                            title.add(oContent.getString("title").toString());
                        }
                    } catch (Exception e) {

                    }


                    DataBaseDropDown mDB = null;

                    String[] aa = url.split("/");
                    String nama = aa[aa.length - 1].toString();
                    if (!nama.contains(".")) {
                        if (!dbMaster.equalsIgnoreCase("")) {
                            String[] aaBB = dbMaster.split("/");
                            nama = aaBB[aaBB.length - 1].toString();
                        }
                    }
                    mDB = new DataBaseDropDown(getContext(), nama.substring(0, nama.indexOf(".")));

                    if (mDB.getWritableDatabase() != null) {
                        String namaTable = table;
                        linearEstimasi[count].removeAllViews();

                        String[] columnNames = new String[kolom.size()];
                        columnNames = kolom.toArray(columnNames);
                        String[] titleNames = new String[title.size()];
                        titleNames = title.toArray(titleNames);
                        final Cursor c = mDB.getWritableDatabase().query(true, namaTable, new String[]{columnNames[0]}, null, null, columnNames[0], null, null, null);

                        final ArrayList<String> spinnerArray = new ArrayList<String>();
                        spinnerArray.add("--Please Select--");
                        if (c.moveToFirst()) {
                            do {
                                String column1 = c.getString(0);
                                spinnerArray.add(column1);
                            } while (c.moveToNext());
                        }
                        c.close();

                        final LinearLayout spinerTitle = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                        TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                        final String titlesss = title.get(0).toString();
                        textViewFirst.setText(Html.fromHtml(titlesss));
                        textViewFirst.setTextSize(15);

                        final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);

                        Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(titlesss));
                                spinner.setSelection(spinnerPosition);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        final String finalNamaTable = namaTable;
                        final int finalI24 = i;
                        final String[] finalColumnNames = columnNames;
                        final String finalNama = nama;
                        final String[] finalTitleNames = titleNames;
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                dummyIdDate = Integer.parseInt(idListTask);
                                List nilai = (List) hashMap.get(dummyIdDate);
                                if (!spinner.getSelectedItem().toString().replace("'", "''").equals("--Please Select--")) {
                                    if (kolom.size() > 1) {
                                        final int counts = linearEstimasi[Integer.valueOf(nilai.get(0).toString())].getChildCount();
                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeViews(1, counts - 1);
                                        addSpinnerDinamic(finalTitleNames, finalNama.substring(0, finalNama.indexOf(".")), linearEstimasi[Integer.valueOf(nilai.get(0).toString())], finalNamaTable, finalColumnNames, 0, finalColumnNames[0] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'", idListTask, type, String.valueOf(finalI24), name);
                                    } else {
                                        // refresh(FormulaMaster,customersId,spinner.getSelectedItem().toString().replace("'","''"),finalNama.substring(0, finalNama.indexOf(".")));
                                    }

                                    Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                    if (cEdit.getCount() > 0) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(jsonObject, titlesss, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");

                                            botListDB.updateDetailRoomWithFlagContent(orderModel);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        Log.w("satu", "2");
                                        RoomsDetail orderModel = null;
                                        try {
                                            orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(null, titlesss, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");

                                            botListDB.insertRoomsDetail(orderModel);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                } else {
                                    //refresh("","","","");
                                    if (kolom.size() > 1) {
                                        RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");

                                        botListDB.deleteDetailRoomWithFlagContent(orderModel);

                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);
                                    }

                                }
                               /* Intent newIntent = new Intent("bLFormulas");
                                mContext.sendBroadcast(newIntent);*/
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // your code here
                            }

                        });

                        linearEstimasi[count].addView(spinerTitle);

                    } else {
                        /*if (deleteContent) {

                            botListDB.deleteRoomsDetailbyId(idDetail, idTab, username);

                        }
                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            Toast.makeText(getApplicationContext(), "Please insert memmory card", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        finish();
                        Intent intent = new Intent(getApplicationContext(), DownloadSqliteDinamicActivity.class);
                        intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                        intent.putExtra("path_db", url);
                        startActivity(intent);
                        return;*/
                    }
                    TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                    params2.setMargins(60, 10, 30, 0);
                    linearEstimasi[count].setLayoutParams(params2);
                    linearLayout.addView(linearEstimasi[count]);
                } else if (type.equalsIgnoreCase("dropdown")) {
                    Log.w("ne", idListTaskMaster);
                    TextView textView = new TextView(mContext);
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);
                    textView.setLayoutParams(new TableRow.LayoutParams(0));

                    TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                    String isi = jsonArrayCild.getJSONObject(i).getString("dropdown").toString();
                    JSONArray jsonArrays = new JSONArray(isi);
                    final ArrayList<String> spinnerArray = new ArrayList<String>();
                    for (int ia = 0; ia < jsonArrays.length(); ia++) {
                        String l = jsonArrays.getJSONObject(ia).getString("label_option").toString();
                        spinnerArray.add(l);
                    }

                    final SearchableSpinner spinner = new SearchableSpinner(mContext);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                    params2.setMargins(30, 10, 30, 40);
                    spinner.setLayoutParams(params2);

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add("");
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));
                    hashMap.put(Integer.parseInt(idListTask), valSetOne);
                    final int finalI7 = i;
                    Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cursorCild.getCount() > 0) {
                        int spinnerPosition = spinnerArrayAdapter.getPosition(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        spinner.setSelection(spinnerPosition);
                    } else {
                        if (spinner.getSelectedItem() != null) {
                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                            botListDB.insertRoomsDetail(orderModel);
                        }

                    }
                    // final EditText et = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {
                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                                botListDB.updateDetailRoomWithFlagContent(orderModel);
                            } else {
                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                                botListDB.insertRoomsDetail(orderModel);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);


                    linearLayout.addView(textView, params1);
                    linearLayout.addView(spinner, params2);


                } else if (type.equalsIgnoreCase("input_kodepos")) {

                } else if (type.equalsIgnoreCase("dropdown_wilayah")) {

                } else if (type.equalsIgnoreCase("checkbox")) {

                } else if (type.equalsIgnoreCase("radio")) {

                } else if (type.equalsIgnoreCase("image_load")) {

                } else if (type.equalsIgnoreCase("hidden")) {

                } else if (type.equalsIgnoreCase("longlat")) {

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments().getString("content");
        title = getArguments().getString("title");
        dbMaster = getArguments().getString("dbMaster");
        idDetail = getArguments().getString("idDetail");
        idTab = getArguments().getString("idTab");
        username = getArguments().getString("username");
        idListTaskMaster = getArguments().getString("idListTaskMaster");
        idchildDetail = getArguments().getString("idChildDetail");
        customersId = getArguments().getString("customersId");


    }

    private String getRandomString() {
        long currentTimeMillis = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();

        char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] result = new char[8];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }

        String resRandom = String.valueOf(currentTimeMillis) + new String(result);

        return resRandom;
    }

    public void addSpinnerDinamic(final String[] jsonValue, final String namedb, final LinearLayout view, final String table, final String[] coloum, final Integer from, final String where, final String idListTask, final String type, final String finalI24, final String name) {

        boolean showSpinner = true;
        DataBaseDropDown mDB = new DataBaseDropDown(getContext(), namedb);
        if (mDB.getWritableDatabase() != null) {
            final Integer asIs = from + 1;
            if (asIs < coloum.length) {
                String titlle = jsonValue[asIs];
                final Cursor c = mDB.getWritableDatabase().query(true, table, new String[]{coloum[asIs]}, where, null, coloum[asIs], null, null, null);
                final ArrayList<String> spinnerArray = new ArrayList<String>();
                if (coloum.length - 1 != asIs) {
                    spinnerArray.add("--Please Select--");
                } else {
                    showSpinner = false;
                }

                if (c.moveToFirst()) {
                    do {
                        String column1 = c.getString(0);

                        spinnerArray.add(column1);
                        if (!showSpinner) {


                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                            if (cEdit.getCount() > 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(jsonObject, titlle, column1).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                    botListDB.updateDetailRoomWithFlagContent(orderModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                RoomsDetail orderModel = null;
                                try {
                                    orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(null, titlle, column1).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                    botListDB.insertRoomsDetail(orderModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    } while (c.moveToNext());
                }
                c.close();

                LinearLayout spinerTitle = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);


                textViewFirst.setText(Html.fromHtml(titlle));
                textViewFirst.setTextSize(15);
                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                final TextView textView = (TextView) spinerTitle.findViewById(R.id.lastSpinner);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                }


                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                final String finalTitlle = titlle;


                Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                if (cEdit.getCount() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(finalTitlle));
                        spinner.setSelection(spinnerPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        final int count = view.getChildCount();
                        view.removeViews(asIs + 1, count - (asIs + 1));
                        if (!spinner.getSelectedItem().toString().replace("'", "''").equals("--Please Select--")) {
                            addSpinnerDinamic(jsonValue, namedb, view, table, coloum, asIs, where + " and " + coloum[asIs] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'", idListTask, type, finalI24, name);

                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                            if (cEdit.getCount() > 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(jsonObject, finalTitlle, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                    botListDB.updateDetailRoomWithFlagContent(orderModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                RoomsDetail orderModel = null;
                                try {
                                    orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(null, finalTitlle, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                    botListDB.insertRoomsDetail(orderModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                if (showSpinner) {
                    spinner.setVisibility(View.VISIBLE);
                } else {

                    if (spinnerArray.get(0).startsWith("http") && spinnerArray.get(0).endsWith(".jpg")) {
                        final RelativeLayout relative = (RelativeLayout) spinerTitle.findViewById(R.id.lastImage);
                        final ImageView imageView = (ImageView) spinerTitle.findViewById(R.id.value);
                        final AVLoadingIndicatorView progress = (AVLoadingIndicatorView) spinerTitle.findViewById(R.id.loader_progress);

                        Picasso.with(getContext()).load(spinnerArray.get(0)).into(imageView);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                                intent.putExtra(ZoomImageViewActivity.KEY_FILE, spinnerArray.get(0));
                                startActivity(intent);
                            }
                        });

                        relative.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                        spinner.setVisibility(View.GONE);
                        textView.setText(spinnerArray.get(0));

                    } else {

                        textView.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.GONE);
                        textView.setText(spinnerArray.get(0));
                    }

                }
                view.addView(spinerTitle);
            }
        }
/*        Intent newIntent = new Intent("bLFormulas");
        mContext.sendBroadcast(newIntent);*/
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // safety check
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals("SOME_ACTION")) {
                List value = (List) hashMap.get(dummyIdDate);
                if (value != null) {
                    if (value.size() > 0) {
                        if (intent.getStringExtra("data").equalsIgnoreCase("")) {
                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "child_detail");
                                    botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                }
                            }
                            Toast.makeText(mContext, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                        } else {
                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {

                                if (decodeFile(intent.getStringExtra("data"))) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), ";" + intent.getStringExtra("data"), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "child_detail");
                                    botListDB.updateDetailRoomWithFlagContent(orderModel);
                                    onResume();
                                }

                            }
                        }
                    } else {
                        Toast.makeText(mContext, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    public boolean decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return false;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            ExifInterface exif = new ExifInterface(path);
            orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            final File f = new File(path);
            if (f.exists()) f.delete();
            try {
                FileOutputStream out = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;

        } catch (Exception e) {
            return false;
        }

    }


}


//64884