package com.byonchat.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.BlockListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.ColorUtils;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;

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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private Contact contact;
    private MessengerDatabaseHelper dbhelper;
    private ImageView profile_id;
    public final static String REFERENCE_MAIN = "main";
    public final static String REFERENCE_CONVERSATION = "conversation";
    public final static String REFERENCE_GROUP = "group";
    public final static String KEY_JABBER_ID = "com.byonchat.android.ViewProfileActivity.JABBER_ID";
    public final static String KEY_REFERENCE = "com.byonchat.android.ViewProfileActivity.REFERENCE";
    public final static String URL_ADD_BLOCK = "https://" + MessengerConnectionService.HTTP_SERVER + "/blocklist/add.php";

    Context context;

    private TextView txtStatus;
    private TextView txtMobile;
    private String reference;
    private ImageButton btnCall;
    private ImageButton btnChat;
    protected ProgressDialog pdialog;
    private Button btnBlock;
    private BlockListDB blockListDB;

    String iconsStoragePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 20) {
            setTheme(R.style.HeaderTransparent);
        }
        setContentView(R.layout.activity_view_profile);

        String jabberId = getIntent().getStringExtra(KEY_JABBER_ID);
        reference = getIntent().getStringExtra(KEY_REFERENCE);
        context = this;
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(this);
        }
        contact = dbhelper.getContact(jabberId);
        if (contact == null) {
            contact = new Contact("+" + jabberId, jabberId, "");
        }

        if (pdialog == null) {
            pdialog = new ProgressDialog(this);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment ..");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        profile_id = (ImageView) findViewById(R.id.profile_id);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(contact.getName());


        btnCall = (ImageButton) findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+" + contact.getJabberId()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                }
            }
        });

        btnChat = (ImageButton) findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, contact.getJabberId());
                startActivity(intent);
            }
        });

        btnBlock = (Button) findViewById(R.id.btnBlock);
        ArrayList<String> listblock = new ArrayList<String>();

        blockListDB = new BlockListDB(this);
        blockListDB.open();
        listblock = blockListDB.getBlockList();
        blockListDB.close();

        String name = contact.getJabberId();
        boolean block = false;

        for (String a : listblock) {
            if (name.equalsIgnoreCase(a)) {
                block = true;
            }
        }

        if (block) {
            btnBlock.setText("Unblock");
        }

        btnBlock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(ViewProfileActivity.this);
                alertbox.setMessage("Are you sure you want to " + btnBlock.getText().toString() + " this contact?");
                alertbox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                            pdialog.show();
                            String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                            if (key.equalsIgnoreCase("null")) {
                                Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();
                            } else {
                                try {
                                    new blockRequest(getApplicationContext()).execute(key);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alertbox.show();
            }
        });


        txtStatus = (TextView) findViewById(R.id.textProfileDataStatus);
        try {
            JSONObject json = new JSONObject(contact.getStatus());
            String aa = json.getString("status") != null ? json.getString("status") : "I love byonchat";
            String text = Html.fromHtml(URLDecoder.decode(aa)).toString();
            if (text.contains("<")) {
                text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
            }
            txtStatus.setText(Message.parsedMessageText(text));
        } catch (Exception e) {
            String aa = contact.getStatus() != null ? contact.getStatus() : "I love byonchat";
            String text = Html.fromHtml(URLDecoder.decode(aa)).toString();
            if (text.contains("<")) {
                text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
            }
            txtStatus.setText(Message.parsedMessageText(text));
        }

        txtMobile = (TextView) findViewById(R.id.textProfileDataMobile);
        txtMobile.setText("+" + Utility.formatPhoneNumber(contact.getJabberId()));


        iconsStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/ByonChat/Photo Profile/";
        final File sdIconStorageDir = new File(iconsStoragePath);
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }

        iconsStoragePath += jabberId + ".jpg";
        final File yourFile = new File(sdIconStorageDir, jabberId + ".jpg");

        File photoFile = getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(contact.getJabberId()));
        Drawable d = getResources().getDrawable(R.drawable.ic_no_photo);
        if (photoFile.exists()) {
            d = Drawable.createFromPath(photoFile.getAbsolutePath());
            profile_id.setImageDrawable(d);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
            dynamicToolbarColor(bitmap);
        }

        if (yourFile.exists()) {

            String signature = new Validations().getInstance(this).getSignatureProfilePicture(contact.getJabberId(), dbhelper);
            Glide.with(this).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + contact.getJabberId() + ".jpg").asBitmap()
                    .signature(new StringSignature(signature))
                    .placeholder(Drawable.createFromPath(iconsStoragePath))
                    .into(new BitmapImageViewTarget(profile_id) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            profile_id.setImageBitmap(resource);
                            try {
                                FileOutputStream fos = new FileOutputStream(yourFile);
                                resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.close();
                            } catch (FileNotFoundException e) {
                            } catch (IOException e) {
                            }

                        }
                    });
        } else {
            String signature = new Validations().getInstance(this).getSignatureProfilePicture(contact.getJabberId(), dbhelper);
            Glide.with(this).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + contact.getJabberId() + ".jpg").asBitmap()
                    .signature(new StringSignature(signature))
                    .placeholder(d)
                    .animate(R.anim.fade_in_sort)
                    .into(new BitmapImageViewTarget(profile_id) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            profile_id.setImageBitmap(resource);
                            try {
                                FileOutputStream fos = new FileOutputStream(yourFile);
                                resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.close();
                            } catch (FileNotFoundException e) {
                            } catch (IOException e) {
                            }
                        }
                    });
        }

        profile_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, FullScreenUpdateProfileActivity.class);
                intent.putExtra(FullScreenUpdateProfileActivity.JAB_ID, contact.getJabberId());
                intent.putExtra(FullScreenUpdateProfileActivity.PATH, iconsStoragePath);
                startActivity(intent);
            }
        });

        toolbarTextAppernce();
    }

    private void dynamicToolbarColor(Bitmap bb) {

        final int color = ColorUtils.getDominantColor1(bb);
        Palette.from(bb).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(color));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(color));
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;
                if (REFERENCE_MAIN.equals(reference)) {
                    intent = new Intent(this, MainActivity.class);
                } else if (REFERENCE_GROUP.equals(reference)) {
                    finish();
                    return true;
                } else {
                    intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID,
                            contact.getJabberId());
                    intent.putExtra(ConversationActivity.KEY_TITLE,
                            contact.getName());
                    intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                            ConversationActivity.CONVERSATION_TYPE_PRIVATE);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class blockRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = HttpHelper.createHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        private MessengerDatabaseHelper messengerHelper;
        String status = "";
        String action;


        public blockRequest(Context context) throws Exception {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... key) {
            try {

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                }

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                Contact contact = messengerHelper.getMyContact();

                String tambah = "0";
                if (btnBlock.getText().toString().equalsIgnoreCase("block")) tambah = "1";

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("tambah", tambah));
                nameValuePairs.add(new BasicNameValuePair("blockme", contact.getJabberId()));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_ADD_BLOCK);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();

                    if (!content.startsWith("<")) {
                        JSONObject jObject = new JSONObject(content);
                        JSONObject menuitemArray = jObject.getJSONObject("blockrequest");
                        status = menuitemArray.getString("status").toString();
                        action = menuitemArray.getString("action").toString();
                    } else {
                        status = "error";
                    }

                } else {
                    //Closes the connection.
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        pdialog.show();
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        } else {
                            try {
                                new blockRequest(mContext).execute(key);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (status.equalsIgnoreCase("done")) {
                    if (action.equalsIgnoreCase("add")) {
                        blockListDB.open();
                        blockListDB.insertListBlock(contact.getJabberId());
                        blockListDB.close();
                        btnBlock.setText("Unblock");
                    } else {
                        blockListDB.open();
                        blockListDB.deleteListBlock(contact.getJabberId());
                        blockListDB.close();
                        btnBlock.setText("Block");
                    }
                } else {
                }
            }
        }
    }
}
