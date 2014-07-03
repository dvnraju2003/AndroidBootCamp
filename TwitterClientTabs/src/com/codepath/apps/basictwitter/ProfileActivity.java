package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.fragments.userTimelineFragment;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends FragmentActivity {
	
	String screenName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		screenName = getIntent().getStringExtra("screen_name");
		loadProfileInfo();
        userTimelineFragment fragmentDemo = (userTimelineFragment) 
                getSupportFragmentManager().findFragmentById(R.id.userTimelineFragment);
            fragmentDemo.populateTimeline(screenName);
	}
	
	private void loadProfileInfo() {		
		if(screenName == null || screenName.equals("")) {		
		TwitterApplication.getRestClient().getMyInfo(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				User u = User.fromJSON(jsonObject);
				getActionBar().setTitle("@"+u.getScreenName());
				populateProfileHeader(u);
				super.onSuccess(jsonObject);
			}
		});
	  }	else {
			TwitterApplication.getRestClient().getUserInfo(screenName,new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject jsonObject) {
					// TODO Auto-generated method stub
					User u = User.fromJSON(jsonObject);
					getActionBar().setTitle("@"+u.getScreenName());
					populateProfileHeader(u);
					super.onSuccess(jsonObject);
				}
			});
		  
	  }
	}
	
	private void populateProfileHeader(User u) {
		TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
		TextView tvTagLIne = (TextView) findViewById(R.id.tvTagline);
		TextView tvFollowers = (TextView) findViewById(R.id.tvfollowers);
		TextView tvFollowing = (TextView) findViewById(R.id.tvfollowing);
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivUserProfileImage);
		
		tvUserName.setText(u.getScreenName());
		tvTagLIne.setText(u.getTagLine());
		tvFollowers.setText(u.getFollowersCount());
		tvFollowing.setText(u.getFollowingCount());
		ImageLoader.getInstance().displayImage(u.getProfileImageUrl(), ivProfileImage);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}
	}

}
