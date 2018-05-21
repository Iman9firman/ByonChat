package com.byonchat.android.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;

import java.util.List;

public class AttachmentAdapter extends
        ArrayAdapter<AttachmentAdapter.AttachmentMenuItem> {

    public AttachmentAdapter(Context c, int resourceId, int textResourceId,
            List<AttachmentMenuItem> items) {
        super(c, resourceId, textResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        AttachmentViewHolder holder = (AttachmentViewHolder) row.getTag();
        if (holder == null) {
            holder = new AttachmentViewHolder(row);
            row.setTag(holder);
        }

        AttachmentMenuItem item = getItem(position);
        holder.imageMenu.setImageResource(item.getResourceIcon());
        holder.textMenu.setText(item.getTitle());
        return row;
    }

    public static class AttachmentMenuItem {
        private int resourceIcon;
        private String title;

        public AttachmentMenuItem(int rIcon, String title) {
            resourceIcon = rIcon;
            this.title = title;
        }

        public int getResourceIcon() {
            return resourceIcon;
        }

        public String getTitle() {
            return title;
        }

    }

    class AttachmentViewHolder {
        private ImageView imageMenu;
        private TextView textMenu;

        public AttachmentViewHolder(View row) {
            imageMenu = (ImageView) row.findViewById(R.id.imageMenu);
            textMenu = (TextView) row.findViewById(R.id.textMenu);

        }
    }

}
