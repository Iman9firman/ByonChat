package com.byonchat.android.personalRoom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.adapter.DetailPictureAdapter;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.personalRoom.transformer.DepthPageTransformer;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.UtilsPD;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lukmanpryg on 4/27/2016.
 */
public class PictureDetailActivity extends AppCompatActivity implements DetailPictureFragment.OnFragmentInteractionListener, AppBarLayout.OnOffsetChangedListener {

    public ArrayList<PictureModel> data = new ArrayList<>();
    int pos;
    private static String URL_DELETE_PICTURE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/delete_photo.php";
    Toolbar toolbar;
    private ImageView mPrevious, mNext;
    private TextView mTitle, mTimestamp, mDescription;
    private ViewPager mViewPager;
    private DetailPictureAdapter adapter;
    private String pmyuserid, puserid, purl, purlthumb, ptitle, ptimestamp, pdesc, pid, pflag, pColor;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private AppBarLayout mAppBarLayout;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        setContentView(R.layout.activity_pr_picture_detail);

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTitle = (TextView) findViewById(R.id.title);
        mTimestamp = (TextView) findViewById(R.id.timestamp);
        mDescription = (TextView) findViewById(R.id.description);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);
        pmyuserid = data.get(pos).getMyuserid();
        pColor = data.get(pos).getColor();

        mTitle.setText(data.get(pos).getTitle());
        mTimestamp.setText("Updates on : " + data.get(pos).getTgl_upload());
        mDescription.setText(Html.fromHtml(data.get(pos).getDescription()));

        adapter = new DetailPictureAdapter(getSupportFragmentManager(), data);
        mViewPager.setAdapter(adapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        if (data.get(pos).getMyuserid().equalsIgnoreCase(data.get(pos).getUserid())) {
            adapter.removeFirst();
            adapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(pos - 1);
        } else {
            mViewPager.setCurrentItem(pos);
        }

        mPrevious = (ImageView) findViewById(R.id.previous);
        mNext = (ImageView) findViewById(R.id.next);
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });

        toolbar.setTitle(" ");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setTitle(" ");
        dynamicToolbarColor();
        toolbarTextAppernce();

        mAppBarLayout.addOnOffsetChangedListener(this);


    }

    private void dynamicToolbarColor() {
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor("#" + pColor));
        collapsingToolbarLayout.setStatusBarScrimColor(Color.parseColor("#" + pColor));
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                mIsTheTitleVisible = true;
                collapsingToolbarLayout.setTitle(mTitle.getText().toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor(Color.parseColor("#" + pColor));
                }
            }

        } else {

            if (mIsTheTitleVisible) {
                mIsTheTitleVisible = false;
                collapsingToolbarLayout.setTitle(" ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor(Color.BLACK);
                }

            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = true;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pmyuserid.equalsIgnoreCase(puserid)) {
            getMenuInflater().inflate(R.menu.menu_pr_picture, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (pmyuserid.equalsIgnoreCase(puserid)) {
            switch (id) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
                default:
                    if (id == R.id.btnEdit) {
                        DialogFragmentEditPicture d = new DialogFragmentEditPicture();
                        d.DialogFragmentEditPicture(purl, purlthumb, ptitle, ptimestamp, pdesc, pmyuserid, puserid, pid, pflag);
                        d.show(getSupportFragmentManager(), "dialog");
                        return true;
                    } else if (id == R.id.btnDelete) {
                        final Dialog dialogConfirmation;
                        dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
                        dialogConfirmation.show();

                        TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                        TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                        txtConfirmation.setText("Confirm Delete");
                        descConfirmation.setVisibility(View.VISIBLE);
                        descConfirmation.setText("Do you want to delete this picture?");

                        Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                        Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);
                        btnNo.setText("Cancel");
                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirmation.dismiss();
                            }
                        });

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirmation.dismiss();
                                new deletePicture().execute(URL_DELETE_PICTURE, puserid, pid);
                            }
                        });
                    }
                    return super.onOptionsItemSelected(item);
            }
        } else {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentCreated(String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String pmyuserid, String puserid, String pid, String pflag, String pColor) {
        this.purl = purl;
        this.purlthumb = purlthumb;
        this.ptitle = ptitle;
        this.ptimestamp = ptimestamp;
        this.pdesc = pdesc;
        this.pmyuserid = pmyuserid;
        this.puserid = puserid;
        this.pid = pid;
        this.pflag = pflag;
        this.pColor = pColor;

        mTitle.setText(ptitle);
        if (ptitle.equalsIgnoreCase("") || ptitle == null) {
            toolbar.setVisibility(View.GONE);
        }
        mTimestamp.setText("Updates on : " + ptimestamp);
        mDescription.setText(Html.fromHtml(pdesc));
    }


    class deletePicture extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(PictureDetailActivity.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            data.put("id_foto", params[2]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {
                Toast.makeText(PictureDetailActivity.this, "Picture has been deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PictureDetailActivity.this, "Failed to delete picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onUserSelectValue(String title, String timestamp, String desc) {
        ptitle = title;
        ptimestamp = timestamp;
        pdesc = desc;

        mTitle.setText(title);
        mTimestamp.setText("Updates on : " + timestamp);
        Spanned spanned = Html.fromHtml(desc);
        mDescription.setText(spanned);
    }

}
