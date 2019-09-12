package com.honda.android.personalRoom.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class NotesPhoto implements Parcelable {
    private File photoFile;
    private String content;
    private String url;
    private boolean selected;

    public NotesPhoto(File photoFile, String content, boolean selected) {
        this.photoFile = photoFile;
        this.content = content;
        this.selected = selected;
    }

    public NotesPhoto(File photoFile, String content) {
        this.photoFile = photoFile;
        this.content = content;
    }

    public NotesPhoto(File photoFile, String content, String url) {
        this.photoFile = photoFile;
        this.content = content;
        this.url = url;
    }

    public NotesPhoto(File photoFile) {
        this.photoFile = photoFile;
    }

    protected NotesPhoto(Parcel in) {
        photoFile = new File(in.readString());
        content = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<NotesPhoto> CREATOR = new Creator<NotesPhoto>() {
        @Override
        public NotesPhoto createFromParcel(Parcel in) {
            return new NotesPhoto(in);
        }

        @Override
        public NotesPhoto[] newArray(int size) {
            return new NotesPhoto[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoFile.getAbsolutePath());
        dest.writeString(content);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}