package com.byonchat.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.PersonalRoomActivity;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.widget.BadgeView;

import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lb.library.SearchablePinnedHeaderListViewAdapter;
import lb.library.StringArrayAlphabetIndexer;

/**
 * Created by Iman Firmansyah on 11/27/2015.
 */
public class NewContactAdapter extends SearchablePinnedHeaderListViewAdapter<Contact> implements Filterable {
    private LayoutInflater mInflater;
    ArrayList<Contact> iconlist;
    ArrayList<Contact> mStringFilterList;
    private Context activity;
    private MessengerDatabaseHelper messengerHelper;
    private Boolean hideRoom = false;
    private String color;
    String filter = "";

    @Override
    public CharSequence getSectionTitle(int sectionIndex) {
        return ((StringArrayAlphabetIndexer.AlphaBetSection) getSections()[sectionIndex]).getName();
    }

    public NewContactAdapter(Context activity, ArrayList<Contact> contacts, Boolean dR, String cr) {
        this.activity = activity;
        mStringFilterList = contacts;
        mInflater = LayoutInflater.from(activity);
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(activity.getApplicationContext());
        }

        this.hideRoom = dR;
        this.color = cr;
        setData(contacts);
    }

    public void setData(final ArrayList<Contact> contacts) {
        this.iconlist = contacts;
        final String[] generatedContactNames = generateContactNames(contacts);
        setSectionIndexer(new StringArrayAlphabetIndexer(generatedContactNames, true));
    }

    private String[] generateContactNames(final List<Contact> contacts) {
        final ArrayList<String> contactNames = new ArrayList<String>();
        if (contacts != null)
            for (final Contact contactEntity : contacts) {
                if (contactEntity.getId() == 1) {
                    contactNames.add(" " + contactEntity.getJabberId());
                } else {
                    contactNames.add(contactEntity.getName());
                }

            }
        return contactNames.toArray(new String[contactNames.size()]);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        View rootView;
        if (convertView == null) {
            holder = new ViewHolder();
            rootView = mInflater.inflate(R.layout.listview_item, parent, false);
            holder.iconView = (ImageView) rootView.findViewById(R.id.imagePhoto);
            holder.textTitle = (TextView) rootView.findViewById(R.id.textTitle);
            holder.textInfo = (TextView) rootView.findViewById(R.id.textInfo);
            holder.roomsOpen = (ImageButton) rootView.findViewById(R.id.roomsOpen);
            holder.headerView = (TextView) rootView.findViewById(R.id.header_text);
            holder.relativeLayout2 = (View) rootView.findViewById(R.id.relativeLayout2);
            holder.dividerContact = (View) rootView.findViewById(R.id.dividerContact);
            holder.dividerContactBlank = (View) rootView.findViewById(R.id.dividerContactBlank);
            rootView.setTag(holder);
        } else {
            rootView = convertView;
            holder = (ViewHolder) rootView.getTag();
        }


        final Contact contact = getItem(position);
        String displayName = contact.getName();
        String itemValue = displayName;

        int startPos = itemValue.toLowerCase(Locale.US).indexOf(filter.toLowerCase(Locale.US));
        int endPos = startPos + filter.length();

        if (startPos != -1) // This should always be true, just a sanity check
        {
            Spannable spannable = new SpannableString(itemValue);
            ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor(color)});
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);

            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.textTitle.setText(spannable);
        } else {
            holder.textTitle.setText(itemValue);
        }


        if (contact.getJabberId().equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
            holder.textTitle.setText(Html.fromHtml(contact.getRealname() != null ? contact.getRealname() : "Name not set"));
            holder.roomsOpen.setVisibility(View.GONE);
            holder.iconView.setVisibility(View.VISIBLE);
            File photoFile = activity.getApplicationContext().getFileStreamPath(MediaProcessingUtil
                    .getProfilePicName(contact));
            Drawable d = activity.getResources().getDrawable(R.drawable.ic_no_photo);
            Glide.with(activity).load(photoFile)
                    .asBitmap()
                    .placeholder(d)
                    .centerCrop()
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis()))).into(new BitmapImageViewTarget(holder.iconView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                    circularBitmapDrawable.setCornerRadius(22);
                    holder.iconView.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            holder.roomsOpen.setVisibility(View.VISIBLE);
            if (hideRoom) {
                holder.roomsOpen.setVisibility(View.GONE);
            }

            holder.iconView.setVisibility(View.VISIBLE);
            final File photoFile = activity.getFileStreamPath(MediaProcessingUtil
                    .getProfilePicName(contact.getJabberId()));

            Drawable d = activity.getResources().getDrawable(R.drawable.ic_no_photo);
            if (photoFile.exists()) {
                d = Drawable.createFromPath(photoFile.getAbsolutePath());
            }

            String signature = new Validations().getInstance(activity).getSignatureProfilePicture(contact.getJabberId(), messengerHelper);
            Glide.with(activity).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + contact.getJabberId() + ".jpg").asBitmap()
                    .centerCrop()
                    .signature(new StringSignature(signature))
                    .placeholder(d)
                    .animate(R.anim.fade_in_sort)
                    .into(new BitmapImageViewTarget(holder.iconView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            holder.iconView.setImageBitmap(resource);
                            SaveMedia task = new SaveMedia();
                            task.execute(new MyTaskParams[] {new MyTaskParams(resource,contact.getJabberId())});
                        }
                    });
        }

        try {
            JSONObject json = new JSONObject(contact.getStatus());
            String aa = json.getString("status") != null ? json.getString("status") : "I love byonchat";
            String text = Html.fromHtml(URLDecoder.decode(aa)).toString();
            if (text.contains("<")) {
                text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
            }
            holder.textInfo.setText(Message.parsedMessageText(text));
        } catch (Exception e) {
            String aa = contact.getStatus() != null ? contact.getStatus() : "I love byonchat";
            String text = Html.fromHtml(URLDecoder.decode(aa)).toString();
            if (text.contains("<")) {
                text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
            }
            holder.textInfo.setText(Message.parsedMessageText(text));
        }
        holder.roomsOpen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PersonalRoomActivity.class);
                intent.putExtra(PersonalRoomActivity.EXTRA_ID, contact.getJabberId());
                intent.putExtra(PersonalRoomActivity.EXTRA_NAME, contact.getRealname());
                context.startActivity(intent);
            }
        });

        //header tulisan
        bindSectionHeader(holder.dividerContactBlank, holder.headerView, holder.relativeLayout2, holder.dividerContact, position);

        return rootView;
    }

    @Override
    public boolean doFilter(final Contact item, final CharSequence constraint) {
        if (TextUtils.isEmpty(constraint)) return true;
        final String displayName = item.getName();
        try {
            filter = constraint.toString();
        } catch (Exception e) {
        }

        return !TextUtils.isEmpty(displayName) && displayName.toLowerCase(Locale.getDefault())
                .contains(constraint.toString().toLowerCase(Locale.getDefault()));
    }

    @Override
    public ArrayList<Contact> getOriginalList() {
        return iconlist;
    }

    private static class ViewHolder {
        TextView headerView;
        View relativeLayout2, dividerContact, dividerContactBlank;
        ImageView iconView;
        TextView textInfo;
        TextView textTitle;
        BadgeView badge;
        ImageButton roomsOpen;
    }

    private class SaveMedia extends AsyncTask<MyTaskParams, Void, MyTaskParams> {
        @Override
        protected MyTaskParams doInBackground(MyTaskParams... resource) {
            MediaProcessingUtil.saveProfilePic(activity, resource[0].getContact(), resource[0].getBar());
            return null;
        }
    }

    private static class MyTaskParams {
        Bitmap bar;
        String contact;

        MyTaskParams(Bitmap bar, String  contact) {
            this.bar = bar;
            this.contact = contact;
        }

        public Bitmap getBar() {
            return bar;
        }

        public void setBar(Bitmap bar) {
            this.bar = bar;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }
    }

}
