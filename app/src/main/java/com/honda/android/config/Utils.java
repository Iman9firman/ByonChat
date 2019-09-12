package com.honda.android.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

	private Context context;
	private SharedPreferences sharedPref;

	private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
	private static final int KEY_MODE_PRIVATE = 0;
	private static final String KEY_SESSION_ID = "sessionId",
			FLAG_MESSAGE = "message";

	public Utils(Context context) {
		this.context = context;
		sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF,
				KEY_MODE_PRIVATE);
	}

	public void storeSessionId(String sessionId) {
		Editor editor = sharedPref.edit();
		editor.putString(KEY_SESSION_ID, sessionId);
		editor.commit();
	}

	public String getSessionId() {
		return sharedPref.getString(KEY_SESSION_ID, null);
	}
	
	public String getSendMessageJSON(String message) {
		String json = null;

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("message", message);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public String getSendMessageGroupJSON(
							String message, String type,
							int sticky, int order_sticky, 
							String groupId,
							int status, String messageid, String useropenmessage) {
		String json = null;

		try {
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", getSessionId());
			jObj.put("message", message);
			jObj.put("type", type);
			jObj.put("sticky", sticky);
			jObj.put("order_sticky", order_sticky);
			jObj.put("groupId", groupId);
			jObj.put("status", status);
			jObj.put("messageid", messageid);
			jObj.put("useropenmessage", useropenmessage);

			json = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public String messageC(String type,String msg,String msgId,String add){
		JSONObject obj = new JSONObject();
		try {
			obj.put("msgType",type);
			obj.put("msg",msg);
			obj.put("msgId",msgId);
			obj.put("msgFrom",add);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

}
