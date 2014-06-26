package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
 
public class ComposeTweetDialog extends DialogFragment {
	private EditText mEditText;
	private TextView characterCount;
	private TwitterClient client;
	private User user;

    public ComposeTweetDialog() {
        // Empty constructor required for DialogFragment
    }

    public static ComposeTweetDialog newInstance(String title) {
    	ComposeTweetDialog frag = new ComposeTweetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    
    public interface ComposeTweetDialogListener {
    	void finish(String result);

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
        //getUserDetails();
        characterCount = (TextView) view.findViewById(R.id.charactercount);
        characterCount.setText("");
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().setTitle("Compose Tweet");
        mEditText.requestFocus();
        mEditText.addTextChangedListener(new TextWatcher() {
  		  
 		   public void afterTextChanged(Editable s) {
 		   }
 		 
 		   public void beforeTextChanged(CharSequence s, int start, 
 		     int count, int after) {
 		   }
 		 
 		   public void onTextChanged(CharSequence s, int start, 
 		     int before, int count) {
 			   String remaining = String.valueOf(140-mEditText.getText().toString().length());
 			  characterCount.setText(remaining +" characters remaining.");
 		   }
 		  });
 		 
        final Button button = 
                (Button) view.findViewById(R.id.btntweet);
	        button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	ComposeTweetDialogListener mDialogResult = (ComposeTweetDialogListener) getActivity();
	            	mDialogResult.finish(mEditText.getText().toString());
	            	ComposeTweetDialog.this.dismiss();
	            	
	            }
	        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }
    
    public void getUserDetails() {
        client.getUserDetails(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					Log.d("debug",json.toString());
					user = User.fromJSON(json);
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug",s.toString());
					// TODO Auto-generated method stub
				}
			});	
    }

}
