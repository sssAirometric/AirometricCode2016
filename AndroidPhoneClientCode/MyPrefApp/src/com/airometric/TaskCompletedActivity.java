package com.airometric;

import com.airometric.storage.Preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TaskCompletedActivity extends AppActivity {

	public Button openmapinapp, openmapinbrowser; 
	protected Preferences pref;		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAppTitle(R.layout.activity_task_completed);
		openmapinapp = (Button) findViewById(R.id.btnopenmap);
		openmapinbrowser = (Button) findViewById(R.id.btnopenmapinbrowser);
		pref = new Preferences(getApplicationContext());
		openmapinapp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent(TaskCompletedActivity.this,GoogleMapsActivity.class);
				startActivity(i);
			}
		});
		
		
		openmapinbrowser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.mnSettings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
