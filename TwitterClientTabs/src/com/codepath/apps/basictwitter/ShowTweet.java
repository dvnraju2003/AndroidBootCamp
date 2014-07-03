package com.codepath.apps.basictwitter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class ShowTweet extends Activity {
	
	private TextView showTweet;
	private TextView showScreenname;
	private ImageView showImage;
	private ImageView ivMedia;
	private Tweet tweet;
	private int MAX_CHARS = 140;
	private TwitterClient client;
	private Context context;
	private ImageButton ibTwtDetReply;
	private ImageButton ibTwtDetRT;
	private ImageView ivTwtDetPic;
	private TextView tvTwtDetName;
	private TextView tvTwtDetScreen;
	private TextView tvTwtDetContent;
	private TextView tvTwtDetTime;
	private TextView tvTwtDetRTCount;
	private EditText etTwtDetRep;
	private TextView tvTwtDetChars;
	private TextView tvTweetButton;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_tweet);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		tweet = (Tweet) getIntent().getSerializableExtra("tweet");
		context = this;
		client = new TwitterClient(this);
		initUIElements();
		setValuesToUI();
		initFunctions();
	}
	
	private void initUIElements() {
		ivTwtDetPic = (ImageView) findViewById(R.id.ivTwtDetPic);
		tvTwtDetName = (TextView) findViewById(R.id.tvTwtDetName);
		tvTwtDetScreen = (TextView) findViewById(R.id.tvTwtDetScreen);
		tvTwtDetContent = (TextView) findViewById(R.id.tvTwtDetContent);
		ivMedia = (ImageView) findViewById(R.id.ivMedia);
		tvTwtDetTime = (TextView) findViewById(R.id.tvTwtDetTime);
		tvTwtDetRTCount = (TextView) findViewById(R.id.tvRetweetC);
		etTwtDetRep = (EditText) findViewById(R.id.etTwtDetRep);
		tvTwtDetChars = (TextView) findViewById(R.id.tvTwtDetChars);
		tvTweetButton = (TextView) findViewById(R.id.tvTwtDetTweet);
		ibTwtDetReply = (ImageButton) findViewById(R.id.ibTwtDetReply);
		ibTwtDetRT = (ImageButton) findViewById(R.id.ibTwtDetRT);

	}
	
	private void setValuesToUI() {
		Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivTwtDetPic);
		tvTwtDetName.setText(tweet.getUser().getName());
		tvTwtDetScreen.setText(tweet.getUser().getScreenName());
		tvTwtDetContent.setText(tweet.getBody());
		if(tweet.getMediaUrl() != null && tweet.getMediaUrl().length() > 0){
			ivMedia.setVisibility(View.VISIBLE);
			Picasso.with(this).load(tweet.getMediaUrl()).into(ivMedia);
		}
		tvTwtDetTime.setText(getCreatedTime(tweet.getCreatedAt()));
		tvTwtDetRTCount.setText(""+tweet.getRetweet_count()+" ");
		etTwtDetRep.setHint("Reply to "+tweet.getUser().getName());
		setCharsText(MAX_CHARS);		
		setRetweetedIcon(tweet.isRetweeted());
	}

	private void setCharsText(int charsRem) {
		String limit = charsRem + "";
		tvTwtDetChars.setText(limit);

	}
	
	private void setRetweetedIcon(boolean isRetweeted){
		if(isRetweeted){
			ibTwtDetRT.setImageResource(R.drawable.ic_retweet_done);
		}else{
			ibTwtDetRT.setImageResource(R.drawable.ic_retweet);
		}
	}
	
	private void initFunctions() {
		setupEditTextListeners();
		setUpTweetButton();
		setUpImageButtons();

	}
	
	private void setupEditTextListeners() {
		etTwtDetRep.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			        etTwtDetRep.setText(tweet.getUser().getScreenName());
			        etTwtDetRep.setSelection(etTwtDetRep.getText().length());
			    }else {
			        
			    }
			   }
			});
		
		etTwtDetRep.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int length = etTwtDetRep.getText().toString().length();
				int rem = MAX_CHARS - length;
				setCharsText(rem);
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

	private void setUpImageButtons() {
		ibTwtDetReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startTweetActivity();
			}
		});
		
		ibTwtDetRT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmRetweet();
			}
		});
		
	}

	protected void startShareIntent() {
		String name=tweet.getUser().getScreenName().substring(0);
		String twtUrl = "https://twitter.com/"+name+"/status/"+tweet.getUid();
		String str = "Check out " + tweet.getUser().getScreenName()+"'s Tweet: "+twtUrl;
		Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.putExtra(Intent.EXTRA_TEXT, str);
	    shareIntent.setType("text/plain");
	    startActivity(Intent.createChooser(shareIntent, "Share Tweet"));		
	}

	protected void confirmRetweet() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
	    b.setTitle("Retweet this to your followers?");
	    b.setPositiveButton("Retweet", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {
	        	if(!tweet.isRetweeted()){
		        	retweet();
	        	}else{
	        		Toast.makeText(ShowTweet.this, "Already retweeted", Toast.LENGTH_SHORT).show();
	        	}
	        }
	    });
	    b.setNegativeButton("Cancel", null);
	    b.create().show();
	    }
	
	private void tweet() {
		String st = etTwtDetRep.getText().toString();
		if (st == null || st.length() == 0 || st.equals("") || st.equals(tweet.getUser().getScreenName())) {
			Toast.makeText(ShowTweet.this, "Write something to post",
					Toast.LENGTH_LONG).show();
		} else if (st.length() > 140) {
			Toast.makeText(ShowTweet.this, "140 chars only",
					Toast.LENGTH_LONG).show();
		} else {
			postTweet(st);
		}

	}

	private void postTweet(String status) {
		client.postNewTweet(status, String.valueOf(tweet.getUid()), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject body) {
				finishActivity();
				// ComposeTweetActivity.this.finish();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				// Handle the failure and alert the user to retry
				Toast.makeText(context,
						"Excepton : " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
private void retweet() {
		
		client.retweet(String.valueOf(tweet.getUid()), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject body) {
				finishActivity();
				// ComposeTweetActivity.this.finish();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				// Handle the failure and alert the user to retry
				Toast.makeText(context,
						"Excepton : " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}



	private void startTweetActivity() {
		Intent reply = new Intent(ShowTweet.this, ComposeNewTweet.class);
		reply.putExtra("purpose", "reply");
		reply.putExtra("screen_name", tweet.getUser().getScreenName());
		reply.putExtra("id", tweet.getUid());
		startActivity(reply);
		
	}

	private void setUpTweetButton() {
		tvTweetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tweet();
				//postTweet(etTwtDetRep.getText().toString());
				
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_tweet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if(item.getItemId() == android.R.id.home){
			setResult(RESULT_OK);
            this.finish();
            return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
    }
	
	private void finishActivity(){
		setResult(RESULT_OK);
        this.finish();
	}

	
	private String getCreatedTime(String time) {

		final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
		sf.setLenient(true);
		Date d = null;
		try {
			d = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DateFormat df = new SimpleDateFormat("h:mm a . dd MMM yy");
		return df.format(d);
	}	

}
