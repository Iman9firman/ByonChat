package com.byonchat.android.contacts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.list.utilLoadImage.TextLoader;
import com.byonchat.android.provider.Group;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

	Context context;
	List<IconItem> rowItems;
	protected ArrayList items;
	public ImageLoader imageLoader;
	public TextLoader textLoader;
	CustomAdapter(Context context, List<IconItem> rowItems) {
		this.context = context;
		this.rowItems = rowItems;
		imageLoader = new ImageLoader(context);
		textLoader = new TextLoader(context);
	}

	@Override
	public int getCount() {
		return rowItems.size();
	}

	@Override
	public Object getItem(int position) {
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return rowItems.indexOf(getItem(position));
	}

	/* private view holder class */
	private class ViewHolder {
		Target profile_pic;
		TextView member_name;
		TextView status;
		TextView contactType;
		ImageButton roomBtn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_test, null);
			holder = new ViewHolder();

			holder.member_name = (TextView) convertView
					.findViewById(R.id.textTitle);
			holder.profile_pic = (Target) convertView.findViewById(R.id.imagePhoto);
			holder.status = (TextView) convertView.findViewById(R.id.textInfo);
			holder.contactType = (TextView) convertView
					.findViewById(R.id.dateInfo);
			holder.roomBtn = (ImageButton) convertView
					.findViewById(R.id.roomsOpen);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		IconItem row_pos = rowItems.get(position);
		holder.member_name.setText(row_pos.getTitle());
		holder.status.setText(row_pos.getInfo());
		holder.contactType.setText(row_pos.getDateInfo());

		if (!row_pos.getTitle().equalsIgnoreCase("")) {

			if (!row_pos.isHaveRoooms()){
				holder.roomBtn.setVisibility(View.GONE);
			}

			if (row_pos.getChatParty() instanceof Group) {
				Picasso.with(context).load(R.drawable.ic_group).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.profile_pic);
			} else {
				String regex = "[0-9]+";
				if (!row_pos.getJabberId().matches(regex)) {
					textLoader.DisplayImage(row_pos.getTitle(), holder.member_name);
					Picasso.with(context).load("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + row_pos.getJabberId() + "_thumb.png").networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.profile_pic);
				} else {
					Picasso.with(context).load("https://"+MessengerConnectionService.F_SERVER+"/toboldlygowherenoonehasgonebefore/"+row_pos.getJabberId()+".jpg").networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.profile_pic);
				}
			}
		}
		return convertView;
	}

}
