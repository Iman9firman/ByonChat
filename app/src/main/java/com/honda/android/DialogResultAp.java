package com.honda.android;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.honda.android.suggest.SuggestGetSet;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogResultAp extends DialogFragment {

    private ListView listView;
    private Button mProceed;
    private TextView emptyList;
    String result;

    public static DialogResultAp newInstance(String result) {
        DialogResultAp f = new DialogResultAp();
        Bundle args = new Bundle();
        args.putString("result", result);

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_frame, container, false);
        listView = (ListView) dialog.findViewById(R.id.listview);
        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        emptyList = (TextView) dialog.findViewById(R.id.emptyList);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
        ArrayList<User> arrayOfUsers = new ArrayList<User>();
        UsersAdapter adapter = new UsersAdapter(getActivity(), arrayOfUsers);
        if(!result.contains("[")){
            listView.setVisibility(View.INVISIBLE);

            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(result);
        }else {
            listView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject c = jsonArray.getJSONObject(i);
                    String title = c.getString("nama_maskapai")+ " ( "+c.getString("kode_penerbangan") +" )";
                    String desc = c.getString("tujuan_penerbangan")+"\n"+c.getString("terminal_pintu")+"\n"+c.getString("waktu");
                    String logo = c.getString("logo_maskapai");
                    User user2 = new User(title,desc,logo);
                    adapter.add(user2);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            listView.setAdapter(adapter);


        }


        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getArguments().getString("result");

    }

    public class User {
        public String name;
        public String hometown;
        public String image;

        public User(String name, String hometown,String image) {
            this.name = name;
            this.hometown = hometown;
            this.image = image;
        }
    }

    public class UsersAdapter extends ArrayAdapter<User> {
        public UsersAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_pop_angkasa, parent, false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.name);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            Target profilePic=(Target) convertView.findViewById(R.id.imagePhoto);
            Picasso.with(getActivity()).load(user.image).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(profilePic);
            title.setText(user.name);
            desc.setText(user.hometown);
            return convertView;
        }
    }

}
