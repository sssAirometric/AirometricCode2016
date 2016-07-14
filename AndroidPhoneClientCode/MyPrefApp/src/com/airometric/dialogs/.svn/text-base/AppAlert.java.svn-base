package com.airometric.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.airometric.config.Constants;

/**
 * Alert class to show the alert messages.
 * 
 */
public class AppAlert extends AlertDialog.Builder {

	public static final String APP_NAME = Constants.APP_NAME;
	public static final String TYPE_INPUT_NUMBER = "Numeric";
	public static final String TYPE_INPUT_TEXT = "Text";
	EditText input = null;
	private Context Objcontext;
	AlertDialog alert;

	/**
	 * Constructor to show the alert dialog with title as application name
	 * 
	 * @param context
	 *            Activity which shows the alert.
	 * @param msg
	 *            The message to be displayed.
	 */

	public AppAlert(Context context, String msg) {
		super(context);
		this.setTitle(APP_NAME);
		this.setMessage(msg);
		Objcontext = context;

		this.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				okClickListener();
			}
		});
		this.setCancelable(false);
		this.show();
	}

	/**
	 * Constructor to show the alert dialog with title as application name and
	 * message id
	 * 
	 * 
	 * @param context
	 *            Activity which shows the alert.
	 * @param sMsgCode
	 *            Message id to be displayed with title
	 * @param msg
	 *            The message to be displayed.
	 */

	public AppAlert(Context context, String msg, String sType) {
		super(context);
		Objcontext = context;
		String sTitle = APP_NAME;
		this.setTitle(sTitle);
		this.setMessage(msg);

		if (sType.equals(TYPE_INPUT_NUMBER)) {
			// Set an EditText view to get user input
			input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);

			LinearLayout lyt = new LinearLayout(context);
			lyt.setPadding(20, 0, 20, 0);
			input.setLayoutParams(lparams);
			lyt.addView(input);
			setView(lyt);
		}
		if (sType.equals(TYPE_INPUT_TEXT)) {
			// Set an EditText view to get user input
			input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);

			LinearLayout lyt = new LinearLayout(context);
			lyt.setPadding(20, 0, 20, 0);
			input.setLayoutParams(lparams);
			lyt.addView(input);
			setView(lyt);
		}

		this.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// Toast.makeText(Objcontext.getApplicationContext(),
				// "Please enter duration", Toast.LENGTH_SHORT).show();
				if (input != null) {
					String sVal = input.getText().toString();
					okClickListener(sVal);
				} else {
					okClickListener();
					// Toast.makeText(Objcontext.getApplicationContext(),
					// "Please enter duration", Toast.LENGTH_SHORT).show();
				}
			}
		});

		this.setCancelable(true);
		this.show();
	}

	/**
	 * Constructor to show the alert dialog with title as application name and
	 * message id
	 * 
	 * 
	 * @param context
	 *            Activity which shows the alert.
	 * @param sMsgCode
	 *            Message id to be displayed with title
	 * @param msg
	 *            The message to be displayed.
	 */

	public AppAlert(Context context, String msg, String sType,
			String sPostButtonTxt, String sNegButtonTxt) {
		super(context);
		String sTitle = APP_NAME;
		this.setTitle(sTitle);
		this.setMessage(msg);

		if (sType.equals(TYPE_INPUT_TEXT)) {
			// Set an EditText view to get user input
			input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			// | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			input.setKeyListener(DigitsKeyListener
					.getInstance("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"));
			LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);

			LinearLayout lyt = new LinearLayout(context);
			lyt.setPadding(20, 0, 20, 0);
			input.setLayoutParams(lparams);
			lyt.addView(input);
			setView(lyt);
		}
		this.setPositiveButton(sPostButtonTxt, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (input != null) {
					String sVal = input.getText().toString();
					okClickListener(sVal);
				} else {
					okClickListener();
				}
			}
		});

		// Added on 18-04

		this.setNegativeButton(sNegButtonTxt, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				cancelClickListener();
			}
		});

		this.setCancelable(true);
		this.show();
	}

	/**
	 * Constructor to show alert with message and list of options
	 * 
	 * @param context
	 *            Activity which shows the alert.
	 * @param msg
	 *            The message to be displayed.
	 * @param items
	 *            List of option to be displayed.
	 */
	public AppAlert(Context context, String msg, String[] items) {
		super(context);
		this.setTitle(msg);
		// DeviceArrayAdapter itemAdapter = new DeviceArrayAdapter(context,
		// R.layout.list_items, items);
		this.setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				okClickListener(arg1);
			}
		});
		// this.setAdapter(itemAdapter, new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// okClickListener(arg1);
		// }
		// });
		this.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				cancelClickListener();
			}
		});
		this.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP
						&& !event.isCanceled()) {
					alert.dismiss();
					backKeyListener();
					return true;
				}
				return false;
			}
		});
		this.setCancelable(true);
		alert = this.show();
	}

	public AppAlert(final Context context, String msg, final String[] items,
			boolean isMultiChoice) {
		super(context);
		this.setTitle(msg);
		final boolean[] itemsChecked = new boolean[items.length];

		this.setMultiChoiceItems(items, itemsChecked,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
					}

				});
		this.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				okClickListener(itemsChecked);
			}
		});

		this.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				cancelClickListener();
			}
		});
		this.setCancelable(true);
		this.show();
	}

	public AppAlert(final Context context, String msg, final String[] items,
			boolean isMultiChoice, String sButtonText) {
		super(context);
		this.setTitle(msg);
		final boolean[] itemsChecked = new boolean[items.length];

		this.setMultiChoiceItems(items, itemsChecked,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
					}

				});
		this.setPositiveButton(sButtonText,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						okClickListener(itemsChecked);
					}
				});

		this.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				cancelClickListener();
			}
		});
		this.setCancelable(true);
		this.show();
	}

	/**
	 * Constructor to show alert dialog with Yes/No buttons
	 * 
	 * @param context
	 *            Activity which shows the alert.
	 * @param msg
	 *            The message to be displayed.
	 * @param isYesNoAlert
	 *            Flag to show the Yes/No buttons.
	 */

	public AppAlert(Context context, String msg, boolean isYesNoAlert) {
		super(context);
		this.setTitle(APP_NAME);
		this.setMessage(msg);
		this.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				okClickListener();
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

	/**
	 * Constructor to show alert dialog with Yes/No buttons
	 * 
	 * @param context
	 *            Activity which shows the alert.
	 * @param sMsgCode
	 *            Message id to be displayed with title
	 * @param msg
	 *            The message to be displayed.
	 * @param isYesNoAlert
	 *            Flag to show the Yes/No buttons.
	 */

	public AppAlert(Context context, String sMsgCode, String msg,
			boolean isYesNoAlert) {
		super(context);
		this.setTitle(APP_NAME + " : " + sMsgCode);
		this.setMessage(msg);
		this.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				okClickListener();
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

	public void okClickListener(int pos) {
	}

	public void okClickListener(String str) {

		/*
		 * if(str.equalsIgnoreCase("")) { }
		 */

	}

	public void okClickListener(boolean[] selected) {
	}

	public void okClickListener() {
	}

	public void cancelClickListener() {
	}

	public void backKeyListener() {

	}
}