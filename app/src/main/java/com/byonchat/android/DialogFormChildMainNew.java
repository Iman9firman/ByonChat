package com.byonchat.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TableRow;
import android.widget.TextView;

import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.widget.SpinnerCustomAdapter;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.byonchat.android.FragmentDinamicRoom.FragmentRoomAPI.function;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogFormChildMainNew extends DialogFragment {

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
    String posisi = "";

    String labelAlert = "";
    //sementara
    String customersId = "";
    Boolean idr = true;
    Boolean tax = true;
    int dummyIdDate;
    String hargaDasarIDR = "0";
    String hargaDasarUSD = "0";
    Button mProceed, mCancel;

    ArrayList<String> lolosReq = new ArrayList<>();

    public static DialogFormChildMainNew newInstance(String content, String title, String dbMaster, String idDetail, String username, String idTab, String idListTaskMaster, String idChildDetail, String customersId, Activity activity, String posisi) {
        Log.w("masudk2", posisi);
        DialogFormChildMainNew f = new DialogFormChildMainNew();
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
        args.putString("posisi", posisi);
        f.setArguments(args);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);
        Log.w("sendiri", "oke5::" + idTab);
        linearLayout = (LinearLayout) dialog.findViewById(R.id.linear);
        nameTitle = (TextView) dialog.findViewById(R.id.name);
        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        if (idchildDetail.equalsIgnoreCase("")) {
            idchildDetail = getRandomString();
        } else {
            mProceed.setText("Update");
            mCancel.setText("Delete");
        }

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(getContext());
        }

        Log.e("errorslow", content);
        try {
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

                Log.w("sida", type + "::" + label);
                if (type.equalsIgnoreCase("load_dropdown")) {

                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    TextView textView = new TextView(getActivity());
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label));
                    textView.setTextSize(15);
                    textView.setLayoutParams(new TableRow.LayoutParams(0));

                    TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);

                    // String downloadForm = jsonArrayCild.getJSONObject(i).getString("formula").toString();
                    String downloadForm = new ValidationsKey().getInstance(getContext()).getTargetUrl(username) + "/bc_voucher_client/webservice/list_api/api_dropdown_spk.php";

                    Log.w("dirimu", downloadForm);
                    final ArrayList<String> spinnerArray = new ArrayList<String>();
                    spinnerArray.add("-");

                    SearchableSpinner spinner = new SearchableSpinner(getActivity());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                    }
                    MessengerDatabaseHelper messengerHelper = null;
                    if (messengerHelper == null) {
                        messengerHelper = MessengerDatabaseHelper.getInstance(getActivity());
                    }

                    Contact contact = messengerHelper.getMyContact();

                    List<String> listId = botListDB.getAllRoomDetailFormWithFlagContentWithOutIdContent(jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail");
                    SpinnerCustomAdapter spinnerArrayAdapter = new SpinnerCustomAdapter(getActivity(), R.layout.simple_spinner_item_black, downloadForm, contact.getJabberId(), spinnerArray);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerArrayAdapter.nilai((ArrayList<String>) listId);
                    spinner.setAdapter(spinnerArrayAdapter);
                    params2.setMargins(30, 10, 30, 40);
                    spinner.setLayoutParams(params2);


                    Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cEdit.getCount() > 0) {
                        int spinnerPosition = spinnerArrayAdapter.getPosition(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        spinner.setSelection(spinnerPosition);
                    }

                    final int finalI7 = i;
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {

                            if (!spinnerArrayAdapter.getValues().get(myPosition).equals("--Please Select--") && !spinnerArrayAdapter.getValues().get(myPosition).equals("-")) {
                                Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                if (cEdit.getCount() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                                    botListDB.updateDetailRoomWithFlagContent(orderModel);
                                } else {
                                    if (String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)).length() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                                        botListDB.insertRoomsDetail(orderModel);
                                    } else {
                                        RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                                        botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                    }
                                }
                            } else {
                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "child_detail");
                                botListDB.deleteDetailRoomWithFlagContent(orderModel);
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(30, 10, 30, 0);

                    LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params12.setMargins(50, 10, 30, 0);

                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add("");
                    valSetOne.add(required);
                    valSetOne.add(type);
                    valSetOne.add(name);
                    valSetOne.add(label);
                    valSetOne.add(String.valueOf(i));

                    valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                    linearLayout.addView(textView, params1);

                    valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                    linearLayout.addView(spinner, params1);
                    View view = new View(getActivity());
                    view.setVisibility(View.INVISIBLE);

                    valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                    linearLayout.addView(view, params2);

                    hashMap.put(Integer.parseInt(idListTask), valSetOne);

                } else if (type.equalsIgnoreCase("form_child")) {

                } else if (type.equalsIgnoreCase("text")) {


                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }
                    tp[count] = new TextView(getContext());
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    tp[count].setText(Html.fromHtml(label));
                    tp[count].setTextSize(15);

                    et[count] = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);
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


                    linearLayout.addView(tp[count], params1);
                    linearLayout.addView(et[count], params2);

                } else if (type.equalsIgnoreCase("formula")) {
                    FormulaMaster = jsonArrayCild.getJSONObject(i).getString("formula").toString();

                    TextView textView = new TextView(getActivity());
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
                    et[count] = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);
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

                    TextView textView = new TextView(getActivity());
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
                    et[count] = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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
                    et[count].setScroller(new Scroller(getActivity()));
                    et[count].setVerticalScrollBarEnabled(true);
                    et[count].setSingleLine(false);
                    et[count].setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
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
                } else if (type.equalsIgnoreCase("text_info")) {

                } else if (type.equalsIgnoreCase("date")) {

                } else if (type.equalsIgnoreCase("time")) {

                } else if (type.equalsIgnoreCase("number")) {


                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    tp[count] = new TextView(getActivity());
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    tp[count].setText(Html.fromHtml(label));
                    tp[count].setTextSize(15);

                    et[count] = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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


                    linearLayout.addView(tp[count], params1);
                    linearLayout.addView(et[count], params2);

                } else if (type.equalsIgnoreCase("currency")) {


                    if (count == null) {
                        count = 0;
                    } else {
                        count++;
                    }

                    tp[count] = new TextView(getActivity());
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    tp[count].setText(Html.fromHtml(label));
                    tp[count].setTextSize(15);

                    et[count] = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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


                    linearLayout.addView(tp[count], params1);
                    linearLayout.addView(et[count], params2);

                } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {

                } else if (type.equalsIgnoreCase("ocr")) {

                } else if (type.equalsIgnoreCase("upload_document")) {

                } else if (type.equalsIgnoreCase("signature")) {

                } else if (type.equalsIgnoreCase("distance_estimation")) {

                } else if (type.equalsIgnoreCase("rate")) {

                } else if (type.equalsIgnoreCase("map")) {

                } else if (type.equalsIgnoreCase("video")) {

                } else if (type.equalsIgnoreCase("dropdown_dinamis")) {
                    Log.w("siap", "1");
                } else if (type.equalsIgnoreCase("new_dropdown_dinamis")) {
                    Log.w("siap", "2");
                    TextView textView = new TextView(getActivity());
                    labelAlert = label;
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label.replace("+", " ")));
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

                    linearEstimasi[count] = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.layout_child, null);
                    String valueTo = value;
                    if (value.startsWith("[")) {

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
                            title.add(oContent.getString("title").toString().replace("+", " "));
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
                    String where = null;
                    if (nama.substring(0, nama.indexOf(".")).equalsIgnoreCase("SQL_29122017_144028_Hey89n63eA")) {

                        where = "kelas = '" + customersId + "'";

                    }

                    if (mDB.getWritableDatabase() != null) {
                        String namaTable = table;
                        linearEstimasi[count].removeAllViews();

                        String[] columnNames = new String[kolom.size()];
                        columnNames = kolom.toArray(columnNames);
                        String[] titleNames = new String[title.size()];
                        titleNames = title.toArray(titleNames);
                        final Cursor c = mDB.getWritableDatabase().query(true, namaTable, new String[]{columnNames[0]}, where, null, columnNames[0], null, null, null);
                        //final Cursor c = mDB.getWritableDatabase().query(true, "siswa", new String[]{"nama_siswa"}, null, null, "nama_siswa", null, null, null);

                        final ArrayList<String> spinnerArray = new ArrayList<String>();
                        spinnerArray.add("--Please Select--");
                        if (c.moveToFirst()) {
                            do {
                                String column1 = c.getString(0);
                                spinnerArray.add(column1);
                            } while (c.moveToNext());
                        }
                        c.close();

                        final LinearLayout spinerTitle = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                        TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                        final String titlesss = title.get(0).toString();
                        textViewFirst.setText(Html.fromHtml(titlesss.replace("+", " ")));
                        textViewFirst.setTextSize(15);

                        final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);

                        Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(titlesss));
                                if (spinnerPosition < 0) {
                                    spinnerPosition = spinnerArrayAdapter.getPosition("--Add--");
                                }
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

                                if (spinner.getSelectedItem().toString().replace("'", "''").equals("--Please Select--")) {

                                    if (kolom.size() > 1) {
                                        RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");

                                        botListDB.deleteDetailRoomWithFlagContent(orderModel);

                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);
                                    }

                                } else if (spinner.getSelectedItem().toString().equals("--Add--")) {
                                    Log.w("siap2", "2");
                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);

                                    JSONObject jsonObject = null;
                                    Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                    if (cursorCild.getCount() > 0) {
                                        try {
                                            jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                            boolean lanjut = false;
                                            for (String abubu : spinnerArray) {
                                                if (abubu.equalsIgnoreCase(jsonObject.getString(titlesss))) {
                                                    lanjut = true;
                                                }
                                            }

                                            if (lanjut) {
                                                jsonObject = null;
                                                if (kolom.size() > 1) {
                                                    RoomsDetail orderModel = null;
                                                    try {
                                                        orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(jsonObject, titlesss, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    // RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                    botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                                }

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    final EditText etFc[] = new EditText[finalTitleNames.length];
                                    int ids = 0;
                                    for (final String ahai : finalTitleNames) {

                                        TextView textView = new TextView(getActivity());
                                        textView.setText(ahai);
                                        textView.setTextSize(15);
                                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params1.setMargins(30, 10, 30, 0);

                                        etFc[ids] = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                                        try {
                                            if (jsonObject != null) {
                                                etFc[ids].setText(jsonObject.getString(ahai));
                                                if (etFc.length - 1 == ids) {
                                                    customersId = etFc[ids].getText().toString();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        final int finalIds = ids;
                                        etFc[ids].addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                if (etFc.length - 1 == finalIds) {
                                                    customersId = s.toString();
                                                }


                                                Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                                if (cEdit.getCount() > 0) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                                        RoomsDetail orderModel = null;
                                                        try {
                                                            orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(jsonObject, ahai, s.toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                        botListDB.updateDetailRoomWithFlagContent(orderModel);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    RoomsDetail orderModel = null;
                                                    try {
                                                        try {
                                                            orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(null, ahai, s.toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    botListDB.insertRoomsDetail(orderModel);
                                                }

                                            }
                                        });

                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(textView, params1);
                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(etFc[ids], params1);
                                        ids++;
                                    }

                                } else {

                                    Log.w("siap2", "1" + "garpu : " + kolom.size());
                                    if (kolom.size() > 1) {
                                        final int counts = linearEstimasi[Integer.valueOf(nilai.get(0).toString())].getChildCount();
                                        linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeViews(1, counts - 1);
                                        addSpinnerDinamic(finalTitleNames, finalNama.substring(0, finalNama.indexOf(".")), linearEstimasi[Integer.valueOf(nilai.get(0).toString())], finalNamaTable, finalColumnNames, 0, finalColumnNames[0] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'", idListTask, type, String.valueOf(finalI24), name);
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
                                        RoomsDetail orderModel = null;
                                        try {
                                            orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), function(null, titlesss, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                            botListDB.insertRoomsDetail(orderModel);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }


                                }
                               /* Intent newIntent = new Intent("bLFormulas");
                                getActivity().sendBroadcast(newIntent);*/
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                // your code here
                            }

                        });

                        linearEstimasi[count].addView(spinerTitle);

                    } else {

                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), DownloadSqliteDinamicActivity.class);
                        intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                        intent.putExtra("path_db", url);
                        startActivity(intent);

                    }
                    TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                    params2.setMargins(60, 10, 30, 0);
                    linearEstimasi[count].setLayoutParams(params2);
                    linearLayout.addView(linearEstimasi[count]);
                } else if (type.equalsIgnoreCase("dropdown")) {

                    TextView textView = new TextView(getActivity());
                    if (required.equalsIgnoreCase("1")) {
                        label += "<font size=\"3\" color=\"red\">*</font>";
                    }
                    textView.setText(Html.fromHtml(label.replace("+", " ")));
                    textView.setTextSize(15);
                    textView.setLayoutParams(new TableRow.LayoutParams(0));

                    TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                    String isi = jsonArrayCild.getJSONObject(i).getString("dropdown").toString();
                    JSONArray jsonArrays = new JSONArray(isi);
                    final ArrayList<String> spinnerArray = new ArrayList<String>();
                    final ArrayList<String> spinnerArrayVal = new ArrayList<String>();
                    spinnerArray.add("--Please Select--");
                    spinnerArrayVal.add("");

                    for (int ia = 0; ia < jsonArrays.length(); ia++) {
                        String l = jsonArrays.getJSONObject(ia).getString("label_option").toString().replace("+", " ");
                        String l_name = jsonArrays.getJSONObject(ia).getString("val_name").toString().replace("+", " ");

                        spinnerArray.add(l.replace("+", " "));
                        spinnerArrayVal.add(l_name.replace("+", " "));
                    }

                    final SearchableSpinner spinner = new SearchableSpinner(getActivity());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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
                    final EditText etD = (EditText) getActivity().getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                    Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(i)));
                    if (cursorCild.getCount() > 0) {
                        int spinnerPosition = spinnerArrayAdapter.getPosition(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        if (spinnerPosition < 0) {
                            etD.setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            spinnerPosition = spinnerArrayVal.indexOf("others");
                        }
                        spinner.setSelection(spinnerPosition);
                    } else {
                        if (spinner.getSelectedItem() != null) {
                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(i)), name, "child_detail");
                            botListDB.insertRoomsDetail(orderModel);
                        }
                    }
                    final int finalI24 = i;
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {
                            String status = spinnerArrayVal.get(myPosition);
                            if (status.equalsIgnoreCase("others")) {
                                etD.setVisibility(View.VISIBLE);
                                etD.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            if (String.valueOf(s).length() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                                botListDB.insertRoomsDetail(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                                botListDB.deleteDetailRoomWithFlagContent(orderModel);
                                            }
                                        }
                                    }
                                });
                            } else {
                                if (idTab.equalsIgnoreCase("2584")) {
                                    if (status.equalsIgnoreCase("insent_marg") || status.equalsIgnoreCase("plat_qr")) {
                                        Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, "{\"idt\":\"2584\",\"usr\":\"1_277091610admin\"}", "{\"iddtl\":\"1671|2427\",\"idtsk\":\"66900\"}", "child_detail", "{\"a\":\"2\",\"b\":\"text\",\"c\":\"1\"}");
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, "{\"idt\":\"2584\",\"usr\":\"1_277091610admin\"}", "{\"iddtl\":\"1671|2427\",\"idtsk\":\"66900\"}", "", "{\"a\":\"2\",\"b\":\"text\",\"c\":\"1\"}", "dett", "child_detail");
                                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idchildDetail, "{\"idt\":\"2584\",\"usr\":\"1_277091610admin\"}", "{\"iddtl\":\"1671|2427\",\"idtsk\":\"66900\"}", "", "{\"a\":\"2\",\"b\":\"text\",\"c\":\"1\"}", "dett", "child_detail");
                                            botListDB.insertRoomsDetail(orderModel);
                                        }
                                        lolosReq.add("dett");
                                        et[0].setVisibility(View.GONE);
                                        tp[0].setVisibility(View.GONE);
                                    } else {
                                        lolosReq.remove("dett");
                                        et[0].setVisibility(View.VISIBLE);
                                        tp[0].setVisibility(View.VISIBLE);
                                    }
                                }

                                etD.setVisibility(View.GONE);
                                Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                if (cEdit.getCount() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                    botListDB.updateDetailRoomWithFlagContent(orderModel);
                                } else {
                                    RoomsDetail orderModel = new RoomsDetail(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "child_detail");
                                    botListDB.insertRoomsDetail(orderModel);
                                }
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
                    linearLayout.addView(etD, params2);

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


        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labelAlert = "";
                Integer start = Integer.valueOf(idListTaskMaster);
                String titleUntuk = "";
                String decsUntuk = "";
                String priceUntuk = "";

                if (start >= 65480) {
                    List<String> errorReq = new ArrayList<String>();
                    boolean berhenti = false;


                    for (Integer key : hashMap.keySet()) {
                        List<String> value = hashMap.get(key);

                        if (lolosReq.indexOf(value.get(3)) == -1) {
                            if (value != null) {
                                if (value.get(1).toString().equalsIgnoreCase("1")) {
                                    //semua masuk sini berhenti
                                    Cursor cursorCild = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(String.valueOf(key), value.get(2).toString(), value.get(5).toString()));
                                    if (cursorCild.getCount() == 0) {
                                        labelAlert += value.get(4).toString() + "\n";
                                        berhenti = true;
                                    }
                                }
                            }
                        }


                    }

                    if (!berhenti) {
                        List<RoomsDetail> list = botListDB.getAllRoomDetailFormWithFlagContent(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail");

                        if (list.size() == 0) {
                            AlertDialog.Builder builder = DialogUtil.generateAlertDialog(getActivity(), "Input Required", labelAlert);
                            builder.show();
                        } else {
                            if (list.size() == 1) {
                                titleUntuk = list.get(0).getContent();
                            } else {
                                titleUntuk = list.get(0).getContent();
                                decsUntuk = list.get(1).getContent();
                            }


                            Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idListTaskMaster, username, idTab, "titleChild", idDetail);
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idListTaskMaster, idTab, username, jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, priceUntuk), idDetail, "", "titleChild");
                                botListDB.updateDetailRoomWithFlagContent(orderModel);
                            } else {
                                RoomsDetail orderModel = new RoomsDetail(idListTaskMaster, idTab, username, jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, priceUntuk), idDetail, "", "titleChild");
                                botListDB.insertRoomsDetail(orderModel);
                            }


                            if (getDialog() != null) {
                                getDialog().dismiss();
                            }
                            mCancel.setText("save");
                            Intent newIntent = new Intent("refreshForm");
                            newIntent.putExtra("posisi", posisi);
                            getActivity().sendBroadcast(newIntent);
                        }


                    } else {
                        AlertDialog.Builder builder = DialogUtil.generateAlertDialog(getActivity(), "Required", labelAlert);
                        builder.show();

                    }


                } else {

                    List<RoomsDetail> list = botListDB.getAllRoomDetailFormWithFlagContent(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail");

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
                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") ||
                                    jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") ||
                                    jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") ||
                                    jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") ||
                                    jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                                titleUntuk = jsonResultType(list.get(u).getContent(), "Nama");
                                if (titleUntuk.equalsIgnoreCase("")) {
                                    titleUntuk = jsonResultType(list.get(u).getContent(), "Name Detail");
                                }

                                if (titleUntuk.equalsIgnoreCase("")) {
                                    titleUntuk = jsonResultType(list.get(u).getContent(), "Nama Siswa");
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
                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown")) {
                                titleUntuk = list.get(u).getContent();
                            } else {
                                decsUntuk = list.get(u).getContent();
                                priceUntuk = "";
                            }
                        }
                    }


                    boolean next = true;
                    if (titleUntuk.equalsIgnoreCase("")) {
                        AlertDialog.Builder builder = DialogUtil.generateAlertDialog(getActivity(), "Required", labelAlert);
                        builder.show();
                        next = false;
                    }

                    if (next) {
                        Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idListTaskMaster, username, idTab, "titleChild", idDetail);
                        if (cEdit.getCount() > 0) {
                            RoomsDetail orderModel = new RoomsDetail(idListTaskMaster, idTab, username, jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, priceUntuk), idDetail, "", "titleChild");
                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                        } else {
                            RoomsDetail orderModel = new RoomsDetail(idListTaskMaster, idTab, username, jsonCreateTypeTitle(idchildDetail, titleUntuk, decsUntuk, priceUntuk), idDetail, "", "titleChild");
                            botListDB.insertRoomsDetail(orderModel);
                        }


                        if (getDialog() != null) {
                            getDialog().dismiss();
                        }
                        mCancel.setText("save");
                        Intent newIntent = new Intent("refreshForm");
                        newIntent.putExtra("posisi", posisi);
                        getActivity().sendBroadcast(newIntent);
                    }
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
                        newIntent.putExtra("posisi", posisi);
                        getActivity().sendBroadcast(newIntent);
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


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCancel.getText().toString().equalsIgnoreCase("Cancel") && mProceed.getText().toString().equalsIgnoreCase("Add")) {
            delete();
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

        Log.w("wow", hasil);

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
        posisi = getArguments().getString("posisi");


    }

    public static String getRandomString() {
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

                if (c.getCount() > 1) {
                    spinnerArray.add("--Please Select--");
                }

                if (coloum.length - 1 == asIs) {
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

                LinearLayout spinerTitle = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);


                textViewFirst.setText(Html.fromHtml(titlle));
                textViewFirst.setTextSize(15);
                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                final TextView textView = (TextView) spinerTitle.findViewById(R.id.lastSpinner);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                }


                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                final String finalTitlle = titlle;

                Cursor cEdit = botListDB.getSingleRoomDetailFormWithFlagContentChild(idchildDetail, jsonCreateIdTabNUsrName(idTab, username), jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMaster), "child_detail", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                if (cEdit.getCount() > 0) {
                    try {
// TODO: 28/10/18  siapa yang ada dropdown dinamis di from child
                        //   if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains(finalTitlle)) {//
                        Log.w("kasofoSOSO2", "alamal");
                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(finalTitlle));
                        spinner.setSelection(spinnerPosition);
                       /* } else {
                            Log.w("kasofoSOSO3", "alamal");
                            return;
                        }*/

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
                    if (spinnerArray.get(0) != null) {
                        if (spinnerArray.get(0).startsWith("http") && spinnerArray.get(0).endsWith(".jpg")) {

                            final RelativeLayout relative = (RelativeLayout) spinerTitle.findViewById(R.id.lastImage);
                            final ImageView imageView = (ImageView) spinerTitle.findViewById(R.id.value);
                            final AVLoadingIndicatorView progress = (AVLoadingIndicatorView) spinerTitle.findViewById(R.id.loader_progress);

                            Picasso.with(getContext()).load(spinnerArray.get(0)).into(imageView);

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(getContext(), ZoomImageViewActivity.class);
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
                }
                view.addView(spinerTitle);
            }
        }
    }
}
