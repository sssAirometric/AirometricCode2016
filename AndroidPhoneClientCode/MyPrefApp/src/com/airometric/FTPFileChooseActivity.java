package com.airometric;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airometric.api.ResponseCodes;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.utility.FTP;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class FTPFileChooseActivity extends AppActivity {

	public static final String CHOOSE_TYPE_FILE = "CHOOSE_FILE";
	public static final String CHOOSE_TYPE_DIR = "CHOOSE_DIR";

	String sHostURL = "";
	String sUsername = "";
	String sPassword = "";
	int port = 21;

	String sChooseType, sLastDir = null, sSelectedPath, toppath, sCurrentPath;

	TextView lblBack, lblPath;
	LinearLayout lytFiles;
	LayoutInflater inflater;
	Handler handler = new Handler();

	FTP ftp = new FTP(handler);
	Thread thrdLogin, thrdGetFiles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAppTitle(R.layout.file_list);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(StringUtils.EXTRA_CHOOSE_TYPE))
				sChooseType = bundle.getString(StringUtils.EXTRA_CHOOSE_TYPE);
			if (bundle.containsKey(StringUtils.EXTRA_FTP_SERVER))
				sHostURL = bundle.getString(StringUtils.EXTRA_FTP_SERVER);
			if (bundle.containsKey(StringUtils.EXTRA_FTP_USER))
				sUsername = bundle.getString(StringUtils.EXTRA_FTP_USER);
			if (bundle.containsKey(StringUtils.EXTRA_FTP_PWD))
				sPassword = bundle.getString(StringUtils.EXTRA_FTP_PWD);

		}

		lblBack = (TextView) findViewById(R.id.lblBack);
		lblPath = (TextView) findViewById(R.id.lblPath);
		lblBack.setOnClickListener(FolderClkListener);

		lytFiles = (LinearLayout) findViewById(R.id.lytFiles);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		doConnect();
	}

	void doConnect() {

		showLoading("Connecting...");
		thrdLogin = new Thread(ConnectRunnable);
		thrdLogin.start();
	}

	Runnable ConnectRunnable = new Runnable() {

		@Override
		public void run() {
			Message msg = new Message();
			Bundle bndle = new Bundle();
			String sResponseCode = "", sResponseDesc = "";

			try {
				if (ftp.ftpConnect(sHostURL, sUsername, sPassword, port)) {
					log("Current directory is " + ftp.getWorkingDir());
					toppath = ftp.getWorkingDir();
					sResponseCode = ResponseCodes.SUCCESS;
				} else {
					sResponseCode = StringUtils.ERROR_CODE;
					sResponseDesc = Messages.FTP_LOGIN_FAILED;
				}
			} catch (Exception e) {
				e.printStackTrace();
				sResponseCode = StringUtils.ERROR_CODE;
				sResponseDesc = Messages.err(e);
			}

			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			progressHandler.sendMessage(msg);
		}
	};

	/**
	 * Handling the message while progressing
	 */

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				thrdLogin.interrupt();
			} catch (Exception e) {
			}

			// ###################

			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				lblBack.setTag(toppath);
				doGetFiles(toppath);
			} else {
				new AppAlert(activity, sResponseDesc) {
					public void okClickListener() {
						putResult(null);
					};
				};
			}
			hideLoading();
		}
	};

	protected void doGetFiles(String path) {
		sCurrentPath = path;
		showLoading("Loading files...");
		// handler.post(GetFilesRunnable);

		thrdGetFiles = new Thread(GetFilesRunnable);
		thrdGetFiles.start();
	}

	Runnable GetFilesRunnable = new Runnable() {

		@Override
		public void run() {
			Message msg = new Message();
			Bundle bndle = new Bundle();
			String sResponseCode = "", sResponseDesc = "";

			try {
				listFiles(sCurrentPath);
				sResponseCode = ResponseCodes.SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
				sResponseCode = StringUtils.ERROR_CODE;
				sResponseDesc = Messages.err(e);
			}

			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			GetFilesHandler.sendMessage(msg);
		}
	};

	private Handler GetFilesHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				thrdGetFiles.interrupt();
			} catch (Exception e) {
			}

			// ###################

			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				lblBack.setTag(toppath);
				// doGetFiles(toppath);
			} else if (sResponseCode.equals(ResponseCodes.FAILURE)) {
				alert("Invalid username/password!");
			} else {
				error(sResponseDesc);
			}
			hideLoading();
		}
	};

	void listFiles(final String sDir) {
		log("listFiles():: sDir - " + sDir);
		sLastDir = sDir;
		runOnUiThread(new Runnable() {
			public void run() {
				lytFiles.removeAllViews();
			}
		});

		try {

			FTPClient mFTPClient = ftp.getClient();

			FTPFile[] ftpdirs2 = mFTPClient.listFiles(sDir);

			for (int i = 0; i < ftpdirs2.length; i++) {
				final FTPFile ftpfile = ftpdirs2[i];

				boolean isDir = ftpfile.isDirectory();

				if (!Validator.isEmpty(sChooseType)
						&& sChooseType.equals(CHOOSE_TYPE_DIR) && !isDir) {
					continue;
				}
				final View layout = inflater.inflate(R.layout.list_item_file,
						null);
				TextView text1 = (TextView) layout.findViewById(R.id.text1);
				TextView text2 = (TextView) layout.findViewById(R.id.text2);
				ImageView icon = (ImageView) layout.findViewById(R.id.icon);
				CheckBox chkChoose = (CheckBox) layout
						.findViewById(R.id.chkChoose);

				if (isDir) {
					icon.setImageResource(R.drawable.folder);
					String new_path = sDir;
					if (sDir.equals("/"))
						new_path += ftpfile.getName();
					else
						new_path += "/" + ftpfile.getName();
					layout.setTag(new_path);
					layout.setOnClickListener(FolderClkListener);
					if (!Validator.isEmpty(sChooseType)
							&& sChooseType.equals(CHOOSE_TYPE_DIR)) {
						chkChoose.setVisibility(View.VISIBLE);
						chkChoose.setTag(new_path);
						chkChoose.setOnCheckedChangeListener(ChkChgeListener);
					}
				} else {
					icon.setImageResource(R.drawable.file);
					layout.setTag(R.id.id_name, ftpfile.getName());
					layout.setTag(R.id.id_path, sDir);

					if (!Validator.isEmpty(sChooseType)
							&& sChooseType.equals(CHOOSE_TYPE_FILE))
						layout.setOnClickListener(FileClkListener);

				}
				text1.setText(ftpfile.getName());
				if (!isDir)
					text2.setText(""
							+ FileUtil.readableFileSize(ftpfile.getSize()));
				runOnUiThread(new Runnable() {
					public void run() {
						lytFiles.addView(layout);
					}
				});
			}
		} catch (Exception e) {
			log("Error while connecting - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		runOnUiThread(new Runnable() {
			public void run() {
				lblPath.setText(sDir);
			}
		});
	}

	OnCheckedChangeListener ChkChgeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				String path = buttonView.getTag().toString();
				putResult(path);
			}
		}
	};

	View.OnClickListener FolderClkListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			String path = view.getTag().toString();
			log("FolderClkListener():: path - " + path);
			if (view == lblBack) {
				if (path != null && !path.equals("") && !path.equals("/")) {
					String prev_path = path.substring(0, path.lastIndexOf("/"));
					log("onClick():: prev_path - " + prev_path);
					doGetFiles(prev_path);
				}
			} else {
				doGetFiles(path);
			}
			lblBack.setTag(sLastDir);
		}
	};

	View.OnClickListener FileClkListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			String filname = view.getTag(R.id.id_name).toString();
			String path = view.getTag(R.id.id_path).toString() + "/" + filname;
			log("FileClkListener():: filname - " + filname);
			log("FileClkListener():: path - " + path);
			putResult(filname, path);

		}
	};

	protected void putResult(String path) {
		log("putResult()... " + path);
		Intent i = this.getIntent();
		i.putExtra("PATH", path);
		setResult(RESULT_OK, i);
		finish();
	}

	protected void putResult(String name, String path) {
		log("putResult()... " + path);
		Intent i = this.getIntent();
		i.putExtra("NAME", name);
		i.putExtra("PATH", path);
		setResult(RESULT_OK, i);

		finish();
	}

	void disconnect() {
		ftp.ftpDisconnect();
	}

	@Override
	protected void onDestroy() {
		disconnect();
		super.onDestroy();
	}

	void log(String msg) {
		L.debug(msg);
	}

	private void onBack() {
		putResult(null);
	}
}
