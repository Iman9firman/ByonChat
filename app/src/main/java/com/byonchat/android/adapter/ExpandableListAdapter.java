package com.byonchat.android.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.model.AddChildFotoExModel;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by imanfirmansyah on 21/03/18.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> ParentItem;
    private HashMap<String, List<String>> ChildItem;
    private Activity activity;

    String idDetail, username, idTab, types, JsonType, name;
    //idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI21))
    BotListDB db;

    public ExpandableListAdapter(Activity activity, Context context, List<String> ParentItem,
                                 HashMap<String, List<String>> ChildItem, String _idDetail, String _username, String _idTab, String _types, String _name) {
        this.activity = activity;
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

        final TextView txtCheckBox = (TextView) convertView.findViewById(R.id.txt_check_box);
        final CheckBox text1 = (CheckBox) convertView.findViewById(R.id.checkbox);
        final TextView editText = (TextView) convertView.findViewById(R.id.editText);
        final TextView txtNumb = (TextView) convertView.findViewById(R.id.txt_numb);
        final ImageView imageA = (ImageView) convertView.findViewById(R.id.imageA);
        final ImageView imageB = (ImageView) convertView.findViewById(R.id.imageB);
        final ImageView imageC = (ImageView) convertView.findViewById(R.id.imageC);
        final ImageView imageD = (ImageView) convertView.findViewById(R.id.imageD);


        /*LinearLayoutManager horizonal = new LinearLayoutManager(convertView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView RvUserList = (RecyclerView) convertView.findViewById(R.id.RvUserList);

        List<AddChildFotoExModel> added_userList = new ArrayList<AddChildFotoExModel>();*/

      /*  if (!as) {
            editText.getText().clear();
        }*/


        try {
            JSONObject jsonObject = new JSONObject(expandedListText);

            txtCheckBox.setText(jsonObject.getString("t"));
            txtNumb.setText((expandedListPosition + 1) + ".");

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

                if (oContent.has("f")) {
                    final JSONArray jsonArray = oContent.getJSONArray("f");
                    if (jsonArray.length() == 0) {
                        imageA.setVisibility(View.VISIBLE);
                        imageB.setVisibility(View.GONE);
                        imageC.setVisibility(View.GONE);
                        imageD.setVisibility(View.GONE);
                        imageA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (activity instanceof DinamicRoomTaskActivity) {
                                    AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                                    ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                                }

                            }
                        });

                    } else if (jsonArray.length() == 1) {
                        imageA.setVisibility(View.VISIBLE);
                        imageB.setVisibility(View.VISIBLE);
                        imageC.setVisibility(View.GONE);
                        imageD.setVisibility(View.GONE);

                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r")).into(imageA);

                        imageA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });

                        imageA.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Delete")
                                        .setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    @TargetApi(11)
                                                    public void onClick(
                                                            DialogInterface dialog, int id) {
                                                        AddChildFotoExModel valueIdValue = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", valueIdValue.getTypes());
                                                        if (cEdit.getCount() > 0) {
                                                            String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                                                            JSONObject lala = null;
                                                            try {
                                                                lala = new JSONObject(text);
                                                                JSONObject jsonObject = new JSONObject(valueIdValue.getExpandedListText());
                                                                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                                                JSONObject oContent = jj.getJSONObject(valueIdValue.getExpandedListPosition());

                                                                if (oContent.has("f")) {
                                                                    oContent.put("f", "e");
                                                                } else {
                                                                    oContent.put("f", "q");
                                                                }
                                                                Log.w("has", lala.toString());
//                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
//                                                                db.updateDetailRoomWithFlagContent(orderModel);

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        dialog.cancel();
                                                    }
                                                })
                                        .show();
                                return false;
                            }
                        });
                        imageB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                                ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                            }
                        });

                    } else if (jsonArray.length() == 2) {
                        imageA.setVisibility(View.VISIBLE);
                        imageB.setVisibility(View.VISIBLE);
                        imageC.setVisibility(View.VISIBLE);
                        imageD.setVisibility(View.GONE);
                        imageC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                                ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                            }
                        });

                        imageA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });

                        imageB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(1).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });

                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r")).into(imageA);
                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(1).getString("r")).into(imageB);
                    } else if (jsonArray.length() == 3) {
                        imageA.setVisibility(View.VISIBLE);
                        imageB.setVisibility(View.VISIBLE);
                        imageC.setVisibility(View.VISIBLE);
                        imageD.setVisibility(View.VISIBLE);
                        imageD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                                ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                            }
                        });

                        imageA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });

                        imageB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(1).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });


                        imageC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(2).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });


                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r")).into(imageA);
                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(1).getString("r")).into(imageB);
                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(2).getString("r")).into(imageC);
                    } else if (jsonArray.length() == 4) {
                        imageA.setVisibility(View.VISIBLE);
                        imageB.setVisibility(View.VISIBLE);
                        imageC.setVisibility(View.VISIBLE);
                        imageD.setVisibility(View.VISIBLE);

                        imageA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });

                        imageB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(1).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });


                        imageC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(2).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });
                        imageD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(3).getString("r"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(intent);

                            }
                        });

                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(0).getString("r")).into(imageA);
                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(1).getString("r")).into(imageB);
                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(2).getString("r")).into(imageC);
                        Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + jsonArray.getJSONObject(3).getString("r")).into(imageD);
                    }
                } else {
                    imageA.setVisibility(View.VISIBLE);
                    imageA.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (activity instanceof DinamicRoomTaskActivity) {
                                AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                                ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                            }

                        }
                    });
                }

            } else {
                imageA.setVisibility(View.VISIBLE);
                imageA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activity instanceof DinamicRoomTaskActivity) {
                            AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                            ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                        }

                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (imageA.getVisibility() != View.VISIBLE) {
            imageA.setVisibility(View.VISIBLE);
            imageA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof DinamicRoomTaskActivity) {
                        AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, 0, "add", name);
                        ((DinamicRoomTaskActivity) activity).yourActivityMethod(aaa);
                    }
                }
            });
        }
        /*if (added_userList.size() < 4) {
            AddChildFotoExModel aaa = new AddChildFotoExModel(idDetail, username, idTab, "cild", types, expandedListPosition, expandedListText, added_userList.size(), "add");
            added_userList.add(aaa);
        }*/

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View formsView = inflater.inflate(R.layout.dialog_edit_text, null, false);
                final EditText edit = (EditText) formsView.findViewById(R.id.edit);

                edit.setText(editText.getText().toString());
                new AlertDialog.Builder(activity)
                        .setView(formsView)
                        .setTitle("Note")
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    @TargetApi(11)
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        editText.setText(edit.getText());
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                                        if (cEdit.getCount() > 0) {
                                            String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                            JSONObject lala = null;
                                            try {
                                                lala = new JSONObject(text);
                                                JSONObject jsonObject = new JSONObject(expandedListText);
                                                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                                JSONObject oContent = jj.getJSONObject(expandedListPosition);
                                                oContent.put("n", edit.getText());

                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        dialog.cancel();
                                    }
                                })
                        /*.setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    @TargetApi(11)
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        editText.setText("");

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                                        if (cEdit.getCount() > 0) {
                                            String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                                            JSONObject lala = null;
                                            try {
                                                lala = new JSONObject(text);
                                                JSONObject jsonObject = new JSONObject(expandedListText);
                                                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                                JSONObject oContent = jj.getJSONObject(expandedListPosition);
                                                oContent.put("n", "");

                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        dialog.cancel();
                                    }
                                })
                        */.show();
            }
        });

        /*editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.w("masudk", "sism");

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        editText.setText("");

                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                        if (cEdit.getCount() > 0) {
                            String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                            JSONObject lala = null;
                            try {
                                lala = new JSONObject(text);
                                JSONObject jsonObject = new JSONObject(expandedListText);
                                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                JSONObject oContent = jj.getJSONObject(expandedListPosition);
                                oContent.put("n", "");

                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        return true;
                    } else {

                        return true;
                    }

                }
                return false;
            }
        });*/


       /* AddFotoChildAdapter addedUsersAdapter = new AddFotoChildAdapter(activity, convertView.getContext(), added_userList);
        addedUsersAdapter.notifyDataSetChanged();
        RvUserList.setLayoutManager(horizonal);
        RvUserList.setAdapter(addedUsersAdapter);*/

        /*editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

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

                        Log.w("kasi", lala.toString());
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                        db.updateDetailRoomWithFlagContent(orderModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

              *//*  Log.w("sebelum", idDetail + "::" + username + "::" + idTab + "::" + "cild" + "::" + types + "::" + s.toString());

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.w("sesudah", idDetail + "::" + username + "::" + idTab + "::" + "cild" + "::" + types + "::" + s.toString());

                    }
                }, 5);*//*
            }
        });*/

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
                            //oContent.put("f", "IMG_2018_07_16_16_05_47.jpg");

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

        if (!isExpanded) {
            listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_arrow_down, 0);
        } else {
            listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_arrow_up, 0);
        }

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
}