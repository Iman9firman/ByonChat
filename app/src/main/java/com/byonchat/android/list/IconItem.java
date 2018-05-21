package com.byonchat.android.list;

import android.net.Uri;

import com.byonchat.android.provider.ChatParty;

import java.util.Comparator;

public class IconItem {
    private String jabberId;
    private String title;
    private String info;
    private long unread;
    private String dateInfo;
    private ChatParty chatParty;
    private Uri imageUri;
    private boolean editMode;
    private String value;
    private int imageAction = 0;
    private boolean haveRoooms = false;
    private String signature;

    public IconItem(String jabberId, String title, String info,
                    String dateInfo, ChatParty contact) {
        this.jabberId = jabberId;
        this.title = title;
        this.info = info;
        this.dateInfo = dateInfo;
        this.chatParty = contact;
    }

    public IconItem(String jabberId, String title, String info,
                    String dateInfo, ChatParty contact, long unreadMessage, String type,String signature) {
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
                    String dateInfo, ChatParty contact, long unreadMessage, String type,boolean haveRoooms) {
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
}
