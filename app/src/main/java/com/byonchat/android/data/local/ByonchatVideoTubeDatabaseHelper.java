package com.byonchat.android.data.local;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.byonchat.android.data.model.Video;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.utils.ByonchatFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ByonchatVideoTubeDatabaseHelper implements ByonchatVideoTubeDataStore {

    protected final SQLiteDatabase sqLiteDatabase;

    public ByonchatVideoTubeDatabaseHelper() {
        ByonchatVideoTubeOpenHelper bcVideoTubeDbOpenHelper = new ByonchatVideoTubeOpenHelper(Byonchat.getApps());
        sqLiteDatabase = bcVideoTubeDbOpenHelper.getReadableDatabase();
    }

    @Override
    public void addOrUpdate(Video video, String localPath) {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.insertWithOnConflict(Video.VideoTubeTable.TABLE_NAME, null,
                    Video.VideoTubeTable.toContentValues(video, localPath), SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void update(Video video) {
        String where = Video.VideoTubeTable.COLUMN_ID + " = " + video.id;

        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.update(Video.VideoTubeTable.TABLE_NAME, Video.VideoTubeTable.toContentValues(video), where, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void delete(Video video) {
        deleteLocalPath(video.url);

        String where = Video.VideoTubeTable.COLUMN_ID_ + " = " + video._id
                + " AND "
                + Video.VideoTubeTable.COLUMN_URL + " = " + DatabaseUtils.sqlEscapeString(video.url);
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Video.VideoTubeTable.TABLE_NAME, where, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void deleteLocalPath(String localPath) {
        File file = new File(localPath);
        if (file.exists()) {
            file.delete();
        }
        ByonchatFileUtil.notifySystem(file);
    }

    @Override
    public List<Video> getDownloadedFiles() {
        String query = "SELECT * FROM "
                + Video.VideoTubeTable.TABLE_NAME;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        List<Video> videos = new ArrayList<>();
        while (cursor.moveToNext()) {
            Video video = Video.VideoTubeTable.parseCursor(cursor);
            videos.add(video);
        }
        cursor.close();
        return videos;
    }

    @Override
    public void clear() {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Video.VideoTubeTable.TABLE_NAME, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
