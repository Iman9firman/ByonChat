package com.honda.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.tabRequest.MapsViewActivity;
import com.honda.android.tabRequest.RelieverListActivity;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.mkg_header_rating_layout)
public class HeaderRatingRecyclerView {

    public static final String XTRA_RELIEVER_JSON = "XTRA_RELIEVER_JSON";
    public static final String XTRA_DETAILS_JSON = "XTRA_DETAILS_JSON";
    public static final String XTRA_LATITUDE = "XTRA_LATITUDE";
    public static final String XTRA_LONGITUDE = "XTRA_LONGITUDE";

    private static String TAG = "HeaderRecyclerView";

    @View(R.id.header_name)
    TextView header_name;

    @View(R.id.frame_total)
    LinearLayout vFrameTotal;

    @View(R.id.header_img_arrow)
    ImageView header_img_arrow;

    @View(R.id.child_button_cancel_approve)
    Button child_button_cancel_approve;

    @View(R.id.jumlahReq)
    TextView totalReq;

    private Context mContext;
    private String headerName, lat, relieverDetail, totals;

    public HeaderRatingRecyclerView(Context context, String headerName, String latlong, String _relieverDetail, String total) {
        this.mContext = context;
        this.headerName = headerName;
        this.lat = latlong;
        this.relieverDetail = _relieverDetail;
        this.relieverDetail = _relieverDetail;
        this.totals = total;


    }

    @Resolve
    private void onResolve() {
        header_name.setText(headerName);
        totalReq.setText(totals);
        child_button_cancel_approve.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Log.w("kaka", "sisp");
                Intent maps = new Intent(mContext, MapsViewActivity.class);
                maps.putExtra(XTRA_RELIEVER_JSON, relieverDetail);
                maps.putExtra(XTRA_LATITUDE, lat.split(":")[0]);
                maps.putExtra(XTRA_LONGITUDE, lat.split(":")[1]);
                mContext.startActivity(maps);
            }
        });
    }

    @Expand
    private void onExpand() {
        header_img_arrow.setRotation(180);
        vFrameTotal.setVisibility(android.view.View.VISIBLE);
    }

    @Collapse
    private void onCollapse() {
        vFrameTotal.setVisibility(android.view.View.GONE);
        header_img_arrow.setRotation(0);
    }
}
