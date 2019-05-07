package com.byonchat.android.adapter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.model.AddChildFotoExModel;
import com.byonchat.android.model.SLAISSItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RadioButtonCheckDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.widget.RadioButtonDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.guna.ocrlibrary.OCRCapture;
import com.multilevelview.MultiLevelAdapter;
import com.multilevelview.MultiLevelRecyclerView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_COMMENT;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_IMG;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_OK;
import static com.byonchat.android.provider.RadioButtonCheckDB.TABLE_NAME;

public class SLAISSAdapter extends MultiLevelAdapter {
    private static final int REQ_CAMERA = 1201;
    private Activity mActivity;
    private ISSHolder mHolder;
    private List<SLAISSItem> mListItems = new ArrayList<>();
    private SLAISSItem mItem;
    private MultiLevelRecyclerView mMultiLevelRecyclerView;
    private RadioButtonCheckDB database;
    String idDetailForm;

    public SLAISSAdapter(Activity activity, String idDetailForm, List<SLAISSItem> listItems, MultiLevelRecyclerView multiLevelRecyclerView) {
        super(listItems);
        this.mActivity = activity;
        this.mListItems = listItems;
        this.idDetailForm = idDetailForm;
        this.mMultiLevelRecyclerView = multiLevelRecyclerView;
        database = new RadioButtonCheckDB(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ISSHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.iss_item_layout, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mHolder = (ISSHolder) holder;
        mItem = mListItems.get(position);

        switch (getItemViewType(position)) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                mHolder.textTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                break;
        }


        Log.w("domoka", position + "::" + String.valueOf(mItem.getId_content()));

        mHolder.textTitle.setText(mItem.getTitle());
        mHolder.textId.setText(String.valueOf(mItem.getId_content()));
        Picasso.with(mActivity).load(R.drawable.ic_att_gallery).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(mHolder.pictPicker);

        if (mItem.hasChildren() && mItem.getChildren().size() > 0) {
            mHolder.itemOnCheckLayout.setVisibility(View.GONE);
            mHolder.buttonPicker.setVisibility(View.GONE);
            setExpandButton(mHolder.imgArrow, mItem.isExpanded());
            mHolder.imgArrow.setVisibility(View.VISIBLE);
        } else {
            mHolder.imgArrow.setVisibility(View.GONE);
            if (checkDB(mHolder.textId.getText().toString())) {
                if (getOkFromDB(mHolder.textId.getText().toString()) == 1) {
                    mHolder.buttonPicker.setBackground(mActivity.getDrawable(R.drawable.check_ok));
                } else {
                    mHolder.buttonPicker.setBackground(mActivity.getDrawable(R.drawable.check_no));
                }

                if (getImgeB(mHolder.textId.getText().toString()) != null) {
                    if (mHolder.textId.getText().toString().split("\\|").length > 1) {
                        Picasso.with(mActivity).load("file:////" + getImgeB(mHolder.textId.getText().toString())).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(mHolder.pictPicker);
                    } else {
                        Picasso.with(mActivity).load("file:////" + getImgeB(mHolder.textId.getText().toString())).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(mHolder.pictPicker);
                    }

                }
                mHolder.textComment.setText(getComment(mHolder.textId.getText().toString()));

            } else {
                mHolder.buttonPicker.setBackground(mActivity.getDrawable(R.drawable.pemcil));
            }
        }

        float density = mActivity.getResources().getDisplayMetrics().density;
        ((ViewGroup.MarginLayoutParams) mHolder.itemView.getLayoutParams()).leftMargin = (int) ((getItemViewType(position) * 20) * density + 0.5f);
    }

    private class ISSHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout itemOnCheckLayout;
        private TextView textTitle;
        private TextView textComment;
        private TextView textId;
        private ImageButton pictPicker;
        private ImageButton buttonPicker;
        private ImageView imgArrow;

