package com.airometric;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.airometric.utility.L;

public class TemplateActivity extends AppActivity {
	
	private TextView lblHello;
	Context context = null;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.temp);
		context = this;

		initLayouts();		
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */
	
	private void initLayouts() {
		L.log("Initialize Layouts...");
		lblHello = (TextView) findViewById(R.id.lblHello);
		lblHello.setText("Initialize Layouts...");
	}
	
	@Override
	public void goBack() {
		super.goBack();
	}
}