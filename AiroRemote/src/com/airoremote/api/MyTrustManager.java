package com.airoremote.api;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * MyTrustManager
 */
public class MyTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}