package com.byonchat.android.createMeme;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Iman Firmansyah on 3/9/2015.
 */
public class SaveImage {

    public boolean storeImage(Bitmap imageData, String filename) {
        String iconsStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/MemeEditor";

        File sdIconStorageDir = new File(iconsStoragePath);
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdir();
        }

        try {
            String filePath = sdIconStorageDir.toString() + "/" + filename
                    + ".png";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(
                    fileOutputStream);

            // compress image according to your format
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

}
