package com.honda.android.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.list.utilLoadImage.ImageLoader;
import com.honda.android.provider.MessengerDatabaseHelper;

import java.util.ArrayList;


/**
 * Created by Asus on 9/16/2014.
 */
public class ListMemberCard extends BaseAdapter
{
    private static final int[] FROM_COLOR = new int[]{49, 179, 110};
    private static final int THRESHOLD = 3;

    private static ArrayList<ItemListMemberCard> catArrayList;
    private LayoutInflater inflater;
    public Context context;
    private MessengerDatabaseHelper dbhelper;
    public ImageLoader imageLoader;
    public ListMemberCard(Context ctx) {
        context = ctx;
        this.catArrayList = new ArrayList<ItemListMemberCard>();
        inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(context);
        }
        imageLoader=new ImageLoader(ctx);
    }

    public void add(ArrayList<ItemListMemberCard> newresults){
        this.catArrayList.addAll(newresults);
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

    public class ViewHolder
    {
        ImageView img_card_logo;
        TextView txt_name_card;
        LinearLayout topPanelLeft,topPanelcenter,topPanelRight;
        RelativeLayout relativeLayout5,relativeLayout4;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_member_card, null);

            holder.txt_name_card = (TextView) convertView.findViewById(R.id.txt_name_card);
           // holder.img_card = (ImageView) convertView.findViewById(R.id.img_card);
            holder.img_card_logo = (ImageView) convertView.findViewById(R.id.img_card_logo);
            holder.topPanelRight = (LinearLayout) convertView.findViewById(R.id.topPanelRight);
            holder.topPanelcenter = (LinearLayout) convertView.findViewById(R.id.topPanelcenter);
            holder.topPanelLeft = (LinearLayout) convertView.findViewById(R.id.topPanelLeft);
            holder.relativeLayout4 = (RelativeLayout) convertView.findViewById(R.id.relativeLayout4);
            holder.relativeLayout5 = (RelativeLayout) convertView.findViewById(R.id.relativeLayout5);
            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();

        if(catArrayList.get(position).getName().equalsIgnoreCase("")){
            holder.relativeLayout5.setVisibility(View.GONE);
        }else {
            imageLoader.DisplayImage("https://"+ MessengerConnectionService.HTTP_SERVER+"/mediafiles/kartu/"+catArrayList.get(position).getName().toLowerCase().replace(" ","_")+".png", holder.img_card_logo);
            holder.txt_name_card.setText(catArrayList.get(position).getName());
            Drawable mDrawableLetf = context.getResources().getDrawable(R.drawable.bg_card_buttom_left);
            mDrawableLetf.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            Drawable mDrawableRight = context.getResources().getDrawable(R.drawable.bg_card_buttom_right);
            mDrawableRight.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            Drawable mDrawableCenter = context.getResources().getDrawable(R.drawable.bg_card_buttom_center);
            mDrawableCenter.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            holder.topPanelRight.setBackground(mDrawableRight);
            holder.topPanelcenter.setBackground(mDrawableCenter);
            holder.topPanelLeft.setBackground(mDrawableLetf);
        }
        return convertView;
    }

}
