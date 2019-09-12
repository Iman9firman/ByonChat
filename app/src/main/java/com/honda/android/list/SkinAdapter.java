package com.honda.android.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.MainActivity;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.provider.Interval;
import com.honda.android.provider.IntervalDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.Skin;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.Validations;
import com.honda.android.utils.ValidationsKey;

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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SkinAdapter extends BaseAdapter
{
    public final static String URL_CEK_APPLY = "https://"+ MessengerConnectionService.HTTP_SERVER+"/themes/boleh.php";
    private static ArrayList<Skin> catArrayList = new ArrayList<Skin>();
    private static ArrayList<Skin> catArray = new ArrayList<Skin>();
    private LayoutInflater inflater;
    public Context context;
    Activity activity;
    ContactsFilter mContactsFilter;
    protected ProgressDialog pdialog;

    private AdapterCallback mAdapterCallback;

    public static interface AdapterCallback {
        void onMethodCallback();
    }



    public SkinAdapter(Activity acti, ArrayList<Skin> selectorSkin) {
        context = acti.getApplicationContext();
        activity = acti;
        this.catArrayList = selectorSkin;
        this.catArray = selectorSkin;
        this.mAdapterCallback = ((AdapterCallback) activity);
        inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (pdialog == null) {
            pdialog = new ProgressDialog(activity.getApplicationContext());
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    public int getCount() {
        return catArrayList.size();
    }

    public Object getItem(int position) {
        return catArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Skin> newList()    {
        ArrayList<Skin> list = catArrayList;
        return(list);
    }


    public class ViewHolder
    {
        TextView txtName;
        TextView txtDesc;
        ImageView imagePhoto;
        FrameLayout btnConfirm;
        FrameLayout btnDetail;
        FrameLayout btnDelete;
        RelativeLayout relativelay;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_skin_selector, null);

            holder.imagePhoto = (ImageView) convertView.findViewById(R.id.imagePhoto);
            holder.txtName = (TextView) convertView.findViewById(R.id.textTitle);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
            holder.btnConfirm = (FrameLayout) convertView.findViewById(R.id.buttonConfirm);
            holder.btnDetail = (FrameLayout) convertView.findViewById(R.id.buttonDetail);
            holder.btnDelete = (FrameLayout) convertView.findViewById(R.id.buttonDelete);
            holder.relativelay = (RelativeLayout) convertView.findViewById(R.id.layoutImageDesc);
            convertView.setTag(holder);
        }
        else  holder=(ViewHolder)convertView.getTag();

        holder.relativelay.setBackgroundResource(R.drawable.tags_rounded_corner);
        GradientDrawable drawable = (GradientDrawable) holder.relativelay.getBackground();
        drawable.setColor(Color.parseColor(catArrayList.get(position).getColor()));
        holder.imagePhoto.setImageBitmap(catArrayList.get(position).getLogo());
        holder.txtName.setText(catArrayList.get(position).getTitle().toUpperCase());
        holder.txtDesc.setText(catArrayList.get(position).getDesc());
        holder.btnDelete.setVisibility(View.VISIBLE);
        if(catArrayList.get(position).getDesc().equalsIgnoreCase("original")){
            holder.btnDelete.setVisibility(View.INVISIBLE);
        }
        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
                alertbox.setMessage("Are you sure you want to Apply?");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        String key = new ValidationsKey().getInstance(context).key(false);
                        if (key.equalsIgnoreCase("null")){
                            Toast.makeText(context,R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        }else{
                            new cekApply(activity).execute(key,catArrayList.get(position).getTitle());
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
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Blm ada tampilannya", Toast.LENGTH_SHORT).show();
            }

        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
                alertbox.setMessage("Are you sure you want to Delete?");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {


                        IntervalDB db = new IntervalDB(context);
                        db.open();
                        Cursor cursorSelect = db.getSingleContact(4);
                        boolean delete = true;
                        if(cursorSelect.getCount()>0){
                            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
                            if(skin.equalsIgnoreCase(catArrayList.get(position).getTitle())){
                                delete = false;
                            }
                        }

                        if (delete){
                            db.deleteSkin(catArrayList.get(position).getTitle());
                            db.close();
                            mAdapterCallback.onMethodCallback();
                        }else{
                            Toast.makeText(context, R.string.themes_aktif, Toast.LENGTH_SHORT).show();
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

        return convertView;
    }

    public Filter getFilter() {
        if (mContactsFilter == null)
            mContactsFilter = new ContactsFilter();

        return mContactsFilter;
    }

    private class ContactsFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = catArray;
                results.count = catArray.size();
            }
            else {
                ArrayList<Skin> filteredContacts = new ArrayList<Skin>();
                for (Skin c : catArray) {
                    if (c.getTitle().toUpperCase().contains( constraint.toString().toUpperCase() )) {
                        filteredContacts.add(c);
                    }
                }
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            catArrayList = (ArrayList<Skin>) results.values;
            notifyDataSetChanged();
        }
    }

    public synchronized void refresAdapter(ArrayList<Skin> list) {
        catArrayList.clear();
        catArrayList.addAll(list);
        catArray.clear();
        catArray.addAll(list);
        notifyDataSetChanged();
    }


    class cekApply extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String code2 = "400";
        String desc;
        String themesName;
        private MessengerDatabaseHelper messengerHelper;

        public cekApply(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            pdialog = ProgressDialog.show(mContext, "",
                    "Please wait a moment", true);
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {
                themesName = key[1];
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
                }

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("nama_theme",  new Validations().getInstance(context).getTitle()));
                nameValuePairs.add(new BasicNameValuePair("nama_theme_baru", key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_CEK_APPLY);
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
                    jObject = new JSONObject(content);

                    if(content.contains("\"code\"")){
                        code2 = jObject.getString("code");
                        desc = jObject.getString("description");
                        error = true;
                    }else{
                        if(content.contains("\"boleh_ganti\"")){
                            code2 = jObject.getString("boleh_ganti");
                            desc = "Your Theme is locked until "+jObject.getString("tgl_habis");
                        }
                    }
                } else {
                    //Closes the connection.
                    error = true;
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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){

                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                            Toast.makeText(mContext,R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        }else{
                            new cekApply(mContext).execute(key,themesName);
                        }
                    }else{
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                if(code2.equalsIgnoreCase("true")){
                    IntervalDB db = new IntervalDB(context);
                    db.open();
                    Cursor cursor = db.getSingleContact(4);
                    if (cursor.getCount()>0) {
                        db.deleteContact(4);
                    }
                    Interval interval = new Interval();
                    interval.setId(4);
                    interval.setTime(themesName);
                    db.createContact(interval);
                    db.close();
                    Intent intent;
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from","0" );
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    activity.startActivity(intent);
                    activity.finish();
                }else{
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
