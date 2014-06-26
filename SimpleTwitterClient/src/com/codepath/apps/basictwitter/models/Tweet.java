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
			tweet.user =  User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.user.save();
			tweet.save();
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

	public static void deleteAll(){
		new Delete().from(Tweet.class).execute();
	}
	
	public static List<Tweet> getAll() {
	    return new Select()
	        .from(Tweet.class)
	        .execute();
	}

}
