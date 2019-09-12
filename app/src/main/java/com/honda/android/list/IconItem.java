package com.honda.android.list;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.honda.android.provider.ChatParty;

import java.util.Comparator;

public class IconItem implements Parcelable {
    public static final String TYPE_ORIGIN = "type_origin";
    public static final String TYPE_MESSAGE_FIND = "type_message_find";
    public boolean isSelected;
    public boolean isPinned;
    public boolean isMuted;

    public String jabberId;
    public String title;
    public String info;
    public long unread;
    public String dateInfo;
    public ChatParty chatParty;
    public Uri imageUri;
    public boolean editMode;
    public String value;
    public int imageAction = 0;
    public boolean haveRoooms = false;
    public String signature;
    public String type;

    public IconItem() {

    }

    public IconItem(String jabberId, String title, String info,
                    String dateInfo, ChatParty contact) {
        this.jabberId = jabberId;
        this.title = title;
        this.info = info;
        this.dateInfo = dateInfo;
        this.chatParty = contact;
    }

    public IconItem(String jabberId, String title, String info,
                    String dateInfo, ChatParty contact, long unreadMessage, String type, String signature) {
        this.jabberId = jabberId;
        this.title = title;
        this.info = info;
        this.dateInfo = dateInfo;
        this.chatParty = contact;
        this.unread = unreadMessage;
        this.value = type;
        this.signature = signature;
    }

    public IconItem(String jabberId, String title, String info,
                    String dateInfo, ChatParty contact, long unreadMessage, String type) {
        this.jabberId = jabberId;
        this.title = title;
        this.info = info;
        this.dateInfo = dateInfo;
        this.chatParty = contact;
        this.unread = unreadMessage;
        this.value = type;
    }

    public IconItem(String jabberId, String title, String info,
                    String dateInfo, ChatParty contact, long unreadMessage, String type, boolean haveRoooms) {
        this.jabberId = jabberId;
        this.title = title;
        this.info = info;
        this.dateInfo = dateInfo;
        this.chatParty = contact;
        this.unread = unreadMessage;
        this.value = type;
        this.haveRoooms = haveRoooms;
    }

    public static Comparator<IconItem> nameSortComparator = new Comparator<IconItem>() {

        public int compare(IconItem s1, IconItem s2) {
            String IconItem1 = s1.getTitle();
            String IconItem2 = s2.getTitle();

            return IconItem1.compareTo(IconItem2);
        }

    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public String getTitle() {
        return title;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri uri) {
        this.imageUri = uri;
    }

    public String getInfo() {
        return info;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setMsisdn(String msisdn) {
        this.jabberId = msisdn;
    }

    public ChatParty getChatParty() {
        return chatParty;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setImageAction(int imageAction) {
        this.imageAction = imageAction;
    }

    public int getImageAction() {
        return imageAction;
    }

    public long getUnread() {
        return unread;
    }

    public void setUnread(long unread) {
        this.unread = unread;
    }

    public void setHaveRoooms(boolean haveRoooms) {
        this.haveRoooms = haveRoooms;
    }

    public boolean isHaveRoooms() {
        return haveRoooms;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IconItem) {
            if (((IconItem) o).getJabberId().equals(jabberId))
                return true;
        }
        return false;
    }

    public boolean areContentsTheSame(IconItem item) {
        return jabberId.equals(item.jabberId)
                && title.equals(item.title)
                && info.equals(item.info)
                /*&& unread == item.unread*/
                && dateInfo.equals(item.dateInfo)
                /*&& chatParty.equals(item.chatParty)
                && imageUri.equals(item.imageUri)
                && editMode == item.editMode
                && value.equals(item.value)
                && imageAction == item.imageAction
                && haveRoooms == item.haveRoooms
                && signature.equals(item.signature)*/
                && type.equals(item.type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(jabberId);
        parcel.writeString(title);
        parcel.writeString(info);
        parcel.writeLong(unread);
        parcel.writeString(dateInfo);
        parcel.writeString(value);
        parcel.writeString(signature);
    }

    protected IconItem(Parcel in) {
        jabberId = in.readString();
        title = in.readString();
        info = in.readString();
        unread = in.readLong();
        dateInfo = in.readString();
        value = in.readString();
        signature = in.readString();
    }

    public static final Creator<IconItem> CREATOR = new Creator<IconItem>() {
        @Override
        public IconItem createFromParcel(Parcel in) {
            return new IconItem(in);
        }

        @Override
        public IconItem[] newArray(int size) {
            return new IconItem[size];
        }
    };
}