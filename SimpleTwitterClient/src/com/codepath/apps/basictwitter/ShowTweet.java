package com.codepath.apps.basictwitter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ShowTweet extends Activity {
	
	private TextView showTweet;
	private TextView showScreenname;
	private ImageView showImage;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_tweet);
		ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#81a3d0"));     
        ab.setBackgroundDrawable(colorDrawable);
        ab.setIcon(R.drawable.ic_tweet);
		ab.setDisplayHomeAsUpEnabled(true);
		String showbody = getIntent().getStringExtra("item_value");
		String showscreenname = getIntent().getStringExtra("item_screen");
		String showimage = getIntent().getStringExtra("item_image");
		
		showTweet = (TextView) findViewById(R.id.tweetBody);
		showScreenname = (TextView) findViewById(R.id.screenName);
		showImage = (ImageView) findViewById(R.id.profileImage);

		showTweet.setText(showbody);
		showScreenname.setText(showscreenname);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(showimage, showImage);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
		switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
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

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_show_tweet,
					container, false);
			return rootView;
		}
	}

}
