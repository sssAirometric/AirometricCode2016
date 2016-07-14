package com.airoremote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airoremote.R;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class PermissionsActivity extends Activity  {

	private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;
	public Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permissions);
		context = this;
		/*if (Build.VERSION.SDK_INT >= 23) {
		    // Marshmallow+
			insertDummyContactWrapper();
			System.out.println("r u der??333132123");
		} else {
		    // Pre-Marshmallow
			doWork();
		}*/
		
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void insertDummyContactWrapper() {
	    List<String> permissionsNeeded = new ArrayList<String>();
	 
	    final List<String> permissionsList = new ArrayList<String>();
	    if (!addPermission(permissionsList,"android.permission.MODIFY_PHONE_STATE"))
	        permissionsNeeded.add("GPS");
	    if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
	        permissionsNeeded.add("Read Contacts");
	    
	 
	    if (permissionsList.size() > 0) {
	        if (permissionsNeeded.size() > 0) {
	            // Need Rationale
	            String message = "You need to grant access to " + permissionsNeeded.get(0);
	            for (int i = 1; i < permissionsNeeded.size(); i++)
	                message = message + ", " + permissionsNeeded.get(i);
	            showMessageOKCancel(message,
	                    new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
	                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
	                        }
	                    });
	            return;
	        }
	        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
	                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
	        return;
	    }
	 
	    //insertDummyContact();
	}
	 
	private boolean addPermission(List<String> permissionsList, String permission) {
	    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
	        permissionsList.add(permission);
	        // Check for Rationale Option
	        if (!shouldShowRequestPermissionRationale(permission))
	            return false;
	    }
	    return true;
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	    switch (requestCode) {
	        case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
	            {
	            Map<String, Integer> perms = new HashMap<String, Integer>();
	            // Initial
	            perms.put("android.permission.MODIFY_PHONE_STATE", PackageManager.PERMISSION_GRANTED);
	            perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
	            
	            // Fill with results
	            for (int i = 0; i < permissions.length; i++)
	                perms.put(permissions[i], grantResults[i]);
	            // Check for ACCESS_FINE_LOCATION
	            if (perms.get("android.permission.MODIFY_PHONE_STATE") == PackageManager.PERMISSION_GRANTED
	                    && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
	                // All Permissions Granted we can use our function here 
	                //insertDummyContact();
	            	doWork();
	            } else {
	                // Permission Denied
	                Toast.makeText(PermissionsActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
	                        .show();
	            }
	            }
	            break;
	        default:
	            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	    }
	}
	
	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
	    new AlertDialog.Builder(PermissionsActivity.this)
	            .setMessage(message)
	            .setPositiveButton("OK", okListener)
	            .setNegativeButton("Cancel", null)
	            .create()
	            .show();
	}
	public void doWork(){
		Toast.makeText(PermissionsActivity.this, "You can do your work from here.", Toast.LENGTH_SHORT)
        .show();
	}
}
