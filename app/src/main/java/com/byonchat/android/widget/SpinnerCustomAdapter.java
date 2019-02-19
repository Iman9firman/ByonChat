package com.byonchat.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class SpinnerCustomAdapter extends ArrayAdapter<String> {

    private ArrayList<String> values;

    public SpinnerCustomAdapter(Context context, int resourceId,
                                String url,String bcUser, ArrayList<String> values) {
        super(context, resourceId, values);
        this.values = values;

        new getListTask(url).execute(bcUser);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public String getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position));
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position));

        return label;
    }

    class getListTask extends AsyncTask<String , Void , ArrayList<String>>{

        private String urlStr;
        private ArrayList<String> valuesNew = new ArrayList<>();
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();

        public getListTask(String url){
            this.urlStr = url;
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {

            HashMap<String, String> data = new HashMap<>();
            data.put("bc_user", strings[0]);
            valuesNew.add("--Please Select--");
            String result = profileSaveDescription.sendPostRequest(urlStr, data);
            try {
                if (result != null){
                    if (result.length() > 0) {
                        final JSONArray jsonArrays = new JSONArray(result);

                        for (int ia = 0; ia < jsonArrays.length(); ia++) {
                            String l = jsonArrays.getJSONObject(ia).getString("spk");
                            valuesNew.add(l);
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return valuesNew;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            values = strings;
            notifyDataSetChanged();
            super.onPostExecute(strings);
        }
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
