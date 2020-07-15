package com.byonchat.android.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.byonchat.android.R;
import com.byonchat.android.utils.Ping;
import com.byonchat.android.utils.Utility;

import java.io.IOException;
import java.net.InetAddress;

public class ToolbarWithIndicator extends Toolbar {
    private static final double WIDTH_PERCENTAGE = 0.8;
    ImageView mTitle = null;
    String path = "forward.byonchat.com";
    Boolean scanCheck = true;
    Boolean submitCheck = true;
    Activity activity;

    public ToolbarWithIndicator(Context context) {
        this(context, null);
    }

    public ToolbarWithIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public ToolbarWithIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = this.getChildAt(i);
                if (view instanceof RelativeLayout) {
                    int width = getMeasuredWidth();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.width = (int) (width * WIDTH_PERCENTAGE);
                    view.setLayoutParams(layoutParams);
                    break;
                }
                if (view instanceof ImageView) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mTitle == null) {
                                mTitle = (ImageView) view.findViewById(R.id.toolbar_indi);
                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_gagal);
                                mTitle.setImageDrawable(myDrawable);
                                submitCheck = false;
                            }
                        }
                    });
                    break;
                }
            }
        } catch (Exception e) {

        }
    }

    public void stopScan() {
        scanCheck = false;
    }

    public Boolean getSubmitCheck() {
        return submitCheck;
    }

    public void startScan(String path, Activity sx) {
        activity = sx;
        new RetrieveFeedTask().execute(path);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        try {

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = this.getChildAt(i);
                if (view instanceof ImageView) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mTitle == null) {
                                mTitle = (ImageView) view.findViewById(R.id.toolbar_indi);
                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_gagal);
                                mTitle.setImageDrawable(myDrawable);
                                submitCheck = false;
                            }
                        }
                    });
                    break;
                }

            }
        } catch (Exception e) {

        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    final InetAddress dest = InetAddress.getByName(urls[0]);
                    final Ping ping = new Ping(dest, new Ping.PingListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onPing(final long timeMs, int count) {
                            if (timeMs > 0 && timeMs < 1000) {
                                if (mTitle != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mTitle != null) {
                                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_success_toolbar);
                                                mTitle.setImageDrawable(myDrawable);
                                                submitCheck = true;
                                            }
                                        }
                                    });

                                }
                            } else if (timeMs == 1 || timeMs >= 1000) {
                                if (mTitle != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mTitle != null) {
                                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_warning);
                                                mTitle.setImageDrawable(myDrawable);
                                                submitCheck = true;
                                            }
                                        }
                                    });

                                }
                            } else {
                                if (mTitle != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mTitle != null) {
                                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_gagal);
                                                mTitle.setImageDrawable(myDrawable);
                                                submitCheck = false;
                                            }
                                        }
                                    });

                                }
                            }
                        }

                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onPingException(final Exception e, final int count) {
                            if (isConnectedToThisServer(urls[0])) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mTitle != null) {
                                            Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_success_toolbar);
                                            mTitle.setImageDrawable(myDrawable);
                                            submitCheck = true;
                                        }
                                    }
                                });

                            } else {
                                if (mTitle != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mTitle != null) {
                                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_gagal);
                                                mTitle.setImageDrawable(myDrawable);
                                                submitCheck = false;
                                            }
                                        }
                                    });

                                }
                            }

                        }

                    });

                    ping.run();
                } else {
                    if (isConnectedToThisServer(urls[0])) {
                        if (mTitle != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mTitle != null) {
                                        Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_success_toolbar);
                                        mTitle.setImageDrawable(myDrawable);
                                        submitCheck = true;
                                    }
                                }
                            });

                        }
                    } else {
                        if (mTitle != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mTitle != null) {
                                        Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_gagal);
                                        mTitle.setImageDrawable(myDrawable);
                                        submitCheck = false;
                                    }
                                }
                            });

                        }
                    }

                }


            } catch (Exception e) {
                if (mTitle != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mTitle != null) {
                                Drawable myDrawable = AppCompatResources.getDrawable(activity.getApplicationContext(), R.drawable.ic_gagal);
                                mTitle.setImageDrawable(myDrawable);
                                submitCheck = false;
                            }
                        }
                    });

                }
                return "";
            }

            return "";
        }

        protected void onPostExecute(String feed) {
            if (scanCheck) {
                new RetrieveFeedTask().execute(path);
            }

        }
    }

    public boolean isConnectedToThisServer(String host) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int exitValue = ipProcess.waitFor();

            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


}