package com.airometric.classes;

import java.io.Serializable;

public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;
	private long cID;
	private String sName, sPhoneNumber;

	public Contact(long _cID, String _sName, String _sPhoneNumber) {
		cID = _cID;
		sName = _sName;
		sPhoneNumber = _sPhoneNumber;
	}

	public String getName() {
		return sName;
	}

	public long getID() {
		return cID;
	}

	public String getPhoneNumber() {
		return sPhoneNumber;
	}

	@Override
	public String toString() {
		String to_string = "ID : " + cID + "; Name : " + sName + "; Phone : "
				+ sPhoneNumber;
		return to_string;
	}
}
