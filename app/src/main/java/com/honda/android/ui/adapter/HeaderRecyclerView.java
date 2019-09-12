package com.honda.android.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.honda.android.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.SingleTop;

@Parent
@SingleTop
@Layout(R.layout.mkg_header_layout)
public class HeaderRecyclerView {

    private static String TAG = "HeaderRecyclerView";

    @View(R.id.header_name)
    TextView header_name;

    @View(R.id.header_img_arrow)
    ImageView header_img_arrow;

    private Context mContext;
    private String headerName;

    public HeaderRecyclerView(Context context, String headerName) {
        this.mContext = context;
        this.headerName = headerName;
    }

    @Resolve
    private void onResolve() {
        header_name.setText(headerName);
    }

    @Expand
    private void onExpand() {
        header_img_arrow.setRotation(180);
    }

    @Collapse
    private void onCollapse() {
        header_img_arrow.setRotation(0);
    }
}
