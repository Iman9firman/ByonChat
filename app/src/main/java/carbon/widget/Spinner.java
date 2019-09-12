package carbon.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.honda.android.R;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import carbon.Carbon;

/**
 * Created by Marcin on 2015-06-11.
 */
public class Spinner extends EditText {
    private boolean isShowingPopup = false;

    public Spinner(Context context) {
        this(context, null);
    }

    public Spinner(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.carbon_spinnerStyle);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        try {
            Resources.Theme theme = context.getTheme();
            TypedValue typedvalueattr = new TypedValue();
            theme.resolveAttribute(R.attr.colorControlNormal, typedvalueattr, true);
            int color = typedvalueattr.resourceId != 0 ? context.getResources().getColor(typedvalueattr.resourceId) : typedvalueattr.data;

            int size = (int) (Carbon.getDip(getContext()) * 24);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

            SVG svg3 = SVG.getFromResource(context, R.raw.carbon_dropdown);
            Canvas canvas = new Canvas(bitmap);
            svg3.setDocumentWidth(bitmap.getWidth());
            svg3.setDocumentHeight(bitmap.getHeight());
            svg3.renderToCanvas(canvas);

            BitmapDrawable dropdown = new BitmapDrawable(bitmap);
            dropdown.setBounds(0, 0, size, size);
            dropdown.setAlpha(Color.alpha(color));
            dropdown.setColorFilter(new LightingColorFilter(0, color));
            setCompoundDrawables(null, null, dropdown, null);
        } catch (SVGParseException e) {

        }

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();


    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.stateToSave = this.isShowingPopup ? 1 : 0;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        this.isShowingPopup = ss.stateToSave > 0;
    }

    static class SavedState extends BaseSavedState {
        int stateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.stateToSave = in.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.stateToSave);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


    // -------------------------------
    // tint
    // -------------------------------

    @Override
    public void setTint(ColorStateList list) {
        super.setTint(list);
    }
}
