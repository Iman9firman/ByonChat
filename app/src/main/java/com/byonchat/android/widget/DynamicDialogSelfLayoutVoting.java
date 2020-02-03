package com.byonchat.android.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.adapter.FormSpinnerAdapterVoting;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.utils.DataScreenSize;
import com.byonchat.android.utils.DialogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DynamicDialogSelfLayoutVoting extends DialogFragment {
	
	String dialogTag;
	boolean screenSize;
	
	Context ctx;
	
	FragmentActivity fragmentActivity;
	String tag;
	String groupname, groupid;
	String voting_id;
	static List<View> lvw;
	static List<EditText> ltextContent;
	int maxcountanswer = 3;
    int countanswer = 0;
    
    EditText et;
    String answer = "";
    int maxTextValue = 30;
    public static int posValueSifat=0;
    public static int posValueTimer=0;
	
	public DynamicDialogSelfLayoutVoting() {}

	public static DynamicDialogSelfLayoutVoting newInstance(FragmentActivity fragmentActivity, String groupid, String groupname, String tag) {		
		DynamicDialogSelfLayoutVoting frag = new DynamicDialogSelfLayoutVoting();
		if(null != fragmentActivity)frag.fragmentActivity = fragmentActivity;
		if(null != groupid)
			frag.groupid = groupid;
		frag.groupname = groupname;
		frag.tag = tag;
		lvw = new ArrayList<View>();
		ltextContent = new ArrayList<EditText>();
		return frag;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.dialog_voting_group, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);         
        
        final Spinner spinnerSifat = (Spinner)view.findViewById(R.id.form_spinner_sifat);
        ArrayList<String> dataSpinner = new ArrayList<String>();
        dataSpinner.add("Umum");
        dataSpinner.add("Rahasia");
        FormSpinnerAdapterVoting spinnerArray = new FormSpinnerAdapterVoting(fragmentActivity, R.layout.form_spinner_item, dataSpinner);
        spinnerSifat.setAdapter(spinnerArray);
		
		final Spinner spinnerTimer = (Spinner)view.findViewById(R.id.form_spinner_timer);
        ArrayList<String> dataSpinTimer = new ArrayList<String>();
        dataSpinTimer.add("1 X 24");
        dataSpinTimer.add("2 X 24");
        dataSpinTimer.add("3 X 24");        
        FormSpinnerAdapterVoting spinnerArrayTimer = new FormSpinnerAdapterVoting(fragmentActivity, R.layout.form_spinner_item, dataSpinTimer);
        spinnerTimer.setAdapter(spinnerArrayTimer);
		
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ly_dynamicView);
        et = new EditText(view.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(DataScreenSize.PixelToDP(getActivity(), 10), 0, DataScreenSize.PixelToDP(getActivity(), 10), 0);//l,t,r,b
        et.setFilters(new InputFilter[] { filter,new InputFilter.LengthFilter(maxTextValue) });
        et.setLayoutParams(lp);       
        ltextContent.add(et);
        linearLayout.addView(et);
        
        et = new EditText(view.getContext());
        et.setFilters(new InputFilter[] { filter,new InputFilter.LengthFilter(maxTextValue) });
        et.setLayoutParams(lp);       
        ltextContent.add(et);
        linearLayout.addView(et);
        
        ImageView addanswer = (ImageView)view.findViewById(R.id.add_answer);
        addanswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(countanswer < maxcountanswer){
					EditText et = new EditText(view.getContext());
					et.setFilters(new InputFilter[] { filter,new InputFilter.LengthFilter(maxTextValue) });
					ltextContent.add(et);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			        lp.setMargins(DataScreenSize.PixelToDP(getActivity(), 10), 0, DataScreenSize.PixelToDP(getActivity(), 10), 0);//l,t,r,b
			        et.setLayoutParams(lp);
			        linearLayout.addView(et);
			        lvw.add(et);
			        et.requestFocus();
			        countanswer++;
				}
			}
		});
        
        ImageView removeanswer = (ImageView)view.findViewById(R.id.remove_answer);
        removeanswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        int szl = lvw.size()-1;
		        if(szl >= 0){
		        	linearLayout.removeView(lvw.get(szl));
		        	lvw.remove(szl);
		        	ltextContent.remove(szl);
		        	if(szl > 0)
		        		lvw.get(szl-1).requestFocus();
		        	else
		        		et.requestFocus();
		        	countanswer--;
		        }
			}
		});
        
        TextView infoDesc = (TextView)view.findViewById(R.id.form_freetext_info);
        infoDesc.setText("Buat Voting");
		TextView headerText = (TextView)view.findViewById(R.id.form_header_edit);
		headerText.setText("Pertanyaan");		
		
		Button dialogButtonOk = (Button) view.findViewById(R.id.multi_confirm_ok);
		Button dialogButtonCancel = (Button) view.findViewById(R.id.multi_confirm_cancel);
		
		dialogButtonOk.setText("BUAT");
		dialogButtonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText txtEdit = (EditText)view.findViewById(R.id.multi_tfpopup_edit);
				final String value = (String)txtEdit.getText().toString();
				if (value.trim().equalsIgnoreCase("")) {
					DialogUtil.generateAlertDialog(fragmentActivity,"Voting","Harap masukkan isi voting !").show();
		        	return;
				}
				for(int i=0; i<ltextContent.size(); i++){
					if(ltextContent.get(i).getText().toString().equalsIgnoreCase("")){
						DialogUtil.generateAlertDialog(fragmentActivity,"Voting","Harap masukkan isi jawaban !").show();
						return;
					}else{
						if(!answer.equalsIgnoreCase("")){
							answer = answer+"|"+ltextContent.get(i).getText().toString();
						}else{
							answer = ltextContent.get(i).getText().toString();
						}
					}

				}
				getDialog().dismiss();
				voting_id = "111222333";
				String choice_answers = answer;
				Date date= new Date();
				String datetime = String.valueOf(new Timestamp(date.getTime()));
				String total_choice_answer = String.valueOf(ltextContent.size());
				String sifat = spinnerSifat.getSelectedItem().toString();

				//voting_id, creator_name, question, answer, total_choice_answer, sifat_voting, voting_timer, groupid
				SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
				sendPostReqAsyncTask.execute(new String[]{
						MessengerConnectionService.GROUP_SERVER + "insert_group_voting",
						voting_id,
						"628158888248",//nomer hardcode
						value,
						answer,
						total_choice_answer,
						String.valueOf(posValueSifat),
						String.valueOf(posValueTimer),
						groupid});
			}
		});
		
		dialogButtonCancel.setText("BATAL");
		dialogButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
        
        return view;
    }
	
	@Override
	public void onActivityCreated(Bundle arg0) {
	    super.onActivityCreated(arg0);    
	    
	    //getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    	 
	}
	
	@Override 
	public void show(FragmentManager manager, String tag)
	{
		dialogTag = tag;
		super.show(manager, dialogTag);
	}
	
	@Override
	public void onDismiss(DialogInterface dialogInterface) {
	    super.dismiss();
	}
	
	InputFilter filter = new InputFilter() {
	    @Override
	    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
	        boolean keepOriginal = true;
	        StringBuilder sb = new StringBuilder(end - start);
	        for (int i = start; i < end; i++) {
	            char c = source.charAt(i);
	            //if (!Character.isLetterOrDigit(currentChar) && !Character.isSpaceChar(currentChar)) {    
                //     sourceAsSpannableBuilder.delete(i, i+1);
                // }
	            //for filter is value number
	            //if (Character.isLetter(c)) {    
	            //	sb.append(c);
                //}else
	            //    keepOriginal = false;
	            sb.append(c);
	            
	        }
	        if (keepOriginal)
	            return null;
	        else {
	            if (source instanceof Spanned) {
	                SpannableString sp = new SpannableString(sb);
	                TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
	                return sp;
	            } else {
	                return sb;
	            }           
	        }
	    }
	    private boolean isCharAllowed(char c) {
	        return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
	    }
	};

	private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			String paramUrl = params[0];
			String paramjson1 = params[1];
			String paramjson2 = params[2];
			String paramjson3 = params[3];
			String paramjson4 = params[4];
			String paramjson5 = params[5];
			String paramjson6 = params[6];
			String paramjson7 = params[7];
			String paramjson8 = params[8];
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(paramUrl);
			BasicNameValuePair invitesBasicNameValuePair1 = new BasicNameValuePair("voting_id", paramjson1);
			BasicNameValuePair invitesBasicNameValuePair2 = new BasicNameValuePair("creator_name", paramjson2);
			BasicNameValuePair invitesBasicNameValuePair3 = new BasicNameValuePair("question", paramjson3);
			BasicNameValuePair invitesBasicNameValuePair4 = new BasicNameValuePair("answer", paramjson4);
			BasicNameValuePair invitesBasicNameValuePair5 = new BasicNameValuePair("total_choice_answer", paramjson5);
			BasicNameValuePair invitesBasicNameValuePair6 = new BasicNameValuePair("answer_content", ".");
			BasicNameValuePair invitesBasicNameValuePair7 = new BasicNameValuePair("answer_code", ".");
			BasicNameValuePair invitesBasicNameValuePair8 = new BasicNameValuePair("sifat_voting", paramjson6);
			BasicNameValuePair invitesBasicNameValuePair9 = new BasicNameValuePair("voting_timer", paramjson7);
			BasicNameValuePair invitesBasicNameValuePair10 = new BasicNameValuePair("groupid", paramjson8);

			List nameValuePairList = new ArrayList();
			nameValuePairList.add(invitesBasicNameValuePair1);
			nameValuePairList.add(invitesBasicNameValuePair2);
			nameValuePairList.add(invitesBasicNameValuePair3);
			nameValuePairList.add(invitesBasicNameValuePair4);
			nameValuePairList.add(invitesBasicNameValuePair5);
			nameValuePairList.add(invitesBasicNameValuePair6);
			nameValuePairList.add(invitesBasicNameValuePair7);
			nameValuePairList.add(invitesBasicNameValuePair8);
			nameValuePairList.add(invitesBasicNameValuePair9);
			nameValuePairList.add(invitesBasicNameValuePair10);

			try {
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
				httpPost.setEntity(urlEncodedFormEntity);
				try {
					HttpResponse httpResponse = httpClient.execute(httpPost);
					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
						stringBuilder.append(bufferedStrChunk);
					}
					return stringBuilder.toString();
				} catch (ClientProtocolException cpe) {
					System.out.println("First Exception cause of HttpResponese :" + cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out.println("Second Exception cause of HttpResponse :" + ioe);
					ioe.printStackTrace();
				}
			} catch (UnsupportedEncodingException uee) {
				System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
				uee.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result.startsWith("error") || result==null){
				Toast.makeText(fragmentActivity, "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
			}else{
				if(result.equalsIgnoreCase("1")){
					Toast.makeText(fragmentActivity, "Success", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(fragmentActivity, "Please try again later", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

}

