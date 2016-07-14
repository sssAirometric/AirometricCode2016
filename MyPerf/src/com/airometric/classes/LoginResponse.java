package com.airometric.classes;

import java.io.Serializable;

public class LoginResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2238932705378780643L;
	public String sStatus, sLevel, sAcceptedTerms, sMarketplaces;

	public LoginResponse(String sStatus, String sLevel, String sAcceptedTerms, String sMarketplaces) {
		this.sStatus = sStatus;
		this.sLevel = sLevel;
		this.sAcceptedTerms = sAcceptedTerms;
		this.sMarketplaces = sMarketplaces;
	}

	public String getStatus() {
		return sStatus;
	}

	public String getLevel() {
		return sLevel;
	}

	public String getAcceptedTerms() {
		return sAcceptedTerms;
	}
	
	public String getsMarketplaces() {
		return sMarketplaces;
	}

	@Override
	public String toString() {
		String toString = "LoginResponse():: Status = " + sStatus
				+ ", Level = " + sLevel + ", AcceptedTerms = " + sAcceptedTerms
				+ ", Marketplaces = " + sMarketplaces;
		return toString;
	}
}
