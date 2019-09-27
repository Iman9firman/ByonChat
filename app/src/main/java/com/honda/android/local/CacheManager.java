package com.honda.android.local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.util.Pair;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public enum CacheManager {
    INSTANCE;
    private final SharedPreferences sharedPreferences;

    CacheManager() {
        sharedPreferences = Byonchat.getApps().getSharedPreferences("byonchat.cache", Context.MODE_PRIVATE);
    }

    public static CacheManager getInstance() {
        return INSTANCE;
    }

    public void cacheLastImagePath(String path) {
        sharedPreferences.edit()
                .putString("last_image_path", path)
                .apply();
    }

    public void clearMessageNotifItems(long roomId) {
        sharedPreferences.edit()
                .remove("push_notif_message_" + roomId)
                .apply();
    }

    public String getLastImagePath() {
        return sharedPreferences.getString("last_image_path", "");
    }

    public void setLastChatActivity(boolean active, long roomId) {
        sharedPreferences.edit()
                .putBoolean("last_chat_status", active)
                .putLong("last_active_chat", roomId)
                .apply();
    }

    public Pair<Boolean, Long> getLastChatActivity() {
        return Pair.create(sharedPreferences.getBoolean("last_chat_status", false),
                sharedPreferences.getLong("last_active_chat", 0));
    }

    public void clearDraftComment(long roomId) {
        sharedPreferences.edit()
                .remove("draft_comment_" + roomId)
                .apply();
    }

    public void clearData() {
        sharedPreferences.edit().clear().apply();
    }
}

