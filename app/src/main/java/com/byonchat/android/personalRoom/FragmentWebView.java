package com.byonchat.android.personalRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utilVideoWebView.VideoEnabledWebChromeClient;
import com.byonchat.android.utilVideoWebView.VideoEnabledWebView;

import java.io.File;


@SuppressLint("ValidFragment")
public class FragmentWebView extends Fragment {
    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private Boolean personal;
    private MessengerDatabaseHelper messengerHelper;
    private Activity mContext;

    public FragmentWebView(Activity ctx) {
        mContext = ctx;
    }

    public static FragmentWebView newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag, Activity act) {
        FragmentWebView fragmentRoomTask = new FragmentWebView(act);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        args.putBoolean("fla", flag);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("aa");
        urlTembak = getArguments().getString("bb");
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        myContact = getArguments().getString("ee");
        color = getArguments().getString("col");
        personal = getArguments().getBoolean("fla");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("aa", title);
        outState.putString("bb", urlTembak);
        outState.putString("cc", username);
        outState.putString("dd", idRoomTab);
        outState.putString("ee", myContact);
        outState.putString("col", color);
        outState.putBoolean("fla", personal);
        super.onSaveInstanceState(outState);
    }

    public final static String KEY_LINK_LOAD = "LINK_LOAD_WEB";
    public final static String KEY_COLOR = "COLOR_WEB";
    ProgressBar progressBarWeb;
    public final static String URL_ADD_ROOM = "https://"+ MessengerConnectionService.HTTP_SERVER+"/room/";
    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;

    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    protected ProgressDialog pdialog;

    BotListDB botListDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }
        View sss = inflater.inflate(R.layout.activity_web_view, container, false);

        /*Location aa = new Location("point A");
        aa.setLatitude(-6.2799206);
        aa.setLongitude(106.7150757);
        Location bb = new Location("point B");
        bb.setLatitude(-6.2799151);
        bb.setLongitude(106.7149063);
        Toast.makeText(mContext,Utility.distanceInMeters(aa,bb)+"   --   "+Utility.sideRange(aa,bb,500),Toast.LENGTH_SHORT).show();*/

        if (pdialog == null) {
            pdialog = new ProgressDialog(mContext);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Loading");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        if(botListDB==null){
            botListDB =  BotListDB.getInstance(mContext);
        }
        progressBarWeb = (ProgressBar) sss.findViewById(R.id.progressBarWeb);
        webView = (VideoEnabledWebView) sss.findViewById(R.id.webView);

        final MyJavaScriptInterface myJavaScriptInterface
                = new MyJavaScriptInterface(mContext);
        webView.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
        webView.getSettings().setJavaScriptEnabled(true);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = sss.findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) sss.findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = inflater.inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }

            // openFileChooser for Android 3.0+
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                Toast.makeText(mContext, "Exception:0",
                        Toast.LENGTH_LONG).show();
                // Update message
                mUploadMessage = uploadMsg;
                String fileType = ".jpg";
                String type = "IMAGE";
                if(acceptType.contains("video")){
                    fileType = ".mp4";
                    type = "VIDEO";
                }

                try {

                    // Create AndroidExampleFolder at sdcard

                    File imageStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES)
                            , "AndroidExampleFolder");

                    if (!imageStorageDir.exists()) {
                        // Create AndroidExampleFolder at sdcard
                        imageStorageDir.mkdirs();
                    }

                    // Create camera captured image file path and name
                    File file = new File(
                            imageStorageDir + File.separator + "UPLOAD_"
                                    + String.valueOf(System.currentTimeMillis())
                                    + fileType);

                    mCapturedImageURI = Uri.fromFile(file);

                    // Camera capture image intent
                    Intent captureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);

                    if (type.equalsIgnoreCase("VIDEO")){
                        captureIntent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE);
                    }

                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType(acceptType);

                    // Create file chooser intent
                    Intent chooserIntent = Intent.createChooser(i, type +" Chooser");

                    // Set camera intent to file chooser
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                            , new Parcelable[]{captureIntent});

                    // On select image call onActivityResult method of activity
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception:" + e,
                            Toast.LENGTH_LONG).show();
                }

            }

            // openFileChooser for Android < 3.0
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Toast.makeText(mContext, "Exception:1",
                        Toast.LENGTH_LONG).show();
                openFileChooser(uploadMsg, "");
            }

            //openFileChooser for other Android versions
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType,
                                        String capture) {
                Toast.makeText(mContext, "Exception:2",
                        Toast.LENGTH_LONG).show();
                openFileChooser(uploadMsg, acceptType);
            }

            // Work on Android 4.4.2 Zenfone 5
            @SuppressWarnings("unused")
            public void showFileChooser(ValueCallback<Uri> filePathCallback,
                                        String acceptType, boolean paramBoolean){
                Toast.makeText(mContext, "Exception:3",
                        Toast.LENGTH_LONG).show();
                // TODO Auto-generated method stub
                openFileChooser(filePathCallback, acceptType);
            }

            @SuppressWarnings("unused")
            public void showFileChooser(ValueCallback<Uri> filePathCallback,
                                        String acceptType) {
                Toast.makeText(mContext, "Exception:4",
                        Toast.LENGTH_LONG).show();
                openFileChooser(filePathCallback, acceptType);
                // TODO Auto-generated method stub
            }


            // The webPage has 2 filechoosers and will send a
            // console message informing what action to perform,
            // taking a photo or updating the file

            public boolean onConsoleMessage(ConsoleMessage cm) {

                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

            }


        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = mContext.getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    mContext.getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                } else {
                    WindowManager.LayoutParams attrs = mContext.getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    mContext.getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });

        webView.setWebChromeClient(webChromeClient);

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        webView.loadUrl(urlTembak);
        Log.w("URL Tujuan",urlTembak);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                progressBarWeb.setVisibility(View.GONE);
                try {
                    webView.stopLoading();
                } catch (Exception e) {
                }
                try {
                    webView.clearView();
                } catch (Exception e) {
                }
                sss.findViewById(R.id.img_no_internet).setVisibility(View.VISIBLE);

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);
                int icon = android.R.drawable.ic_dialog_alert;
                alertbox.setIcon(icon);
                alertbox.setMessage("No Internet Connection");
                alertbox.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        getActivity().finish();
                    }
                });
                alertbox.show();
                super.onReceivedError(webView, errorCode, description, failingUrl);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBarWeb.setVisibility(View.GONE);
            }
        });


        return sss;
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.action_next_close, menu);
         configureActionItem(menu);
         return super.onCreateOptionsMenu(menu);
     }
 */
    private void configureActionItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_action_close);
        ImageButton btn = (ImageButton) MenuItemCompat.getActionView(item).findViewById(
                R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if(mWvUrl.canGoBack()){
                    mWvUrl.goBack();
                }else{*/
                getActivity().finish();
                //  }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!webChromeClient.onBackPressed()) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        getActivity().finish();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MyJavaScriptInterface {
        Context mContext;

        MyJavaScriptInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void tutupJendela(){
            getActivity().finish();
        }
        @JavascriptInterface
        public void kembaliJendela(){
            if (!webChromeClient.onBackPressed()) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    getActivity().finish();
                }
            }
        }

        @JavascriptInterface
        public void bukaPercakapan(String jid){
            Intent intent = new Intent(mContext, ConversationActivity.class);
            intent.putExtra(ConversationActivity.KEY_JABBER_ID, jid);
            startActivity(intent);
        }

        @JavascriptInterface
        public void addRoomC(final String jid,String name,String desc){
            ContactBot a  = new ContactBot(jid,desc,name,"","");
            botListDB.insertScrDetails(a);
        }

        @JavascriptInterface
        public void addRoomP(final String jid){
//blm ada pikirna
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {

        if(requestCode==FILECHOOSER_RESULTCODE){
            if (null == this.mUploadMessage) {
                return;
            }

            Uri result=null;

            try{
                if (resultCode != mContext.RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            } catch(Exception e) {
                Toast.makeText(mContext, "activity :"+e,
                        Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

    }

}