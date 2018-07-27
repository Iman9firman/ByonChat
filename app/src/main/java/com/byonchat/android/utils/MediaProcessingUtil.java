package com.byonchat.android.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.Contact;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaProcessingUtil {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMdd-HHmmss", Locale.getDefault());
    public static final String PROFILEPIC_FILE_NAME = "profile-pic";
    public static final String ALBUM_NAME = "ByonChat";
    public static final String FILES_PATH = Byonchat.getAppsName() + File.separator + "Files";
    public static final int PIC_HEIGHT = 480;
    public static final int PIC_WIDTH = 640;

    public static String createFileName(String ext, String name) {
        if (ext == null)
            ext = "jpeg";
        if (name == null) {
            name = "-";
        } else {
            name = "-" + name + "-";
        }
        return "bc" + name + dateFormat.format(new Date()) + "." + ext;
    }

    public static String createFileNameAll(Context context, String ext) {
        if (ext == null)
            ext = "jpeg";
        if (ext.contains(".")) {
            ext = ext.substring(ext.lastIndexOf("."));
        }
        String count = "0";
        String squence = "";
        String date = dateFormat.format(new Date());
        int idSelect = 22;
        String type = "VID";
        if (ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".jpeg")) {
            idSelect = 21;
            type = "IMG";
        }
        squence = new Validations().getInstance(context).getContentValidation(idSelect);

        if (squence.equalsIgnoreCase("")) {
            count = "0";
            new Validations().getInstance(context).setContentValidation(idSelect, count + ";" + date);
        } else {
            String nn[] = squence.split(";");
            if (nn.length == 2) {
                if (nn[1].split("-")[0].equalsIgnoreCase(date.split("-")[0])) {
                    String nilai = "0";
                    if (!nn[0].equalsIgnoreCase("")) {
                        nilai = nn[0];
                    }
                    count = String.valueOf(Integer.valueOf(nilai) + 1);
                    new Validations().getInstance(context).setContentValidation(idSelect, count + ";" + date);
                } else {
                    count = String.valueOf(0);
                    new Validations().getInstance(context).setContentValidation(idSelect, count + ";" + date);
                }
            } else {
                count = String.valueOf(Integer.valueOf(count) + 1);
                new Validations().getInstance(context).setContentValidation(idSelect, count + ";" + date);
            }
        }
        return "BC-" + type + "-" + date + "-" + count + ext;
    }

    public static String generateFilePath(String fileName, String extension) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(),
                ImageUtil.isImage(fileName) ? ImageUtil.IMAGE_PATH : FILES_PATH);

        if (!file.exists()) {
            file.mkdirs();
        }

        int index = 0;
        String directory = file.getAbsolutePath() + File.separator;
        String[] fileNameSplit = splitFileName(fileName);
        while (true) {
            File newFile;
            if (index == 0) {
                newFile = new File(directory + fileNameSplit[0] + extension);
            } else {
                newFile = new File(directory + fileNameSplit[0] + "-" + index + extension);
            }
            if (!newFile.exists()) {
                return newFile.getAbsolutePath();
            }
            index++;
        }
    }

    public static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    public static void notifySystem(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        Byonchat.getApps().sendBroadcast(mediaScanIntent);
    }

    public static String getExtension(String fileName) {
        int lastDotPosition = fileName.lastIndexOf('.');
        String ext = fileName.substring(lastDotPosition + 1);
        ext = ext.replace("_", "");
        return ext.trim().toLowerCase();
    }

    public static File getDirectory() {
        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                ALBUM_NAME);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    public static String getProfilePicName(Contact contact) {
        return MediaProcessingUtil.PROFILEPIC_FILE_NAME + "-"
                + contact.getJabberId() + ".jpg";
    }

    public static String getProfilePicName(String jabberId) {
        return MediaProcessingUtil.PROFILEPIC_FILE_NAME + "-"
                + jabberId + ".jpg";
    }

    public static String getProfilePicNameSmall(Contact contact) {
        return MediaProcessingUtil.PROFILEPIC_FILE_NAME + "-"
                + contact.getJabberId() + "_small.jpg";
    }

    public static String getProfilePic(String contact) {
        return MediaProcessingUtil.PROFILEPIC_FILE_NAME + "-"
                + contact + ".jpg";
    }

    public static File getOutputFile(String ext, String name) {
        File dir = getDirectory();

        return new File(dir, createFileName(ext, name));
    }

    public static void saveProfilePic(Context context, String JabberId, Bitmap resource) {
        String fname = MediaProcessingUtil.getProfilePicName(JabberId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fname, Activity.MODE_PRIVATE);
            fos.write(byteArray);
            fos.flush();
        } catch (IOException e) {
            Log.e(context.getClass().getSimpleName(),
                    "failed saving profile picture for "
                            + JabberId + " :" + e.getMessage(),
                    e);
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
    }


    public static File getOutputFile(String ext) {
        return getOutputFile(ext, null);
    }

    public static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = Byonchat.getApps().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    public static String getRealPathFromURI(ContentResolver resolver,
                                            Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(contentUri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public static File getResizedImage(File src, String destName, int width,
                                       int height) {
        FileOutputStream out = null;
        File f = new File(destName);
        try {
            out = new FileOutputStream(f);

            Bitmap b = getScaledBitmap(src.getAbsolutePath(), width, height);
            b.compress(Bitmap.CompressFormat.JPEG, 70, out);
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
            }
        }
        return f;
    }

    public static File getResizedImage(File src, String destName) {
        return getResizedImage(src, destName, PIC_WIDTH, PIC_HEIGHT);
    }

    public static void saveFileToGallery(Context context, Uri imageUri) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void addCropExtra(Intent i) {
        i.putExtra("crop", "true");
        i.putExtra("scale", true);
        i.putExtra("aspectX", 4);
        i.putExtra("aspectY", 4);
        i.putExtra("outputX", 480);
        i.putExtra("outputY", 480);
        i.putExtra("scaleUpIfNeeded", true);

    }

    public static void addCropExtraFree(Intent i) {
        i.putExtra("crop", "true");
        i.putExtra("scale", true);
        i.putExtra("scaleUpIfNeeded", true);

    }

    public static Bitmap getScaledBitmap(String path, float width, float height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int scaleSize = 1;
        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight) {
                scaleSize = Math.round(srcHeight / height);
            } else {
                scaleSize = Math.round(srcWidth / width);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = scaleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static void copyFile(File source, File dest, boolean deleteSource)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        FileInputStream fis = new FileInputStream(source);
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            if (deleteSource) {
                source.delete();
            }
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
    }

    public static String getFileFromUrl(Activity activity, String url,
                                        String name, String ext) throws Exception {

        String fname = createFileName(ext, name);
        InputStream in = null;
        OutputStream out = null;

        try {
            HttpClient httpClient = HttpHelper.createHttpClient(activity);
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            in = response.getEntity().getContent();
            out = activity.openFileOutput(fname, Activity.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
            ;
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                }
            ;
        }

        return fname;
    }


    public static String getFileFromUrl(Context activity, String url,
                                        String name, String ext) throws Exception {

        String fname = createFileName(ext, name);
        InputStream in = null;
        OutputStream out = null;

        try {
            HttpClient httpClient = HttpHelper.createHttpClient(activity);
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            in = response.getEntity().getContent();
            out = activity.openFileOutput(fname, Activity.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
            ;
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                }
            ;
        }

        return fname;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap src, float round) {
        Bitmap result = null;
        if (src != null) {
            // Source image size
            int width = src.getWidth();
            int height = src.getHeight();
            // create result bitmap output
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // set canvas for painting
            Canvas canvas = new Canvas(result);
            canvas.drawARGB(0, 0, 0, 0);

            // configure paint
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            // configure rectangle for embedding
            final Rect rect = new Rect(0, 0, width, height);
            final RectF rectF = new RectF(rect);

            // draw Round rectangle to canvas
            canvas.drawRoundRect(rectF, round, round, paint);

            // create Xfer mode
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // draw source image to canvas
            canvas.drawBitmap(src, rect, rect, paint);
        }
        // return final image
        return result;
    }

    public static Bitmap getRoundedCornerBitmapBorder(Bitmap src, float round) {
        Bitmap result = null;
        if (src != null) {
            // Source image size
            int width = src.getWidth();
            int height = src.getHeight();
            // create result bitmap output
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // set canvas for painting
            Canvas canvas = new Canvas(result);
            canvas.drawARGB(0, 0, 0, 0);


            // configure paint
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            // configure rectangle for embedding
            final Rect rect = new Rect(0, 0, width, height);
            final RectF rectF = new RectF(rect);

            // draw Round rectangle to canvas
            canvas.drawRoundRect(rectF, round, round, paint);

            // create Xfer mode
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // draw source image to canvas
            canvas.drawBitmap(src, rect, rect, paint);
        }
        // return final image
        return result;
    }


    public static String getFileFromUrlMessage(Activity activity, String url,
                                               String name, String ext) throws Exception {

        String fname = createFileName(ext, name);
        InputStream in = null;
        FileOutputStream out = null;

        try {
            HttpClient httpClient = HttpHelper.createHttpClient(activity);
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            in = response.getEntity().getContent();
            out = new FileOutputStream(new File(getDirectory(), "/" + fname));

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
            ;
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                }
            ;
        }

        return getDirectory().getAbsolutePath() + "/" + fname;
    }

    public static Bitmap decodeFileListConversation(File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = 50;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale++;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inScaled = false;
            o2.inDither = false;
            o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    public static Bitmap decodeFileSend(File f) {
        int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
        Bitmap bmpPic = BitmapFactory.decodeFile(f.getPath());
        if ((bmpPic.getWidth() >= 150) && (bmpPic.getHeight() >= 150)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 150) && (bmpPic.getHeight() >= 150)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(f.getPath(), bmpOptions);
            }
        }
        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;
        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
        }

        return bmpPic;
    }

    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static Bitmap decodeSampledBitmapFromResourceMemOpt(
            InputStream inputStream, int reqWidth, int reqHeight) {

        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;

        try {
            while ((len = inputStream.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            /*    int[] pids = { android.os.Process.myPid() };
             *//* MemoryInfo myMemInfo = mAM.getProcessMemoryInfo(pids)[0];
            Log.e(TAG, "dalvikPss (decoding) = " + myMemInfo.dalvikPss);*//*
             */
            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
