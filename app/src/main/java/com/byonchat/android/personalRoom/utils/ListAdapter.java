package com.byonchat.android.personalRoom.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private List<String> m_item;
    private List<String> m_path;
    public ArrayList<Integer> m_selectedItem;
    Context m_context;
    Boolean m_isRoot;
    public ListAdapter(Context p_context,List<String> p_item, List<String> p_path,Boolean p_isRoot) {
        m_context=p_context;
        m_item=p_item;
        m_path=p_path;
        m_selectedItem=new ArrayList<Integer>();
        m_isRoot=p_isRoot;
    }

    @Override
    public int getCount() {
        return m_item.size();
    }

    @Override
    public Object getItem(int position) {
        return m_item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int p_position, View p_convertView, ViewGroup p_parent)
    {
        View m_view = null;
        ViewHolder m_viewHolder = null;
        if (p_convertView == null)
        {
            LayoutInflater m_inflater = LayoutInflater.from(m_context);
            m_view = m_inflater.inflate(R.layout.cloudstorage_layout, null);
            m_viewHolder = new ViewHolder();
            m_viewHolder.m_tvFileName = (TextView) m_view.findViewById(R.id.tv_filename_cloud);
            m_viewHolder.m_tvDate = (TextView) m_view.findViewById(R.id.tv_date_cloud);
            m_viewHolder.m_tvCountFile = (TextView) m_view.findViewById(R.id.tv_countFile_cloud);
            m_viewHolder.m_ivIcon = (ImageView) m_view.findViewById(R.id.iv_image_cloud);
            m_viewHolder.m_cbCheck = (CheckBox) m_view.findViewById(R.id.cb_check_cloud);
            m_view.setTag(m_viewHolder);
        }
        else
        {
            m_view = p_convertView;
            m_viewHolder = ((ViewHolder) m_view.getTag());
        }
            m_viewHolder.m_tvFileName.setText(m_item.get(p_position));
            m_viewHolder.m_ivIcon.setImageResource(setFileImageType(new File(m_path.get(p_position))));
            m_viewHolder.m_tvDate.setText(getLastDate(p_position, m_viewHolder));
            m_viewHolder.m_tvCountFile.setText(getCountFile(p_position, m_viewHolder));
            m_viewHolder.m_cbCheck.setVisibility(View.GONE);
            /*if(m_viewHolder.m_tvFileName.getText().toString().equals("../"))
            {
                m_viewHolder.m_cbCheck.setVisibility(View.INVISIBLE);
            } else {
                m_viewHolder.m_cbCheck.setVisibility(View.VISIBLE);
            }*/
            m_viewHolder.m_cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        m_selectedItem.add(p_position);
                    } else {
                        m_selectedItem.remove(m_selectedItem.indexOf(p_position));
                    }
                }
            });
        return m_view;
    }

    class ViewHolder
    {
        CheckBox m_cbCheck;
        ImageView m_ivIcon;
        TextView m_tvFileName;
        TextView m_tvDate;
        TextView m_tvCountFile;
    }

    private int setFileImageType(File m_file)
    {
        int m_lastIndex=m_file.getAbsolutePath().lastIndexOf(".");
        String m_filepath=m_file.getAbsolutePath();
        if (m_file.isDirectory())
            return R.drawable.baseline_folder_black_24;
        else
        {
            if (m_lastIndex > 0){
                if(m_filepath.substring(m_lastIndex).equalsIgnoreCase(".png"))
                {
                    return R.drawable.baseline_insert_photo_black_24;
                }
                else if(m_filepath.substring(m_lastIndex).equalsIgnoreCase(".jpg"))
                {
                    return R.drawable.baseline_insert_photo_black_24;
                }
                else
                {
                    return R.drawable.baseline_insert_drive_file_black_24;
                }
            } else {
                return R.drawable.baseline_insert_drive_file_black_24;
            }

        }
    }

    String getLastDate(int p_pos , ViewHolder holder)
    {
        File m_file=new File(m_path.get(p_pos));
        if (holder.m_tvFileName.getText().toString().equals("../")) {
            return "";
        } else {
            SimpleDateFormat m_dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return m_dateFormat.format(m_file.lastModified());
        }
        /*if (m_file.isDirectory()){
             int countFile = m_file.listFiles().length;
             if (p_pos == 0){
                 return "";
             } else {
                 switch (countFile){
                     case 0 :
                         return "";
                     case 1 :
                         return String.valueOf(countFile + " item");
                     default :
                         return String.valueOf(countFile + " items");
                 }
             }
        } else {
            SimpleDateFormat m_dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return m_dateFormat.format(m_file.lastModified());
        }*/
    }

    String getCountFile(int p_pos, ViewHolder holder){
        File m_file=new File(m_path.get(p_pos));
        try {
            if (m_file.isDirectory()){
                int countFile = m_file.listFiles().length;
                if (holder.m_tvFileName.getText().toString().equals("../")){
                    return "";
                } else {
                    switch (countFile){
                        case 0 :
                            return "0 item";
                        case 1 :
                            return String.valueOf(countFile + " item");
                        default :
                            return String.valueOf(countFile + " items");
                    }
                }
            } else {
                return "";
            }
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
