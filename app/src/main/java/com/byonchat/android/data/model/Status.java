package com.byonchat.android.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.byonchat.android.utils.AndroidUtil;

public class Status implements Parcelable {
    public static final String TYPE_TEXT = "text";

    protected ProgressListener progressListener;
    protected DownloadingListener downloadingListener;

    public int _id;
    public long id;
    public String title;
    public String subtitle;
    public String nama_apv1;
    public String nama_apv2;
    public String nama_apv3;
    public String no_requester;
    public String create_date;
    public String history;
    public String id_request;
    public String id_history;
    public int progress;
    public String type;
    public String url;
    public String timestamp;
    public boolean isDownloading;
    public boolean isDownloaded;
    public boolean isSelected;
    public boolean isPinned;

    public Status() {
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
        void onDownloading(Status file, boolean downloading);
    }

    public interface ProgressListener {
        void onProgress(Status file, int percentage);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean areContentsTheSame(Status file) {
        return id == file.id
                && title.equals(file.title)
                && subtitle.equals(file.subtitle)
                && nama_apv1.equals(file.nama_apv1)
                && nama_apv2.equals(file.nama_apv2)
                && nama_apv3.equals(file.nama_apv3)
                && no_requester.equals(file.no_requester)
                && create_date.equals(file.create_date)
                && id_request.equals(file.id_request)
                && id_history.equals(file.id_history)
                && progress == file.progress
                && type.equals(file.type)
                && url.equals(file.url)
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
        parcel.writeString(nama_apv1);
        parcel.writeString(nama_apv2);
        parcel.writeString(nama_apv3);
        parcel.writeString(no_requester);
        parcel.writeString(create_date);
        parcel.writeString(id_request);
        parcel.writeString(id_history);
        parcel.writeString(type);
        parcel.writeString(url);
        parcel.writeString(timestamp);
        parcel.writeByte((byte) (isDownloaded ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    protected Status(Parcel in) {
        id = in.readLong();
        title = in.readString();
        subtitle = in.readString();
        nama_apv1 = in.readString();
        nama_apv2 = in.readString();
        nama_apv3 = in.readString();
        no_requester = in.readString();
        create_date = in.readString();
        id_request = in.readString();
        id_history = in.readString();
        type = in.readString();
        url = in.readString();
        timestamp = in.readString();
        isDownloaded = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int id_request) {
            return new Status[id_request];
        }
    };

}
