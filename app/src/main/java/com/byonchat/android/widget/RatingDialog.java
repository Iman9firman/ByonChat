package com.byonchat.android.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.byonchat.android.R;

/**
 * Created by Zharfan on 14/02/2019
 * */

public class RatingDialog extends Dialog implements View.OnClickListener {

    private MyRatingDialogListener listener;
    private TextView textKet;
    private RatingBar ratingBar;
    private EditText commentBox;

    private String title;
    private String subtitle;

    public RatingDialog(Activity activity){
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rating_layout);
        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_submit_ratinglayout :
                String comment = commentBox.getText().toString();
                Integer rating = (int) ratingBar.getRating();
                listener.userSubmit(comment, rating);
                dismiss();
                break;
            case R.id.but_cancel_ratinglayout :
                listener.userCancel();
                dismiss();
                break;
        }
    }

    public void set(String title,String subtitle){
        this.title = title;
        this.subtitle = subtitle;
    }

    @SuppressLint("SetTextI18n")
    private void initViews(){

        TextView textTitle = findViewById(R.id.text_title_ratinglayout);
        TextView textSubtitle = findViewById(R.id.text_subtitle_ratinglayout);
        textKet = findViewById(R.id.keterangan_ratinglayout);
        ratingBar = findViewById(R.id.ratingbar_ratinglayout);
        commentBox = findViewById(R.id.edittext_comment_ratinglayout);
        Button butCancel = findViewById(R.id.but_cancel_ratinglayout);
        Button butSubmit = findViewById(R.id.but_submit_ratinglayout);
        ratingBar.setRating(1.0f);
        textKet.setText("Kurang");

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating < 1.0f){
                ratingBar.setRating(1.0f);
            }
            switch ((int) rating){
                case 1 :
                    textKet.setText("Kurang");
                    break;
                case 2 :
                    textKet.setText("Cukup");
                    break;
                case 3 :
                    textKet.setText("Baik");
                    break;
                case 4 :
                    textKet.setText("Sangat Baik");
                    break;
                case 5 :
                    textKet.setText("Sempurna");
                    break;
            }
        });

        butCancel.setOnClickListener(this);
        butSubmit.setOnClickListener(this);
        textTitle.setText(title);
        textSubtitle.setText(subtitle);
    }

    public MyRatingDialogListener getListener() {
        return listener;
    }

    public void setListener(MyRatingDialogListener listener) {
        this.listener = listener;
    }

    public interface MyRatingDialogListener{
        void userSubmit(String comment,Integer rating);
        void userCancel();
    }
}
