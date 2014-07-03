package com.codepath.apps.basictwitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	public TweetArrayAdapter(Context context,List<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Tweet tweet = getItem(position);
		View view;
		if(convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			view = inflator.inflate(R.layout.tweet_item, parent, false);
		} else {
			view = convertView;
		}
		final ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
		TextView tvUserName = (TextView) view.findViewById(R.id.tvHandle);
		TextView tvBody = (TextView) view.findViewById(R.id.tvWhen);
		TextView tvCreatedat = (TextView) view.findViewById(R.id.tvContent);
		Button btRetweet = (Button) view.findViewById(R.id.btRetweet);
		Button btReply = (Button) view.findViewById(R.id.btReply);
		
		ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
		tvUserName.setText("@"+tweet.getUser().getScreenName());
		tvBody.setText(tweet.getBody());
		tvCreatedat.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        setRetweetedIcon(btRetweet, tweet.isRetweeted());
		setUpImageClick(ivProfileImage,tweet.getUser().getScreenName());
        setUpActionButtons(btReply, btRetweet, tweet);
        setUpItemClickListener(view, tweet);

        view.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			launchDetailsActivity(tweet);
		}
	});

		return view;
	}
	
	private void setUpActionButtons(Button btReply, final Button btRetweet, final Tweet twt) {
		btReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startTweetActivity(twt);	
			}
		});
		
		btRetweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmRetweet(btRetweet, twt);
			}
		});
	}
	
	private void setUpItemClickListener(View v, final Tweet clicked){
		
	}
	
	protected void launchDetailsActivity(Tweet clicked) {
		Intent showtweet = new Intent(getContext(), ShowTweet.class);
		showtweet.putExtra("tweet", clicked);
		getContext().startActivity(showtweet);
	}

	
	private void startTweetActivity(Tweet tweet) {
		Intent reply = new Intent(getContext(), ComposeNewTweet.class);
		reply.putExtra("purpose", "reply");
		reply.putExtra("screen_name", tweet.getUser().getScreenName());
		reply.putExtra("id", tweet.getUid());
		getContext().startActivity(reply);
		
	}
	
	protected void confirmRetweet(final Button btRetweet, final Tweet tweet) {
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
	    b.setTitle("Retweet this Post to your followers?");
	    b.setPositiveButton("Retweet", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {
	        	if(!tweet.isRetweeted()){
		        	retweet(btRetweet, tweet);
	        	}else{
	        		Toast.makeText(getContext(), "Already retweeted", Toast.LENGTH_SHORT).show();
	        	}
	        }
	    });
	    b.setNegativeButton("Cancel", null);
	    b.create().show();
	 }
	
	public void setUpImageClick(ImageView ivProfileImage,final String screenName) {
		ivProfileImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getContext(),ProfileActivity.class);
				i.putExtra("screen_name", screenName.substring(0));
				getContext().startActivity(i);
				
			}
		});
		
	}
	
private void retweet(final Button btRetweet, final Tweet tweet) {
		
		if(!tweet.isRetweeted()){
			TwitterApplication.getRestClient().retweet(String.valueOf(tweet.getUid()), new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject body) {
					
						tweet.setRetweeted(true);
						setRetweetedIcon(btRetweet, true);
					// ComposeTweetActivity.this.finish();
					super.onSuccess(body);
				}

				public void onFailure(Throwable e, JSONObject error) {
					// Handle the failure and alert the user to retry
					Toast.makeText(getContext(),
							"Excepton : " + e.getLocalizedMessage(),
							Toast.LENGTH_SHORT).show();
				}
			});
		}	
	}

	
	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		 sf.setLenient(true);
			Date d = null;
			try {
				d = sf.parse(rawJsonDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long currentTime = System.currentTimeMillis();
			long created = d.getTime();
			String timeAgo = DateUtils.getRelativeDateTimeString(getContext(), created, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();
			//String timeAgo = TimeUtils.millisToLongDHMS(currentTime - created);
			if(timeAgo.contains("/")){
				SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
			    SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
			    try {
					Date date = format1.parse(timeAgo);
					return format2.format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(timeAgo.contains("Yesterday")){
				return "1d";
			}
			String result = timeAgo.substring(0,  timeAgo.indexOf(","));
			int indexOfSpace = result.indexOf(" ");
			String t1 = result.substring(0,indexOfSpace);
			String t2 = result.substring(indexOfSpace+1, indexOfSpace+2);
			return t1+t2;
	}
	
	private void setRetweetedIcon(Button btRetweet, boolean isRetweeted){
		if(isRetweeted){
			btRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweet_done, 0, 0, 0);
		}else{
			btRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweet, 0, 0, 0);
		}
	}

}
