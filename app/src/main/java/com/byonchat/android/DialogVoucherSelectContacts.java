package com.byonchat.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.ListVoucherContactAdapter;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.VouchersDB;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import static com.byonchat.android.utils.Utility.reportCatch;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogVoucherSelectContacts extends DialogFragment {

    private TextView mJudul, mDone;
    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private boolean contactIsRefereshing = false;
    protected ProgressDialog pdialog;
    private HashMap<String, Contact> contacts = new HashMap<String, Contact>();
    private MessengerDatabaseHelper messengerHelper;
    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " WHERE _id > 1 order by lower(" + Contact.NAME + ")";
    private MessengerConnectionService.MessengerConnectionBinder binder;
    private String action;
    private String type;
    RelativeLayout frame_tengah;
    FrameLayout frame2, frame1;
    Intent getintent;
    String memeFile;
    String messageText;
    Context context;
    ArrayList<IconItem> items = new ArrayList<IconItem>();
    ListVoucherContactAdapter mAdapter;
    EditText mSearchList;
    private String id, judul, serial, tglvalid, nominal, bgcolor, textcolor, background;
    VouchersDB vouchersDB;
    SQLiteDatabase mDb;

    public static DialogVoucherSelectContacts newInstance(String id, String judul, String serial, String tglvalid, String nominal, String bgcolor, String textcolor, String background) {
        DialogVoucherSelectContacts f = new DialogVoucherSelectContacts();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        args.putString("pbackground", background);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_voucher_select_contact, container, false);
        mDone = (TextView) dialog.findViewById(R.id.btn_done);
        mJudul = (TextView) dialog.findViewById(R.id.name);
        mBack = (ImageView) dialog.findViewById(R.id.btn_back);
        frame_tengah = (RelativeLayout) dialog.findViewById(R.id.frame_tengah);
        frame1 = (FrameLayout) dialog.findViewById(R.id.frame_1);
        frame2 = (FrameLayout) dialog.findViewById(R.id.frame_2);
        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.recylerView);

        String color = "";
        if(bgcolor.equalsIgnoreCase("") || bgcolor.equalsIgnoreCase("null")){
            color = "1e8cc4";
        }else{
            color = bgcolor;
        }

        GradientDrawable drawable = (GradientDrawable) frame1.getBackground();
        drawable.setColor(Color.parseColor("#"+color));
        frame2.setBackgroundColor(Color.parseColor("#"+color));

        String txtcolor = "";
        if(textcolor.equalsIgnoreCase("") || textcolor.equalsIgnoreCase("null")){
            txtcolor = "ffffff";
        }else{
            txtcolor = textcolor;
        }

        if (vouchersDB == null) {
            vouchersDB = new VouchersDB(context);
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        mJudul.setText("Select Contact");
        mJudul.setTextColor(Color.parseColor("#"+txtcolor));
        mDone.setText("Done");
        mDone.setTextColor(Color.parseColor("#"+txtcolor));
        getintent = getActivity().getIntent();
        action = getintent.getAction();
        type = getintent.getType();
        memeFile = getintent.getStringExtra("file") != null ? getintent.getStringExtra("file") : "";
        messageText = getintent.getStringExtra("messageText") != null ? getintent.getStringExtra("messageText") : "";

        if (type == null) {
            type = getintent.getStringExtra("type") != null ? getintent.getStringExtra("type") : "";
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getActivity());
        }

        Contact contact = messengerHelper.getMyContact();

        if (contact == null) {
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            startActivity(intent);
        }
        if (contacts == null) {
            contacts = new HashMap<String, Contact>();
        }

        refreshContactList();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mAdapter = new ListVoucherContactAdapter(getContext(), items);

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cid = "";
                String cname = "";

                if (mDb == null) {
                    mDb = getActivity().openOrCreateDatabase("VOUCHERS.db", Context.MODE_PRIVATE, null);
                }
                Cursor c = mDb.rawQuery("SELECT * FROM choose_contact WHERE amid=1", null);
                if (c.moveToFirst()) {
                    cid = c.getString(1);
                    cname = c.getString(2);
                }
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogVoucherSelectContact");
                    Fragment prev2 = getFragmentManager().findFragmentByTag("DialogTransferVoucher1");
                    if (prev != null || prev2 != null) {
//                        ft.remove(prev);
                        DialogFragment df = (DialogFragment) prev;
                        DialogFragment df2 = (DialogFragment) prev2;
                        df.dismiss();
                        df2.dismiss();
                    }
                    ft.addToBackStack(null);

                    DialogFragment newFragment = DialogTransferVoucherTwo.newInstance(id,judul,serial,tglvalid,nominal,cid,cname,"","",background);
                    newFragment.show(ft, "DialogTransferVoucher2");
                }
            }
        });

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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
        try {
            id = getArguments().getString("pid");
            judul = getArguments().getString("pjudul");
            serial = getArguments().getString("pserial");
            tglvalid = getArguments().getString("ptglvalid");
            nominal = getArguments().getString("pnominal");
            bgcolor = getArguments().getString("pbgcolor");
            textcolor = getArguments().getString("ptextcolor");
            background = getArguments().getString("pbackground");
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void handleSendImage(Intent intent, String type, String jabber) {
        try {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                String selectedImagePath = ImageFilePath.getPath(getContext().getApplicationContext(), imageUri);
                Intent intent2 = new Intent(getContext().getApplicationContext(), ConfirmationSendFile.class);
                String jabberId = jabber;
                intent2.putExtra("file", selectedImagePath);
                intent2.putExtra("name", jabberId);
                intent2.putExtra("type", type);
                startActivity(intent2);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void refreshContactList() {
        try {
            AsyncTask<Void, Contact, Void> contactLoader = new ContactLoader();
            contactLoader.execute();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    class ContactLoader extends AsyncTask<Void, Contact, Void> {
        ArrayList<IconItem> arrayListContact = new ArrayList<IconItem>();

        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (contactIsRefereshing)
                    return null;
                contactIsRefereshing = true;
//            if(contactsFragment != null){
//                contactsFragment.clearItems();
//            }
                if (items != null) {
                    items.clear();
                }

                contacts.clear();
                HashMap<Long, Contact> dbMap = loadContactFromDb();
                if (dbMap.size() > 0) {
                    for (Iterator<Long> iterator = dbMap.keySet().iterator(); iterator
                            .hasNext(); ) {
                        Long l = iterator.next();
                        Contact c = dbMap.get(l);
                        publishProgress(c);
                    }
                } else {
                    publishProgress(new Contact[]{null});
                }
            }catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Contact... values) {
            try {
                Contact data = values[0];
                if (data != null) {
                    IconItem item = new IconItem(data.getJabberId(),
                            data.getName(), data.getStatus() != null ? data.getStatus() : "", null, data);
                    setProfilePicture(item, data);
                    arrayListContact.add(item);
                    contacts.put(data.getJabberId(), data);

                } else {
                    mAdapter = new ListVoucherContactAdapter(getContext(), arrayListContact);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mAdapter);
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            Collections.sort(arrayListContact, IconItem.nameSortComparator);
            for (IconItem itemss : arrayListContact) {
            }

            mAdapter = new ListVoucherContactAdapter(getContext(), arrayListContact);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    private HashMap<Long, Contact> loadContactFromDb() {
        Contact contact = new Contact();
        Cursor cursor = messengerHelper.query(SQL_SELECT_CONTACTS, null);

        HashMap<Long, Contact> dbMap = new HashMap<Long, Contact>();
        while (cursor.moveToNext()) {

            contact = new Contact(cursor);
            dbMap.put(Long.valueOf(contact.getJabberId()), contact);
        }
        cursor.close();
        return dbMap;
    }

    private void setProfilePicture(IconItem item, Contact contact) {
        try {
            File f = getActivity().getFileStreamPath(MediaProcessingUtil
                    .getProfilePicName(contact));
            if (f.exists()) {
                item.setImageUri(Uri.fromFile(f));
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }
}
