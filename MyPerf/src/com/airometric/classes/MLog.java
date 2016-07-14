package com.airometric.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.airometric.config.Constants;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MLog implements Thread.UncaughtExceptionHandler {
	public static boolean reporting;
	String VersionName;
	private int VersionCode;
	String PackageName;
	String FilePath;
	String PhoneModel;
	String AndroidVersion;
	String Board;
	String Brand;
	String Device;
	String Display;
	String FingerPrint;
	String Host;
	String ID;
	String Manufacturer;
	String Model;
	String Product;
	String Tags;
	long Time;
	String Type;
	String User;
	public String uniqueId;
	private static String TAG = Constants.APP_NAME + "_CrashReporter";

	private List<String> loging;
	HashMap<String, String> CustomParameters = new HashMap<String, String>();

	private Thread.UncaughtExceptionHandler PreviousHandler;
	private static MLog S_mInstance;
	private Context CurContext;

	public static MLog getInstance() {
		if (S_mInstance == null) {
			S_mInstance = new MLog();
		}
		return S_mInstance;
	}

	private String CreateCustomInfoString() {
		String CustomInfo = "";
		Iterator<String> iterator = CustomParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String CurrentKey = iterator.next();
			String CurrentVal = CustomParameters.get(CurrentKey);
			CustomInfo += CurrentKey + " = " + CurrentVal + "\n";
		}
		return CustomInfo;
	}

	public void Init(Context context) {
		PreviousHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		CurContext = context;
		reporting = false;
		loging = new LinkedList<String>();
		S_mInstance = this;
		// CheckErrorAndSend(CurContext);
	}

	public long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	void RecoltInformations(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			VersionName = pi.versionName;
			// versionNumber
			VersionCode = pi.versionCode;

			// Package name
			PackageName = pi.packageName;
			// Device model
			PhoneModel = android.os.Build.MODEL;
			// Android version
			AndroidVersion = android.os.Build.VERSION.RELEASE;

			Board = android.os.Build.BOARD;
			Brand = android.os.Build.BRAND;
			Device = android.os.Build.DEVICE;
			Display = android.os.Build.DISPLAY;
			FingerPrint = android.os.Build.FINGERPRINT;
			Host = android.os.Build.HOST;
			ID = android.os.Build.ID;
			Model = android.os.Build.MODEL;
			Product = android.os.Build.PRODUCT;
			Tags = android.os.Build.TAGS;
			Time = android.os.Build.TIME;
			Type = android.os.Build.TYPE;
			User = android.os.Build.USER;
			try {
				uniqueId = Secure.getString(Constants.root
						.getApplicationContext().getContentResolver(),
						Secure.ANDROID_ID);
			} catch (Exception e) {
				uniqueId = "other";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String CreateInformationString() {
		RecoltInformations(CurContext);

		String ReturnVal = "";
		ReturnVal += "programs : " + TAG;
		ReturnVal += "\n";
		ReturnVal += "VersionCode : " + VersionCode;
		ReturnVal += "\n";
		ReturnVal += "VersionName : " + VersionName;
		ReturnVal += "\n";
		ReturnVal += "Package : " + PackageName;
		ReturnVal += "\n";
		ReturnVal += "FilePath : " + FilePath;
		ReturnVal += "\n";
		ReturnVal += "Phone Model" + PhoneModel;
		ReturnVal += "\n";
		ReturnVal += "Android Version : " + AndroidVersion;
		ReturnVal += "\n";
		ReturnVal += "Board : " + Board;
		ReturnVal += "\n";
		ReturnVal += "Brand : " + Brand;
		ReturnVal += "\n";
		ReturnVal += "Device : " + Device;
		ReturnVal += "\n";
		ReturnVal += "Display : " + Display;
		ReturnVal += "\n";
		ReturnVal += "Finger Print : " + FingerPrint;
		ReturnVal += "\n";
		ReturnVal += "Host : " + Host;
		ReturnVal += "\n";
		ReturnVal += "ID : " + ID;
		ReturnVal += "\n";
		ReturnVal += "Model : " + Model;
		ReturnVal += "\n";
		ReturnVal += "Product : " + Product;
		ReturnVal += "\n";
		ReturnVal += "Tags : " + Tags;
		ReturnVal += "\n";
		ReturnVal += "Time : " + Time;
		ReturnVal += "\n";
		ReturnVal += "Type : " + Type;
		ReturnVal += "\n";
		ReturnVal += "User : " + User;
		ReturnVal += "\n";
		ReturnVal += "unique id : " + uniqueId;
		ReturnVal += "\n";
		ReturnVal += "Total Internal memory : " + getTotalInternalMemorySize();
		ReturnVal += "\n";
		ReturnVal += "Available Internal memory : "
				+ getAvailableInternalMemorySize();
		ReturnVal += "\n";

		try {
			ActivityManager am = (ActivityManager) Constants.root
					.getSystemService(Context.ACTIVITY_SERVICE);
			ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
			am.getMemoryInfo(mInfo);
			ReturnVal += " app availMem " + mInfo.availMem;
			ReturnVal += "\n";
			ReturnVal += " is it on low memory? " + mInfo.lowMemory;
			ReturnVal += "\n";
			ReturnVal += " threshold " + mInfo.threshold;
			ReturnVal += "\n";

		} catch (Exception e) {
			ReturnVal += MLog.e("had error geting memory info", e);
		}

		return ReturnVal;
	}

	public void uncaughtException(Thread t, Throwable e) {
		MLog.e("error", e);
		String Report = "";
		Date CurDate = new Date();
		Report += "Error Report collected on : " + CurDate.toString();
		Report += "\n";
		Report += "\n";
		Report += "Informations :";
		Report += "\n";
		Report += "==============";
		Report += "\n";
		Report += "\n";
		Report += CreateInformationString();

		Report += "Custom Informations :\n";
		Report += "=====================\n";
		Report += CreateCustomInfoString();

		Report += "\n\n";
		Report += "Stack : \n";
		Report += "======= \n";
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		Report += stacktrace;

		Report += "\n";
		Report += "Cause : \n";
		Report += "======= \n";

		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			Report += result.toString();
			cause = cause.getCause();
		}
		printWriter.close();

		Report += "\n\nLogCat:\n";

		String[] log = loging.toArray(new String[1]);
		for (int i = 0; i < log.length; ++i) {
			Report += log[i] + "\n";
		}

		Report += "****  End of current Report ***";
		SaveAsFile(Report);
		// SendErrorMail( Report );
		PreviousHandler.uncaughtException(t, e);
	}

	private void createErrorDialog(String error, Context context) {
		final String errorMsg = error;
		final MLog self = this;
		final Activity _context = Constants.root;
		Log.i(TAG, "created Dialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		TextView tv = new TextView(_context);
		builder.setTitle("The game had a fc, what happend?");
		final EditText et = new EditText(_context);
		LinearLayout ll = new LinearLayout(_context);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(tv);
		// ll.addView(et);
		builder.setView(et);
		builder.setPositiveButton("Send report", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(Constants.root.getApplicationContext(),
						"thank you", Toast.LENGTH_SHORT);
				self.sendErrorServer(_context, et.getText().toString(),
						errorMsg);
				MLog.i("dialog clicked " + et.getText().toString());
			}
		});
		MLog.i("created dialog");
		try {
			Dialog dialog = builder.create();
			dialog.show();
		} catch (Exception e) {
			MLog.e("dialog error", e);
		}
		MLog.i("dialog is showen");
	}

	public void sendErrorServer(Context context, String userText,
			String errorMsg) {
		// p.setParameter("error", errorMsg);
		// p.setParameter("user", userText);
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(""); // place server side script URL
												// here

		try {
			try {
				uniqueId = Secure.getString(Constants.root
						.getApplicationContext().getContentResolver(),
						Secure.ANDROID_ID);
			} catch (Exception e) {
				uniqueId = "other";
			}
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			RecoltInformations(Constants.root);

			nameValuePairs.add(new BasicNameValuePair("app", PackageName));
			nameValuePairs.add(new BasicNameValuePair("user", userText));
			nameValuePairs.add(new BasicNameValuePair("device", uniqueId));
			nameValuePairs.add(new BasicNameValuePair("version", ""
					+ VersionCode));
			nameValuePairs.add(new BasicNameValuePair("group", TAG));
			nameValuePairs.add(new BasicNameValuePair("error", errorMsg));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			MLog.i(PackageName);
			// Execute HTTP Post Request
			// HttpResponse respond=httpclient.execute(httppost);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String respond = httpclient.execute(httppost, responseHandler);

			MLog.i("*******************sent a message and the respond is");

			MLog.i(respond);
		} catch (Exception e) {
			// couldn't save, save to file
			SaveAsFile("_________________________________\n" + userText
					+ "\n\n\n" + errorMsg);
			Toast.makeText(
					Constants.root,
					"couldn't send, will send next time you will open the game",
					Toast.LENGTH_LONG);
		}
	}

	private void SaveAsFile(String ErrorContent) {
		try {
			Random generator = new Random();
			int random = generator.nextInt(99999);
			String FileName = "stack-" + random + ".stacktrace";
			FileOutputStream trace = CurContext.openFileOutput(FileName,
					Context.MODE_PRIVATE);
			trace.write(ErrorContent.getBytes());
			trace.close();
		} catch (Exception e) {
		}
	}

	private String[] GetErrorFileList() {
		File dir = new File(FilePath + "/");
		// Try to create the files folder if it doesn't exist
		dir.mkdir();
		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace");
			}
		};
		return dir.list(filter);
	}

	public void CheckErrorAndSend(Context _context) {
		try {
			FilePath = _context.getFilesDir().getAbsolutePath();
			Log.i(TAG, "errors: " + GetErrorFileList().length);
			if (GetErrorFileList().length > 0) {
				String WholeErrorText = "";
				String[] ErrorFileList = GetErrorFileList();
				int curIndex = 0;
				final int MaxSendMail = 1;
				for (String curString : ErrorFileList) {
					if (curIndex++ <= MaxSendMail) {
						WholeErrorText += "New Trace collected :\n";
						WholeErrorText += "=====================\n ";
						String filePath = FilePath + "/" + curString;
						BufferedReader input = new BufferedReader(
								new FileReader(filePath));
						String line;
						while ((line = input.readLine()) != null) {
							WholeErrorText += line + "\n";
						}
						input.close();
					}
					// DELETE FILES !!!!
					File curFile = new File(FilePath + "/" + curString);
					curFile.delete();
				}
				reporting = true;
				Log.i(TAG, "found error");
				createErrorDialog(WholeErrorText, _context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String i(String string) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		String info = string + " (" + trace[3].getClassName() + "."
				+ trace[3].getMethodName() + ":" + trace[3].getLineNumber()
				+ ") I";
		Log.i(TAG, info);
		getInstance().loging.add(info);
		return info;
	}

	public static String e(String string, Throwable e) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		String info = string + " (" + trace[3].getClassName() + "."
				+ trace[3].getMethodName() + ":" + trace[3].getLineNumber()
				+ ") E";
		getInstance().loging.add(info);

		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		getInstance().loging.add(result.toString());
		info += "\n\n" + result.toString();
		return info;

	}

	public static String e(String string) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		String info = string + " (" + trace[3].getClassName() + "."
				+ trace[3].getMethodName() + ":" + trace[3].getLineNumber()
				+ ") E";
		getInstance().loging.add(info);

		Log.e(TAG, info);
		return info;
	}
}
// see http://androidsnippets.com/error-reporting-class
