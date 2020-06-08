package com.byonchat.android.smsSolders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivitySMS extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(WelcomeActivitySMS.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");


        dialog.show();
        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();
        Cursor cursor = db.getSingleContact(23);
        if (cursor.getCount() > 0) {
            launchHomeScreen(1);
            finish();
        } else {

            RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                @Override
                public void onTaskDone(String key) {
                    new Refresh(WelcomeActivitySMS.this).execute("https://bb.byonchat.com/smsgateway/kuota.php", key);
                }
            }, getApplicationContext());
            testAsyncTask.execute();

        }
        cursor.close();
        db.close();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        addBottomDots(0);
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen(0);
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen(int i) {
        if (i == 1) {
            startActivity(new Intent(WelcomeActivitySMS.this, HomeSMSSolders.class));
            finish();
        } else {
            startActivity(new Intent(WelcomeActivitySMS.this, RegisterSMSActivity.class));
            finish();
        }

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private class Refresh extends AsyncTask<String, String, String> {
        String error = "";
        private Activity activity;
        private Context context;

        public Refresh(Activity activity) {
            this.activity = activity;
            context = activity;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (error.length() > 0) {
                if (error.equalsIgnoreCase("404")) {
                    IntervalDB db = new IntervalDB(getApplicationContext());
                    db.open();
                    Cursor cursor = db.getSingleContact(23);
                    if (cursor.getCount() > 0) {
                        db.deleteContact(23);
                    }
                    cursor.close();
                    db.close();

                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }

            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String kye) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpPost httppost = new HttpPost(valueIWantToSend);

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", kye));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(data);
                        String buka = jObject.getString("code");
                        if (buka.equalsIgnoreCase("404")) {
                            error = buka;
                        } else {
                            String desc = jObject.getString("description");
                            if (buka.equalsIgnoreCase("500")) {
                                error = desc;
                            } else if (buka.equalsIgnoreCase("200")) {
                                launchHomeScreen(1);
                            } else {
                                error = desc;
                            }
                        }
                    } catch (Exception e) {
                        error = "Tolong periksa koneksi internet.";
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                WelcomeActivitySMS.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClientProtocolException e) {
                WelcomeActivitySMS.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                WelcomeActivitySMS.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
