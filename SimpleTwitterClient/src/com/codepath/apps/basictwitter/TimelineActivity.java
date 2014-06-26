package com.codepath.apps.basictwitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.codepath.apps.basictwitter.ComposeTweetDialog.ComposeTweetDialogListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends FragmentActivity implements ComposeTweetDialogListener{
	
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> atweets;
	private PullToRefreshListView lvTweets;
	//private ListView lvTweets;

	FragmentManager fm = getSupportFragmentManager();
	ComposeTweetDialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar ab = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#81a3d0"));     
        ab.setBackgroundDrawable(colorDrawable);
        ab.setIcon(R.drawable.ic_tweet);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		if(isNetworkAvailable()){
			//clear DB so that only new tweets are saved
			Tweet.deleteAll();
			populateTimeline();
		}else{
			Toast.makeText(this, "No network connectivity", Toast.LENGTH_SHORT).show();
			//load from DB
			atweets.clear();
			atweets.notifyDataSetInvalidated();
			atweets.addAll(Tweet.getAll());
			lvTweets.onRefreshComplete();
		}
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                // Your code to refresh the list contents
                // Make sure you call listView.onRefreshComplete()
                // once the loading is done. This can be done from here or any
                // place such as when the network request has completed successfully.
            	populateHomeTimeLineSinceLatest();
            } 
        });

		tweets = new ArrayList<Tweet>();
		atweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(atweets);
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> adapter, View item, int pos,
					long id) {
				launchComposeView(pos);

			}
		});
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
	                // Triggered only when new data needs to be appended to the list
	                // Add whatever code is needed to append new items to your AdapterView
		    	Tweet lastTweet = (Tweet) lvTweets.getItemAtPosition(totalItemsCount-1);
		    	if(lastTweet == null){
		    		populateTimeline();

	        	}else{
	        		customLoadMoreDataFromApi(String.valueOf(lastTweet.getUid()-1));
	        	}
		    	
		        
		     }
	        });
	  }
	
	  public void customLoadMoreDataFromApi(String Id) {
	        loadMore(Id);
	        
	    }
	  
	  public void loadMore(String Id) {
	    	client.getExtraHomeTimeline(Id,new JsonHttpResponseHandler() {				
				@Override
				public void onSuccess(JSONArray json) {
					atweets.addAll(Tweet.fromJSONArray(json));
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug",s.toString());
					// TODO Auto-generated method stub
				}
			});
		}
	  
	  public void populateHomeTimeLineSinceLatest() {
		  
	    	Tweet latestTweet = (Tweet) lvTweets.getItemAtPosition(0);
	    	if(latestTweet == null) {
	    		lvTweets.onRefreshComplete();
	    	} else if(latestTweet != null ) {
	    	String sinceId = String.valueOf(latestTweet.getUid());
	        client.getHomeTimeLineSincelatest(sinceId, new JsonHttpResponseHandler() {
	            public void onSuccess(JSONArray json) {
	            	atweets.addAll(Tweet.fromJSONArray(json));
	            	atweets.notifyDataSetChanged();
					lvTweets.onRefreshComplete();
	            }

	            @Override
				public void onFailure(Throwable e, String s) {
				}
				@Override
				public void onFinish() {
					lvTweets.onRefreshComplete();
					super.onFinish();
				}
	        });
	      }  
	    } 
	  	  
		public void launchComposeView(int pos) {
			  // first parameter is the context, second is the class of the activity to launch
			  Intent i = new Intent(TimelineActivity.this, ShowTweet.class);
			  i.putExtra("postion", pos);
			  i.putExtra("item_value", tweets.get(pos).getBody());
			  i.putExtra("item_screen", tweets.get(pos).getUser().getScreenName());
			  i.putExtra("item_image", tweets.get(pos).getUser().getProfileImageUrl());			  
			  startActivity(i); // brings up the second activity
			}

	public void populateTimeline() {
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray json) {
				atweets.addAll(Tweet.fromJSONArray(json));
				Log.d("debug",json.toString());
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug",s.toString());
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.compose_tweet) {
			ComposeTweetDialog composetweet = new ComposeTweetDialog();
            // Show Alert DialogFragment
			composetweet.show(fm, "Compose Tweet");
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish(String result) {
		client.postNewTweet(result, new JsonHttpResponseHandler() {
			        @Override
					public void onSuccess(JSONObject object) {
						atweets.clear();
						populateTimeline();
					}
					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug",s.toString());
						// TODO Auto-generated method stub
					}
				});				
	         }
	
	private Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
}
