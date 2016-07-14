package com.airometric.utility;

import org.apache.commons.lang3.StringEscapeUtils;

public class XMLUtils {

	public static void main(String args[]) {

		// handling xml special character & in Java String
		String xmlWithSpecial = "Java & HTML"; // xml String with & as special
												// characters
		System.out.println("Original unescaped XML String: " + xmlWithSpecial);
		System.out.println("Escaped XML String in Java: "
				+ StringEscapeUtils.escapeXml(xmlWithSpecial));

		// handling xml special character > in String on Java
		xmlWithSpecial = "Java > HTML"; // xml String with & as special
										// characters
		System.out.println("Original unescaped XML String: " + xmlWithSpecial);
		System.out.println("Escaped XML String : "
				+ StringEscapeUtils.escapeXml(xmlWithSpecial));

		// handling xml and html special character < in String
		xmlWithSpecial = "Java < HTML"; // xml String with & as special
										// characters
		System.out.println("Original unescaped XML String: " + xmlWithSpecial);
		System.out.println("Escaped XML String: "
				+ StringEscapeUtils.escapeXml(xmlWithSpecial));

		// handling html and xml special character " in Java
		xmlWithSpecial = "Java \" HTML"; // xml String with & as special
											// characters
		System.out.println("Original unescaped XML String: " + xmlWithSpecial);
		System.out.println("Escaped XML String: "
				+ StringEscapeUtils.escapeXml(xmlWithSpecial));

		// handling xml special character ' in String from Java
		xmlWithSpecial = "Java ' HTML"; // xml String with & as special
										// characters
		System.out.println("Original unescaped XML String: " + xmlWithSpecial);
		System.out.println("Escaped XML String: "
				+ StringEscapeUtils.escapeXml(xmlWithSpecial));

	}

	public static String clean(String sXML) {
		return StringEscapeUtils.escapeXml(sXML);
	}

}

// Read more:
// http://javarevisited.blogspot.com/2012/09/how-to-replace-escape-xml-special-characters-java-string.html#ixzz2UBmTjn8Q
