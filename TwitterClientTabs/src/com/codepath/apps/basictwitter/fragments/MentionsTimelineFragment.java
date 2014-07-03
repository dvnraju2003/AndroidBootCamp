package com.codepath.apps.basictwitter.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MentionsTimelineFragment extends TweetsListFragment {

	private TwitterClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterApplication.getRestClient();
		populateTimeline();
	}
	
	
	public void populateTimeline() {
		showProgressBar();
		client.getMentionsTimeline(new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray json) {
				hideProgressBar();
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
