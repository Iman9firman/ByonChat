package com.honda.android.communication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by imanfirmansyah on 1/3/17.
 */
public class DummyActivity
    extends Activity
    {
        @Override
        public void onCreate(Bundle icicle ) {
        super.onCreate( icicle );
        finish();
    }
}
