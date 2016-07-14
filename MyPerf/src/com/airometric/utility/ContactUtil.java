package com.airometric.utility;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

import com.airometric.classes.Contact;

public class ContactUtil {

	public static ArrayList<Contact> getAllContactsBySearch(Context context,
			String strSearchContactName) {
		ContentResolver cr = context.getContentResolver();
		ArrayList<Contact> lstContacts = new ArrayList<Contact>();
		Cursor cur = null;

		String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
				+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("
				+ Contacts.DISPLAY_NAME + " != '' ))";

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		if (!Validator.isEmpty(strSearchContactName)) {
			uri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_FILTER_URI,
					strSearchContactName);
		}
		String[] projection = { BaseColumns._ID, Contacts.DISPLAY_NAME,
				Contacts.PHOTO_ID, Contacts.HAS_PHONE_NUMBER };

		cur = cr.query(uri, projection, select, null, Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC");

		if (cur.moveToFirst()) {

			int contactIdIndex = cur.getColumnIndex(BaseColumns._ID);
			int displayNameIndex = cur.getColumnIndex(Contacts.DISPLAY_NAME);

			do {
				long contactId = cur.getLong(contactIdIndex);
				String sCntName = cur.getString(displayNameIndex);
				String phoneNumber = "";

				Cursor phones = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {
					phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					Contact contact = new Contact(contactId, sCntName,
							phoneNumber);
					lstContacts.add(contact);
				}
				phones.close();
			} while (cur.moveToNext());

		}

		cur.close();
		return lstContacts;
	}
}
