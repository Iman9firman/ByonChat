package com.honda.android.videotrimmer.utils;

import android.support.annotation.NonNull;
import com.honda.android.videotrimmer.interfaces.OnTrimVideoListener;
import java.io.IOException;
import java.util.Formatter;

/**
 * Created by byonc on 5/9/2017.
 */

public class TrimVideoUtils {
    public static void throwValueBack(@NonNull String outpath, @NonNull String textMessage, @NonNull OnTrimVideoListener callback) throws IOException {
        genVideoUsingMp4Parser(outpath, textMessage, callback);
    }

    private static void genVideoUsingMp4Parser(@NonNull String outpath, @NonNull String textMessage, @NonNull OnTrimVideoListener callback) throws IOException {
        if (callback != null)
        callback.getResult(outpath, textMessage);
    }

    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        Formatter mFormatter = new Formatter();
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


}
