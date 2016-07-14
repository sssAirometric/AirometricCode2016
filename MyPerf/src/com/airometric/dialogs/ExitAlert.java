package com.airometric.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.airometric.LoginActivity;
import com.airometric.config.Constants;
import com.airometric.config.Messages;

/**
 * Exit alert custom alert to confirmation alert.
 * 
 */
public class ExitAlert extends AlertDialog.Builder {

	public ExitAlert(Context context) {
		super(context);
		this.setTitle(Constants.APP_NAME);
		this.setMessage(Messages.CONFIRM_CLOSE_APP);
		this.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				okClickListener();
				System.exit(0);
			}
		});
		this.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				cancelClickListener();
			}
		});
		this.setCancelable(false);
		this.show();
	}

	public void okClickListener() {
		
	}

	public void cancelClickListener() {
	}
}