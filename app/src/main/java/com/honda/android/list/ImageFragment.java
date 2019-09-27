package com.honda.android.list;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.utils.TouchImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.regex.Pattern;

public class ImageFragment extends Fragment implements TouchImageView.OnTouchListeners {
    public static final String KEY_BUNDLE_FILENAME = "com.honda.android.list.ImageFragment.FILENAME";
    public static final String KEY_BUNDLE_FILECAPTION = "com.honda.android.list.ImageFragment.FILECAPTION";
    private Uri imageUri;
    private String caption;
    private boolean show = true;
    LinearLayout mainCaption;
    TextView testCaption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUri = Uri.fromFile(new File((String) getArguments().get(
                KEY_BUNDLE_FILENAME)));
        caption = (String) getArguments().get(KEY_BUNDLE_FILECAPTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View iv = inflater.inflate(R.layout.conversation_image_gallery, container, false);

        TouchImageView imageView = (TouchImageView) iv.findViewById(R.id.touchImageView);

        imageView.setBackgroundColor(Color.BLACK);
//        try {
//            imageView.setImageBitmap(decodeUri(imageUri));
            Picasso.with(getContext()).load(imageUri).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imageView);
//        } catch (FileNotFoundException e) {Q

//            e.printStackTrace();
//        }
        imageView.setOnTouchListener(this);

        mainCaption = (LinearLayout) iv.findViewById(R.id.caption);
        testCaption = (TextView) iv.findViewById(R.id.textCaption);
        Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
        boolean isHTML = htmlPattern.matcher(caption).matches();
        if (isHTML) {
            if (caption.contains("<")) {
                testCaption.setText(Html.fromHtml(Html.fromHtml(caption).toString()));
            } else {
                testCaption.setText(Html.fromHtml(caption));
            }
        } else {
            testCaption.setText(Html.fromHtml(caption));
        }

        if(caption.length()>0){
            FooterAnimation();
            mainCaption.setVisibility(View.VISIBLE);
        }


        return iv;
    }


    @Override
    public void onTouch() {
        if(caption.length()>0){
            if(show){
                mainCaption.setVisibility(View.VISIBLE);
                FooterAnimation();
                show = false;
            }else{
                mainCaption.setVisibility(View.GONE);
                headerAnimation();
                show=true;
            }
        }

    }

    public void FooterAnimation() {
        Animation hide = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        mainCaption.startAnimation(hide);
    }

    public void headerAnimation() {
        Animation hide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_from_top);
        mainCaption.startAnimation(hide);
    }
}
