package com.byonchat.android.helpers;

import android.support.v7.app.AppCompatActivity;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by byonc on 4/18/2017.
 */

public class Constants extends AppCompatActivity {

    public static final int REQUEST_CODE_CAPTURE = 2000;

    public static final int RESULT_REFRESH_ROOM = 1;

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;
    public static final int ERROR = 2003;

    public static final int MAX_LIMIT = 999;

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 23;
    public static final int PERMISSION_REQUEST_CAMERA = 24;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 124;

    public static final String DOWNLOAD_URL_WO_PARAM = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/video_local.php";
    public static final String DOWNLOAD_URL_W_PARAM = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/video_local.php?keywords=";

    public static final String URLLAPORSELECTED = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/selectapop.php";

    public static final String PREF_WRITE_EXTERNAL_STORAGE_REQUESTED = "writeExternalRequested";
    public static final String PREF_CAMERA_REQUESTED = "cameraRequested";

    public static final String EXTRA_ITEM = "extra_item";
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_COLOR = "extra_color";
    public static final String EXTRA_COLORTEXT = "extra_colortext";
    public static final String EXTRA_PARENT = "parent_comment";
    public static final String EXTRA_POSITION = "position_comment";
    public static final String EXTRA_JUMLAH_COMMENT = "jumlah_comment";
    public static final String EXTRA_TEXT_JUMLAH_COMMENT = "text_jumlah_comment";
    public static final String EXTRA_TEXT_CONTENT_COMMENT = "text_content_comment";
    public static final String EXTRA_TAB_MOVEMENT = "extra_tab_movement";
    public static final String EXTRA_GRID_SIZE = "extra_grid_size";
    public static final String EXTRA_ROOM = "EXTRA_ROOM";
    public static final String EXTRA_SERVICE_PERMISSION = "extra_service_permission";

    public static final String EXTRA_GRID_SIZE_THREE = "extra_grid_size_three";
    public static final String EXTRA_GRID_SIZE_FOUR = "extra_grid_size_four";

    protected static final String[] CAMERA_PERMISSION = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
    };

    protected static final int RC_CAMERA_PERMISSION = 128;

    protected static final int REQ_CAMERA = 1201;
    protected static final int REQ_GALLERY = 1202;
    protected static final int SEND_PICTURE_CONFIRMATION_REQUEST = 9203;
    protected static final int SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST = 9204;
    protected static final int POST_BEFORE_AFTER = 7204;
    protected static final int COMMENT_TREE = 7205;

    protected static final String URL_POST = "https://bb.byonchat.com/bc_voucher_client/webservice/proses/repost_attachment.php";

    public static String SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE status = ?";

    public static String SQL_SELECT_TOTAL_BADGE_TAB_MENU = "SELECT count(*) total FROM tab_menu_badge"
            + " WHERE id_tab = ?";

    public static String SQL_DELETE_BADGE_TAB_MENU = "DELETE FROM tab_menu_badge"
            + " WHERE id_tab = ?";

    public static String URL_LAPOR_SELECTED = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/selectapop.php";

    public static Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();

    public interface ACTION {
        public static String STARTFOREGROUND_ACTION = "com.byonchat.android.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.byonchat.android.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}

