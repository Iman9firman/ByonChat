package com.byonchat.android.data.local;

import com.byonchat.android.data.model.Video;

import java.util.List;

public interface ByonchatVideoTubeFileStore {

    void addOrUpdate(Video video, String localPath);

    void update(Video video);

    void delete(Video video);

    void deleteLocalPath(String localPath);

    List<Video> getDownloadedFiles();
}
