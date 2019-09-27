package com.honda.android.contacts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.honda.android.R;
import com.honda.android.list.IconItem;
import com.honda.android.provider.ChatParty;
import com.honda.android.provider.Message;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class ChatsListFragment extends ListFragment {

	List<IconItem> rowItems;

	private MessengerDatabaseHelper messengerHelper;
	CustomAdapter adapter ;
	private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD = "SELECT count(*) total FROM "
			+ Message.TABLE_NAME
			+ " WHERE ("
			+ Message.DESTINATION
			+ "=? OR "
			+ Message.SOURCE + "=? )"
			+" AND status = 12";
	private static final SimpleDateFormat dateInfoFormat = new SimpleDateFormat(
			"dd/MM/yyyy", Locale.getDefault());
	private static final SimpleDateFormat hourInfoFormat = new SimpleDateFormat(
			"HH:mm", Locale.getDefault());

	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.contact, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (messengerHelper == null) {
			messengerHelper = MessengerDatabaseHelper.getInstance(getContext());
		}

		rowItems = new ArrayList<IconItem>();

		Cursor cursor;
		String myJabberId = messengerHelper.getMyContact().getJabberId();
		cursor = messengerHelper.query(
				getContext().getString(R.string.sql_chat_list),
				new String[]{myJabberId, Message.TYPE_READSTATUS, myJabberId, Message.TYPE_READSTATUS});

		int indexName = cursor.getColumnIndex(com.honda.android.provider.Contact.NAME);
		int indexJabberId = cursor.getColumnIndex("number");
		int indexMessage = cursor.getColumnIndex(Message.MESSAGE);
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE), 0, 0, 0);
		while (cursor.moveToNext()) {
			String jabberId = cursor.getString(indexJabberId);
			String name = cursor.getString(indexName);

			String message = cursor.getString(indexMessage);
			if (message == null)
				continue;

			Message vo = new Message(cursor);
			message = Message.parsedMessageBodyHtmlCode(vo, getContext());
			Date d = null;

			d = vo.getSendDate();

			String dInfo = null;
			if (d != null) {
				if (d.getTime() < cal.getTimeInMillis()) {
					dInfo = dateInfoFormat.format(d);
				} else {
					dInfo = hourInfoFormat.format(d);
				}
			}
			ChatParty cparty = null;
			if (vo.isGroupChat()) {
				cparty = messengerHelper.getGroup(jabberId);
			} else {
				if (name != null) {
					cparty = new com.honda.android.provider.Contact(name, jabberId, "");
				} else {
					name = "+" + Utility.formatPhoneNumber(jabberId);
					String regex = "[0-9]+";
					if (!jabberId.matches(regex)) {
						name = jabberId;
					}
				}
			}


			long total = 0;
			Cursor cursor2 = messengerHelper.query(
					SQL_SELECT_TOTAL_MESSAGES_UNREAD,
					new String[]{jabberId,
							jabberId});
			int indexTotal = cursor2.getColumnIndex("total");
			while (cursor2.moveToNext()) {
				total = cursor2.getLong(indexTotal);
			}
			cursor2.close();

			IconItem iconItem = new IconItem(jabberId, name, message,
					dInfo, cparty, total, Message.getStatusMessage(vo, myJabberId));
			if (cparty instanceof com.honda.android.provider.Contact) {
				// setProfilePicture(iconItem, (Contact) cparty);
			}
			rowItems.add(iconItem);

		}
		adapter = new CustomAdapter(getActivity(), rowItems);
		setListAdapter(adapter);
		getListView().setFastScrollEnabled(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.chat_item_menu, menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		super.onStart();

		/*if (!MessengerConnectionService.started) {
			MessengerConnectionService.startService(this);
		}*/
	}

	@Override
	public void onResume() {
		super.onResume();
		/*refreshChatList();
		((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE))
				.cancel(NotificationReceiver.NOTIFY_ID);

		IntentFilter f = new IntentFilter(
				MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
		f.addAction(MessengerConnectionService.ACTION_MESSAGE_DELIVERED);
		f.addAction(MessengerConnectionService.ACTION_MESSAGE_SENT);
		f.addAction(MessengerConnectionService.ACTION_MESSAGE_FAILED);
		f.addAction(MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY);
		f.setPriority(2);
		context.registerReceiver(broadcastHandler, f);*/
	}
	@Override
	public void onPause() {
	//	context.unregisterReceiver(broadcastHandler);
		super.onPause();
	}

	public void refreshList() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

}
