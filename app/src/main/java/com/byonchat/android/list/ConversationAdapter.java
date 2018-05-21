package com.byonchat.android.list;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConfirmationSendFile;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FragmentDinamicRoom.LoginRoomActivity;
import com.byonchat.android.LoadingGetTabRoomActivity;
import com.byonchat.android.R;
import com.byonchat.android.NewSelectContactActivity;
import com.byonchat.android.WebViewByonActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.PhotoSortrActivity;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Files;
import com.byonchat.android.provider.FilesDatabaseHelper;
import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.ChatImageRounded;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.GalleryDisplayHandler;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.PicassoOwnCache;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.RequestUploadSite;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.widget.CircleProgressBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import static com.byonchat.android.utils.PicassoOwnCache.cacheDir;

public class ConversationAdapter extends BaseAdapter {
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    private static final int INFO_VIEW = 0;
    private static final int RIGHT_VIEW = 1;
    private static final int LEFT_VIEW = 2;
    private static final int FIRST_ROW_VIEW = 3;
    private static final int LEFT_GROUP_VIEW = 4;
    private static final int LEFT_VIEW_OPTION = 5;
    private static final int RIGHT_VIEW_RECALL = 6;

    private List items;
    private String sourceAddr;
    private String destinationAddr;
    private Activity activity;
    private Context context;
    private View.OnClickListener viewClickListener;
    PicassoOwnCache cache;

    public ConversationAdapter(Activity activity, Context context, String source,
                               String destination, List items) {
        sourceAddr = source;
        destinationAddr = destination;
        this.items = items;
        this.activity = activity;
        this.context = context;
    }

    public void add(List items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void refreshList() {
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setBtnClickListener(View.OnClickListener btnClickListener) {
        this.viewClickListener = btnClickListener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == RIGHT_VIEW) {
            return createRightView(position, convertView, parent);
        } else if (viewType == LEFT_VIEW) {
            return createLeftView(position, convertView, parent);
        } else if (viewType == INFO_VIEW) {
            return createInfoView(position, convertView, parent);
        } else if (viewType == LEFT_GROUP_VIEW) {
            return createListView(position, convertView, parent);
        } else if (viewType == LEFT_VIEW_OPTION) {
            return createLeftViewOption(position, convertView, parent);
        } else if (viewType == RIGHT_VIEW_RECALL) {
            return createRightViewOption(position, convertView, parent);
        } else {
            return createFirstView(position, convertView, parent);
        }
    }

    private View createFirstView(int position, View convertView,
                                 ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_load_earlier, parent,
                    false);
        }
        ConversationLoadEarlier holder = (ConversationLoadEarlier) row.getTag();
        if (holder == null) {
            holder = new ConversationLoadEarlier(row);
            row.setTag(holder);
        }

