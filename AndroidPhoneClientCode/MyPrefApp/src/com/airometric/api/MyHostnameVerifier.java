package com.airometric.api;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * MyHostnameVerifier
 */
public class MyHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}
