package com.byonchat.android.list;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Message;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BotAdapter extends BaseAdapter implements Filterable {

    private static ArrayList<ContactBot> catArrayList = new ArrayList<ContactBot>();
    private static ArrayList<ContactBot> catArray = new ArrayList<ContactBot>();
    private LayoutInflater inflater;
    public Context context;
    ContactsFilter mContactsFilter;
    public boolean showTitle = false;
    String bold;

    public BotAdapter(Context ctx, ArrayList<ContactBot> contactBot, boolean showTitle) {
        context = ctx;
        this.showTitle = showTitle;
        this.catArrayList = contactBot;
        this.catArray = contactBot;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public BotAdapter(Context ctx, ArrayList<ContactBot> contactBot, boolean showTitle, String b) {
        bold = b;
        context = ctx;
        this.showTitle = showTitle;
        this.catArrayList = contactBot;
        this.catArray = contactBot;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return catArrayList.size();
    }

    public Object getItem(int position) {
        return catArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public ArrayList<ContactBot> newList() {
        ArrayList<ContactBot> list = catArrayList;
        return (list);
    }


    public class ViewHolder {
        TextView txtName;
        TextView txtInfo;
        ImageView btnChat;
        Target view;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            // convertView = inflater.inflate(R.layout.list_item_contact_bot, null);
            convertView = inflater.inflate(R.layout.list_item_room_chat_order, null);

            holder.btnChat = (ImageView) convertView.findViewById(R.id.btnChat);
            holder.view = (Target) convertView.findViewById(R.id.imagePhoto);
            holder.txtName = (TextView) convertView.findViewById(R.id.textTitle);
            holder.txtInfo = (TextView) convertView.findViewById(R.id.textInfo);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        if (catArrayList.get(position).getLink().equalsIgnoreCase("") || catArrayList.get(position).getLink().equalsIgnoreCase("null")) {
            Picasso.with(context)
                    .load(R.drawable.ic_room_door)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.view);
        } else {
            Picasso.with(context).load(catArrayList.get(position).getLink()).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.view);
        }

        holder.txtName.setText(Message.highlightText(bold, catArrayList.get(position).getRealname()));
        String desc = catArrayList.get(position).getDesc();
        if (Message.isJSONValid(catArrayList.get(position).getDesc())) {
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(catArrayList.get(position).getDesc());
                desc = jObject.getString("desc");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String text = Html.fromHtml(desc).toString();
        if (text.contains("<")) {
            text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
        }
        holder.txtInfo.setText(Message.highlightText(bold, Message.parsedMessageText(text)));

        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, catArrayList.get(position).getName());
                context.startActivity(intent);
            }
        });

        IntervalDB db = new IntervalDB(context);
        db.open();

        Cursor cursorSelect = db.getSingleContact(24);
        if (cursorSelect.getCount() > 0) {
            holder.btnChat.setVisibility(View.GONE);

        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mContactsFilter == null)
            mContactsFilter = new ContactsFilter();

        return mContactsFilter;
    }

    private class ContactsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = catArray;
                results.count = catArray.size();
            } else {
                ArrayList<ContactBot> filteredContacts = new ArrayList<ContactBot>();
                for (ContactBot c : catArray) {
                    if (c.getRealname().contains(constraint.toString().replace(" ", "_"))) {
                        filteredContacts.add(c);
                    }
                }
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            catArrayList = (ArrayList<ContactBot>) results.values;
            notifyDataSetChanged();
        }
    }

    public synchronized void refresAdapter(ArrayList<ContactBot> list) {
        catArrayList.clear();
        catArrayList.addAll(list);
        catArray.clear();
        catArray.addAll(list);
        notifyDataSetChanged();
    }

    public synchronized void refresAdapter() {
        catArrayList.clear();
        catArray.clear();
        notifyDataSetChanged();
    }
}
