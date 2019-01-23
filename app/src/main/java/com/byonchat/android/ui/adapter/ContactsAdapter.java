package com.byonchat.android.ui.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.utils.Utility;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {
    private List<Contact> defaultList;
    private List<Contact> chatListAll;
    private List<Contact> searchList;
    private List<Contact> resultList;
    private ChatAdapterListener listener;

    String charString;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout global;
        CircularImageView imView;
        TextView tvName;
        TextView tvChat;
        TextView tvTime;
        TextView tvUnread;
        ImageView click, centang;
        RelativeLayout content, imager;

        public MyViewHolder(final View view) {
            super(view);
            global = (RelativeLayout) view.findViewById(R.id.global_layout);
            content = (RelativeLayout) view.findViewById(R.id.layoutTitle);
            imager = (RelativeLayout) view.findViewById(R.id.layoutImager);
            imView = (CircularImageView) view.findViewById(R.id.imagePhoto);
            tvName = (TextView) view.findViewById(R.id.group_name);
            tvChat = (TextView) view.findViewById(R.id.last_chat);
            tvTime = (TextView) view.findViewById(R.id.last_time);
            tvUnread = (TextView) view.findViewById(R.id.unreaded);
            click = (ImageView) view.findViewById(R.id.appear);
            centang = (ImageView) view.findViewById(R.id.centang2);
            click.setVisibility(View.GONE);
            global.setBackground(null);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //listener.onRowClickContact(v, getAdapterPosition());
        }
    }

    public ContactsAdapter(Context context, ChatAdapterListener listener, List<Contact> contactList/*, List<Contact> searchList*/) {
        this.context = context;
        this.listener = listener;
        this.chatListAll = contactList;
        this.defaultList = contactList;
        this.searchList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (chatListAll.size() != 0 && chatListAll != null) {
            final Contact item = chatListAll.get(position);
//            holder.imView.setImageResource(R.drawable.pic);

            String uriImage = "https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + item.getJabberId() + ".jpg";
            String iconsStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/ByonChat/Photo Profile/";
            final File sdIconStorageDir = new File(iconsStoragePath);
            if (!sdIconStorageDir.exists()) {
                sdIconStorageDir.mkdirs();
            }

            iconsStoragePath += item.getJabberId() + ".jpg";
            final File yourFile = new File(sdIconStorageDir, item.getJabberId() + ".jpg");

            if (uriImage != null) {
                Drawable y = Drawable.createFromPath(iconsStoragePath);
                if (y == null) {
                    y = context.getResources().getDrawable(R.drawable.ic_no_photo);
                }
                Glide.with(holder.imView.getContext())
                        .load(uriImage).asBitmap()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(y)
                        .into(new BitmapImageViewTarget(holder.imView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                holder.imView.setImageBitmap(resource);
                                try {
                                    FileOutputStream fos = new FileOutputStream(yourFile);
                                    resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.close();
                                } catch (FileNotFoundException e) {
                                } catch (IOException e) {
                                }
                            }
                        });
            }

            holder.tvName.setText(item.getName());
            holder.tvChat.setText("+" + Utility.formatPhoneNumber(item.getJabberId()));
            if (charString != null) {
//                textHighlighter = new TextHighlighter();
//                if (item.getName().toLowerCase().contains(charString.toLowerCase())) {
//                    textHighlighter.setForegroundColor(context.getResources().getColor(R.color.colorBack))
//                            .addTarget(holder.tvName)
//                            .highlight(charString, TextHighlighter.BASE_MATCHER);
//                }
            }
            holder.global.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRowClickContact(v, item.getJabberId());
                }
            });
        }
        holder.tvTime.setVisibility(View.GONE);
        holder.tvUnread.setVisibility(View.GONE);
        holder.centang.setVisibility(View.GONE);
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                charString = charSequence.toString();
                if (charString.isEmpty()) {
                    chatListAll.clear();
                    resultList.clear();
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : searchList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    resultList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resultList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                chatListAll = (ArrayList<Contact>) filterResults.values;
                if (chatListAll != null) {
                    if (chatListAll.size() <= defaultList.size() - 1) {
                        listener.counting(chatListAll.size());
                    }
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        if (chatListAll != null) {
            return chatListAll.size();
        } else {
            return 0;
        }
    }

    public interface ChatAdapterListener {
        void onRowClickContact(View view, String JabberID);

        void counting(int yzou);
    }

}
