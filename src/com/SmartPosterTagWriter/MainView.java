package com.SmartPosterTagWriter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainView extends Activity implements Runnable {

	EditText 				myEditText1;	
	Button 					myButton1;

	/**
	 * This method is called when the activity is first created.
	 **/
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);	
		
		View view = this.getWindow().getDecorView();
	    view.setBackgroundColor(0xffffff);
	    
		setButtonListener();	
	}

	private void setButtonListener()
	{
		myEditText1 = (EditText) findViewById(R.id.editText1);  	
		myButton1 = (Button) findViewById(R.id.button1);  

		myEditText1.setTextColor(Color.BLACK);
		myButton1.setTextColor(Color.BLACK);

		myButton1.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {		    					
				//open writter
				Intent myIntent = new Intent(MainView.this, WriteToPoster.class);
				myIntent.putExtra("url", myEditText1.getText().toString()); //Optional parameters
				MainView.this.startActivity(myIntent);
			}  
		});		
	}

	/** Called on thread.start() */
	@Override
	public void run() {
	}

}