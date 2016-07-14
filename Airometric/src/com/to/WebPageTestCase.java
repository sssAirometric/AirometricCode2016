package com.to;

import java.io.Serializable;

public class WebPageTestCase implements Serializable{
	private static final long serialVersionUID = 1L;
	private String webPageURL= "";
	private String numberofrepeatcyclesInWeb="";
	private int numberOfParam =2;
	public String getNumberofrepeatcyclesInWeb() {
		return numberofrepeatcyclesInWeb;
	}
	public void setNumberofrepeatcyclesInWeb(String numberofrepeatcyclesInWeb) {
		this.numberofrepeatcyclesInWeb = numberofrepeatcyclesInWeb;
	}
	public int getNumberOfParam() {
		return numberOfParam;
	}
	public String getWebPageURL() {
		return webPageURL;
	}
	public void setWebPageURL(String webPageURL) {
		this.webPageURL = webPageURL;
	}
}
