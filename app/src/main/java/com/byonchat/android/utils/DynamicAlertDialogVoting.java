package com.byonchat.android.utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DynamicAlertDialogVoting extends DialogFragment {

	String dialogTag;
	String title;
	int icon;
	String message;
	String datahelper;
	boolean showNegativeButton;
	FragmentActivity fragmentActivity;
	String groupid = "";
	String creatorVoting = "";
	String voting_id = "";
	String choice_answer = "";
	String dateTime = "";
	String sifatVoting = "";
	String votingTimer = "";
	TextView questionTv;
	TextView titleTime;
	TextView textViewTime;
	RadioGroup rg;
	int selectedId;
	String radiovalue;
	private boolean iscreatorVoting;
	private double runcountTime = 0;

	public DynamicAlertDialogVoting() {}

	public static DynamicAlertDialogVoting newInstance(FragmentActivity fragmentActivity, String title, int icon, String message, boolean showNegativeButton, boolean iscreatorVoting, String datahelper) {
		DynamicAlertDialogVoting frag = new DynamicAlertDialogVoting();
		if(null != fragmentActivity)frag.fragmentActivity = fragmentActivity;
		frag.title = title;
		frag.icon = icon;
		frag.message = message;
		frag.showNegativeButton = showNegativeButton;
		frag.iscreatorVoting = iscreatorVoting;
		if(null != datahelper)
			frag.datahelper = datahelper;
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("icon", String.valueOf(icon));
		args.putString("message", message);
		args.putBoolean("showNegativeButton", showNegativeButton);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// FOR USE MY LAYOUT
		final View view = inflater.inflate(R.layout.dialog_answer_voting, container);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		titleTime = (TextView)view.findViewById(R.id.titleTime);
		titleTime.setText("Waktu tersisa:");
		textViewTime = (TextView)view.findViewById(R.id.textViewTime);
		questionTv = (TextView)view.findViewById(R.id.multi_tfpopup_edit);
		questionTv.setText(message);
		TextView infoDesc = (TextView)view.findViewById(R.id.form_freetext_info);
		infoDesc.setText("Voting");
		TextView headerText = (TextView)view.findViewById(R.id.form_header_edit);
		headerText.setText("Pertanyaan");

		Button dialogButtonOk = (Button) view.findViewById(R.id.multi_confirm_ok);


		final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ly_dynamicView);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(DataScreenSize.PixelToDP(getActivity(), 10), 0, DataScreenSize.PixelToDP(getActivity(), 10), 0);//l,t,r,b

		String[] si = null;
		Log.i("--datahelper[0]--", datahelper);
		if(datahelper.contains(("_#_"))){
			si = datahelper.split("\\_#_");
			groupid = si[0];
			creatorVoting = si[1];
			voting_id = si[2];
			choice_answer = si[3];
			dateTime = si[5];
			sifatVoting = si[6];
			votingTimer = si[7];
		}

		String[] arrtime1 = dateTime.split("\\.");
		String time1 = arrtime1[0];
		Date tdate = new Date();
		String time2 = String.valueOf(new Timestamp(tdate.getTime()));
		String cvoteTimer = votingTimer;
		if(cvoteTimer.equalsIgnoreCase("0"))
			cvoteTimer = "24";
		else if(cvoteTimer.equalsIgnoreCase("1"))
			cvoteTimer = "48";
		if(cvoteTimer.equalsIgnoreCase("2"))
			cvoteTimer = "72";
		long millisIn24Hours = 1000 * 60 * 60 * Integer.parseInt(cvoteTimer);
		Date parsedDate = null;
		Date parsedDate2 = null;
		Date parsedDate3 = null;
		String timeBalance = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			parsedDate = (Date) formatter.parse(time1);
			parsedDate2 = (Date) formatter.parse(time2);
			Date hours24ago = new Date(parsedDate.getTime() + millisIn24Hours);
			timeBalance = String.valueOf(new Timestamp(hours24ago.getTime()));
			parsedDate3 = (Date) formatter.parse(timeBalance);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long diffInMilli = (parsedDate2.getTime()-parsedDate.getTime());
		long diffInMilli2 = (parsedDate3.getTime()-parsedDate.getTime());

		long diffInMilli3 = diffInMilli2 - diffInMilli;
		int seconds3 = (int) (diffInMilli3 / 1000) ;
		String startCountTime = timeConversion(seconds3);
		//Log.i("............seconds3...........", ""+seconds3+" ; "+startCountTime+" :: "+votingTimer);

		textViewTime.setText(startCountTime);
		CounterClass timer = new CounterClass(diffInMilli3,1000);
		timer.start();

		Log.i("--choice_answer--", choice_answer);
		String[] answerSplit = choice_answer.split("\\|");
		final int lengtAnswer = answerSplit.length;
		Log.i("--answerSplit.length--", ""+answerSplit.length);
		//add radio buttons
		final RadioButton[] rb = new RadioButton[lengtAnswer];
		rg = new RadioGroup(view.getContext()); //create the RadioGroup
		rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
		for(int i=0; i<lengtAnswer; i++){
			rb[i]  = new RadioButton(view.getContext());
			rb[i].setText(answerSplit[i]);
			Log.i("--answerSplit--", answerSplit[i]);
			rb[i].setId(i);
			rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout

		}
		linearLayout.addView(rg);

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected
				//selectedId = rg.getCheckedRadioButtonId();
				selectedId = checkedId;
				RadioButton rb=(RadioButton)view.findViewById(checkedId);
				Log.i("--RADIO BUTTON SELECTED CODE ID--", ""+checkedId);
				radiovalue = rb.getText().toString();
				Log.i("--RADIO BUTTON SELECTED CODE VALUE--", ""+radiovalue);
			}
		});

		dialogButtonOk.setText("OK");
		dialogButtonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			//
				if(null != radiovalue){
					if(!radiovalue.equalsIgnoreCase("")){
						double limitCountTime = runcountTime / 1000.0;
						if(limitCountTime > 1){
							getDialog().dismiss();
							SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
							sendPostReqAsyncTask.execute(new String[]{
									MessengerConnectionService.GROUP_SERVER + "insert_group_accept_voting",
									voting_id,
									"6281902337829",//nomer hardcode
									"1",
									"1",
									groupid});
						}else{
							DialogUtil.generateAlertDialog(fragmentActivity, "Voting", "Maaf waktu Anda habis!").show();
						}
					}else{
						DialogUtil.generateAlertDialog(fragmentActivity,"Voting","Harap pilih salah satu jawaban!").show();
					}
				}else{
					DialogUtil.generateAlertDialog(fragmentActivity,"Voting","Harap pilih salah satu jawaban!").show();
				}

			}
		});

		//getDialog().setCancelable(false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		// FOR ANIMATION DIALOG
	//	getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
	}

	@Override
	public void show(FragmentManager manager, String tag)
	{
		dialogTag = tag;
		super.show(manager, dialogTag);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private String timeConversion(int totalSeconds) {

		final int MINUTES_IN_AN_HOUR = 60;
		final int SECONDS_IN_A_MINUTE = 60;

		int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
		int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
		int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
		int hours = totalMinutes / MINUTES_IN_AN_HOUR;

		//String r = hours + " hours " + minutes + " minutes " + seconds + " seconds";
		String r = hours + ":" + plusOne(minutes)+minutes + ":" + plusOne(seconds)+seconds + "";
		Log.i("TOTAL COUNT TIME = ", r);
		return r;
	}

	private String plusOne(int number){
		String d = "";
		if(number < 10){
			d = "0";
		}
		return d;
	}

	class CounterClass extends CountDownTimer {
		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			//textViewTime.setText("Completed."); 
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long millis = millisUntilFinished;
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
					TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
					TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
			runcountTime = millis;
			//Log.i("--START_COUNT_TIME_VOTING--", hms+" ; "+millis); 
			textViewTime.setText(hms);
		}
	}

	private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			String paramUrl = params[0];
			String paramjson1 = params[1];
			String paramjson2 = params[2];
			String paramjson3 = params[3];
			String paramjson4 = params[4];
			String paramjson5 = params[5];
			HttpClient httpClient = null;
			try {
				httpClient = HttpHelper.createHttpClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
			HttpPost httpPost = new HttpPost(paramUrl);
			BasicNameValuePair invitesBasicNameValuePair1 = new BasicNameValuePair("voting_id", paramjson1);
			BasicNameValuePair invitesBasicNameValuePair2 = new BasicNameValuePair("answer_name", paramjson2);
			BasicNameValuePair invitesBasicNameValuePair3 = new BasicNameValuePair("answer_code", paramjson3);
			BasicNameValuePair invitesBasicNameValuePair4 = new BasicNameValuePair("answer_content", paramjson4);
			BasicNameValuePair invitesBasicNameValuePair5 = new BasicNameValuePair("groupid", paramjson5);

			List nameValuePairList = new ArrayList();
			nameValuePairList.add(invitesBasicNameValuePair1);
			nameValuePairList.add(invitesBasicNameValuePair2);
			nameValuePairList.add(invitesBasicNameValuePair3);
			nameValuePairList.add(invitesBasicNameValuePair4);
			nameValuePairList.add(invitesBasicNameValuePair5);

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

