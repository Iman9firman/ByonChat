package com.honda.android.data.local;

import com.honda.android.data.model.Video;

import java.util.List;

public interface ByonchatVideoTubeFileStore {

    void addOrUpdate(Video video, String localPath);

    void update(Video video);

    void delete(Video video);

    void deleteLocalPath(String localPath);

    List<Video> getDownloadedFiles();
}