        return row;
    }

    private void displayConversation(final ConversationMessageHolder holder,
                                     final Message data, final int position, final String form) {

        if (Message.TYPE_IMAGE.equals(data.getType())
                || Message.TYPE_LOC.equals(data.getType())
                || Message.TYPE_VIDEO.equals(data.getType())) {


            holder.imagePlay.setVisibility(View.GONE);
            holder.imageMessage.setImageDrawable(null);
            holder.progressBarImage.setVisibility(View.GONE);
            holder.buttonDownload.setVisibility(View.GONE);
            holder.imageMessage.setOnClickListener(null);

            if (Message.TYPE_IMAGE.equals(data.getType())) {
                String urlFile = "";
                String caption = "";
                String fileServer = "";

                if (isJSONValid(data.getMessage())) {
                    Log.w("senid", data.getMessage());
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(data.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("s");
                            caption = jObject.getString("c");
                            if (urlFile.equalsIgnoreCase("")) {
                                fileServer = jObject.getString("u");
                                if (fileServer.equalsIgnoreCase("")) {
                                    holder.imageMessage.setVisibility(View.VISIBLE);
                                    holder.imageMessage.setImageResource(R.drawable.ic_media_not_found);
                                    holder.progressBarInfinite.setVisibility(View.GONE);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    holder.imageMessage.setVisibility(View.VISIBLE);
                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                    db.open();
                    final FilesURL files = db.retriveFiles(data.getId());
                }

                holder.captionText.setText("");
                holder.captionText.setVisibility(View.GONE);
                if (caption != null || !caption.equalsIgnoreCase("")) {
                    holder.captionText.setVisibility(View.VISIBLE);
                    holder.captionText.setText(Html.fromHtml(caption + "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;"));
                }


                if ("".equals(urlFile)) {
                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                    db.open();
                    final FilesURL files = db.retriveFiles(data.getId());
                    if (files != null) {
                        if (files.getImage() != null) {
                            holder.progressBarInfinite.setVisibility(View.GONE);
                            holder.buttonDownload.setVisibility(View.VISIBLE);
                            holder.progressBarImage.setProgress(0);

                            final File f = new File(PicassoOwnCache.fullCacheDir, files.getCache());
                            if (f.exists()) {
                                Picasso.with(activity)
                                        .load(f)
                                        .placeholder(R.drawable.ic_media_not_found)
                                        .noFade()
                                        .resize(10, 10)
                                        .centerCrop()
                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                        .transform(blurTransformation)
                                        .fetch(new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Picasso.with(activity)
                                                        .load(f) // image url goes here
                                                        .noFade()
                                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                                        .resize(10, 10)
                                                        .centerCrop()
                                                        .placeholder(holder.imageMessage.getDrawable())
                                                        .into(holder.imageMessage);
                                            }

                                            @Override
                                            public void onError() {
                                            }
                                        });
                            } else {
                                Picasso.with(activity)
                                        .load(files.getImage())
                                        .placeholder(holder.imageMessage.getDrawable())
                                        .noFade()
                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                        .resize(10, 10)
                                        .centerCrop()
                                        .transform(blurTransformation)
                                        .fetch(new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Picasso.with(activity)
                                                        .load(files.getImage()) // image url goes here
                                                        .noFade()
                                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                                        .resize(10, 10)
                                                        .centerCrop()
                                                        .placeholder(holder.imageMessage.getDrawable())
                                                        .into(holder.imageMessage);
                                            }

                                            @Override
                                            public void onError() {
                                            }
                                        });
                            }
                        } else {
                            String regex = "[0-9]+";
                            if (data.getSource().matches(regex)) {
                                holder.progressBarInfinite.setVisibility(View.VISIBLE);
                                holder.buttonDownload.setVisibility(View.GONE);
                                db.deleteFile(String.valueOf(data.getId()));
                                final String finalUrlFile = urlFile;
                                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                                    @Override
                                    public void onTaskDone(String key) {
                                        if (!key.equalsIgnoreCase("null")) {
                                            RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                                @Override
                                                public void onTaskDone(String key) {
                                                    if (key.contains("byonchat.com")) {
                                                        String aa = key.replace("/v2/uploaded/thb/", MessengerConnectionService.DOWNLOAD_LINK_THUMB);
                                                        Message bb = new Message("", "", aa);
                                                        new FileDownloadHandlerImageThumbP2PSave().execute(new Message[]{data, bb});
                                                    }
                                                }
                                            }, activity, key, finalUrlFile, RequestUploadSite.REQUEST_KEYS_URL_Thum);
                                            testAsyncTask.execute();
                                        }
                                    }
                                }, activity);
                                testAsyncTask.execute();
                            }
                        }

                        if (files.getStatus() != null) {
                            if (files.getStatus().equalsIgnoreCase("download")) {
                                holder.progressBarImage.setVisibility(View.VISIBLE);
                                holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                holder.buttonDownload.setVisibility(View.GONE);
                                holder.imageMessage.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        longClickActionDelete(data, position);
                                        return true;
                                    }
                                });
                            } else if (files.getStatus().equalsIgnoreCase("failed")) {
                                String regex = "[0-9]+";
                                if (!data.getSource().matches(regex)) {
                                    holder.progressBarImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                    intent.putExtra(UploadService.ACTION, "download");
                                    intent.putExtra(UploadService.KEY_MESSAGE, data);
                                    activity.getApplicationContext().startService(intent);
                                } else {
                                    holder.progressBarImage.setVisibility(View.GONE);
                                    holder.buttonDownload.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        String regex = "[0-9]+";
                        if (!data.getSource().matches(regex)) {
                            FilesURL files2 = new FilesURL((int) data.getId(), "0", "download", "");
                            db.insertFilesUpload(files2);
                            holder.imageMessage.setImageDrawable(null);
                            holder.progressBarImage.setProgress(0);
                            holder.progressBarImage.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                            intent.putExtra(UploadService.ACTION, "download");
                            intent.putExtra(UploadService.KEY_MESSAGE, data);
                            activity.getApplicationContext().startService(intent);

                        } else {
                            holder.progressBarInfinite.setVisibility(View.VISIBLE);
                            holder.buttonDownload.setVisibility(View.GONE);
                            final String finalUrlFile1 = urlFile;
                            RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                                @Override
                                public void onTaskDone(String key) {
                                    if (!key.equalsIgnoreCase("null")) {
                                        RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                            @Override
                                            public void onTaskDone(String key) {
                                                if (key.contains("byonchat.com")) {
                                                    String aa = key.replace("/v2/uploaded/thb/", MessengerConnectionService.DOWNLOAD_LINK_THUMB);
                                                    Message bb = new Message("", "", aa);
                                                    new FileDownloadHandlerImageThumbP2PSave().execute(new Message[]{data, bb});
                                                }
                                            }
                                        }, activity, key, finalUrlFile1, RequestUploadSite.REQUEST_KEYS_URL_Thum);
                                        testAsyncTask.execute();
                                    }
                                }
                            }, activity);
                            testAsyncTask.execute();
                        }
                    }
                    db.close();

                    holder.leftTextHeaderImage.setVisibility(View.GONE);

                    holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.buttonDownload.setVisibility(View.GONE);
                            holder.leftTextHeaderImage.setVisibility(View.GONE);
                            holder.progressBarImage.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                            intent.putExtra(UploadService.ACTION, "download");
                            intent.putExtra(UploadService.KEY_MESSAGE, data);
                            activity.getApplicationContext().startService(intent);
                        }
                    });
                    holder.buttonDownload.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            longClickActionDelete(data, position);
                            return true;
                        }
                    });
                } else {
                    final File f = new File(urlFile);
                    holder.progressBarImage.setVisibility(View.GONE);
                    holder.buttonDownload.setVisibility(View.GONE);
                    holder.progressBarInfinite.setVisibility(View.GONE);
                    if (f.exists()) {
                        boolean longclik = true;
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                        db.open();
                        FilesURL files = db.retriveFiles(data.getId());
                        db.close();
                        if (files != null) {
                            if (files.getStatus() != null) {
                                if (files.getStatus().equalsIgnoreCase("upload")) {
                                    if (Integer.valueOf(files.getProgress()) == 1) {
                                        holder.progressBarInfinite.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.progressBarImage.setVisibility(View.VISIBLE);
                                        holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                    }
                                } else if (files.getStatus().equalsIgnoreCase("failed")) {
                                    holder.progressBarInfinite.setVisibility(View.GONE);
                                    holder.progressBarImage.setVisibility(View.GONE);
                                    holder.buttonDownload.setVisibility(View.VISIBLE);
                                    holder.buttonDownload.setScaleY(-1);
                                    longclik = false;
                                    holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.buttonDownload.setVisibility(View.GONE);
                                            holder.progressBarImage.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                            intent.putExtra(UploadService.ACTION, "getLinkUpload");
                                            intent.putExtra(UploadService.KEY_MESSAGE, data);
                                            activity.startService(intent);
                                        }
                                    });
                                }
                            } else {
                                holder.imageMessage
                                        .setImageResource(R.drawable.ic_media_not_found);
                                holder.progressBarImage.setVisibility(View.GONE);
                                holder.buttonDownload.setVisibility(View.GONE);
                            }
                        }
                        Picasso.with(activity)
                                .load(f)
                                .placeholder(holder.imageMessage.getDrawable())
                                .noFade()
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .resize(50, 50)
                                .centerCrop()
                                .fetch(new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Picasso.with(activity)
                                                .load(f) // image url goes here
                                                .noFade()
                                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                                .resize(150, 150)
                                                .centerCrop()
                                                .placeholder(holder.imageMessage.getDrawable())
                                                .into(holder.imageMessage);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                        holder.imageMessage
                                .setOnClickListener(new GalleryDisplayHandler(
                                        activity, destinationAddr, data.getMessage(), Message.TYPE_IMAGE));
                        if (longclik) {
                            holder.imageMessage.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    longClickAction(data, position);
                                    return true;
                                }
                            });
                        }
                    } else {
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                        db.open();
                        final FilesURL files = db.retriveFiles(data.getId());
                        if (files != null) {
                            if (files.getImage() != null) {
                                Picasso.with(activity)
                                        .load(files.getImage())
                                        .placeholder(holder.imageMessage.getDrawable())
                                        .resize(50, 50)
                                        .centerCrop()
                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                        .into(holder.imageMessage, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Picasso.with(activity)
                                                        .load(files.getImage()) // image url goes here
                                                        .resize(50, 50)
                                                        .centerCrop()
                                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                                        .placeholder(holder.imageMessage.getDrawable())
                                                        .into(holder.imageMessage);
                                            }

                                            @Override
                                            public void onError() {
                                            }
                                        });

                            } else {
                                holder.imageMessage
                                        .setImageResource(R.drawable.ic_media_not_found);
                                holder.progressBarImage.setVisibility(View.GONE);
                                holder.buttonDownload.setVisibility(View.GONE);
                            }

                            if (files.getStatus() != null) {
                                if (files.getStatus().equalsIgnoreCase("download")) {
                                    holder.progressBarImage.setVisibility(View.VISIBLE);
                                    holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                    holder.buttonDownload.setVisibility(View.GONE);
                                }
                            } else {
                                holder.imageMessage
                                        .setImageResource(R.drawable.ic_media_not_found);
                            }
                        }
                        db.close();

                        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.buttonDownload.setVisibility(View.GONE);
                                holder.leftTextHeaderImage.setVisibility(View.GONE);
                                holder.progressBarImage.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                intent.putExtra(UploadService.ACTION, "download");
                                intent.putExtra(UploadService.KEY_MESSAGE, data);
                                activity.getApplicationContext().startService(intent);
                            }
                        });
                        holder.buttonDownload.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                longClickActionDelete(data, position);
                                return true;
                            }
                        });

                    }
                }

            } else if (Message.TYPE_VIDEO.equals(data.getType())) {

                holder.imageMessage.setImageDrawable(null);
                holder.progressBarImage.setVisibility(View.GONE);
                holder.progressBarInfinite.setVisibility(View.GONE);
                holder.buttonDownload.setVisibility(View.GONE);
                holder.imageMessage.setOnClickListener(null);

                String urlFile = "";
                String path = "";
                String outpath = "";
                String startpos = "";
                String endpos = "";
                String caption = "";
                String fileServer = "";
                if (isJSONValid(data.getMessage())) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(data.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("s");
                            path = jObject.getString("s");
                            outpath = jObject.getString("o");
                            startpos = jObject.getString("sp");
                            endpos = jObject.getString("ep");
                            caption = jObject.getString("c");
                            if (urlFile.equalsIgnoreCase("")) {
                                fileServer = jObject.getString("u");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                holder.captionText.setText("");
                holder.captionText.setVisibility(View.GONE);
                if (caption != null || !caption.equalsIgnoreCase("")) {
                    holder.captionText.setVisibility(View.VISIBLE);
                    holder.captionText.setText(Html.fromHtml(caption + "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;"));
                }

                if ("".equals(urlFile)) {
                    holder.buttonDownload.setVisibility(View.VISIBLE);
                    holder.progressBarImage.setProgress(0);
                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                    db.open();
                    final FilesURL files = db.retriveFiles(data.getId());
                    if (files != null) {
                        Picasso.with(activity)
                                .load(files.getImage())
                                .placeholder(holder.imageMessage.getDrawable())
                                .resize(50, 50)
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(holder.imageMessage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Picasso.with(activity)
                                                .load(files.getImage()) // image url goes here
                                                .resize(50, 50)
                                                .centerCrop()
                                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                                .placeholder(holder.imageMessage.getDrawable())
                                                .into(holder.imageMessage);
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
//                        holder.imageMessage.setImageBitmap(files.getImage());
                        if (files.getStatus() != null) {
                            if (files.getStatus().equalsIgnoreCase("download")) {
                                holder.progressBarImage.setVisibility(View.VISIBLE);
                                holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                holder.buttonDownload.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        String[] url = fileServer.split("\\.");
                        final String finalFileServer = url[0] + ".jpg";
                        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                            @Override
                            public void onTaskDone(String key) {
                                if (!key.equalsIgnoreCase("null")) {
                                    RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                        @Override
                                        public void onTaskDone(String key) {
                                            if (key.contains("byonchat.com")) {
                                                String aa = key.replace("/v2/uploaded/thb/", MessengerConnectionService.DOWNLOAD_LINK_THUMB);
                                                Message bb = new Message("", "", aa);
                                                new FileDownloadHandlerImageTumbP2PSave().execute(new Message[]{data, bb});
                                            }
                                        }
                                    }, activity, key, finalFileServer, RequestUploadSite.REQUEST_KEYS_URL_Thum);
                                    testAsyncTask.execute();

                                }
                            }
                        }, activity);
                        testAsyncTask.execute();

                    }

                    db.close();

                    holder.leftTextHeaderImage.setVisibility(View.GONE);

                    holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.buttonDownload.setVisibility(View.GONE);
                            holder.leftTextHeaderImage.setVisibility(View.GONE);
                            holder.progressBarImage.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                            intent.putExtra(UploadService.ACTION, "download");
                            intent.putExtra(UploadService.KEY_MESSAGE, data);
                            activity.getApplicationContext().startService(intent);
                        }
                    });
                    holder.buttonDownload.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            longClickActionDelete(data, position);
                            return true;
                        }
                    });
                } else {
                    File f = new File(urlFile);
                    holder.progressBarImage.setVisibility(View.GONE);
                    holder.buttonDownload.setVisibility(View.GONE);
                    holder.progressBarInfinite.setVisibility(View.GONE);
                    if (f.exists()) {
                        boolean longclik = true;
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                        db.open();
                        FilesURL files = db.retriveFiles(data.getId());
                        db.close();
                        if (files != null) {
                            if (files.getStatus() != null) {
                                if (files.getStatus().equalsIgnoreCase("upload")) {

                                    if (Integer.valueOf(files.getProgress()) == 1) {
                                        holder.progressBarInfinite.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.progressBarImage.setVisibility(View.VISIBLE);
                                        holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                    }

                                } else if (files.getStatus().equalsIgnoreCase("failed")) {
                                    holder.progressBarImage.setVisibility(View.GONE);
                                    holder.buttonDownload.setVisibility(View.VISIBLE);
                                    holder.buttonDownload.setScaleY(-1);
                                    longclik = false;
                                    holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.buttonDownload.setVisibility(View.GONE);
                                            holder.progressBarImage.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                            intent.putExtra(UploadService.ACTION, "getLinkUpload");
                                            intent.putExtra(UploadService.KEY_MESSAGE, data);
                                            intent.putExtra(UploadService.KEY_STATUS_VIDEO, "2");
                                            activity.startService(intent);
                                        }
                                    });
                                }
                            }
                        }

                        GalleryDisplayHandler displayHandler = new GalleryDisplayHandler(
                                activity, destinationAddr, urlFile, Message.TYPE_VIDEO);
                        holder.imageMessage.setOnClickListener(displayHandler);
                        holder.imagePlay.setOnClickListener(displayHandler);
                        holder.imagePlay.setVisibility(View.VISIBLE);
                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(
                                urlFile,

                                MediaStore.Images.Thumbnails.MINI_KIND);
                        holder.imageMessage.setImageBitmap(thumb);
                        holder.imagePlay.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                longClickAction(data, position);
                                return true;
                            }
                        });

                    } else {
                        holder.buttonDownload.setVisibility(View.VISIBLE);

                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                        db.open();
                        final FilesURL files = db.retriveFiles(data.getId());
                        if (files != null) {
                            if (files.getImage() != null) {
                                Picasso.with(activity)
                                        .load(files.getImage())
                                        .placeholder(R.drawable.ic_media_not_found)
                                        .noFade()
                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                        .resize(10, 10)
                                        .centerCrop()
                                        .transform(blurTransformation)
                                        .fetch(new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Picasso.with(activity)
                                                        .load(files.getImage()) // image url goes here
                                                        .noFade()
                                                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                                        .resize(10, 10)
                                                        .centerCrop()
                                                        .placeholder(holder.imageMessage.getDrawable())
                                                        .into(holder.imageMessage);
                                            }

                                            @Override
                                            public void onError() {
                                            }
                                        });
//                                holder.imageMessage.setImageBitmap(files.getImage());
                            } else {
                                holder.imageMessage
                                        .setImageResource(R.drawable.ic_media_not_found);
                                holder.progressBarImage.setVisibility(View.GONE);
                                holder.buttonDownload.setVisibility(View.GONE);
                            }

                            if (files.getStatus() != null) {
                                if (files.getStatus().equalsIgnoreCase("download")) {
                                    holder.progressBarImage.setVisibility(View.VISIBLE);
                                    holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                    holder.buttonDownload.setVisibility(View.GONE);
                                }
                            } else {
                                holder.imageMessage
                                        .setImageResource(R.drawable.ic_media_not_found);
                            }

                        }
                        db.close();


                        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.buttonDownload.setVisibility(View.GONE);
                                holder.leftTextHeaderImage.setVisibility(View.GONE);
                                holder.progressBarImage.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                intent.putExtra(UploadService.ACTION, "download");
                                intent.putExtra(UploadService.KEY_MESSAGE, data);
                                activity.getApplicationContext().startService(intent);
                            }
                        });
                        holder.buttonDownload.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                longClickActionDelete(data, position);
                                return true;
                            }
                        });

                    }
                }

            } else if (Message.TYPE_LOC.equals(data.getType())) {
                holder.imageMessage.setImageResource(R.drawable.ic_map_marker);
//                Picasso.with(activity).load(R.drawable.ic_map_marker).into(holder.imageMessage);
                holder.captionText.setText("");
                holder.captionText.setVisibility(View.GONE);
                holder.progressBarInfinite.setVisibility(View.GONE);
                holder.imageMessage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        longClickActionDelete(data, position);
                        return true;
                    }
                });

                holder.buttonDownload.setVisibility(View.GONE);

                if (!data.getMessage().equals("")) {
                    String[] latlong = data.getMessage().split(
                            Message.LOCATION_DELIMITER);
                    boolean loadStaticMap = true;
                    holder.captionText.setVisibility(View.VISIBLE);
                    String text = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                    holder.captionText.setText(Html.fromHtml(text + latlong[3] + "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;"));
                    if (latlong.length == 6) {
                        loadStaticMap = false;
                        File f = activity.getFileStreamPath(latlong[5]);
                        if (f.exists() && f.length() > 1000) {
                            holder.imageMessage.setImageURI(Uri.fromFile(f));
//                            Picasso.with(activity).load(Uri.fromFile(f)).into(holder.imageMessage);
                        }
                    }

                    if (loadStaticMap) {
                        Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                        intent.putExtra(UploadService.ACTION, "downloadLocation");
                        intent.putExtra(UploadService.KEY_MESSAGE, data);
                        activity.getApplicationContext().startService(intent);
                    }

                    try {
                        holder.imageMessage
                                .setOnClickListener(new LocationDisplayHandler(
                                        activity, data.getSource(), Double
                                        .parseDouble(latlong[0]),
                                        Double.parseDouble(latlong[1]), latlong[2], latlong[3], latlong[4]));
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
            holder.imageLayout.setVisibility(View.VISIBLE);
            holder.textMessage.setVisibility(View.GONE);
        } else if (Message.TYPE_TEXT.equals(data.getType()) || Message.TYPE_BROADCAST.equals(data.getType())) {

            if (holder.RelatifOpen != null) {
                holder.RelatifOpen.setVisibility(View.GONE);
            }
            holder.btnOpenLink.setVisibility(View.GONE);

            boolean btnOpen = false;
            if (new Validations().getInstance(activity.getApplicationContext()).cekRoom(data.getSource())) {
                if (data.getMessage().replace("<br/>", "").startsWith("http://") || data.getMessage().replace("<br/>", "").startsWith("https://") || data.getMessage().replace("<br/>", "").startsWith("bc://")) {
                    btnOpen = true;
                }
            }

            if (btnOpen) {
                final String link[] = data.getMessage().split(";");
                holder.RelatifOpen.setVisibility(View.VISIBLE);
                holder.btnOpenLink.setVisibility(View.VISIBLE);
                holder.leftTimeDua.setVisibility(View.VISIBLE);
                holder.textTime.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.GONE);
                holder.textMessage.setVisibility(View.GONE);
                holder.frameMainText.setVisibility(View.GONE);


                if (link.length == 2) {
                    Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
                    boolean isHTML = htmlPattern.matcher(data.getMessage()).matches();
                    if (isHTML) {
                        if (link[1].contains("<")) {
                            holder.btnOpenLink.setText(Html.fromHtml(Html.fromHtml(link[1]).toString()));
                        } else {
                            holder.btnOpenLink.setText(Html.fromHtml(link[1]));
                        }
                    } else {
                        holder.btnOpenLink.setText(Html.fromHtml(link[1]));
                    }
                }
                holder.btnOpenLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (link[0].startsWith("https://") || link[0].startsWith("http://")) {
                            Intent intent = new Intent(activity.getApplicationContext(), WebViewByonActivity.class);
                            intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, link[0]);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.getApplicationContext().startActivity(intent);
                        } else if (link[0].startsWith("bc://")) {
                            String room[] = link[0].split("//");
                            Intent intent = new Intent(activity, ByonChatMainRoomActivity.class);
                            intent.putExtra(ConversationActivity.KEY_JABBER_ID, room[1]);

                            if (room.length == 3) {
                                intent.putExtra("firstTab", room[2]);
                            } else {
                                intent.putExtra("firstTab", "");
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    }
                });
                holder.btnOpenLink.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickActionDelete(data, position);
                        return true;
                    }
                });
            } else {
                String text = Html.fromHtml(data.getMessage()).toString();
                Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
                boolean isHTML = htmlPattern.matcher(data.getMessage()).matches();

                if (Message.TYPE_BROADCAST.equals(data.getType())) {
                    holder.textMessage.setTextColor(Color.MAGENTA);
                    holder.textMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_broadcasts, 0, 0, 0);
                } else {
                    holder.textMessage.setTextColor(Color.BLACK);
                    holder.textMessage.setCompoundDrawables(null, null, null, null);
                }
                String space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
                if (form.equalsIgnoreCase("right")) {
                    space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
                }


                String message = data.getMessage().replace("\\r\\n", "<br>").replace("\\n", "<br>");

                if (isHTML) {
                    if (text.contains("<")) {
                        holder.textMessage.setText(Html.fromHtml(Html.fromHtml(message).toString() + space));
                    } else {
                        holder.textMessage.setText(Html.fromHtml(message + space));
                    }
                } else {
                    String a = Html.fromHtml(message + space).toString();
                    if (a.contains("<")) {
                        holder.textMessage.setText(Html.fromHtml(a));
                    } else {
                        holder.textMessage.setText(a);
                    }
                }

                //holder.textMessage.setText(Html.fromHtml(Html.fromHtml(data.getMessage()).toString() ));
                Linkify.addLinks(holder.textMessage, Linkify.ALL);
                holder.textMessage.setLinkTextColor(Color.BLUE);
                holder.imageLayout.setVisibility(View.GONE);
                holder.textMessage.setVisibility(View.VISIBLE);
            }
        } else if (Message.TYPE_REPORT_TARIK.equals(data.getType())) {
            holder.textMessage.setVisibility(View.VISIBLE);
            holder.textMessage.setText(data.getMessage());

        }
    }

    private void displayConversationOptionButton(final ConversationLeftOptionHolder holder,
                                                 final Message data, final int position) {
        if (Message.TYPE_IMAGE.equals(data.getType())
                || Message.TYPE_LOC.equals(data.getType())
                || Message.TYPE_VIDEO.equals(data.getType())) {

        } else {
            String title = data.getMessage();
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(data.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jObject != null) {
                try {
                    title = jObject.getString("buka");
                    JSONArray menuitemArray = jObject.getJSONArray("tete");
                    if (holder.menuList.getChildCount() > 0) holder.menuList.removeAllViews();
                    for (int i = 0; i < menuitemArray.length(); i++) {
                        String a = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("title").toString()));
                        final String b = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("url").toString()));
                        final String c = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("color").toString()));
                        LayoutInflater inflater = null;
                        inflater = (LayoutInflater) activity
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View mLinearView = inflater.inflate(R.layout.list_item_btn_open_conversation, null);

                        final TextView titlew = (TextView) mLinearView
                                .findViewById(R.id.textTitle);
                        final RelativeLayout relativeLayout = (RelativeLayout) mLinearView
                                .findViewById(R.id.relativelay);

                        LayerDrawable layers = (LayerDrawable) activity.getResources().getDrawable(R.drawable.btn_open_normal);
                        GradientDrawable shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.main_color));
                        shape.setColor(Color.parseColor("#" + c));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            relativeLayout.setBackgroundDrawable(layers);
                        } else {
                            relativeLayout.setBackground(layers);
                        }

                        titlew.setText(a);
                        holder.menuList.addView(mLinearView);
                        mLinearView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (b.startsWith("https://") || b.startsWith("http://")) {
                                    Intent intent = new Intent(activity.getApplicationContext(), WebViewByonActivity.class);
                                    intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, b);
                                    intent.putExtra(WebViewByonActivity.KEY_COLOR, c);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.getApplicationContext().startActivity(intent);
                                } else if (b.startsWith("bc://")) {
                                    String room[] = b.split("//");
                                    Intent intent = new Intent(activity.getApplicationContext(), ConversationActivity.class);
                                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, room[1]);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.getApplicationContext().startActivity(intent);
                                }
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            String text = Html.fromHtml(title).toString();
            Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
            boolean isHTML = htmlPattern.matcher(title).matches();

            if (Message.TYPE_BROADCAST.equals(data.getType())) {
                holder.textMessage.setTextColor(Color.MAGENTA);
                holder.textMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_broadcasts, 0, 0, 0);
            } else {
                holder.textMessage.setTextColor(Color.BLACK);
                holder.textMessage.setCompoundDrawables(null, null, null, null);
            }
            String space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
            String spasi = "            ";

            if (isHTML) {
                if (text.contains("<")) {
                    holder.textMessage.setText(Html.fromHtml(Html.fromHtml(title).toString() + space));
                } else {
                    holder.textMessage.setText(Html.fromHtml(title + space));
                }
            } else {
                holder.textMessage.setText(title + spasi);
                String a = Html.fromHtml(title + space).toString();
                if (a.contains("<")) {
                    holder.textMessage.setText(Html.fromHtml(a));
                }
            }

            Linkify.addLinks(holder.textMessage, Linkify.ALL);
            holder.textMessage.setLinkTextColor(Color.BLUE);
            holder.textMessage.setVisibility(View.VISIBLE);
        }
    }

    private void displayConversationRecallHistory(final ConversationRightRecallHolder holder,
                                                  final Message data, final int position) {
        if (Message.TYPE_REPORT_TARIK.equals(data.getType())) {
            String text = null;
            String space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
            if (text != null) {
                text += "<br/>" + data.getMessage() + space;
            } else {
                text = data.getMessage() + space;
            }
            holder.textMessage.setText(Html.fromHtml(text));
            Linkify.addLinks(holder.textMessage, Linkify.ALL);
        }
    }


    private View createListView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_left, parent, false);
        }

        ConversationLeftHolder holder = (ConversationLeftHolder) row.getTag();
        if (holder == null) {
            holder = new ConversationLeftHolder(row);
            row.setTag(holder);
        }

        List<Object> listConv = (List<Object>) getItem(position);
        String text = null;
        if (!listConv.get(0).equals("")) {
            text = "<b>" + (String) listConv.get(0) + "</b>";
        }

        for (int i = 1; i < listConv.size(); i++) {
            final Message data = (Message) listConv.get(i);
            if (Message.TYPE_IMAGE.equals(data.getType())
                    || Message.TYPE_LOC.equals(data.getType())
                    || Message.TYPE_VIDEO.equals(data.getType())) {


                holder.imagePlay.setVisibility(View.GONE);
                holder.imageMessage.setImageDrawable(null);
                holder.progressBarImage.setVisibility(View.GONE);
                holder.buttonDownload.setVisibility(View.GONE);
                holder.imageMessage.setOnClickListener(null);

                if (Message.TYPE_IMAGE.equals(data.getType())) {
                    String[] s = data.getMessage().split(";");
                    holder.captionText.setText("");
                    holder.captionText.setVisibility(View.GONE);
                    if (s.length > 2) {
                        holder.captionText.setVisibility(View.VISIBLE);
                        holder.captionText.setText(Html.fromHtml(s[2] + "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;"));
                    }


                } else if (Message.TYPE_VIDEO.equals(data.getType())) {

                    holder.imageMessage.setImageDrawable(null);
                    holder.progressBarImage.setVisibility(View.GONE);
                    holder.buttonDownload.setVisibility(View.GONE);
                    holder.imageMessage.setOnClickListener(null);

                    String[] s = data.getMessage().split(";");
                    holder.captionText.setText("");
                    holder.captionText.setVisibility(View.GONE);
                    if (s.length > 2) {
                        holder.captionText.setVisibility(View.VISIBLE);
                        holder.captionText.setText(Html.fromHtml(s[2] + "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;"));
                    }

                    if ("".equals(s[0])) {
                        holder.buttonDownload.setVisibility(View.VISIBLE);
                        holder.progressBarImage.setProgress(0);
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                        db.open();
                        final FilesURL files = db.retriveFiles(data.getId());
                        if (files != null) {
//                            holder.imageMessage.setImageBitmap(files.getImage());
                            Picasso.with(activity)
                                    .load(files.getImage())
                                    .placeholder(holder.imageMessage.getDrawable())
                                    .noFade()
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .resize(10, 10)
                                    .centerCrop()
                                    .into(holder.imageMessage);
//                                    .fetch(new Callback() {
//                                        @Override
//                                        public void onSuccess() {
//                                            Picasso.with(activity)
//                                                    .load(files.getImage()) // image url goes here
//                                                    .noFade()
//                                                    .resize(10, 10)
//                                                    .centerCrop()
//                                                    .placeholder(holder.imageMessage.getDrawable())
//                                                    .into(holder.imageMessage);
//                                        }
//
//                                        @Override
//                                        public void onError() {
//                                        }
//                                    });
                            if (files.getStatus() != null) {
                                if (files.getStatus().equalsIgnoreCase("download")) {
                                    holder.progressBarImage.setVisibility(View.VISIBLE);
                                    holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                    holder.buttonDownload.setVisibility(View.GONE);
                                }
                            }
                        }
                        db.close();

                        holder.leftTextHeaderImage.setVisibility(View.GONE);
                        holder.progressBarInfinite.setVisibility(View.GONE);

                        final ConversationLeftHolder finalHolder3 = holder;
                        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalHolder3.buttonDownload.setVisibility(View.GONE);
                                finalHolder3.leftTextHeaderImage.setVisibility(View.GONE);
                                finalHolder3.progressBarImage.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                intent.putExtra(UploadService.ACTION, "download");
                                intent.putExtra(UploadService.KEY_MESSAGE, data);
                                activity.getApplicationContext().startService(intent);
                            }
                        });
                        holder.buttonDownload.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                longClickActionDelete(data, position);
                                return true;
                            }
                        });
                    } else {
                        File f = new File(s[0]);
                        holder.progressBarImage.setVisibility(View.GONE);
                        holder.progressBarInfinite.setVisibility(View.GONE);
                        holder.buttonDownload.setVisibility(View.GONE);
                        if (f.exists()) {
                            boolean longclik = true;
                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                            db.open();
                            FilesURL files = db.retriveFiles(data.getId());
                            db.close();
                            if (files != null) {
                                if (files.getStatus() != null) {
                                    if (files.getStatus().equalsIgnoreCase("upload")) {
                                        holder.progressBarImage.setVisibility(View.VISIBLE);
                                        holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                    } else if (files.getStatus().equalsIgnoreCase("failed")) {
                                        holder.progressBarImage.setVisibility(View.GONE);
                                        holder.progressBarInfinite.setVisibility(View.GONE);
                                        holder.buttonDownload.setVisibility(View.VISIBLE);
                                        longclik = false;
                                        final ConversationLeftHolder finalHolder4 = holder;
                                        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finalHolder4.buttonDownload.setVisibility(View.GONE);
                                                finalHolder4.progressBarImage.setVisibility(View.VISIBLE);
                                                Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                                intent.putExtra(UploadService.ACTION, "getLinkUpload");
                                                intent.putExtra(UploadService.KEY_MESSAGE, data);
                                                activity.startService(intent);
                                            }
                                        });
                                    }
                                }
                            }

                            GalleryDisplayHandler displayHandler = new GalleryDisplayHandler(
                                    activity, destinationAddr, s[0], Message.TYPE_VIDEO);
                            holder.imageMessage.setOnClickListener(displayHandler);
                            holder.imagePlay.setOnClickListener(displayHandler);
                            holder.imagePlay.setVisibility(View.VISIBLE);
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(
                                    s[0],

                                    MediaStore.Images.Thumbnails.MINI_KIND);
                            holder.imageMessage.setImageBitmap(thumb);
                            holder.imagePlay.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    longClickAction(data, position);
                                    return true;
                                }
                            });

                        } else {
                            holder.buttonDownload.setVisibility(View.VISIBLE);

                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                            db.open();
                            FilesURL files = db.retriveFiles(data.getId());
                            if (files != null) {
                                if (files.getImage() != null) {
//                                    holder.imageMessage.setImageBitmap(files.getImage());
                                    Picasso.with(activity)
                                            .load(files.getImage())
                                            .placeholder(holder.imageMessage.getDrawable())
                                            .noFade()
                                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                            .resize(10, 10)
                                            .centerCrop()
                                            .into(holder.imageMessage);
//                                            .fetch(new Callback() {
//                                                @Override
//                                                public void onSuccess() {
//                                                    Picasso.with(activity)
//                                                            .load(files.getImage()) // image url goes here
//                                                            .noFade()
//                                                            .resize(10, 10)
//                                                            .centerCrop()
//                                                            .placeholder(holder.imageMessage.getDrawable())
//                                                            .into(holder.imageMessage);
//                                                }
//
//                                                @Override
//                                                public void onError() {
//                                                }
//                                            });
                                } else {
                                    holder.imageMessage
                                            .setImageResource(R.drawable.ic_media_not_found);
                                    holder.progressBarImage.setVisibility(View.GONE);
                                    holder.buttonDownload.setVisibility(View.GONE);
                                }

                                if (files.getStatus() != null) {
                                    if (files.getStatus().equalsIgnoreCase("download")) {
                                        holder.progressBarImage.setVisibility(View.VISIBLE);
                                        holder.progressBarImage.setProgress(Integer.valueOf(files.getProgress()));
                                        holder.buttonDownload.setVisibility(View.GONE);
                                    }
                                } else {
                                    holder.imageMessage
                                            .setImageResource(R.drawable.ic_media_not_found);
                                }
                            }
                            db.close();

                            final ConversationLeftHolder finalHolder5 = holder;
                            holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finalHolder5.buttonDownload.setVisibility(View.GONE);
                                    finalHolder5.leftTextHeaderImage.setVisibility(View.GONE);
                                    finalHolder5.progressBarImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                                    intent.putExtra(UploadService.ACTION, "download");
                                    intent.putExtra(UploadService.KEY_MESSAGE, data);
                                    activity.getApplicationContext().startService(intent);
                                }
                            });
                            holder.buttonDownload.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    longClickActionDelete(data, position);
                                    return true;
                                }
                            });

                        }
                    }
                } else if (Message.TYPE_LOC.equals(data.getType())) {
                    holder.imageMessage.setImageResource(R.drawable.ic_map_marker);
                    holder.captionText.setText("");
                    holder.captionText.setVisibility(View.GONE);
                    holder.progressBarInfinite.setVisibility(View.GONE);

                    holder.imageMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            longClickActionDelete(data, position);
                            return true;
                        }
                    });

                    holder.buttonDownload.setVisibility(View.GONE);
                    if (!data.getMessage().equals("")) {
                        String[] latlong = data.getMessage().split(
                                Message.LOCATION_DELIMITER);
                        boolean loadStaticMap = true;
                        holder.captionText.setVisibility(View.VISIBLE);
                        String text1 = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                        holder.captionText.setText(Html.fromHtml(text1 + latlong[3] + "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160&#160;"));
                        if (latlong.length == 6) {
                            loadStaticMap = false;
                            File f = activity.getFileStreamPath(latlong[5]);
                            if (f.exists() && f.length() > 1000) {
                                holder.imageMessage.setImageURI(Uri.fromFile(f));
//                                Picasso.with(activity).load(Uri.fromFile(f)).into(holder.imageMessage);
                            }
                        }

                        if (loadStaticMap) {
                            Intent intent = new Intent(activity.getApplicationContext(), UploadService.class);
                            intent.putExtra(UploadService.ACTION, "downloadLocation");
                            intent.putExtra(UploadService.KEY_MESSAGE, data);
                            activity.getApplicationContext().startService(intent);
                        }

                        try {
                            holder.imageMessage
                                    .setOnClickListener(new LocationDisplayHandler(
                                            activity, data.getSource(), Double
                                            .parseDouble(latlong[0]),
                                            Double.parseDouble(latlong[1]), latlong[2], latlong[3], latlong[4]));
                        } catch (NumberFormatException nfe) {
                        }
                    }
                }
                holder.imageLayout.setVisibility(View.VISIBLE);
                holder.textMessage.setVisibility(View.GONE);
            } else {
                Log.w("masuk", data.getMessage());

                String space = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
                if (text != null) {
                    text += "<br/>" + data.getMessage() + space;
                } else {
                    text = data.getMessage() + space;
                }
                holder.textMessage.setText(Html.fromHtml(text));
                Linkify.addLinks(holder.textMessage, Linkify.ALL);
                holder.textMessage.setLinkTextColor(Color.BLUE);
                holder.imageLayout.setVisibility(View.GONE);
                holder.textMessage.setVisibility(View.VISIBLE);
            }

        }

        try {
            Date d = ((Message) listConv.get((listConv.size() - 1)))
                    .getSendDate();

            holder.textTime.setText(hourFormat.format(d));
            holder.textTimeMedia.setText(hourFormat.format(d));

            if (holder.leftTimeDua != null && holder.leftTimeDua.getVisibility() == View.VISIBLE) {
                holder.leftTimeDua.setText(hourFormat.format(d));
            }
        } catch (Exception e) {
        }
        return row;
    }

    private View createLeftView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_left, parent, false);
        }
        ConversationLeftHolder holder = (ConversationLeftHolder) row.getTag();
        if (holder == null) {
            holder = new ConversationLeftHolder(row);
            row.setTag(holder);
        }

        Message data = (Message) getItem(position);
        displayConversation(holder, data, position, "left");
        try {
            Date d = data.getSendDate();
            holder.textTime.setText(hourFormat.format(d));
            holder.textTimeMedia.setText(hourFormat.format(d));

            if (holder.leftTimeDua != null && holder.leftTimeDua.getVisibility() == View.VISIBLE) {
                holder.leftTimeDua.setText(hourFormat.format(d));
            }

        } catch (Exception e) {
        }

        return row;
    }

    private View createInfoView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_info, parent, false);
        }

        ConversationInfoHolder holder = (ConversationInfoHolder) row.getTag();
        if (holder == null) {
            holder = new ConversationInfoHolder(row);
            row.setTag(holder);
        }

        String date = (String) getItem(position);
        holder.textDate.setText(date);

        return row;
    }

    private View createLeftViewOption(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_custome, parent, false);
        }
        ConversationLeftOptionHolder holder = (ConversationLeftOptionHolder) row.getTag();
        if (holder == null) {
            holder = new ConversationLeftOptionHolder(row);
            row.setTag(holder);
        }

        Message data = (Message) getItem(position);
        displayConversationOptionButton(holder, data, position);
        try {
            Date d = data.getSendDate();
            holder.textTime.setText(hourFormat.format(d));
        } catch (Exception e) {
        }

        return row;
    }


    private View createRightViewOption(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_bubble_recall, parent, false);
        }
        ConversationRightRecallHolder holder = (ConversationRightRecallHolder) row.getTag();
        if (holder == null) {
            holder = new ConversationRightRecallHolder(row);
            row.setTag(holder);
        }

        Message data = (Message) getItem(position);
        displayConversationRecallHistory(holder, data, position);
        try {
            Date d = data.getSendDate();
            holder.textTime.setText(hourFormat.format(d));
        } catch (Exception e) {
        }

        return row;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View createRightView(int position, View convertView,
                                 ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.conversation_right, parent, false);
        }

        ConversationRightHolder holder = (ConversationRightHolder) row.getTag();
        if (holder == null) {
            holder = new ConversationRightHolder(row);
            row.setTag(holder);
        }
        Message data = (Message) getItem(position);
        displayConversation(holder, data, position, "right");

        holder.imageFailedButton.setVisibility(View.GONE);
        holder.imageStatus.setVisibility(View.VISIBLE);
        holder.textTime.setVisibility(View.VISIBLE);

        if (activity.getResources().getDisplayMetrics().density == 2.0) {
            FrameLayout.LayoutParams llp = (FrameLayout.LayoutParams) holder.textMessage.getLayoutParams();
            llp.setMargins(0, 0, 10, 0); // llp.setMargins(left, top, right, bottom);
            holder.textMessage.setLayoutParams(llp);
        }

        switch (data.getStatus()) {
            case Message.STATUS_DELIVERED:
                Date d = data.getSendDate();
                holder.textTime.setText(hourFormat.format(d));
                holder.textTimeMedia.setText(hourFormat.format(d));
                holder.progressBarImage.setVisibility(View.GONE);
                holder.progressBarInfinite.setVisibility(View.GONE);
                holder.imageStatus.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_delivered));
                holder.imageStatusMedia.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_delivered));
                break;

            case Message.STATUS_SENT:
                d = data.getSendDate();
                holder.textTime.setText(hourFormat.format(d));
                holder.textTimeMedia.setText(hourFormat.format(d));
                holder.progressBarImage.setVisibility(View.GONE);
                holder.progressBarInfinite.setVisibility(View.GONE);
                holder.imageStatus.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_sent));
                holder.imageStatusMedia.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_sent));
                break;

            case Message.STATUS_INPROGRESS:
                holder.imageStatus.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_pending));
                holder.imageStatusMedia.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_pending));
                d = data.getSendDate();
                holder.textTime.setText(hourFormat.format(d));
                holder.textTimeMedia.setText(hourFormat.format(d));
                //  holder.textTime.setVisibility(View.INVISIBLE);
                break;

            case Message.STATUS_FAILED:
                holder.imageStatus.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_failed));
                holder.imageStatusMedia.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_failed));
                holder.imageStatus.setVisibility(View.INVISIBLE);
                d = data.getSendDate();
                holder.textTime.setText(hourFormat.format(d));
                holder.textTimeMedia.setText(hourFormat.format(d));
                //holder.textTime.setVisibility(View.INVISIBLE);
                holder.imageFailedButton.setVisibility(View.VISIBLE);
                break;

            case Message.STATUS_NOTSEND:
                holder.imageStatus.setBackground(null);
                holder.imageStatusMedia.setBackground(null);
                d = data.getSendDate();
                holder.textTime.setText(hourFormat.format(d));
                holder.textTimeMedia.setText(hourFormat.format(d));
                break;

            case Message.STATUS_READ:
                d = data.getSendDate();
                holder.textTime.setText(hourFormat.format(d));
                holder.textTimeMedia.setText(hourFormat.format(d));
                holder.progressBarImage.setVisibility(View.GONE);
                holder.progressBarInfinite.setVisibility(View.GONE);
                holder.imageStatus.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_read));
                holder.imageStatusMedia.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_message_read));
                break;

        }
        return row;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        Object o = items.get(position);
        int type = FIRST_ROW_VIEW;
        if (o instanceof Message) {
            Message msg = ((Message) o);
            if (msg.getSource().equals(sourceAddr)) {
                if (msg.getType().equalsIgnoreCase(Message.TYPE_REPORT_TARIK)) {
                    type = RIGHT_VIEW_RECALL;
                } else {
                    type = RIGHT_VIEW;
                }
            } else {
                String regex = "[0-9]+";
                if (!msg.getSource().matches(regex) && msg.getType() == Message.TYPE_TEXT) {
                    if (isJSONValid(msg.getMessage())) {
                        JSONObject jObject = null;
                        try {
                            jObject = new JSONObject(msg.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jObject != null) {
                            type = LEFT_VIEW_OPTION;
                        }
                    } else {
                        type = LEFT_VIEW;
                    }
                } else {
                    type = LEFT_VIEW;
                }
            }
        } else if (o instanceof String) {
            type = INFO_VIEW;
        } else if (o instanceof List) {
            type = LEFT_GROUP_VIEW;
        }
        return type;

    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    abstract class ConversationMessageHolder {

        /*protected LinearLayout linearImageMessage;*/
        protected TextView textMessage;
        protected ProgressBar progressBarInfinite;
        protected ChatImageRounded imageMessage;
        protected RelativeLayout imageLayout;
        protected RelativeLayout RelatifOpen;
        protected ImageView imagePlay;
        protected TextView leftTimeDua;
        protected TextView textTime;
        protected TextView textTimeMedia;
        protected TextView captionText;
        protected TextView leftTextHeaderImage;
        protected ImageButton buttonDownload;
        protected Button btnOpenLink;
        protected FrameLayout frameMainText;
        CircleProgressBar progressBarImage;
    }

    class ConversationLeftHolder extends ConversationMessageHolder {

        public ConversationLeftHolder(View row) {
            /*linearImageMessage = (LinearLayout) row.findViewById(R.id.leftLinearImageMessage);*/
            textMessage = (TextView) row.findViewById(R.id.leftMessage);
            progressBarInfinite = (ProgressBar) row.findViewById(R.id.progressBarInfinite);
            leftTimeDua = (TextView) row.findViewById(R.id.leftTimeDua);
            captionText = (TextView) row.findViewById(R.id.leftCaptionImage);
            imageMessage = (ChatImageRounded) row.findViewById(R.id.leftImageMessage);
            imagePlay = (ImageButton) row.findViewById(R.id.leftImagePlay);
            RelatifOpen = (RelativeLayout) row.findViewById(R.id.RelatifOpen);
            imageLayout = (RelativeLayout) row.findViewById(R.id.leftImageLayout);
            textTime = (TextView) row.findViewById(R.id.leftTime);
            textTimeMedia = (TextView) row.findViewById(R.id.leftTimeMedia);
            leftTextHeaderImage = (TextView) row.findViewById(R.id.leftTextHeaderImage);
            buttonDownload = (ImageButton) row.findViewById(R.id.leftButtonDownload);
            progressBarImage = (CircleProgressBar) row.findViewById(R.id.left_progressBar);
            btnOpenLink = (Button) row.findViewById(R.id.btnOpenLink);
            frameMainText = (FrameLayout) row.findViewById(R.id.frameMainText);

            //  circleProgressBar.setProgress(30);
            //  progressBarImage.setProgressWithAnimation(100);
            progressBarImage.setColor(activity.getApplicationContext().getResources().getColor(R.color.colorPrimary));
        }
    }

    class ConversationRightHolder extends ConversationMessageHolder {
        private ImageView imageStatus;
        private ImageView imageStatusMedia;
        private ImageView imageFailedButton;


        public ConversationRightHolder(View row) {
            /*linearImageMessage = (LinearLayout) row.findViewById(R.id.rightLinearImageMessage);*/
            progressBarInfinite = (ProgressBar) row.findViewById(R.id.progressBarInfinite);
            textMessage = (TextView) row.findViewById(R.id.rightMessage);
            captionText = (TextView) row.findViewById(R.id.rightCaptionImage);
            imageMessage = (ChatImageRounded) row.findViewById(R.id.rightImageMessage);
            textTime = (TextView) row.findViewById(R.id.rightTime);
            textTimeMedia = (TextView) row.findViewById(R.id.rightTimeMedia);
            imageStatus = (ImageView) row.findViewById(R.id.imageStatus);
            imageStatusMedia = (ImageView) row.findViewById(R.id.imageStatusMedia);
            imageFailedButton = (ImageView) row.findViewById(R.id.imageFailed);
            btnOpenLink = (Button) row.findViewById(R.id.btnOpenLink);
            imagePlay = (ImageView) row.findViewById(R.id.rightImagePlay);
            imageLayout = (RelativeLayout) row
                    .findViewById(R.id.rightImageLayout);
            progressBarImage = (CircleProgressBar) row.findViewById(R.id.right_progressBar);
            buttonDownload = (ImageButton) row.findViewById(R.id.rightButtonDownload);
            progressBarImage.setColor(activity.getApplicationContext().getResources().getColor(R.color.colorPrimary));
            frameMainText = (FrameLayout) row.findViewById(R.id.frameMainText);
        }
    }

    class ConversationListHolder {
        private ListView listView;
        protected TextView textTime;

        public ConversationListHolder(View row) {
            listView = (ListView) row.findViewById(R.id.messageList);
            textTime = (TextView) row.findViewById(R.id.listTime);
        }
    }

    class ConversationInfoHolder {
        private TextView textDate;

        public ConversationInfoHolder(View row) {
            textDate = (TextView) row.findViewById(R.id.conversationInfo);
        }
    }

    class ConversationLeftOptionHolder {

        private TextView textMessage;
        protected ImageView imageMessage;
        protected TextView textTime;
        private LinearLayout menuList;


        public ConversationLeftOptionHolder(View row) {
            textMessage = (TextView) row.findViewById(R.id.leftMessage);
            imageMessage = (ImageView) row.findViewById(R.id.leftImageMessage);
            textTime = (TextView) row.findViewById(R.id.leftTime);
            menuList = (LinearLayout) row.findViewById(R.id.list_menu);
        }
    }

    class ConversationRightRecallHolder {

        private TextView textMessage;
        protected TextView textTime;


        public ConversationRightRecallHolder(View row) {
            textMessage = (TextView) row.findViewById(R.id.leftMessage);
            textTime = (TextView) row.findViewById(R.id.leftTime);
        }
    }

    class ConversationLoadEarlier {
        private Button btnLoad;

        public ConversationLoadEarlier(View row) {
            btnLoad = (Button) row.findViewById(R.id.conversationBtnLoad);
            btnLoad.setOnClickListener(viewClickListener);
        }
    }

    public void longClickAction(final Message msg, final int pos) {
        Log.w("apa", "masukSIni");
        final Dialog dialog;
        dialog = DialogUtil.customDialogConversation(activity);
        dialog.show();

        FrameLayout btnDelete = (FrameLayout) dialog.findViewById(R.id.btnDelete);
        FrameLayout btnDeleteRounded = (FrameLayout) dialog.findViewById(R.id.btnDeleteRounded);
        View garis1 = (View) dialog.findViewById(R.id.garis1);
        FrameLayout btnCopy = (FrameLayout) dialog.findViewById(R.id.btnCopy);
        View garis2 = (View) dialog.findViewById(R.id.garis2);
        FrameLayout btnResend = (FrameLayout) dialog.findViewById(R.id.btnResend);
        View garis3 = (View) dialog.findViewById(R.id.garis3);
        FrameLayout btnForward = (FrameLayout) dialog.findViewById(R.id.btnForward);
        FrameLayout btnForwardRounded = (FrameLayout) dialog.findViewById(R.id.btnForwardRounded);
        View garis4 = (View) dialog.findViewById(R.id.garis4);
        FrameLayout btnShare = (FrameLayout) dialog.findViewById(R.id.btnShare);
        View garis5 = (View) dialog.findViewById(R.id.garis5);
        FrameLayout btnEditMeme = (FrameLayout) dialog.findViewById(R.id.btnEditMeme);
        FrameLayout btnEditMemeRounded = (FrameLayout) dialog.findViewById(R.id.btnEditMemeRounded);
        View garis6 = (View) dialog.findViewById(R.id.garis6);
        FrameLayout btnRecall = (FrameLayout) dialog.findViewById(R.id.btnRecall);

        btnDelete.setVisibility(View.VISIBLE);
        btnResend.setVisibility(View.VISIBLE);
        garis3.setVisibility(View.VISIBLE);
        btnForward.setVisibility(View.VISIBLE);
        garis4.setVisibility(View.VISIBLE);
        btnShare.setVisibility(View.VISIBLE);
        if (msg.getType().equalsIgnoreCase(Message.TYPE_IMAGE)) {
            garis5.setVisibility(View.VISIBLE);
            btnEditMeme.setVisibility(View.VISIBLE);
            btnEditMemeRounded.setVisibility(View.VISIBLE);
        }

        if (msg.getSource().equalsIgnoreCase(sourceAddr)) {
            btnEditMemeRounded.setVisibility(View.GONE);
            garis6.setVisibility(View.VISIBLE);
            btnRecall.setVisibility(View.VISIBLE);
        } else {
            btnEditMeme.setVisibility(View.GONE);
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String urlFile = "";
                String caption = "";
                String fileServer = "";
                if (isJSONValid(msg.getMessage())) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(msg.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("s");
                            caption = jObject.getString("c");
                            if (urlFile.equalsIgnoreCase("")) {
                                fileServer = jObject.getString("u");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                File file = new File(urlFile);
                if (file.exists()) {
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String type = msg.getType();
                    String title = "";
                    if (Message.TYPE_IMAGE.equals(type)) {
                        shareIntent.setType("image*//*");
                        title += " Image";
                    } else if (Message.TYPE_VIDEO.equals(type)) {
                        shareIntent.setType("video*//*");
                        title += " Video";
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    activity.startActivity(Intent.createChooser(shareIntent, "Share " + title + " using"));
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final Dialog dialogConfirmation;
                dialogConfirmation = DialogUtil.customDialogConversationConfirmation(activity);
                dialogConfirmation.show();

                String delMessage = "Delete message '"
                        + Message.parsedMessageBody(msg, 10, activity.getApplicationContext()) + "'?";

                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                txtConfirmation.setText("Confirmation");
                descConfirmation.setVisibility(View.VISIBLE);
                descConfirmation.setText(delMessage);

                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirmation.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConversationActivity myActivity2 = (ConversationActivity) activity;
                        myActivity2.deleteMessage(msg, pos);
                        dialogConfirmation.dismiss();
                    }
                });
            }
        });

        btnEditMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, PhotoSortrActivity.class);
                intent.putExtra("file", msg.getMessage());
                intent.putExtra("type", Message.TYPE_IMAGE);
                activity.startActivity(intent);
            }
        });

        btnEditMemeRounded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(activity, PhotoSortrActivity.class);
                intent.putExtra("file", msg.getMessage());
                intent.putExtra("type", Message.TYPE_IMAGE);
                activity.startActivity(intent);
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent2 = new Intent(activity, ConfirmationSendFile.class);
                intent2.putExtra("file", msg.getMessage());
                intent2.putExtra("name", destinationAddr);
                intent2.putExtra("type", msg.getType());
                activity.startActivity(intent2);
            }
        });

        btnRecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final Dialog dialogConfirmation;
                dialogConfirmation = DialogUtil.customDialogConversationConfirmation(activity);
                dialogConfirmation.show();

                String delMessage = "Are you sure you want to " + activity.getResources().getString(R.string.menu_conversation_tarik) + " '"
                        + Message.parsedMessageBody(msg, 10, activity.getApplicationContext()) + "'?";

                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                txtConfirmation.setText("Confirmation");
                descConfirmation.setVisibility(View.VISIBLE);
                descConfirmation.setText(delMessage);

                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirmation.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirmation.dismiss();
                        ConversationActivity myActivity2 = (ConversationActivity) activity;
                        myActivity2.tarikPesan(msg);
                    }
                });
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(activity, NewSelectContactActivity.class);
                intent.putExtra("file", msg.getMessage());
                intent.putExtra("type", msg.getType());
                activity.startActivity(intent);
            }
        });
    }


    public void longClickActionDelete(final Message msg, final int pos) {

        final Dialog dialog;
        dialog = DialogUtil.customDialogConversation(activity);
        dialog.show();

        FrameLayout btnDeleteRounded = (FrameLayout) dialog.findViewById(R.id.btnDeleteRounded);
        btnDeleteRounded.setVisibility(View.VISIBLE);

        btnDeleteRounded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final Dialog dialogConfirmation;
                dialogConfirmation = DialogUtil.customDialogConversationConfirmation(activity);
                dialogConfirmation.show();

                String delMessage = "Delete message '"
                        + Message.parsedMessageBody(msg, 10, activity.getApplicationContext()) + "'?";

                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                txtConfirmation.setText("Confirmation");
                descConfirmation.setVisibility(View.VISIBLE);
                descConfirmation.setText(delMessage);

                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirmation.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConversationActivity myActivity2 = (ConversationActivity) activity;
                        myActivity2.deleteMessage(msg, pos);
                        dialogConfirmation.dismiss();
                    }
                });
            }
        });
    }

    class FileDownloadHandlerImageTumbP2PSave extends AsyncTask<Message, Message, Bitmap> {
        Message data;
        Message link;

        @Override
        protected Bitmap doInBackground(Message... params) {
            if (params.length == 0) {
                return null;
            }
            data = params[0];
            link = params[1];
            try {
                URL url = new URL(link.getMessage());
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(120000);//set 2 minutes
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("getBmpFromUrl error: ", e.getMessage().toString());
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            MessengerDatabaseHelper databaseHelper = MessengerDatabaseHelper.getInstance(activity);
            if (result != null) {
                String name = data.getSource();
                String additionalInfo = "";
                if (data.isGroupChat()) {
                    name = data.getSourceInfo();
                    if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                        name = "";
                    }

                    String gname = databaseHelper.getGroup(data.getSource()).getName();
                    if ("".equals(name)) {
                        additionalInfo = "Group Chat '" + gname + "'";
                    } else {
                        additionalInfo = " on " + gname;
                    }
                }

                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                db.open();
                FilesURL files;
                if (result != null) {
                    String regex = "[0-9]+";
                    if (!data.getSource().matches(regex)) {
                        Bitmap b = MediaProcessingUtil.fastblur(context, result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    } else {
                        Bitmap b = MediaProcessingUtil.fastblur(activity, result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    }
                } else {
                    files = new FilesURL((int) data.getId(), "0", "tumb", "");
                }

                db.insertFiles(files);
                db.close();
                result.recycle();

                if (data.isGroupChat()) {
                    name = data.getSourceInfo();
                    if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                        name = "";
                    }

                    String gname = databaseHelper.getGroup(data.getSource()).getName();
                    if ("".equals(name)) {
                        additionalInfo = "Group Chat '" + gname + "'";
                    } else {
                        additionalInfo = " on " + gname;
                    }
                }


                if (!"".equals(name)) {
                    Contact contact = databaseHelper.getContact(name);
                    String regex = "[0-9]+";
                    if (!name.matches(regex)) {
                    } else {
                        name = "+" + Utility.formatPhoneNumber(name);
                    }
                    if (contact != null) {
                        name = contact.getName();
                    }
                }

                Intent intent = new Intent(MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
                intent.putExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT, data);
                intent.putExtra(MessengerConnectionService.KEY_CONTACT_NAME, name + additionalInfo);
                activity.sendOrderedBroadcast(intent, null);
            }
        }
    }

    class FileDownloadHandlerImageThumbP2PSave extends AsyncTask<Message, Message, Bitmap> {
        Message data;
        Message link;

        @Override
        protected Bitmap doInBackground(Message... params) {
            if (params.length == 0) {
                return null;
            }
            data = params[0];
            link = params[1];
            try {
                URL url = new URL(link.getMessage());
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.connect();
                connection.setConnectTimeout(120000);//set 2 minutes
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("getBmpFromUrl error: ", e.getMessage().toString());
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            MessengerDatabaseHelper databaseHelper = MessengerDatabaseHelper.getInstance(activity);
            if (result != null) {
                String name = data.getSource();
                String additionalInfo = "";
                if (data.isGroupChat()) {
                    name = data.getSourceInfo();
                    if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                        name = "";
                    }

                    String gname = databaseHelper.getGroup(data.getSource()).getName();
                    if ("".equals(name)) {
                        additionalInfo = "Group Chat '" + gname + "'";
                    } else {
                        additionalInfo = " on " + gname;
                    }
                }

                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(activity);
                db.open();
                FilesURL files;
                if (result != null) {
                    String regex = "[0-9]+";
                    if (!data.getSource().matches(regex)) {
                        Bitmap b = MediaProcessingUtil.fastblur(context, result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    } else {
                        Bitmap b = MediaProcessingUtil.fastblur(activity, result, 10);
                        cache.getInstance();

                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String iname = "bc-" + n + ".jpg";
                        saveImage(b, iname);
                        files = new FilesURL((int) data.getId(), "0", "tumb", link.getMessage(), iname);
                    }
                } else {
                    files = new FilesURL((int) data.getId(), "0", "tumb", "");
                }

                db.insertFiles(files);
                db.close();
                result.recycle();

                if (data.isGroupChat()) {
                    name = data.getSourceInfo();
                    if (databaseHelper.getMyContact().getJabberId().equals(name)) {
                        name = "";
                    }

                    String gname = databaseHelper.getGroup(data.getSource()).getName();
                    if ("".equals(name)) {
                        additionalInfo = "Group Chat '" + gname + "'";
                    } else {
                        additionalInfo = " on " + gname;
                    }
                }


                if (!"".equals(name)) {
                    Contact contact = databaseHelper.getContact(name);
                    String regex = "[0-9]+";
                    if (!name.matches(regex)) {
                    } else {
                        name = "+" + Utility.formatPhoneNumber(name);
                    }
                    if (contact != null) {
                        name = contact.getName();
                    }
                }

                Intent intent = new Intent(MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
                intent.putExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT, data);
                intent.putExtra(MessengerConnectionService.KEY_CONTACT_NAME, name + additionalInfo);
                activity.sendOrderedBroadcast(intent, null);
            }
        }
    }

    protected Bitmap getRoundedBitmap(Bitmap srcBitmap, int cornerRadius) {
        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth(), // Width
                srcBitmap.getHeight(), // Height

                Bitmap.Config.ARGB_8888 // Config
        );
        Canvas canvas = new Canvas(dstBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        srcBitmap.recycle();

        return dstBitmap;
    }

    Transformation blurTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap blurred = new MediaProcessingUtil().fastblur(activity.getApplicationContext(), source, 25);
            source.recycle();
            return blurred;
        }

        @Override
        public String key() {
            return "blur()";
        }
    };

    private void saveImage(Bitmap finalBitmap, String iname) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        System.out.println(root + " Root value in saveImage Function");
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
//        File myDir = new File(root + "/Alhamdulillah Images");
        if (!fullCacheDir.exists()) {
            fullCacheDir.mkdirs();
        }
        File file = new File(fullCacheDir, iname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File[] files = fullCacheDir.listFiles();
        int numberOfImages = files.length;
        System.out.println("Total images in Folder " + numberOfImages);
    }
}