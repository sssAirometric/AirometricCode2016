package com.airometric;

import java.util.LinkedHashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.dialogs.ExitAlert;
import com.airometric.utility.L;

public class TermsOfUsageActivity extends AppActivity {

	private CheckBox chkAccept, chkDntAccept;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAppTitle(R.layout.termsofusage);

		initLayouts();

	}

	private void initLayouts() {

		L.log("Initialize Layouts...");

		chkAccept = (CheckBox) findViewById(R.id.chkAccept);
		chkDntAccept = (CheckBox) findViewById(R.id.chkDntAccept);

		chkAccept.setChecked(false);
		chkDntAccept.setChecked(false);

		chkAccept.setOnCheckedChangeListener(changeListener);
		chkDntAccept.setOnCheckedChangeListener(changeListener);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton btn, boolean cheked) {
			if (cheked)
				if (btn == chkAccept) {
					AcceptTermsOfUsageTask activateThrd = new AcceptTermsOfUsageTask();
					activateThrd.execute();
					showActivity(TestTypeActivity.class);
				} else if (btn == chkDntAccept) {
					new ExitAlert(activity) {
						public void cancelClickListener() {
							chkDntAccept.setChecked(false);
						};
					};
				}
		}
	};

	private class AcceptTermsOfUsageTask extends
			AsyncTask<LinkedHashMap, Integer, Long> {

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {

		}

		@Override
		protected Long doInBackground(LinkedHashMap... params) {
			APIManager api_client = new APIManager(activity);
			APIManager.Status status = api_client
					.processAcceptTermsOfUsage(pref.getUsername());
			L.log("AcceptTermsOfUsageTask():: status => " + status);
			if (status == APIManager.Status.SUCCESS) {
				String sResponse = api_client.getResponse();
				L.debug("AcceptTermsOfUsageTask Response Status - " + sResponse);
				if (sResponse.equals(ResponseCodes.TERMS_ACCEPT_SUCCESS))
					pref.setAsTermsOfUsageAccepted(true);
				else
					pref.setAsTermsOfUsageAccepted(false);
			}
			return null;
		}
	}
}
