package com.byonchat.android.adapter;

import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.list.ListMenu;
import com.byonchat.android.list.ListMenuHolder;

public class ListVotingAdapter extends ArrayAdapter<ListMenu>{

	FragmentActivity fragmentActivity; 
    int layoutResourceId;    
    ListMenu data = null;
    ListMenuHolder holder = null;
    String tag;
    String groupId;
    private int countlistdata = 0;
    
	private String voting_id;

    public ListVotingAdapter(FragmentActivity fragmentActivity, int layoutResourceId, ListMenu data, String groupId, String tag) {
        super(fragmentActivity, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.fragmentActivity = fragmentActivity;
        this.data = data;
        if(data.size() == 0 )countlistdata = 1;
        else countlistdata = data.size();
        this.tag = tag;
        this.groupId = groupId;
    }
    
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ListMenu lm = (ListMenu) data.get(position);
        
        if(row == null)
        {
            LayoutInflater inflater = (fragmentActivity).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListMenuHolder();
            holder.text = (TextView)row.findViewById(R.id.listmenuitemtext);
            holder.imgIcon = (ImageView)row.findViewById(R.id.listmenuitemicon);

            holder.text.setText(lm.text);
            holder.imgIcon.setImageResource(lm.imageIcon);
            
            row.setTag(holder);           
            
        }
        else
        {
            holder = (ListMenuHolder)row.getTag();
        }
        
        row.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v) {        		
        		/*//Toast.makeText(fragmentActivity, "Action List > groupId = "+lm.text2, Toast.LENGTH_LONG).show();
        		//Intent intent = new Intent(fragmentActivity, DummyChartActivity.class);

        		//====================================================================================
                TaskListener taskListener = new TaskListener() {

            			@Override
            			public void onTaskStart(FragmentActivity activity) {
            			}

            			@Override
            			public void onTaskFinished(boolean isCanceled, FragmentActivity activity, TaskResult taskResult) {
            				Log.i("==onTaskFinished errorMessage==",""+isCanceled+" >> "+taskResult.errorMessage);
            				//getDialog().dismiss();

            				if (taskResult.errorMessage != null) {
            					Toast.makeText(fragmentActivity, taskResult.errorMessage, Toast.LENGTH_LONG).show();
            					return;
            				}

            				if(taskResult.result.contains("error")){
            					Toast.makeText(fragmentActivity, taskResult.result, Toast.LENGTH_LONG).show();
            					return;
            				}


            			Map ret = null;
         				try {
         					ret = new Gson().fromJson(URLDecoder.decode(taskResult.result, "UTF-8"), Map.class);
         				} catch (JsonSyntaxException e) {
         					// TODO Auto-generated catch block
         					e.printStackTrace();
         				} catch (UnsupportedEncodingException e) {
         					// TODO Auto-generated catch block
         					e.printStackTrace();
         				}
         				if(null != ret){
         					String available = (String)ret.get("available");
         					Map<String,String> m = new HashMap<String, String>();

         					String groupid = "", voting_id = "", answer_name ="", answer_code="", answer_content="", timestamp="";
         					Log.i("--JSON CHART--", ret.toString());
         					if(available.equalsIgnoreCase("true")){
         						db.deleteAllDataAcceptVoting();
         						Map<String, String> ms;
         						ArrayList datas = (ArrayList)ret.get("data");
         						for (int i = 0; i < datas.size(); i++) {
         							LinkedTreeMap data = (LinkedTreeMap)datas.get(i);
         							groupid = data.get("groupid").toString();
         							voting_id = data.get("voting_id").toString();
         							answer_name = data.get("answer_name").toString();
         							answer_code = data.get("answer_code").toString();
         							answer_content = data.get("answer_content").toString();
         							timestamp = data.get("datetime").toString();

         							ModelTableGroupAcceptVoting mgav = new ModelTableGroupAcceptVoting();
         							mgav.setGroupId(groupid);
         							mgav.setVotingId(voting_id);
         							mgav.setAnswerName(answer_name);
         							mgav.setAnswerCode(answer_code);
         							mgav.setAnswerContent(answer_content);
         							mgav.setCreateTimestamp(timestamp);
         							db.addDataTableGroupAcceptVoting(mgav);
         						}
         					}

         					Intent intent = new Intent(fragmentActivity, DummyChartActivity2.class);
         	        		intent.putExtra("groupId", groupId);
         	        		intent.putExtra("voting_id", lm.text2);
         	        		Log.i(">>>LIST VOTING_ID>>>", lm.text2);
         	        		fragmentActivity.startActivity(intent);
         	        		//fragmentActivity.finish();


         					}else{
         						Toast.makeText(fragmentActivity, "DATA KOSONG", Toast.LENGTH_LONG).show();
         					}
            			}
            		};

            	voting_id = lm.text2;
         		final String TRANSACTION_TASK_KEY = "TASK-GET-ACCEPT-VOTING";
         		GetAcceptVotingTask task = new GetAcceptVotingTask(groupId, voting_id, fragmentActivity, TRANSACTION_TASK_KEY, fragmentActivity, taskListener, false, false);
         		task.execute();
                //====================================================================================

        		//Intent intent = new Intent(fragmentActivity, DummyChartActivityWebview.class);
	        	//intent.putExtra("groupId", groupId);
	        	//intent.putExtra("voting_id", lm.text2);
	        	//fragmentActivity.startActivity(intent);*/
        	}
        });
                
        return row;
    }
	
	@Override
	public int getViewTypeCount() {
		return countlistdata;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position;
	}
    
}

