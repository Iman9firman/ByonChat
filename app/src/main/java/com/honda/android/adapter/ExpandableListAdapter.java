package com.honda.android.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.honda.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.honda.android.R;
import com.honda.android.ZoomImageViewActivity;
import com.honda.android.model.AddChildFotoExModel;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.RoomsDetail;
import com.honda.android.utils.DialogUtil;
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
    String groupActive = "";
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
    public View getChildView(int listPos, int expandedListPos,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        final String expandedListText = (String) getChild(listPos, expandedListPos);
        final int listPosition = listPos;
        final int expandedListPosition = expandedListPos;

        LayoutInflater layoutInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.expandable_cild, null);

        final TextView txtCheckBox = (TextView) convertView.findViewById(R.id.txt_check_box);

        final RadioGroup pilihan = (RadioGroup) convertView.findViewById(R.id.pilihan);
        final RadioButton okeP = (RadioButton) convertView.findViewById(R.id.radiOK);
        final RadioButton notP = (RadioButton) convertView.findViewById(R.id.radiNOK);

        final TextView editText = (TextView) convertView.findViewById(R.id.editText);
        final TextView txtNumb = (TextView) convertView.findViewById(R.id.txt_numb);
        final ImageView imageA = (ImageView) convertView.findViewById(R.id.imageA);
        final ImageView imageB = (ImageView) convertView.findViewById(R.id.imageB);
        final ImageView imageC = (ImageView) convertView.findViewById(R.id.imageC);
        final ImageView imageD = (ImageView) convertView.findViewById(R.id.imageD);

        try {
            JSONObject jsonObject = new JSONObject(expandedListText);
            txtNumb.setText((expandedListPosition + 1) + ".");
            txtCheckBox.setText(jsonObject.getString("t"));


            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
            if (cEdit.getCount() > 0) {

                String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                JSONObject lala = new JSONObject(text);
                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));
                JSONObject oContent = jj.getJSONObject(expandedListPosition);

                if (oContent.getString("v").equalsIgnoreCase("1")) {
                    okeP.setChecked(true);
                } else if (oContent.getString("v").equalsIgnoreCase("0")) {
                    notP.setChecked(true);
                } else {
                    okeP.setChecked(false);
                    notP.setChecked(false);
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

                        imageA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                try {
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(0).getString("r"));
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(0);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageA.setImageResource(R.drawable.ic_att_photo);

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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(0).getString("r"));
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(1).getString("r"));
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(0);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageB.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(1);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageA.setImageResource(R.drawable.ic_att_photo);
                        imageB.setImageResource(R.drawable.ic_att_photo);
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(0).getString("r"));
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(1).getString("r"));
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(2).getString("r"));
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(0);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageB.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(1);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageC.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(2);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();
                                                                // Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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


                        imageA.setImageResource(R.drawable.ic_att_photo);
                        imageB.setImageResource(R.drawable.ic_att_photo);
                        imageC.setImageResource(R.drawable.ic_att_photo);

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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(0).getString("r"));
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(1).getString("r"));
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(2).getString("r"));
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
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////storage/emulated/0/Pictures/com.honda.android" + jsonArray.getJSONObject(3).getString("r"));
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(0);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageB.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(1);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageC.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(2);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();
                                                                // Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);

                                                                if (jsonArray.length() == 4) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
                                                                } else if (jsonArray.length() == 3) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageC);
                                                                } else if (jsonArray.length() == 2) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageB);
                                                                } else if (jsonArray.length() == 1) {
                                                                    Picasso.with(context).load(R.drawable.ic_att_photo).into(imageA);
                                                                }

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

                        imageD.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                                    JSONArray jsonArray1 = oContent.getJSONArray("f");
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                                        jsonArray1.remove(3);
                                                                    }
                                                                }

                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                                                db.updateDetailRoomWithFlagContent(orderModel);
                                                                notifyDataSetChanged();
                                                                // Picasso.with(context).load(R.drawable.ic_att_photo).into(imageD);
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
                        imageA.setImageResource(R.drawable.ic_att_photo);
                        imageB.setImageResource(R.drawable.ic_att_photo);
                        imageC.setImageResource(R.drawable.ic_att_photo);
                        imageD.setImageResource(R.drawable.ic_att_photo);


                    }
                } else {
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
                }

            } else {
                Log.w("siap", "nomerA");
                okeP.setChecked(false);
                notP.setChecked(false);
                editText.setText("");
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
            }


        } catch (JSONException e) {
            e.printStackTrace();
            okeP.setChecked(false);
            notP.setChecked(false);
            editText.setText("");
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
        }

        if (imageA.getVisibility() != View.VISIBLE) {
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
        }

        txtCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = DialogUtil.generateAlertDialog(activity,
                        "", txtNumb.getText().toString() + " " + txtCheckBox.getText().toString());
                builder.setPositiveButton("Close", null);
                builder.show();
            }
        });

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
                        .show();
            }
        });

        pilihan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    Log.w("har1", checkedRadioButton.getText().toString());
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", types);
                    if (cEdit.getCount() > 0) {
                        Log.w("har2", checkedRadioButton.getText().toString());
                        String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        JSONObject lala = null;
                        try {
                            lala = new JSONObject(text);

                            JSONObject jsonObject = new JSONObject(expandedListText);

                            JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                            JSONObject oContent = jj.getJSONObject(expandedListPosition);

                            if (checkedRadioButton.getText().toString().equalsIgnoreCase("OK")) {
                                oContent.put("v", "1");
                            } else {
                                oContent.put("v", "0");
                            }

                            Log.w("duus", lala.toString());
                            // {"49":[{"iD":"163749","v":"0","n":""}],"customersId":"BNDSH"}

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), types, name, "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
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