        ISSHolder(final View v) {
            super(v);
            itemOnCheckLayout = v.findViewById(R.id.layout_oncheck_item);
            textTitle = v.findViewById(R.id.title_item);
            textId = v.findViewById(R.id.id_item);
            textComment = v.findViewById(R.id.comment_item);
            buttonPicker = v.findViewById(R.id.picker_item);
            pictPicker = v.findViewById(R.id.pict_item);
            imgArrow = v.findViewById(R.id.arrow_item);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMultiLevelRecyclerView.toggleItemsGroup(getAdapterPosition());
                    imgArrow.animate().rotation(mListItems.get(getAdapterPosition()).isExpanded() ? -180 : 0).start();
                    mMultiLevelRecyclerView.scrollToPosition(mListItems.size() - 1);
                }
            });

            buttonPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButtonDialog radioButtonDialog;
                    if (checkDB(textId.getText().toString())) {
                        radioButtonDialog = new RadioButtonDialog(mActivity, getOkFromDB(textId.getText().toString()), new RadioButtonDialog.MyRadioDialogListener() {
                            @Override
                            public void userSubmit(int value) {
                                updateDB(textId.getText().toString(), value, null, null);
                                notifyDataSetChanged();
                            }
                        });
                        radioButtonDialog.show();
                    } else {
                        radioButtonDialog = new RadioButtonDialog(mActivity, -1, new RadioButtonDialog.MyRadioDialogListener() {
                            @Override
                            public void userSubmit(int value) {
                                insertDB(textId.getText().toString(), value, null, null);
                                notifyDataSetChanged();
                            }
                        });
                        radioButtonDialog.show();
                    }
                }
            });

            pictPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkDB(textId.getText().toString())) {
                        if (getImgeB(textId.getText().toString()) != null) {
                            Intent intent = new Intent(mActivity, ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////" + getImgeB(textId.getText().toString()));
                            mActivity.startActivity(intent);

                        } else {
                            AddChildFotoExModel aaa = new AddChildFotoExModel(textId.getText().toString(), "", "", "cild", "update", getAdapterPosition(), String.valueOf(getOkFromDB(textId.getText().toString())), 0, "add", "");
                            ((DinamicSLATaskActivity) mActivity).yourActivityMethod(aaa);
                        }


                    } else {
                        AddChildFotoExModel aaa = new AddChildFotoExModel(textId.getText().toString(), "", "", "cild", "insert", getAdapterPosition(), "", 0, "add", "");
                        ((DinamicSLATaskActivity) mActivity).yourActivityMethod(aaa);


                    }


                }
            });

            pictPicker.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AddChildFotoExModel aaa = new AddChildFotoExModel(textId.getText().toString(), "", "", "cild", "update", getAdapterPosition(), String.valueOf(getOkFromDB(textId.getText().toString())), 0, "add", "");
                    ((DinamicSLATaskActivity) mActivity).yourActivityMethod(aaa);
                    return false;
                }
            });

            textComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View formsView = inflater.inflate(R.layout.dialog_edit_text, null, false);
                    final EditText edit = (EditText) formsView.findViewById(R.id.edit);

                    edit.setText(textComment.getText().toString());

                    new AlertDialog.Builder(mActivity)
                            .setView(formsView)
                            .setTitle("Note")
                            .setPositiveButton("Save",
                                    new DialogInterface.OnClickListener() {
                                        @TargetApi(11)
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            textComment.setText(edit.getText());

                                            if (checkDB(textId.getText().toString())) {
                                                updateDB(textId.getText().toString(), 0, null, edit.getText().toString());
                                            } else {
                                                insertDB(textId.getText().toString(), 0, null, edit.getText().toString());
                                            }


                                            dialog.cancel();
                                        }
                                    })
                            .show();
                }
            });


        }
    }

    public void insertDB(String id, int value, String image, String comment) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_DETAIL, id);
        values.put(COLUMN_OK, value);
        if (image != null) {
            values.put(COLUMN_IMG, image);
        }
        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateDB(String id, int value, String image, String comment) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (image != null) {
            values.put(COLUMN_IMG, image);
        }

        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }

        if (image == null && comment == null) {
            values.put(COLUMN_OK, value);
        }

        db.update(TABLE_NAME, values, COLUMN_ID_DETAIL + " = ?",
                new String[]{String.valueOf(id)});
    }

    private boolean checkDB(String id) {
        boolean isExist = false;

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    private int getOkFromDB(String id) {
        int ok = 0;
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_OK},
                COLUMN_ID_DETAIL + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getInt(cursor.getColumnIndex(COLUMN_OK));
        }
        cursor.close();

        return ok;
    }

    private String getImgeB(String id) {
        String ok = "";
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID_DETAIL, COLUMN_IMG},
                COLUMN_ID_DETAIL + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getString(cursor.getColumnIndex(COLUMN_IMG));
        }
        cursor.close();

        if (ok != null) {
            if (!ok.equalsIgnoreCase("")) {
                if (ok.split("\\|").length > 1) {
                    ok = ok.split("\\|")[0];
                }
            }

        }


        return ok;
    }

    private String getComment(String id) {
        String ok = "";
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID_DETAIL, COLUMN_COMMENT},
                COLUMN_ID_DETAIL + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
        }
        cursor.close();

        return ok;
    }


    public void removeDB() {
        database.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    private void setExpandButton(ImageView expandButton, boolean isExpanded) {
        // set the icon based on the current state
        expandButton.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
    }

}


