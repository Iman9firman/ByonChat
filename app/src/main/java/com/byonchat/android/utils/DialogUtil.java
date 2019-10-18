package com.byonchat.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.model.Image;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class DialogUtil {
    public static AlertDialog.Builder generateAlertDialog(Activity activity,
                                                          String title, String message) {
        View form = activity.getLayoutInflater().inflate(
                R.layout.custom_information, null);
        TextView tv = (TextView) form.findViewById(R.id.customInformationText);
        tv.setText(Html.fromHtml(message));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setView(form);
        return builder;
    }

    public static AlertDialog.Builder generateAlertDialogLeft(Activity activity,
                                                              String title, String message) {
        View form = activity.getLayoutInflater().inflate(
                R.layout.custom_information_left, null);
        TextView tvN = (TextView) form.findViewById(R.id.name);
        TextView viewHarga = (TextView) form.findViewById(R.id.viewHarga);
        TextView tvD = (TextView) form.findViewById(R.id.desc);

        tvN.setText(title);
        if (message.equalsIgnoreCase("")) {
            viewHarga.setVisibility(View.GONE);
            tvD.setVisibility(View.GONE);
        }
        tvD.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(form);
        return builder;
    }

    public static AlertDialog.Builder generateAlertDialogLeftBOBO(final Activity activity,
                                                                  String title, String message, String viewMessage, String viewTitle, final String imaaa) {
        View form = activity.getLayoutInflater().inflate(
                R.layout.custom_information_left, null);
        TextView tvN = (TextView) form.findViewById(R.id.name);
        TextView ss = (TextView) form.findViewById(R.id.ss);
        TextView viewHarga = (TextView) form.findViewById(R.id.viewHarga);
        TextView tvD = (TextView) form.findViewById(R.id.desc);
        RelativeLayout relativeImage = (RelativeLayout) form.findViewById(R.id.relativeImage);
        relativeImage.setVisibility(View.GONE);


        if (!imaaa.equalsIgnoreCase("") && imaaa.endsWith(".jpg")) {
            Log.w("da", imaaa);
            relativeImage.setVisibility(View.VISIBLE);
            ImageView iam = (ImageView) form.findViewById(R.id.image);
            Picasso.with(activity).load(imaaa).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(iam);

            iam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    Intent intent = new Intent(activity, ZoomImageViewActivity.class);
                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, imaaa);
                    activity.startActivity(intent);
                }
            });

        }




     /*   if (!imaaa.equalsIgnoreCase("") && imaaa.endsWith(".jpg")) {

            relativeImage.setVisibility(View.VISIBLE);
            ImageView iam = (ImageView) form.findViewById(R.id.image);
            Picasso.with(activity).load(imaaa).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(iam);
            iam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    Intent intent = new Intent(activity, ZoomImageViewActivity.class);
                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, imaaa);
                    activity.startActivity(intent);
                }
            });

        }*/


        ss.setText("Nama");
        viewHarga.setText(viewMessage);
        tvN.setText(title);
        if (message.equalsIgnoreCase("")) {
            viewHarga.setVisibility(View.GONE);
            tvD.setVisibility(View.GONE);
        }
        tvD.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(form);
        return builder;
    }

    public static AlertDialog.Builder generateAlertDialogLeftNoImage(final Activity activity,
                                                                     final String title, String message, String viewMessage, String viewTitle) {
        View form = activity.getLayoutInflater().inflate(
                R.layout.custom_information_left, null);
        TextView tvN = (TextView) form.findViewById(R.id.name);
        TextView ss = (TextView) form.findViewById(R.id.ss);
        TextView viewHarga = (TextView) form.findViewById(R.id.viewHarga);
        TextView tvD = (TextView) form.findViewById(R.id.desc);
        RelativeLayout relativeImage = (RelativeLayout) form.findViewById(R.id.relativeImage);
        relativeImage.setVisibility(View.GONE);


        ss.setText(viewTitle);
        viewHarga.setText(viewMessage);
        tvN.setText(title);
        if (message.equalsIgnoreCase("")) {
            viewHarga.setVisibility(View.GONE);
            tvD.setVisibility(View.GONE);
            relativeImage.setVisibility(View.GONE);
        }
        tvD.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(form);
        return builder;
    }

    public static AlertDialog.Builder generateAlertDialogLeftImage(final Activity activity,
                                                                   final String title, String message, String viewMessage, String viewTitle, String username) {
        View form = activity.getLayoutInflater().inflate(
                R.layout.custom_information_left, null);
        TextView tvN = (TextView) form.findViewById(R.id.name);
        TextView ss = (TextView) form.findViewById(R.id.ss);
        RelativeLayout iama = (RelativeLayout) form.findViewById(R.id.relativeImage);
        ImageView iam = (ImageView) form.findViewById(R.id.image);
        TextView viewHarga = (TextView) form.findViewById(R.id.viewHarga);
        TextView tvD = (TextView) form.findViewById(R.id.desc);

        ss.setText(viewTitle != null ? viewTitle : "");
        viewHarga.setText(viewMessage != null ? viewMessage : "");
        iama.setVisibility(View.VISIBLE);
        Log.w("INI LISJ", new ValidationsKey().getInstance(activity).getTargetUrl(username) + "/bc_voucher_client/images/list_task/" + title.split(";")[0]);
        Picasso.with(activity).load(new ValidationsKey().getInstance(activity).getTargetUrl(username) + "/bc_voucher_client/images/list_task/" + title.split(";")[0]).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(iam);

        tvN.setVisibility(View.GONE);
        if (message.equalsIgnoreCase("")) {
            viewHarga.setVisibility(View.GONE);
            tvD.setVisibility(View.GONE);
        }
        tvD.setText(message);

        iam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(activity, ZoomImageViewActivity.class);
                intent.putExtra(ZoomImageViewActivity.KEY_FILE, new ValidationsKey().getInstance(activity).getTargetUrl(username) + "/bc_voucher_client/images/list_task/" + title.split(";")[0]);
                activity.startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(form);
        return builder;
    }


    public static Dialog customDialogConversation(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout linearlayout = (LinearLayout) dialog.findViewById(R.id.linearLayout1);
        FrameLayout frameLayout1 = (FrameLayout) dialog.findViewById(R.id.frameLayout1);

        GradientDrawable linear = (GradientDrawable) linearlayout.getBackground();
        linear.setColor(activity.getResources().getColor(R.color.softBlue3));

        GradientDrawable frame = (GradientDrawable) frameLayout1.getBackground();
        frame.setColor(activity.getResources().getColor(R.color.white));
        return dialog;
    }

    public static Dialog customDialogConversationConfirmation(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout linearlayout = (LinearLayout) dialog.findViewById(R.id.linearLayout1);
        FrameLayout frameLayout1 = (FrameLayout) dialog.findViewById(R.id.frameLayout1);

        GradientDrawable linear = (GradientDrawable) linearlayout.getBackground();
        linear.setColor(activity.getResources().getColor(R.color.softBlue3));

        GradientDrawable frame = (GradientDrawable) frameLayout1.getBackground();
        frame.setColor(activity.getResources().getColor(R.color.white));
        return dialog;
    }

    public static AlertDialog.Builder generateViewDialog(Activity activity,
                                                         String title, View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setView(v);
        return builder;
    }

    public static AlertDialog.Builder generateItemsDialog(Activity activity,
                                                          String title, String[] items,
                                                          DialogInterface.OnClickListener clickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setItems(items, clickListener);
        return builder;
    }

    public static AlertDialog.Builder generateItemsDialog(Activity activity,
                                                          String title, ArrayAdapter adapter,
                                                          DialogInterface.OnClickListener clickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setAdapter(adapter, clickListener);
        return builder;
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null)
            return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();

        return location;
    }
}
