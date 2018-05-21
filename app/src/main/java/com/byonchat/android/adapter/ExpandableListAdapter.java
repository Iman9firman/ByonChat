package com.byonchat.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by imanfirmansyah on 21/03/18.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> ParentItem;
    private HashMap<String, List<String>> ChildItem;

    String idDetail, username, idTab, types, JsonType, name;
    //idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI21))
    BotListDB db;

    public ExpandableListAdapter(Context context, List<String> ParentItem,
                                 HashMap<String, List<String>> ChildItem, String _idDetail, String _username, String _idTab, String _types, String _name) {
        this.context = context;
        this.ParentItem = ParentItem;
        this.ChildItem = ChildItem;

        this.idDetail = _idDetail;
        this.username = _username;
        this.idTab = _idTab;
        this.types = _types;
        this.name = _name;

        if (db == null) {
            db = BotListDB.getInstance(context);
        }

    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return ChildItem.get(ParentItem.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_cild, null);
        }

        final CheckBox text1 = (CheckBox) convertView.findViewById(R.id.checkbox);
        Button button = (Button) convertView.findViewById(R.id.button4);
        final EditText editText = (EditText) convertView.findViewById(R.id.editText);

        //text1.setChecked(false);

        try {

            JSONObject jsonObject = new JSONObject(expandedListText);

            text1.setText((expandedListPosition + 1) + ". " + jsonObject.getString("t"));

            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
            if (cEdit.getCount() > 0) {
                String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                JSONObject lala = new JSONObject(text);
                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));
                JSONObject oContent = jj.getJSONObject(expandedListPosition);

                if (oContent.getString("v").equalsIgnoreCase("1")) {
                    text1.setChecked(true);
                } else {
                    text1.setChecked(false);
                }

                editText.setText(oContent.getString("n"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getVisibility() == View.VISIBLE) {
                    editText.setVisibility(View.GONE);
                } else {
                    editText.setVisibility(View.VISIBLE);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                            if (cEdit.getCount() > 0) {
                                String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                                JSONObject lala = null;
                                try {
                                    lala = new JSONObject(text);
                                    JSONObject jsonObject = new JSONObject(expandedListText);
                                    JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                    JSONObject oContent = jj.getJSONObject(expandedListPosition);
                                    oContent.put("n", s.toString());

                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                                    db.updateDetailRoomWithFlagContent(orderModel);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
                }
            }
        });


        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (text1.isChecked()) {

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                    if (cEdit.getCount() > 0) {
                        String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                        JSONObject lala = null;
                        try {
                            lala = new JSONObject(text);
                            JSONObject jsonObject = new JSONObject(expandedListText);
                            JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                            JSONObject oContent = jj.getJSONObject(expandedListPosition);
                            oContent.put("v", "1");

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                } else {
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                    if (cEdit.getCount() > 0) {
                        String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                        JSONObject lala = null;
                        try {
                            lala = new JSONObject(text);
                            JSONObject jsonObject = new JSONObject(expandedListText);
                            JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                            JSONObject oContent = jj.getJSONObject(expandedListPosition);
                            oContent.put("v", "0");

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });




        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.ChildItem.get(this.ParentItem.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.ParentItem.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.ParentItem.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_parent, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        //listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText((listPosition + 1) + ". " + listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    private String jsonCheckBox(String idT, String idS, String val, String note) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", idT + "|" + idS);
            obj.put("val", val);
            obj.put("not", note);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public static String removeJson(final String object, final JSONArray from) {
        String hasil = "";
        final List<JSONObject> objs = asList(from);
        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            if (!obj.toString().equals(object)) {
                ja.put(obj);
            }
        }

        hasil = ja.toString().substring(1, ja.toString().length() - 1);

        return hasil;
    }

    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }


    // {"idT":"0","idS":"1","val":"1","not":"srls"},{"idT":"0","idS":"0","val":"1","not":"srls"}


}