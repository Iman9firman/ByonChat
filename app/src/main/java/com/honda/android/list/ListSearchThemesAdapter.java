package com.honda.android.list;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.SearchThemesActivity;
import com.honda.android.SkinSelectorActivity;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.utilLoadImage.ImageLoader;
import com.honda.android.provider.IntervalDB;
import com.honda.android.provider.Skin;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Asus on 9/16/2014.
 */
public class ListSearchThemesAdapter extends BaseAdapter
{
    public static final String URL = "https://"+ MessengerConnectionService.HTTP_SERVER+"/uploads/skins/";
    private static ArrayList<ItemListSearchTheme> ArrayList;
    private LayoutInflater inflater;
    public Context context;
    ArrayList<Skin> themeArrayList;
    public ImageLoader imageLoader;
    AlertDialog.Builder alertbox;
    ProgressDialog mProgressDialog;
    public ListSearchThemesAdapter(Context ctx, ArrayList<Skin> themeArrayLists) {
        context = ctx;
        this.ArrayList = new ArrayList<ItemListSearchTheme>();
        this.themeArrayList = themeArrayLists;
        inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(ctx);

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Please wait a moment");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

    }

    public void add(ArrayList<ItemListSearchTheme> newresults){
        this.ArrayList.addAll(newresults);
    }

    public int getCount() {
        return ArrayList.size();
    }

    public Object getItem(int position) {
        return ArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }




    public class ViewHolder
    {
        TextView txtName,txtDetail;
        TextView textPoint;
        FrameLayout buttonDownload;
        FrameLayout btnInfo;
        ImageView logo;
        RelativeLayout relativelay;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_search_themes, null);

            holder.txtName = (TextView) convertView.findViewById(R.id.titleTxt);
            holder.txtDetail = (TextView) convertView.findViewById(R.id.detailTxt);
            holder.textPoint = (TextView) convertView.findViewById(R.id.textPoint);
            holder.buttonDownload = (FrameLayout) convertView.findViewById(R.id.buttonDownload);
            holder.btnInfo = (FrameLayout) convertView.findViewById(R.id.buttonInfo);
            holder.logo = (ImageView) convertView.findViewById(R.id.logo);
            holder.relativelay = (RelativeLayout) convertView.findViewById(R.id.layoutImageDesc);
            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();
        holder.relativelay.setBackgroundResource(R.drawable.tags_rounded_corner);
        GradientDrawable drawable = (GradientDrawable) holder.relativelay.getBackground();
        drawable.setColor(Color.parseColor("#"+ArrayList.get(position).getColor()));
        imageLoader.DisplayImage(URL + ArrayList.get(position).getLogo2(), holder.logo);
        holder.txtName.setText(ArrayList.get(position).getName());
        String desc =ArrayList.get(position).getDesc();

        if (desc.length() > 25) {
           desc =  desc.substring(0, 25)+"...";
        }

        holder.textPoint.setText(ArrayList.get(position).getPoin()+" pts");
        holder.txtDetail.setText(desc);

        boolean showDownload = true;

        for (int i = 0; i < themeArrayList.size(); i++) {
           if(ArrayList.get(position).getName().toLowerCase().equalsIgnoreCase(themeArrayList.get(i).getTitle())){
               showDownload= false;
           }
        }

        if(showDownload){
            holder.buttonDownload.setVisibility(View.VISIBLE);
        }else{
            holder.buttonDownload.setVisibility(View.INVISIBLE);
        }

        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(NetworkInternetConnectionStatus.getInstance(context).isOnline(context)){
                    alertbox = new AlertDialog.Builder(context);
                    alertbox.setMessage("Are you sure you want to Download Theme "+ ArrayList.get(position).getName().toUpperCase()+ "?");
                    alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            holder.buttonDownload.setVisibility(View.INVISIBLE);
                            arg0.dismiss();
                            mProgressDialog.show();
                            new DownloadImages(context,
                                    ArrayList.get(position).getName(),
                                    ArrayList.get(position).getDesc(),
                                    ArrayList.get(position).getColor(),
                                    "https://"+ MessengerConnectionService.HTTP_SERVER+"/uploads/skins/"+ArrayList.get(position).getLogo(),
                                    "https://"+ MessengerConnectionService.HTTP_SERVER+"/uploads/skins/"+ArrayList.get(position).getLogoHeader(),
                                    "https://"+ MessengerConnectionService.HTTP_SERVER+"/uploads/skins/"+ArrayList.get(position).getBackground())
                                    .execute();
                        }

                    });
                    alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    alertbox.show();
                }else{
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });



        holder.btnInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                    alertbox.setTitle(ArrayList.get(position).getName().toUpperCase());
                    alertbox.setMessage(ArrayList.get(position).getDesc());
                    alertbox.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    alertbox.show();
            }

        });
        return convertView;

    }

    public void success(){
        ((SearchThemesActivity) context).finish();
        Intent intent = new Intent(context, SkinSelectorActivity.class);
        context.startActivity(intent);
    }


    public class DownloadImages extends AsyncTask{
        String[] URLs;
        private Context mContext;
        String title,descripsi,color;
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        public DownloadImages(Context context,String title,String descripsi,String color,String data1,String data2,String data3) {
          URLs = new String[]{data1, data2, data3};
            mContext = context;
            this.title=title;
            this.descripsi=descripsi;
            this.color=color;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            for (int i= 0;i<URLs.length;i++) {
                try {
                    URL url = new URL(URLs[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmapArray.add(BitmapFactory.decodeStream(input));
                    publishProgress(i);
                }catch (Exception e) {
                    mProgressDialog.dismiss();
                }
            }
            return null;
        }

        @Override
        protected  void onProgressUpdate(Object... values){
            super.onProgressUpdate(values);
            if(bitmapArray.size()==3){
                mProgressDialog.dismiss();
                    IntervalDB  db = new IntervalDB(context);
                    db.open();
                    Cursor cursor =  db.getCountSkin();
                    Skin skin = new Skin(title,descripsi,"#"+color,bitmapArray.get(0),bitmapArray.get(1),bitmapArray.get(2));
                    cursor.close();
                    db.createSkin(skin);
                    db.close();
                    success();
            }
        }
    }
}


