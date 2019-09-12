package com.honda.android.listeners;

import android.os.Environment;
import android.util.Log;

import com.honda.android.model.Folder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by byonc on 4/18/2017.
 */

public interface OnFolderClickListener {

    void onFolderClick(Folder bucket);
}