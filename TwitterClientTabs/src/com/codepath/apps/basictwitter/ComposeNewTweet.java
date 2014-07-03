package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.userTimelineFragment;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

public class ComposeNewTweet extends FragmentActivity {
	
	private EditText etStatus;
	private TextView tvChars;
	private int MAX_CHARS = 140;
	private TwitterClient client;
	private Context context;
	private boolean isReplyAction;
	private ImageView ivPic;
	private TextView tvName;
	private TextView tvScreenName;
	private User currentUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_new_tweet);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
		isReplyAction = checkReply();
		etStatus = (EditText) findViewById(R.id.etStatus);
		tvChars = (TextView) findViewById(R.id.tvChars);
	    ivPic = (ImageView) findViewById(R.id.ivUserImg);
	    tvName = (TextView) findViewById(R.id.tvTwtName);
	    tvScreenName = (TextView) findViewById(R.id.tvScreenName);
	    
		if(isReplyAction){
			String replyScreen = getIntent().getStringExtra("screen_name");
			etStatus.setText(replyScreen);
			etStatus.setSelection(etStatus.getText().length());
		}
	    
	    etStatus.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		verifyCredentials();
		setCharsText();
		setUpStatusListener();
		
	}
	

	private boolean checkReply() {
		String purpose = getIntent().getStringExtra("purpose");
		if(purpose != null && purpose.equals("reply")){
			return true;
		}
		return false;
	}

	private void setUpProfileInfo() {
		tvName.setText(currentUser.getName());
		tvScreenName.setText(currentUser.getScreenName());
		Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivPic);
		
	}
	
	
	private void setCharsText() {
		int rem = MAX_CHARS - etStatus.getText().toString().length();
		String limit = rem + " characters remaining";
		tvChars.setText(limit);
		//miChars.setTitle(""+charsRem);
		
	}
	
	private void setUpStatusListener() {
		etStatus.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setCharsText();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	
	private void finishActivity(){
		setResult(RESULT_OK);
        this.finish();
	}
	
	private void verifyCredentials() {
		client = new TwitterClient(context);
		client.getMyInfo(new JsonHttpResponseHandler() { 
		@Override
	        public void onSuccess(int code, JSONObject json) {
            currentUser = User.fromJSON(json);
            setUpProfileInfo();
		}
		public void onFailure(Throwable e, JSONObject error) {
		    // Handle the failure and alert the user to retry
		   Toast.makeText(context, "Excepton : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		  }
		});	
	}
	
	private void tweet() {
		String st = etStatus.getText().toString();
		if(st == null || st.length() == 0 || st.equals("")){
			Toast.makeText(ComposeNewTweet.this,
					"Write something to post",
					Toast.LENGTH_LONG).show();
		}else if(st.length() > 140){
			Toast.makeText(ComposeNewTweet.this,
					"140 chars only",
					Toast.LENGTH_LONG).show();
		}else{
			postTweet(st);
		}
		
	}

	private void postTweet(String status){
		client = new TwitterClient(context);
		String inReplyTo = null;
		if(isReplyAction){
			inReplyTo = String.valueOf(getIntent().getLongExtra("id", 0));
		}
		client.postNewTweet(status, inReplyTo ,new JsonHttpResponseHandler() { 
		@Override
	            public void onSuccess(JSONObject object) {
			    Intent i = new Intent(ComposeNewTweet.this, TimelineActivity.class);
			    setResult(RESULT_OK, i);        
			    startActivity(i);
			    super.onSuccess(object);
		   }
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose_new_tweet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			this.finish();
			return true;			
		} else if(id == R.id.tweet_item) {
			tweet();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
