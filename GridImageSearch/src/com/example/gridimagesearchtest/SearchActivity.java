package com.example.gridimagesearchtest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends ActionBarActivity {
	EditText etSearch;
	GridView gvImage;
	Button btnSearch;
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;
	private final int REQUEST_CODE = 20;
	String colorSetting;
	String sizeSetting;
	String imageFilterSetting;
	String siteFilterSetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvImage.setAdapter(imageAdapter);
		gvImage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				Intent i = new Intent(getApplicationContext(),ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("result", imageResult);
				startActivity(i);
			}
		});
		gvImage.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
	                // Triggered only when new data needs to be appended to the list
	                // Add whatever code is needed to append new items to your AdapterView
		        customLoadMoreDataFromApi(totalItemsCount);
	                // or customLoadMoreDataFromApi(totalItemsCount);
		     }
	        });
	}
	
	public void setupViews() {
		etSearch = (EditText) findViewById(R.id.etSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		gvImage = (GridView) findViewById(R.id.gvImage);
	}

	   // Append more data into the adapter
    public void customLoadMoreDataFromApi(int totalCount) {
    	String query = etSearch.getText().toString();
        loadMore(query, totalCount);
    }
    
    
    public void loadMore(String query, int start) {
    	Log.d("DEBUG", "loadmore "+String.valueOf(start));
		String url = constructUrl(start)+query;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new JsonHttpResponseHandler(){
           @Override
			public void onSuccess(int statusCode, JSONObject result){
        	   JSONArray imageJsonResults = null;
				try {
					imageJsonResults = result.getJSONObject("responseData").getJSONArray("results");
					imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					Log.d("DEBUG", "New Values:"+String.valueOf(imageAdapter.getCount()));					
				} catch (JSONException e) {										
				}
              }
			});
		}

	public String constructUrl(int start){
			String url="";
			try {
				if(colorSetting != null && imageFilterSetting != null &&  sizeSetting != null && siteFilterSetting != null) {
				url = "https://ajax.googleapis.com/ajax/services/search/images?"
						+"v=1.0&rsz=8&start="+start+"&imgsz="+colorSetting+"&imgtype="+imageFilterSetting+
						"&imgcolor="+sizeSetting+"&as_sitesearch="+siteFilterSetting+"&q=";
				Log.d("DEBUG", "URL is"+url);
				} else if(colorSetting != null && imageFilterSetting == null &&  sizeSetting == null && siteFilterSetting == null) {
					url = "https://ajax.googleapis.com/ajax/services/search/images?"
							+"v=1.0&rsz=8&start="+start+"&imgsz="+colorSetting+"&q=";
					Log.d("DEBUG", "URL is"+url);
					} else if(colorSetting == null && imageFilterSetting != null &&  sizeSetting == null && siteFilterSetting == null) {
						url = "https://ajax.googleapis.com/ajax/services/search/images?"
								+"v=1.0&rsz=8&start="+start+"&imgtype="+imageFilterSetting+"&q=";
						Log.d("DEBUG", "URL is"+url);
						} else if(colorSetting == null && imageFilterSetting == null &&  sizeSetting != null && siteFilterSetting == null) {
							url = "https://ajax.googleapis.com/ajax/services/search/images?"
									+"v=1.0&rsz=8&start="+start+"&imgcolor="+sizeSetting+"&q=";
							Log.d("DEBUG", "URL is"+url);
							} else if(colorSetting == null && imageFilterSetting == null &&  sizeSetting == null && siteFilterSetting != null) {
								url = "https://ajax.googleapis.com/ajax/services/search/images?"
										+"v=1.0&rsz=8&start="+start+"&as_sitesearch="+siteFilterSetting+"&q=";
								Log.d("DEBUG", "URL is"+url);
								} else {
									url = "https://ajax.googleapis.com/ajax/services/search/images?"
											+"v=1.0&rsz=8&start="+start+"&q=";
								}
			} catch(Exception ex){
				Log.d("DEBUG", ex.getMessage());
			}
			return url;
		}
    	
	public void onImageSearch(View V) {
		String query = etSearch.getText().toString();
		String url = constructUrl(0)+query;
		AsyncHttpClient client = new AsyncHttpClient();
		if(isNetworkAvailable()) {
		client.get(url, new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(JSONObject response) {
			JSONArray imageJsonResults = null;
			try {
				imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
				imageResults.clear();
				imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
				Log.d("DEBUG", "number is "+imageResults.size());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		 }
		});
	  } else {
		  Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show();
	  }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.menu_item_settings) {
			Intent settings = new Intent(SearchActivity.this,SettingsActivity.class);
			startActivityForResult(settings, REQUEST_CODE);
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
      // REQUEST_CODE is defined above
      if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
         // Extract name value from result extras
         colorSetting = data.getExtras().getString("color");
         sizeSetting = data.getExtras().getString("size");
         imageFilterSetting = data.getExtras().getString("imagefilter");
         siteFilterSetting = data.getExtras().getString("sitefilter");
      }
    }
	
	private Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public Boolean isOnline() {
	    try {
	        Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
	        int returnVal = p1.waitFor();
	        boolean reachable = (returnVal==0);
	        return reachable;
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return false;
	}

}
