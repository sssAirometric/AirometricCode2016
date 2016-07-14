package com.airometric;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.airometric.R;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class ForgotPasswordActivity extends AppActivity {

	private AppActivity activity = null;

	private Button btnSubmit;
	private EditText txtEmailID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.forgot_password);

		activity = this;
		initLayouts();
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */
	private void initLayouts() {
		L.log("Initialize Layouts...");

		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(clickListener);

		txtEmailID = (EditText) findViewById(R.id.txtEmailID);

	}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view == btnSubmit) {

				String sEmailID = txtEmailID.getText().toString();

				if (Validator.isEmpty(sEmailID)) {
					alert("Please enter your Email ID");
				} else if (!Validator.isValidEmail(sEmailID)) {
					alert("Please enter valid Email ID");
				} else {
					alert("Done");
				}

			}
		}

	};

	@Override
	public void goBack() {
		super.goBack();
		showActivity(LoginActivity.class);
	}
}