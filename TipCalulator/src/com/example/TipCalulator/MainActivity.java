package com.example.TipCalulator;

import com.example.TipCalculator.R;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	 private SeekBar seekBar;
	 private EditText totalAmount;
	 private TextView textView;
	 private TextView totaltip;
	 private TextView totalamount;
	 private TextView persontip;
	 private TextView persontotal;
	 private Spinner spinner;
	 private int persons=1;


	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_main);

	  totalAmount = (EditText) findViewById(R.id.amount);
	  totalAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
	  seekBar = (SeekBar) findViewById(R.id.seekBar1);
	  textView = (TextView) findViewById(R.id.showtip);
	  textView.setText("");
	  totaltip = (TextView) findViewById(R.id.totaltip);
	  totaltip.setText("");
	  totalamount = (TextView) findViewById(R.id.totalamount);
	  totalamount.setText("");
	  persontip = (TextView) findViewById(R.id.persontip);
	  persontip.setText("");
	  persontotal = (TextView) findViewById(R.id.persontotal);
	  persontotal.setText("");
	  
	  // Text watcher added to calulate amounts based on  onchange listeners.
	 
	  totalAmount.addTextChangedListener(new TextWatcher() {
		  
		   public void afterTextChanged(Editable s) {
		   }
		 
		   public void beforeTextChanged(CharSequence s, int start, 
		     int count, int after) {
		   }
		 
		   public void onTextChanged(CharSequence s, int start, 
		     int before, int count) {
			  if(totalAmount.getText().toString().length() > 1) {
			  	Double amount = (Double) (Float.parseFloat(totalAmount.getText().toString()) * seekBar.getProgress() / 100.00);
		    	Double finalamount = (Double) (Float.parseFloat(totalAmount.getText().toString()) * 1.00);
		        textView.setText("Tip is "+seekBar.getProgress() + "%");
		        totaltip.setText("Total Tip is $"+roundvalues(amount));
		        totalamount.setText("Total Amount is $"+(roundvalues(amount+finalamount)));
		        persontip.setText("Tip per person: "+roundvalues(amount/persons));
			    persontotal.setText("Total per person is $"+(roundvalues((amount+finalamount)/persons)));
			  }  
		   }
		  });
		 
	  
	  // Seekbar listener event to calculate  tip based on percentage entered.

	  seekBar.setOnSeekBarChangeListener(
	                new OnSeekBarChangeListener() {
	    int progress = 0;
	        @Override
	      public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
	        progress = progresValue;
		  	Double amount = (Double) (Float.parseFloat(totalAmount.getText().toString()) * seekBar.getProgress() / 100.00);
	    	Double finalamount = (Double) (Float.parseFloat(totalAmount.getText().toString()) * 1.00);
	        textView.setText("Tip is "+seekBar.getProgress() + "%");
	        totaltip.setText("Total Tip is $"+roundvalues(amount));
	        totalamount.setText("Total Amount is $"+(roundvalues(amount+finalamount)));
	        persontip.setText("Tip per person: "+roundvalues(amount/persons));
		    persontotal.setText("Total per person is $"+(roundvalues((amount+finalamount)/persons)));

	      }

	      @Override
	      public void onStartTrackingTouch(SeekBar seekBar) {

	      }

	      @Override
	      public void onStopTrackingTouch(SeekBar seekBar) {

	      }
	  });
	  
	  // Spinner listener event to calculate tip based on number of people.
	  
	    spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				persontip.setText("");
				persons = Integer.valueOf((String) parent.getItemAtPosition(pos));
				String amount = totalAmount.getText().toString();
				if(!amount.equals("")){
				  	Double tipamount = (Double) (Float.parseFloat(totalAmount.getText().toString()) * seekBar.getProgress() / 100.00);
			    	Double finalamount = (Double) (Float.parseFloat(totalAmount.getText().toString()) * 1.00);
					persontip.setText("Tip per person: "+roundvalues(tipamount/persons));
				    persontotal.setText("Total per person is $"+(roundvalues((tipamount+finalamount)/persons)));
				}else {
					persontip.setText("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	 }
	 
	 // method to convert results to 2 decimals.
     public Double roundvalues(Double number) {
   	  number = number*100;
   	  number = (double) Math.round(number);
   	  number = number /100;
   	  return number;
     }

}
