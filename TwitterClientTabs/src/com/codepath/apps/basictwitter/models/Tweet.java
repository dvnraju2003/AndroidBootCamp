package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;


    @Table(name = "Tweets")
    public class Tweet extends Model implements Serializable {
    private static final long serialVersionUID = 7871651683071415249L;
    
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
    
    @Column(name = "Body")
	private String body;
	
    @Column(name = "CreatedAt")
	private String createdAt;
    
    @Column(name = "UserId")
	private long userId;
	
    @Column(name = "retweet_count")
	 private int retweet_count;
    
	 @Column(name = "favourites_count")
	 private int favourites_count;
	 
	 @Column(name = "favorited")
	 private boolean favorited;
	 
	 @Column(name = "retweeted")
	 private boolean retweeted;
	 

	@Column(name = "mediaUrl")
	 private String mediaUrl;
	 
	 
	
	
	private User user;
	
	public Tweet(){
		super();
	}	
	
	
	private static Tweet fromJSON(JSONObject jsonObject){
		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.uid = jsonObject.getLong("id");
			tweet.retweet_count = jsonObject.getInt("retweet_count");
			tweet.favourites_count = jsonObject.getInt("favorite_count");
			tweet.favorited = jsonObject.getBoolean("favorited");
			tweet.retweeted = jsonObject.getBoolean("retweeted");
			tweet.mediaUrl = setMediaUrlIfAvailable(jsonObject);
			tweet.user =  User.fromJSON(jsonObject.getJSONObject("user"));
			//tweet.user.save();
			//tweet.save();
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return tweet;	
	}
	
	public static ArrayList<Tweet> fromJSONArray(JSONArray array) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(array.length());
		for (int x =0; x<array.length();x++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = array.getJSONObject(x);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if(tweet != null) {
				tweets.add(tweet);
			}
			
		}
		return tweets;
	}
	
	
	@Override
	public String toString() {
		return getBody() + "-" + getUser().getName();
		
	}
	

	public String getBody() {
		return body;
	}


	public long getUid() {
		return uid;
	}

	

	public String getCreatedAt() {
		return createdAt;
	}
	
	public long getUserId() {
		return userId;
		}



	public User getUser() {
		return user;
	}
	
	public void setUser(User u) {
		user=u;
		}
	
	 public int getRetweet_count() {
		return retweet_count;
	}


	public int getFavourites_count() {
		return favourites_count;
	}


	public boolean isFavorited() {
		return favorited;
	}


	public boolean isRetweeted() {
		return retweeted;
	}
	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public static void deleteAll(){
		new Delete().from(Tweet.class).execute();
	}
	
	public static List<Tweet> getAll() {
	    return new Select()
	        .from(Tweet.class)
	        .execute();
	}
	
	private static String setMediaUrlIfAvailable(JSONObject jsonObject) {
		//extract entities
		String mediaUrl = null;
		try {
			JSONObject entities = jsonObject.getJSONObject("entities");
			JSONArray media = entities.getJSONArray("media");
			JSONObject first = media.getJSONObject(0);
			String mediaURl = first.getString("media_url");
			if(mediaURl != null && mediaURl.trim().length() > 0){
				mediaUrl = mediaURl.trim();
			}
		} catch (JSONException e) {
			mediaUrl = null;
			e.printStackTrace();
		}
		return mediaUrl;
	}

}
