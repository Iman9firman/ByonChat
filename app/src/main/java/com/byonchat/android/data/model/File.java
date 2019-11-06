package com.byonchat.android.data.model;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.byonchat.android.utils.AndroidUtil;

public class File implements Parcelable {
    public static final String TYPE_TEXT = "text";

    protected ProgressListener progressListener;
    protected DownloadingListener downloadingListener;

    public int _id;
    public long id;
    public String title;
    public String subtitle;
    public String description;
    public String url;
    public String thumbnail;
    public String size;
    public int progress;
    public String type;
    public String file_type;
    public String timestamp;

    //Add Condition by Mas Maul
    public String kode_jjt;
    public String id_pembobotan;
    public String id_section;
    public String id_subsection;

    public boolean isDownloading;
    public boolean isDownloaded;
    public boolean isSelected;
    public boolean isPinned;

    //additional
    public String id_detail_area;
    public String nama_requester;
    public String nik_requester;
    public String id_history;

    public File() {
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setDownloadingListener(DownloadingListener downloadingListener) {
        this.downloadingListener = downloadingListener;
    }

    public void setDownloading(boolean downloading) {
        this.isDownloading = downloading;
        this.isSelected = false;
        AndroidUtil.runOnUIThread(() -> {
            if (downloadingListener != null) {
                downloadingListener.onDownloading(this, downloading);
            }
        });
    }

    public void setProgress(int percentage) {
        this.progress = percentage;
        AndroidUtil.runOnUIThread(() -> {
            if (progressListener != null) {
                progressListener.onProgress(this, progress);
            }
        });
    }

    public interface DownloadingListener {
        void onDownloading(File file, boolean downloading);
    }

    public interface ProgressListener {
        void onProgress(File file, int percentage);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean areContentsTheSame(File file) {
        return id == file.id
                && title.equals(file.title)
                && subtitle.equals(file.subtitle)
                && description.equals(file.description)
                && url.equals(file.url)
                && size.equals(file.size)
                && progress == file.progress
                && type.equals(file.type)
                && file_type.equals(file.file_type)
                && timestamp.equals(file.timestamp)
                && isDownloading == file.isDownloading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(subtitle);
        parcel.writeString(description);
        parcel.writeString(url);
        parcel.writeString(size);
        parcel.writeString(type);
        parcel.writeString(file_type);
        parcel.writeString(timestamp);
        parcel.writeByte((byte) (isDownloaded ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    protected File(Parcel in) {
        id = in.readLong();
        title = in.readString();
        subtitle = in.readString();
        description = in.readString();
        url = in.readString();
        size = in.readString();
        type = in.readString();
        file_type = in.readString();
        timestamp = in.readString();
        isDownloaded = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };

}
