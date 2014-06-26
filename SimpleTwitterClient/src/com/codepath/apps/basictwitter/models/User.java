package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Users")
public class User extends Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 782520282314155005L;
	
	@Column(name = "Name")
	private String name;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "ScreenName")
	private String screenName;
	
	@Column(name = "ProfileImageUrl")
	private String profileImageUrl;

	public User(){
		super();
	}
	
	public static User fromJSON(JSONObject jsonObject) {
		User u = new User();
		try {
			u.name = jsonObject.getString("name");
			u.uid = jsonObject.getLong("id");
			u.screenName = jsonObject.getString("screen_name");
			u.profileImageUrl = jsonObject.getString("profile_image_url");
			
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
		return u;
	}
	
	public List<Tweet> tweets() {
        return getMany(Tweet.class, "User");
    }
	
	public static List<User> getAll() {
	    return new Select()
	        .from(User.class)
	        .execute();
	}
	
	public static List<User> getUser(long userId) {
	    return new Select()
	        .from(User.class)
	        .where("uid = ?", userId)
	        .execute();
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

}
