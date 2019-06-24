package com.byonchat.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.tabRequest.MapsViewActivity;
import com.byonchat.android.tabRequest.RelieverListActivity;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.SingleTop;
import com.toptoche.searchablespinnerlibrary.SearchableListDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

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

    @View(R.id.spinnerGender)
    Spinner header_gender;

    private Context mContext;
    private String headerName, lat, relieverDetail, totals, gender_pos;

    public OnSpinnerChangeListener onChanger;

    public HeaderRatingRecyclerView(Context context, String gender_pos, String headerName, String latlong, String _relieverDetail, String total, OnSpinnerChangeListener onChanger) {
        this.mContext = context;
        this.headerName = headerName;
        this.gender_pos = gender_pos;
        this.lat = latlong;
        this.relieverDetail = _relieverDetail;
        this.relieverDetail = _relieverDetail;
        this.totals = total;
        this.onChanger = onChanger;

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
                maps.putExtra("XTRA_NAME",headerName.split(" - ")[0]);
                maps.putExtra(XTRA_RELIEVER_JSON, relieverDetail);
                maps.putExtra(XTRA_LATITUDE, lat.split(":")[0]);
                maps.putExtra(XTRA_LONGITUDE, lat.split(":")[1]);
                mContext.startActivity(maps);
            }
        });

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Tampilkan semua");
        spinnerArray.add("Laki - laki");
        spinnerArray.add("Perempuan");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            header_gender.setBackground(mContext.getResources().getDrawable(R.drawable.spinner_background));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        header_gender.setAdapter(spinnerArrayAdapter);

        if(gender_pos.equalsIgnoreCase("Laki - laki")){
            header_gender.setSelection(1);
        }else if(gender_pos.equalsIgnoreCase("Perempuan")){
            header_gender.setSelection(2);
        }else {
            header_gender.setSelection(0);
        }

        header_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                onChanger.onItemChanges(position, header_gender.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Expand
    private void onExpand() {
        header_img_arrow.setRotation(180);
    }

    @Collapse
    private void onCollapse() {
        header_img_arrow.setRotation(0);
    }

    public interface OnSpinnerChangeListener {
        void onItemChanges(int position, String gender);
    }
}
