package com.byonchat.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.encodeToBase64;

public class CaptureSignature extends Activity { 
 
    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign, mCancel;
   // public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
   // File mypath;
 
    private String uniqueId;
    private EditText yourName;
 
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signature);

        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature);
        mClear = (Button)findViewById(R.id.clear);
        mGetSign = (Button)findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button)findViewById(R.id.cancel);
        mView = mContent;
 
        yourName = (EditText) findViewById(R.id.yourName);
 
        mClear.setOnClickListener(new View.OnClickListener()
        {        
            public void onClick(View v) 
            {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });
 
        mGetSign.setOnClickListener(new View.OnClickListener()
        {        
            public void onClick(View v) 
            {
                Log.v("log_tag", "Panel Saved");
                boolean error = captureSignature();
                if(!error){
                    mView.setDrawingCacheEnabled(true);
                    Bundle b = new Bundle();
                    b.putString("status", mSignature.save(mView));
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK,intent);   
                    finish();
                }
            }
        });
 
        mCancel.setOnClickListener(new View.OnClickListener()
        {        
            public void onClick(View v) 
            {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
 
    private boolean captureSignature() {
 
        boolean error = false;
        String errorMessage = "";
 
        if(error){
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }
 
        return error;
    }
 

    public class signature extends View 
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
 
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
 
        public signature(Context context, AttributeSet attrs) 
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }
 
        public String save(View v)
        {
            String myBase64Image = "";
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if(mBitmap == null)
            {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
            }
            Canvas canvas = new Canvas(mBitmap);
            try
            {
                v.draw(canvas);
                myBase64Image = encodeToBase64(mBitmap, Bitmap.CompressFormat.PNG, 80);

            }
            catch(Exception e) 
            { 
                Log.v("log_tag", e.toString()); 
            }
            return myBase64Image;
        }
 
        public void clear() 
        {
            path.reset();
            invalidate();
        }
 
        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawPath(path, paint);
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);
 
            switch (event.getAction()) 
            {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;
 
            case MotionEvent.ACTION_MOVE:
 
            case MotionEvent.ACTION_UP:
 
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) 
                {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                path.lineTo(eventX, eventY);
                break;
 
            default:
                debug("Ignored touch event: " + event.toString());
                return false;
            }
 
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
 
            lastTouchX = eventX;
            lastTouchY = eventY;
 
            return true;
        }
 
        private void debug(String string){
        }
 
        private void expandDirtyRect(float historicalX, float historicalY) 
        {
            if (historicalX < dirtyRect.left) 
            {
                dirtyRect.left = historicalX;
            } 
            else if (historicalX > dirtyRect.right) 
            {
                dirtyRect.right = historicalX;
            }
 
            if (historicalY < dirtyRect.top) 
            {
                dirtyRect.top = historicalY;
            } 
            else if (historicalY > dirtyRect.bottom) 
            {
                dirtyRect.bottom = historicalY;
            }
        }
 
        private void resetDirtyRect(float eventX, float eventY) 
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}