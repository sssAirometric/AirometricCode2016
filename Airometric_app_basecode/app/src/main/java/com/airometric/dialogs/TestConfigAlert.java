package com.airometric.dialogs;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.R.dimen;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airometric.R;
import com.airometric.classes.TestConfig;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.storage.Preferences;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class TestConfigAlert extends Dialog implements
		android.view.View.OnClickListener {

	private Context Objcontext;
	private Activity activity;
	private EditText txtTestName;
	private Button btnTestConfigSave, btnTestConfigCancel;
	// private TestConfig testconfig = null;
	ArrayAdapter<String> marketplaceAdapter;
	Spinner marketplaceSpinner;
	Preferences objPref;

	public TestConfigAlert(Activity activity) {
		super(activity);
		this.activity = activity;
		objPref = new Preferences(activity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test_config_alert);

		L.debug("TestConfigAlert Activity...........");

		txtTestName = (EditText) findViewById(R.id.txtTestName);
		btnTestConfigSave = (Button) findViewById(R.id.btnTestConfigSave);
		btnTestConfigCancel = (Button) findViewById(R.id.btnTestConfigCancel);
		marketplaceSpinner = (Spinner) findViewById(R.id.spinner_marketplace);
		btnTestConfigSave.setOnClickListener(this);
		btnTestConfigCancel.setOnClickListener(this);
		
		if (StringUtils.MARKET_PLACES_MAP == null) {
			Preferences preferences = new Preferences(activity);
			StringUtils.MARKET_PLACES_MAP = new LinkedHashMap<String, String>(
					preferences.loadMarketplaces());
		}

		marketplaceAdapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_item,
				StringUtils.getStringArray(StringUtils.MARKET_PLACES_MAP
						.keySet()));
		marketplaceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		marketplaceSpinner.setAdapter(marketplaceAdapter);
		
		L.debug("Spinner position:" + marketplaceSpinner.getSelectedItemPosition());
		
		if (objPref != null
				&& objPref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false)) {
			txtTestName
					.setText(objPref.getValue(Preferences.KEY_TEST_NAME, ""));
			marketplaceSpinner.setSelection(objPref.getValue(Preferences.KEY_SPINNER_SELECTED_ITEM_POS, 0));
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnTestConfigSave) {

			String str = txtTestName.getText().toString();

			if (Validator.isEmpty(str)) {
				Toast.makeText(activity, Messages.ENTER_TEST_NAME,
						Toast.LENGTH_SHORT).show();
				return;
			}

			// Store test name and selected MP in preference...

			if (!txtTestName.getText().toString().isEmpty())
				objPref.putValue(Preferences.KEY_TEST_NAME, txtTestName
						.getText().toString());

			objPref.putValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID,
					StringUtils.MARKET_PLACES_MAP.get(marketplaceSpinner
							.getSelectedItem().toString()));

			for (Entry<String, String> entry : StringUtils.MARKET_PLACES_MAP
					.entrySet()) {
				if (entry.getValue().equals(
						StringUtils.MARKET_PLACES_MAP.get(marketplaceSpinner
								.getSelectedItem().toString()))) {
					L.debug("M_Place Name: " + entry.getKey());
					objPref.putValue(Preferences.KEY_SELECTED_MARKET_PALCE,
							entry.getKey().toString());
				}
			}
			
			objPref.putValue(Preferences.KEY_SPINNER_SELECTED_ITEM_POS, marketplaceSpinner.getSelectedItemPosition());
			
			L.debug("Spinner position on Save:" + marketplaceSpinner.getSelectedItemPosition());

			L.debug("Test Name: "
					+ objPref.getValue(Preferences.KEY_TEST_NAME, ""));

			L.debug("M_PLACE: "
					+ objPref.getValue(Preferences.KEY_SELECTED_MARKET_PALCE,
							""));

			objPref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, true);
			dismiss();
			reload();
		}

		if (v == btnTestConfigCancel) {
			dismiss();
		}

	}

	public void reload() {

		Intent intent = activity.getIntent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_ANIMATION);
		activity.finish();
		activity.overridePendingTransition(0, 0);
		activity.startActivity(intent);
	}

}
