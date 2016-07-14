package com.airometric;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class LocalFilesChooseActivity extends AppActivity {

	public static final String CHOOSE_TYPE_FILE = "CHOOSE_FILE";
	public static final String CHOOSE_TYPE_DIR = "CHOOSE_DIR";
	String sChooseType, sLastDir = null, sRootDir, sSelectedPath;

	TextView lblBack, lblPath;
	LinearLayout lytFiles;
	LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAppTitle(R.layout.file_list);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("Choose_Type")) {
				sChooseType = bundle.getString("Choose_Type");
				log("Choose Type - " + sChooseType);
			}
		}

		lblBack = (TextView) findViewById(R.id.lblBack);
		lblPath = (TextView) findViewById(R.id.lblPath);
		lblBack.setOnClickListener(FolderClkListener);

		sRootDir = Environment.getExternalStorageDirectory().toString();

		lytFiles = (LinearLayout) findViewById(R.id.lytFiles);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listFiles(sRootDir);

	}

	void listFiles(final String sDir) {
		log("listFiles():: sDir - " + sDir);
		sLastDir = sDir;
		lytFiles.removeAllViews();

		try {

			File rootDir = new File(sDir);
			File[] files = rootDir.listFiles();
			int total_files = files.length;
			for (int i = 0; i < total_files; i++) {
				final File file = files[i];
				file.getParent();
				boolean isDir = file.isDirectory();
				View layout = inflater.inflate(R.layout.list_item_file, null);
				TextView text1 = (TextView) layout.findViewById(R.id.text1);
				TextView text2 = (TextView) layout.findViewById(R.id.text2);
				CheckBox chkChoose = (CheckBox) layout
						.findViewById(R.id.chkChoose);
				ImageView icon = (ImageView) layout.findViewById(R.id.icon);

				if (isDir) {
					icon.setImageResource(R.drawable.folder);
					layout.setTag(file.getAbsolutePath());
					layout.setOnClickListener(FolderClkListener);
					if (sChooseType != null
							&& sChooseType.equals(CHOOSE_TYPE_DIR)) {
						chkChoose.setVisibility(View.VISIBLE);
						chkChoose.setTag(file.getAbsolutePath());
						chkChoose.setOnCheckedChangeListener(ChkChgeListener);
					}
				} else {
					icon.setImageResource(R.drawable.file);
					layout.setTag(R.id.id_name, file.getName());
					layout.setTag(R.id.id_path, sDir);

					if (!Validator.isEmpty(sChooseType)
							&& sChooseType.equals(CHOOSE_TYPE_FILE))
						layout.setOnClickListener(FileClkListener);
				}
				text1.setText(file.getName());
				if (!isDir)
					text2.setText("" + FileUtil.readableFileSize(file.length()));
				lytFiles.addView(layout);
			}
		} catch (Exception e) {
			log("Error while connecting - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		lblPath.setText(sDir);
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
			if (view.getTag() != null) {
				String path = view.getTag().toString();
				log("FolderClkListener():: path - " + path);
				if (view == lblBack) {
					if (path != null && !path.equals("")
							&& !path.equals(sRootDir)) {
						String prev_path = new File(path).getParent();
						log("onClick():: prev_path - " + prev_path);
						listFiles(prev_path);
					}
				} else {
					listFiles(path);
				}
				lblBack.setTag(sLastDir);
			}
		}
	};

	View.OnClickListener FileClkListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			String filname = view.getTag(R.id.id_name).toString();
			String path = view.getTag(R.id.id_path).toString() + "/" + filname;
			log("FileClkListener():: filname - " + filname);
			log("FileClkListener():: path - " + path);
			putResult(path);
		}
	};

	private void putResult(String path) {

		Intent i = this.getIntent();
		i.putExtra("PATH", path);
		setResult(RESULT_OK, i);

		finish();
	}

	void log(String msg) {
		L.debug(msg);
	}

	private void onBack() {
		putResult(null);
	}
}
