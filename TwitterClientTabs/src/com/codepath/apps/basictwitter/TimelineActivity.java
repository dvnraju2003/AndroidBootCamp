package com.codepath.apps.basictwitter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.listeners.FragmentTabListener;

public class TimelineActivity extends FragmentActivity {
	private int RESULT_CODE = 123;
	private HomeTimelineFragment homeFragment;
	private MentionsTimelineFragment mentionsFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}
	
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
		    .newTab()
		    .setText("Home")
		    .setIcon(R.drawable.ic_home)
		    .setTag("HomeTimelineFragment")
		    .setTabListener(new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this,
                        "home", HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
		    .newTab()
		    .setText("Mentions")
		    .setIcon(R.drawable.ic_mention)
		    .setTag("MentionsTimelineFragment")
		    .setTabListener(new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this,
                        "mentions", MentionsTimelineFragment.class));
		actionBar.addTab(tab2);
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
			showComposeTweet();
		}
		if(id == R.id.profile) {
			launchUserDetailActivity("");
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void launchUserDetailActivity(String screen_name){
		Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
		i.putExtra("screen_name", screen_name);
		startActivity(i);
	}
	
	private void showComposeTweet() {
		Intent i = new Intent(this, ComposeNewTweet.class);
		startActivityForResult(i, RESULT_CODE);
		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_CODE && resultCode == RESULT_OK){
			String openTab = getActionBar().getSelectedTab().getTag().toString();
			if(openTab.equals("HomeTimelineFragment")){
				homeFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);
				homeFragment.populateTimeline();
			}else{
				mentionsFragment = (MentionsTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);
				mentionsFragment.populateTimeline();
			}
		}
	}

	}

