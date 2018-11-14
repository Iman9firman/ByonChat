package com.byonchat.android.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.byonchat.android.utils.AndroidUtil;

public class Video implements Parcelable {
    public static final String DATABASE_NAME = "videotube.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TYPE_TEXT = "text";

    protected ProgressListener progressListener;
    protected DownloadingListener downloadingListener;

    public int _id;
    public long id;
    public String title;
    public String description;
    public String url;
    public String thumbnail;
    public String length;
    public String size;
    public int progress;
    public String type;
    public String video_type;
    public String add_date;
    public boolean isDownloading;
    public boolean isDownloaded;
    public boolean isSelected;
    public boolean isPinned;

    public Video() {
    }

    public abstract static class VideoTubeTable {
        public static final String TABLE_NAME = "video_tube";
        public static final String COLUMN_ID_ = "_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_PROGRESS = "progress";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_VIDEO_TYPE = "video_type";
        public static final String COLUMN_ADD_DATE = "add_date";
        public static final String COLUMN_IS_DOWNLOADING = "is_downloading";
        public static final String COLUMN_IS_DOWNLOADED = "is_downloaded";
        public static final String COLUMN_IS_PINNED = "is_pinned";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID_ + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_ID + " LONG," +
                        COLUMN_TITLE + " TEXT," +
                        COLUMN_DESCRIPTION + " TEXT," +
                        COLUMN_URL + " TEXT," +
                        COLUMN_THUMBNAIL + " TEXT," +
                        COLUMN_LENGTH + " TEXT," +
                        COLUMN_SIZE + " TEXT," +
                        COLUMN_PROGRESS + " INTEGER DEFAULT 0," +
                        COLUMN_TYPE + " TEXT," +
                        COLUMN_VIDEO_TYPE + " TEXT," +
                        COLUMN_ADD_DATE + " TEXT," +
                        COLUMN_IS_DOWNLOADING + " INTEGER DEFAULT 0," +
                        COLUMN_IS_DOWNLOADED + " INTEGER DEFAULT 0," +
                        COLUMN_IS_PINNED + " INTEGER DEFAULT 0" +
                        " ); ";

        public static ContentValues toContentValues(Video video, String localPath) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, video.id);
            values.put(COLUMN_TITLE, video.title);
            values.put(COLUMN_DESCRIPTION, video.description);
            values.put(COLUMN_URL, localPath);
            values.put(COLUMN_THUMBNAIL, video.thumbnail);
            values.put(COLUMN_LENGTH, video.length);
            values.put(COLUMN_SIZE, video.size);
            values.put(COLUMN_PROGRESS, video.progress);
            values.put(COLUMN_TYPE, video.type);
            values.put(COLUMN_VIDEO_TYPE, video.video_type);
            values.put(COLUMN_ADD_DATE, video.add_date);
            values.put(COLUMN_IS_DOWNLOADING, video.isDownloading);
            values.put(COLUMN_IS_DOWNLOADED, video.isDownloaded);
            values.put(COLUMN_IS_PINNED, video.isPinned);
            return values;
        }

        public static ContentValues toContentValues(Video video) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, video.id);
            values.put(COLUMN_TITLE, video.title);
            values.put(COLUMN_DESCRIPTION, video.description);
            values.put(COLUMN_URL, video.url);
            values.put(COLUMN_THUMBNAIL, video.thumbnail);
            values.put(COLUMN_LENGTH, video.length);
            values.put(COLUMN_SIZE, video.size);
            values.put(COLUMN_PROGRESS, video.progress);
            values.put(COLUMN_TYPE, video.type);
            values.put(COLUMN_VIDEO_TYPE, video.video_type);
            values.put(COLUMN_ADD_DATE, video.add_date);
            values.put(COLUMN_IS_DOWNLOADING, video.isDownloading);
            values.put(COLUMN_IS_DOWNLOADED, video.isDownloaded);
            values.put(COLUMN_IS_PINNED, video.isPinned);
            return values;
        }

        public static Video parseCursor(Cursor cursor) {
            Video video = new Video();
            video._id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_));
            video.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            video.title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            video.description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            video.url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL));
            video.thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL));
            video.length = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LENGTH));
            video.size = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SIZE));
            video.progress = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS));
            video.type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
            video.video_type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_TYPE));
            video.add_date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADD_DATE));
            video.isDownloading = cursor.getShort(cursor.getColumnIndexOrThrow(COLUMN_IS_DOWNLOADING)) == 1;
            video.isDownloaded = cursor.getShort(cursor.getColumnIndexOrThrow(COLUMN_IS_DOWNLOADED)) == 1;
            video.isPinned = cursor.getShort(cursor.getColumnIndexOrThrow(COLUMN_IS_PINNED)) == 1;
            return video;
        }
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
        void onDownloading(Video video, boolean downloading);
    }

    public interface ProgressListener {
        void onProgress(Video video, int percentage);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean areContentsTheSame(Video video) {
        return id == video.id
                && title.equals(video.title)
                && description == video.description
                && url.equals(video.url)
                && length.equals(video.length)
                && size.equals(video.size)
                && progress == video.progress
                && type.equals(video.type)
                && video_type.equals(video.video_type)
                && add_date.equals(video.add_date)
                && isDownloading == video.isDownloading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(url);
        parcel.writeString(length);
        parcel.writeString(size);
        parcel.writeString(type);
        parcel.writeString(video_type);
        parcel.writeString(add_date);
        parcel.writeByte((byte) (isDownloaded ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    protected Video(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        length = in.readString();
        size = in.readString();
        type = in.readString();
        video_type = in.readString();
        add_date = in.readString();
        isDownloaded = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    /*public String getExtension() {
        return ByonchatFileUtil.getExtension(getAttachmentName());
    }

    public String getAttachmentName() {
        if (attachmentName == null) {
            try {
                JSONObject payload = SoloRawDataExtractor.getPayload(this);
                attachmentName = payload.optString("file_name", "");
            } catch (Exception ignored) {
                //Do nothing
            }

            if (!TextUtils.isEmpty(attachmentName)) {
                return attachmentName;
            }

            String file = "";
            try {
                JSONObject json = new JSONObject(message);
                file = json.getString("file");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int fileNameEndIndex = file.lastIndexOf(" [/file]");
            int fileNameBeginIndex = file.lastIndexOf('/', fileNameEndIndex) + 1;

            String fileName = file.substring(fileNameBeginIndex, fileNameEndIndex);
            try {
                fileName = fileName.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                fileName = fileName.replaceAll("\\+", "%2B");
                attachmentName = URLDecoder.decode(fileName, "UTF-8");
                return attachmentName;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            throw new RuntimeException("The filename '" + fileName + "' is not valid UTF-8");
        }

        return attachmentName;
    }*/
}
