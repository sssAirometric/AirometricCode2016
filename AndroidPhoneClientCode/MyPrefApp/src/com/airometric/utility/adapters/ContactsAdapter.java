package com.airometric.utility.adapters;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airometric.AppActivity;
import com.airometric.R;
import com.airometric.classes.Contact;
import com.airometric.utility.L;

public class ContactsAdapter extends BaseAdapter {
	private ArrayList<Contact> list;
	private Contact selected;
	private int textViewResourceId;
	private AppActivity context;
	LayoutInflater inflater;

	public ContactsAdapter(AppActivity context, int textViewResourceId,
			ArrayList<Contact> _list) {
		this.context = context;
		this.list = _list;
		this.textViewResourceId = textViewResourceId;
		inflater = this.context.getLayoutInflater();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(textViewResourceId, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.lytContact = (LinearLayout) convertView
					.findViewById(R.id.lytContact);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text1.setText(list.get(position).getName());
		holder.text2.setText(list.get(position).getPhoneNumber());

		long contactId = list.get(position).getID();
		Contact contact = list.get(position);
		holder.lytContact.setTag(contact);

//		holder.lytContact.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Contact chk_contact = (Contact) holder.lytContact.getTag();
//				selected = chk_contact;
//				L.debug(" chk_contact id -- > "
//						+ chk_contact.getID());
//			}
//		});

		return convertView;
	}

	public Contact getSelectedContact() {
		return selected;
	}

	static class ViewHolder {
		TextView text1, text2;
		LinearLayout lytContact;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
