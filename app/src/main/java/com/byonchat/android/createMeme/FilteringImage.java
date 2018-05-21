package com.byonchat.android.createMeme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.byonchat.android.R;


public class FilteringImage {

    public static Bitmap createContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 128.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 128.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 128.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }



    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static Bitmap doBrightness(Bitmap originalImage, int value) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, originalImage.getConfig());
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = originalImage.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                R += value;
                if (R > 255) {
                    R = 255;
                } else if (R < 0) {
                    R = 0;
                }
                G += value;
                if (G > 255) {
                    G = 255;
                } else if (G < 0) {
                    G = 0;
                }
                B += value;
                if (B > 255) {
                    B = 255;
                } else if (B < 0) {
                    B = 0;
                }
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    public static Bitmap setColorMatrixGrayScale(Bitmap original) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                getColorMatrixGrayScale()));
        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

    private static ColorMatrix getColorMatrixGrayScale() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        return colorMatrix;
    }

    public static Bitmap setColorMatrixSepia(Bitmap original) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                getColorMatrixSepia()));
        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

    private static ColorMatrix getColorMatrixSepia() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);

        // Convert to grayscale, then apply brown color
        colorMatrix.postConcat(colorScale);

        return colorMatrix;
    }

    public static Bitmap setColorMatrixBW(Bitmap original) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                getColorMatrixBW()));
        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }


    private static ColorMatrix getColorMatrixBW() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        float m = 255f;
        float t = -255*128f;
        ColorMatrix threshold = new ColorMatrix(new float[] {
                m, 0, 0, 1, t,
                0, m, 0, 1, t,
                0, 0, m, 1, t,
                0, 0, 0, 1, 0
        });

        // Convert to grayscale, then scale and clamp
        colorMatrix.postConcat(threshold);

        return colorMatrix;
    }

    public static Bitmap setColorMatrixInvert(Bitmap original) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                getColorMatrixInvert()));
        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

    public static Bitmap headerColor(Window window,Context context,int color) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        Bitmap bmp1 = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.header_skin)).getBitmap();
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        final Rect rect = new Rect(0, 0, bmp1.getWidth(), bmp1.getHeight());
        Canvas canvas = new Canvas(bmOverlay);
        final RectF rectf = new RectF(rect);
        canvas.drawRect(rectf, paint);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
        return bmOverlay;
    }

    public static void SystemBarBackground (Window window,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
    public static Bitmap viewAll(Context context,int color,int drawable) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        Bitmap bmp1 = ((BitmapDrawable) context.getResources().getDrawable(drawable)).getBitmap();
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        final Rect rect = new Rect(0, 0, bmp1.getWidth(), bmp1.getHeight());
        Canvas canvas = new Canvas(bmOverlay);
        final RectF rectf = new RectF(rect);
        canvas.drawRect(rectf, paint);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        return bmOverlay;
    }

    public static Bitmap setBitmapColor(Context context,int color,Bitmap bmp1) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        final Rect rect = new Rect(0, 0, bmp1.getWidth(), bmp1.getHeight());
        Canvas canvas = new Canvas(bmOverlay);
        final RectF rectf = new RectF(rect);
        canvas.drawRect(rectf, paint);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        return bmOverlay;
    }

    private static ColorMatrix getColorMatrixInvert() {
        return new ColorMatrix(new float[] {
                -1,  0,  0,  0, 255,
                0, -1,  0,  0, 255,
                0,  0, -1,  0, 255,
                0,  0,  0,  1,   0
        });
    }


    public static Bitmap setPopArtGradientFromBitmap3(Bitmap bmp) {
        Bitmap image = bmp.copy(Bitmap.Config.ARGB_8888, true);
        float radius = (float) (image.getWidth()/0.7);
        RadialGradient gradient = new RadialGradient(image.getWidth()/2, image.getHeight()/2, radius,  Color.TRANSPARENT, Color.BLUE, Shader.TileMode.CLAMP);


        Canvas canvas = new Canvas(image);
        canvas.drawARGB(1, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(gradient);

        final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        final RectF rectf = new RectF(rect);

        canvas.drawRect(rectf, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(image, rect, rect, paint);

        return image;
    }

    public static Bitmap flip(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }



}
