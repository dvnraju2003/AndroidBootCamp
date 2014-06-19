package com.example.gridimagesearchtest;

import com.example.gridimagesearchtest.CustomOnItemSelectedListener;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;

public class SettingsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	public void onSaveSettings(View v) {
		Spinner colorSpinner = (Spinner) findViewById(R.id.color_array);
		Spinner sizeSpinner = (Spinner) findViewById(R.id.size_array);
		Spinner imageSpinner = (Spinner) findViewById(R.id.image_array);
		EditText etSiteFilter = (EditText) findViewById(R.id.etFilter);

		String color = colorSpinner.getSelectedItem().toString();
		String size = sizeSpinner.getSelectedItem().toString();
		String imagefilter = imageSpinner.getSelectedItem().toString();
		String site = etSiteFilter.getText().toString();
		
		  // Prepare data intent 
		  Intent sendsettings = new Intent();
		  // Pass relevant data back as a result
		  sendsettings.putExtra("color", color);
		  sendsettings.putExtra("size", size);
		  sendsettings.putExtra("imagefilter", imagefilter);
		  sendsettings.putExtra("sitefilter", site);
		  // Activity finished ok, return the data
		  setResult(RESULT_OK, sendsettings); // set result code and bundle data for response.
		  // closes the activity and returns to first screen
		  this.finish();
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_settings,
					container, false);
			return rootView;
		}
	}

}
