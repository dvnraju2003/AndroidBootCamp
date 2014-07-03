package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;

import com.codepath.apps.basictwitter.EndlessScrollListener;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TweetArrayAdapter;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;

public class TweetsListFragment extends Fragment {
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> atweets;
	private PullToRefreshListView lvTweets;
	private TwitterClient client;
	private OnScrollListener scrollListener;
	
	  public interface OnScrollListener {
		    public void onLoadMore(int page, int totalItemsCount);
		  }
	  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate the view
		View v = inflater.inflate(com.codepath.apps.basictwitter.R.layout.fragement_tweets_list, container, false);
		//assign view preferences.
		client = TwitterApplication.getRestClient();
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(atweets);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
	                // Triggered only when new data needs to be appended to the list
	                // Add whatever code is needed to append new items to your AdapterView
		    	Tweet lastTweet = (Tweet) lvTweets.getItemAtPosition(totalItemsCount-1);
		    	if(lastTweet == null){
		    		atweets.clear();
		    		atweets.notifyDataSetInvalidated();
	        	}else{
	        		String Id = String.valueOf(lastTweet.getUid()-1);
	        		client.getExtraHomeTimeline(Id,new JsonHttpResponseHandler() {
	        			@Override
	        			public void onSuccess(JSONArray json) {
	        				atweets.addAll(Tweet.fromJSONArray(json));
	        			}
	        			@Override
	        			public void onFailure(Throwable e, String s) {
	        				Log.d("debug", "failed");
	        				Log.d("debug",s.toString());
	        				// TODO Auto-generated method stub
	        			}
	        		});
	        	  } 
		        }
	        });
		return v;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		tweets = new ArrayList<Tweet>();
		atweets = new TweetArrayAdapter(getActivity(), tweets);
		super.onCreate(savedInstanceState);
	}
	
	public void addAll(ArrayList<Tweet> tweets) {
		atweets.addAll(tweets);
	}
	
	public void clear() {
		atweets.clear();
	}
	
	public void showProgressBar() {
        getActivity().setProgressBarIndeterminateVisibility(true); 
    }

   public void hideProgressBar() {
        getActivity().setProgressBarIndeterminateVisibility(false); 
    }

}
