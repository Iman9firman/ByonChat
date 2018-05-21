package com.byonchat.android.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.byonchat.android.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lukmanpryg on 7/22/2016.
 */
public class ListVoucherParticipantOutletsAdapter extends BaseExpandableListAdapter{
    private Context _context;
    private List<ItemVoucherParticipantOutlets> _listGroupTitle; // header titles
    private HashMap<String, List<ItemVoucherParticipantOutlets>> _listDataMembers;

    public ListVoucherParticipantOutletsAdapter(Context context, List<ItemVoucherParticipantOutlets> listGroupTitle,
                                 HashMap<String, List<ItemVoucherParticipantOutlets>> listDataMembers) {
        this._context = context;
        this._listGroupTitle = listGroupTitle;
        this._listDataMembers = listDataMembers;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataMembers.get(this._listGroupTitle.get(groupPosition).getPlace())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ItemVoucherParticipantOutlets memData=(ItemVoucherParticipantOutlets)getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_voucher_participant_outlets_member, null);
        }

        TextView txtDisName= (TextView) convertView.findViewById(R.id.txtdistrict);
        TextView mNumber = (TextView) convertView.findViewById(R.id.number);
        mNumber.setText((childPosition+1)+". ");
        txtDisName.setText(memData.getPlace());
//        txtDisName.setText(Html.fromHtml("<b>"+memData.getPlace()+"</b>"));
//        TextView txtNum= (TextView) convertView.findViewById(R.id.txtdnum);
//        txtNum.setText(Html.fromHtml("<b>"+memData.getNum()+"</b>"));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataMembers.get(this._listGroupTitle.get(groupPosition).getPlace()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listGroupTitle.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listGroupTitle.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }



    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ItemVoucherParticipantOutlets gData = (ItemVoucherParticipantOutlets) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_voucher_participant_outlets_title, null);
        }

        /*ALWAYS EXPANDED*/
        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);
        /*ALWAYS EXPANDED*/

        TextView txtProName= (TextView) convertView.findViewById(R.id.txtprovince);
        txtProName.setText(gData.getPlace());
//        txtProName.setText(Html.fromHtml("<b>"+gData.getPlace()+"</b>"));
//        TextView txtNum= (TextView) convertView.findViewById(R.id.txtpnum);
//        txtNum.setText(Html.fromHtml("<b>"+gData.getNum()+"</b>"));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
