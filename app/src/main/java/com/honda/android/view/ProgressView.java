package com.honda.android.view;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public interface ProgressView {
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Visibility {
    }

    void setProgress(int progress);

    int getProgress();

    void setFinishedColor(int finishedColor);

    int getFinishedColor();

    void setUnfinishedColor(int unfinishedColor);

    int getUnfinishedColor();

    void setVisibility(@Visibility int visibility);
}

