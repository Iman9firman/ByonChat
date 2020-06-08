package com.byonchat.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.list.utilLoadImage.ImageLoaderRefresh;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MembersDB;
import com.byonchat.android.provider.MembersDetail;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemberDetailActivity extends AppCompatActivity implements  Animation.AnimationListener {
    public final static String KEY_MEMBERS_ID = "MEMBERS_ID";
    public final static String KEY_MEMBERS_NAME = "MEMBERS_NAME";
    public final static String KEY_MEMBERS_COLOR = "MEMBERS_COLOR";
    public final static String URL_GET_DETAIL_MEMBERS = "https://"+ MessengerConnectionService.HTTP_SERVER+"/memberships/detail_kartu.php";
    private MessengerDatabaseHelper dbhelper;
    ProgressBar progressBar;
    Contact contact;
    Context context = this;
    String id_card;
    String card_name;
    String card_color;
    String barcode;
    TextView title;
    TextView desc;
    TextView txtPromo;
    TextView voucher;
    TextView sync;
    LinearLayout mainLayout;
    RelativeLayout layoutVoucher;
    Target imgViewPhoto;
    ImageView imgViewBarcode;
    TouchImageView imgViewCard;
    public ImageLoaderRefresh imgProfile;
    public ImageLoader imgBarcode;
    public ImageLoaderLarge imgCard;
    private boolean isBackOfCardShowing = true;
    private Animation animation1;
    private Animation animation2;
    String linkPromo = "";
    String linkContactUs = "";
    MembersDB membersDB;
    private Context mContext = this;
    String encodedDesc;
    String judulPromo;
    String promo;
    String foto;
    String bonus;
    String judulBonus;
    String room;
    String time;
    Activity activity = this;
    ImageButton btn_refresh;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_detail_activity);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


  /*cek Version tidak dibutuhkan material design
        if (!new Validations().getInstance(getApplicationContext()).getContentValidation(16).equalsIgnoreCase(getResources().getString(R.string.version))) {
            finish();
            Intent i = new Intent();
            i.setClass(getApplicationContext(), SplashScreen.class);
            startActivity(i);
        }*/

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(context);
        }

        if(membersDB == null){
            membersDB  = new MembersDB(mContext);
        }

        contact = dbhelper.getMyContact();
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        layoutVoucher = (RelativeLayout) findViewById(R.id.layoutVoucher);
        title = (TextView) findViewById(R.id.title);
        desc = (TextView) findViewById(R.id.desc);
        txtPromo = (TextView) findViewById(R.id.txtPromo);
        voucher = (TextView) findViewById(R.id.voucher);
        sync = (TextView) findViewById(R.id.sync);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgViewPhoto = (Target) findViewById(R.id.photo);
        imgViewBarcode = (ImageView) findViewById(R.id.barcode);
        imgViewCard = (TouchImageView) findViewById(R.id.imageViewCard);
        btn_refresh = (ImageButton) findViewById(R.id.btn_refresh);

        id_card = getIntent().getStringExtra(KEY_MEMBERS_ID);
        card_name = getIntent().getStringExtra(KEY_MEMBERS_NAME);
        card_color = getIntent().getStringExtra(KEY_MEMBERS_COLOR);
        title.setText(card_name);
        title.setTextColor(Color.parseColor(card_color));
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).headerCostume(getWindow(),card_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(card_color));
        }

        imgProfile = new ImageLoaderRefresh(context);
        imgBarcode = new ImageLoader(context);
        imgCard = new ImageLoaderLarge(context,true);
        membersDB.open();
        mainLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Cursor cursor = membersDB.getDetailMembers(id_card);
        if(cursor.getCount()>0){
            String time = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_TIME));
            if(new Validations().getInstance(context).getShowOneDay(time)==0){
                mainLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                fillContent();
            }else {
                if(NetworkInternetConnectionStatus.getInstance(context).isOnline(context)){
                    Thread splashTread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(200);
                                }

                            } catch (InterruptedException e) {
                            } finally {
                                String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                                if (key.equalsIgnoreCase("null")){
                                    mainLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    fillContent();
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                                }else{
                                    new requestDetailMembers(context).execute(key, id_card);
                                }
                            }
                        }
                    };

                    splashTread.start();
                }else{
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    mainLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    fillContent();
                }
            }
        }else{
            request();
        }
        cursor.close();
        membersDB.close();

        animation1 = AnimationUtils.loadAnimation(context, R.anim.to_middle);
        animation1.setAnimationListener(MemberDetailActivity.this);
        animation2 = AnimationUtils.loadAnimation(context, R.anim.from_middle);
        animation2.setAnimationListener(MemberDetailActivity.this);
        imgCard.DisplayImage("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/kartu/" + card_name.toLowerCase().replace(" ", "_") + "_belakang.png", imgViewCard);
        imgCard.DisplayImage("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/kartu/" + card_name.toLowerCase().replace(" ", "_") + "_depan.png", imgViewCard);
        findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imgViewCard.clearAnimation();
                imgViewCard.setAnimation(animation1);
                imgViewCard.startAnimation(animation1);
            }
        });

        imgViewCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String card = "https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/kartu/" + card_name.toLowerCase().replace(" ", "_") + "_depan.png";
                if (!isBackOfCardShowing) {
                    card = "https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/kartu/" + card_name.toLowerCase().replace(" ", "_") + "_belakang.png";
                }

                Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                intent.putExtra(ZoomImageViewActivity.KEY_FILE, card);
                startActivity(intent);
            }
        });

        imgViewBarcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                intent.putExtra(ZoomImageViewActivity.KEY_FILE, barcode);
                startActivity(intent);
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    mainLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    request();
                } else {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });


       findViewById(R.id.promo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    Intent intent = new Intent(context, WebViewByonActivity.class);
                    intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, linkPromo);
                    intent.putExtra(WebViewByonActivity.KEY_COLOR, card_color);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.contactUs).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!linkContactUs.equalsIgnoreCase("")){
                    if(NetworkInternetConnectionStatus.getInstance(context).isOnline(context)){
                        Intent intent = new Intent(context, ConversationActivity.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, linkContactUs);
                        startActivity(intent);
                    }else{
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_save_menu, menu);
        configureActionItem(menu);
        return true;
    }

    private void configureActionItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_action_save);
        Button btn = (Button) MenuItemCompat.getActionView(item).findViewById(
                R.id.buttonAbSave);
        btn.setCompoundDrawables(null,null,null,null);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setText("Refresh");
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    mainLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    request();
                } else {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    @Override
    protected void onPause()
    {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
    @Override
    protected void onResume()
    {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID_CARD);
        super.onResume();
    }

    public void request(){
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    Thread splashTread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(200);
                                }

                            } catch (InterruptedException e) {
                            } finally {
                                String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                                if (key.equalsIgnoreCase("null")) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                                } else {
                                    new requestDetailMembers(context).execute(key, id_card);
                                }
                            }
                        }
                    };

                    splashTread.start();
                } else {
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation==animation1) {
            if (isBackOfCardShowing) {
                imgCard.DisplayImage("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/kartu/" + card_name.toLowerCase().replace(" ", "_") + "_belakang.png", imgViewCard);
            } else {
                imgCard.DisplayImage("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/kartu/" + card_name.toLowerCase().replace(" ", "_") + "_depan.png", imgViewCard);
            }
            imgViewCard.clearAnimation();
            imgViewCard.setAnimation(animation2);
            imgViewCard.startAnimation(animation2);
        } else {
            isBackOfCardShowing=!isBackOfCardShowing;
            findViewById(R.id.btnRotate).setEnabled(true);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    class requestDetailMembers extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public requestDetailMembers(Context context) {

            this.mContext = context;
            membersDB.open();
            membersDB.deleteDetailMembers(id_card);
            membersDB.close();
        }


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key",key[0]));
                nameValuePairs.add(new BasicNameValuePair("id_kartu",key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_DETAIL_MEMBERS);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                HttpResponse response = httpClient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    JSONObject result = new JSONObject(content);
                    String des = result.getString("deskripsi").toString();
                    barcode = result.getString("barcode").toString();
                    promo = result.getString("url_promo").toString();
                    judulPromo = result.getString("judul_promo").toString();
                    bonus = result.getString("bonus").toString();
                    judulBonus = result.getString("judul_bonus").toString();
                    foto = result.getString("foto").toString();
                    room = result.getString("nama_kamar").toString();
                    encodedDesc = URLDecoder.decode(des, "UTF-8");
                    membersDB.open();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    String time_str = dateFormat.format(cal.getTime());
                    MembersDetail membersDetail = new MembersDetail(Long.parseLong(id_card),time_str,barcode,promo,judulPromo,bonus,judulBonus,room,encodedDesc,foto);
                    membersDB.insertMembersDetail(membersDetail);
                    membersDB.close();

                } else {
                    //Closes the connection.
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content =  e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                        if (key.equalsIgnoreCase("null")){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        }else{
                            new requestDetailMembers(mContext).execute(key,id_card);
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(mContext, R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                fillContent();
            }
        }

    }

    public void fillContent(){
        membersDB.open();
        Cursor cursor = membersDB.getDetailMembers(id_card);
        if(cursor.getCount()>0){
            judulPromo = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_J_PROMO));
            encodedDesc = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_DESC));
            judulBonus = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_J_BONUS));
            bonus = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_BONUS));
            time = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_TIME));
            promo = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_PROMO));
            room = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_ROOM));
            foto = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_FOTO));
            barcode = cursor.getString(cursor.getColumnIndexOrThrow(MembersDB.MEMBERS_DETAIL_BARCODE));
            mainLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            String[] sDB = time.split(" ");
            sync.setText("(last sync : "+time);
            desc.setText(Html.fromHtml(encodedDesc));
            txtPromo.setText(judulPromo);
            voucher.setText(judulBonus + " " + bonus);
            linkPromo = promo+"bc_id="+contact.getJabberId();
            linkContactUs = room;
            if (judulBonus.equalsIgnoreCase("")||judulBonus==null){
                layoutVoucher.setVisibility(View.GONE);
            }
            mainLayout.setVisibility(View.VISIBLE);
            Picasso.with(this).load(foto)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imgViewPhoto);
           /* if(clear){
                imgProfile.clearCache();
                imgBarcode.clearCache();
                imgCard.clearCache();
            }*/
            imgBarcode.DisplayImage(barcode, imgViewBarcode);

        }else{
            request();
        }
        cursor.close();
        membersDB.close();
    }

}
