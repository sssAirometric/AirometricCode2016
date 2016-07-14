package com.airometric;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.airometric.classes.Contact;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.utility.ContactUtil;
import com.airometric.utility.L;
import com.airometric.utility.adapters.ContactsAdapter;

public class ChooseContactActivity extends AppActivity {
	private AppActivity activity = null;
	private ListView lstGroupContacts;
	private TextView txtContactSearch;
	private ArrayList<String> arrLstContactNames, arrLstContactIDs;
	private ContactsAdapter adprContacts;
	private Handler mHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.choose_contacts);
		activity = this;
		initLayouts();
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */
	private void initLayouts() {
		L.log("Initialize Layouts...");
		lstGroupContacts = (ListView) findViewById(R.id.lstGroupContacts);
		txtContactSearch = (TextView) findViewById(R.id.txtContactSearch);
		txtContactSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				getAndPopulate(txtContactSearch.getText().toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		showLoading(Messages.PLZ_WAIT_LOADING_CONTACTS);
		new Thread() {
			@Override
			public void run() {
				getAndPopulate("");
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						hideLoading();
					}
				});
			}
		}.start();

	}

	private void getAndPopulate(String strSearch) {
		L.debug("Search Contact Name --> " + strSearch);
		ArrayList<Contact> arrLstGroupContacts = ContactUtil
				.getAllContactsBySearch(activity, strSearch);

		L.log("arrLstGroupContacts Length == " + arrLstGroupContacts.size());

		arrLstContactNames = new ArrayList<String>();
		arrLstContactIDs = new ArrayList<String>();
		for (Contact cg : arrLstGroupContacts) {
			arrLstContactNames.add(cg.getName());
			arrLstContactIDs.add("" + cg.getID());
		}

		adprContacts = new ContactsAdapter(activity,
				R.layout.list_item_contact, arrLstGroupContacts);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				lstGroupContacts.setAdapter(adprContacts);
				lstGroupContacts.setItemsCanFocus(false);
				lstGroupContacts
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long sss) {
								final Contact item = (Contact) parent
										.getItemAtPosition(position);
								putResult(item);
							}
						});
			}
		});
	}

	private void putResult(Contact contact) {

		Intent i = this.getIntent();
		i.putExtra(StringUtils.EXTRA_SELECTED_CONTACT, contact);
		setResult(RESULT_OK, i);

		debug("putResult -> " + contact);
		finish();
	}

	@Override
	public void goBack() {
		finish();
	}
}