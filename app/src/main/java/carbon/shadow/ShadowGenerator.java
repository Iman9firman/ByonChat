package carbon.shadow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RSRuntimeException;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import carbon.widget.CornerView;

public class ShadowGenerator {
    public static final int ALPHA = 51;

    private static RenderScript renderScript;
    private static ScriptIntrinsicBlur blurShader;
    private static Paint paint = new Paint();
    private static boolean software = false;
    static RectF roundRect = new RectF();

    private static void blur(Bitmap bitmap, float radius) {
        radius = Math.max(0, Math.min(radius, 25));
        if (software) {
            blurSoftware(bitmap, radius);
        } else {
            blurRenderScript(bitmap, radius);
        }
    }

    private static void blurSoftware(Bitmap bitmap, float radius) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] halfResult = new int[bitmap.getWidth() * bitmap.getHeight()];
        int rad = (int) Math.ceil(radius);
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int sumBlack = 0, sumAlpha = 0;
                for (int i = -rad; i <= rad; i++) {
                    int pixel = pixels[y * bitmap.getWidth() + Math.max(0, Math.min(x + i, bitmap.getWidth() - 1))];
                    sumBlack += pixel & 0xff;
                    sumAlpha += (pixel >> 24) & 0xff;
                }
                int blurredBlack = sumBlack / (rad * 2 + 1);
                int blurredAlpha = sumAlpha / (rad * 2 + 1);
                halfResult[y * bitmap.getWidth() + x] = Color.argb(blurredAlpha, blurredBlack, blurredBlack, blurredBlack);
            }
        }
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int sumBlack = 0, sumAlpha = 0;
                for (int i = -rad; i <= rad; i++) {
                    int pixel = halfResult[Math.max(0, Math.min(y + i, bitmap.getHeight() - 1)) * bitmap.getHeight() + x];
                    sumBlack += pixel & 0xff;
                    sumAlpha += (pixel >> 24) & 0xff;
                }
                int blurredBlack = sumBlack / (rad * 2 + 1);
                int blurredAlpha = sumAlpha / (rad * 2 + 1);
                pixels[y * bitmap.getWidth() + x] = Color.argb(blurredAlpha, blurredBlack, blurredBlack, blurredBlack);
            }
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    private static void blurRenderScript(Bitmap bitmap, float radius) {
        Allocation inAllocation = Allocation.createFromBitmap(renderScript, bitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation outAllocation = Allocation.createTyped(renderScript, inAllocation.getType());

        blurShader.setRadius(radius);
        blurShader.setInput(inAllocation);
        blurShader.forEach(outAllocation);

        outAllocation.copyTo(bitmap);
    }

    public static Shadow generateShadow(View view, float elevation) {
        if (!software && renderScript == null) {
            try {
                renderScript = RenderScript.create(view.getContext());
                blurShader = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            } catch (RSRuntimeException ignore) {
                software = true;
            }
        }

        CornerView cornerView = (CornerView) view;

        int e = (int) Math.ceil(elevation);
        int c = Math.max(e,cornerView.getCornerRadius());

        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(e * 2 + 2 * c+1, e * 2 + 2 * c+1, Bitmap.Config.ARGB_8888);

        Canvas shadowCanvas = new Canvas(bitmap);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff000000);

        roundRect.set(e, e, bitmap.getWidth() - e, bitmap.getHeight() - e);
        shadowCanvas.drawRoundRect(roundRect, c, c, paint);

        blur(bitmap, elevation);

        return new NinePatchShadow(bitmap, elevation, c);
    }
}
