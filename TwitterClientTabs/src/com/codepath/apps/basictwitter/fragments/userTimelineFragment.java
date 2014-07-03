package com.codepath.apps.basictwitter.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class userTimelineFragment extends TweetsListFragment {
	
	private TwitterClient client;
	String screenName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		if(screenName != null && !screenName.equals("")) {
			populateTimeline(screenName);
		} else {
			populateTimeline(null);
		}
	}
	
	public void populateTimeline(String screenName) {
		showProgressBar();
		client.getUserTimeline(screenName,new JsonHttpResponseHandler() {			
			@Override
			public void onSuccess(JSONArray json) {
				hideProgressBar();
				clear();
				addAll(Tweet.fromJSONArray(json));
				Log.d("debug",json.toString());
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				hideProgressBar();
				Log.d("debug",s.toString());
				// TODO Auto-generated method stub
			}
		});
		
	}

}
