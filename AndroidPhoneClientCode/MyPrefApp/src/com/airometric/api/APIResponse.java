package com.airometric.api;

public class APIResponse {

	public APIResponse() {

	}

	public static String getLoginResponse() {
		String resp = "";

		resp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<ACE>"
				+ "  <ResponseCode>0</ResponseCode>"
				+ "  <ResponseMessage>Login Success</ResponseMessage>"
				+ "  <SessionID>1234567890</SessionID>"
				+ "  <ApplicationID>5</ApplicationID>" + "</ACE>";
		return resp;
	}

	public static String getBillingRules() {
		String resp = "";

		resp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<ACE>"
				+ "<ResponseCode></ResponseCode>"
				+ "<ResponseMessage></ResponseMessage>" + "<RuleList>"
				+ "<Rule ID=\"1\" Name=\"Billing One\" />"
				+ "<Rule ID=\"2\" Name=\"Billing 2\" />"
				+ "<Rule ID=\"3\" Name=\"Billing 3\" />"
				+ "<Rule ID=\"4\" Name=\"Billing 4\" />" + "</RuleList>"
				+ "</ACE>";
		return resp;
	}

	public static String getBillingRuleDetails() {

		String resp = "";

		resp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<ACE>"
				+ "<ResponseCode>0</ResponseCode>"
				+ "<ResponseMessage>Success</ResponseMessage>"
				+ "<Connections>";
		long strt = 8956789;
		int no = 15;
		strt = strt - no;
		for (int i = 1; i < no; i++) {

			resp += "<Connection ID=\""
					+ i
					+ "\" Name=\""
					+ (strt + i)
					+ "\" DeviceTypeID=\"2\" DeviceID=\"1\" >"
					+ "<ConnectionAttribute ID=\"8\" Name=\"Owner\" Value=\"Krishna"
					+ i
					+ "\" />"
					+ "<ConnectionAttribute ID=\"9\" Name=\"Address\" Value=\"Bangalore\" />"
					+ "<ConnectionAttribute ID=\"10\" Name=\"Subdivision\" Value=\"4\" />"
					+ "<ConnectionAttribute ID=\"11\" Name=\"PreviousValue\" Value=\"41111\" />"
					+ "<DeviceAttributes>"
					+ "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"1\" AttributeName=\"Address\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"1\" />"
					+ "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"2\" AttributeName=\"\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"2\" />"
					+ "</DeviceAttributes>" + "</Connection>";
		}

		/*
		 * + "</Connection>" +
		 * "<Connection ID=\"3\" Name=\"8956787\" DeviceTypeID=\"2\" DeviceID=\"1\" >"
		 * + "<ConnectionAttribute ID=\"8\" Name=\"Owner\" Value=\"Yasho\" />" +
		 * "<ConnectionAttribute ID=\"9\" Name=\"Address\" Value=\"Chennai\" />"
		 * +
		 * "<ConnectionAttribute ID=\"10\" Name=\"Subdivision\" Value=\"4\" />"
		 * +
		 * "<ConnectionAttribute ID=\"11\" Name=\"PreviousValue\" Value=\"41511\" />"
		 * + "<DeviceAttributes>" +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"1\" AttributeName=\"Address\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"1\" />"
		 * +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"3\" AttributeType=\"2\" AttributeName=\"\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"2\" />"
		 * + "</DeviceAttributes>" + "</Connection>" +
		 * "<Connection ID=\"4\" Name=\"8956786\" DeviceTypeID=\"2\" DeviceID=\"1\" >"
		 * + "<ConnectionAttribute ID=\"8\" Name=\"Owner\" Value=\"Siva\" />" +
		 * "<ConnectionAttribute ID=\"9\" Name=\"Address\" Value=\"Nellai\" />"
		 * +
		 * "<ConnectionAttribute ID=\"10\" Name=\"Subdivision\" Value=\"7\" />"
		 * +
		 * "<ConnectionAttribute ID=\"11\" Name=\"PreviousValue\" Value=\"41911\" />"
		 * + "<DeviceAttributes>" +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"1\" AttributeName=\"Address\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"1\" />"
		 * +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"3\" AttributeType=\"2\" AttributeName=\"\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"2\" />"
		 * + "</DeviceAttributes>" + "</Connection>" +
		 * "<Connection ID=\"5\" Name=\"8956788\" DeviceTypeID=\"2\" DeviceID=\"3\" >"
		 * +
		 * "<ConnectionAttribute ID=\"11\" Name=\"Owner\" Value=\"Avinash\" />"
		 * +
		 * "<ConnectionAttribute ID=\"12\" Name=\"Address\" Value=\"Mysore\" />"
		 * +
		 * "<ConnectionAttribute ID=\"13\" Name=\"Subdivision\" Value=\"3\" />"
		 * +
		 * "<ConnectionAttribute ID=\"11\" Name=\"PreviousValue\" Value=\"412211\" />"
		 * + "<DeviceAttributes>" +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"1\" AttributeName=\"Address\" AttributeValue=\"80022\" Date=\"\" CalibrationID=\"1\" />"
		 * +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"2\" AttributeName=\"\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"2\" />"
		 * + "</DeviceAttributes>" + "</Connection>" +
		 * "<Connection ID=\"6\" Name=\"8956785\" DeviceTypeID=\"2\" DeviceID=\"3\" >"
		 * + "<ConnectionAttribute ID=\"11\" Name=\"Owner\" Value=\"Satha\" />"
		 * +
		 * "<ConnectionAttribute ID=\"12\" Name=\"Address\" Value=\"Chennai\" />"
		 * +
		 * "<ConnectionAttribute ID=\"13\" Name=\"Subdivision\" Value=\"7\" />"
		 * +
		 * "<ConnectionAttribute ID=\"11\" Name=\"PreviousValue\" Value=\"41211\" />"
		 * + "<DeviceAttributes>" +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"1\" AttributeName=\"Address\" AttributeValue=\"80022\" Date=\"\" CalibrationID=\"1\" />"
		 * +
		 * "<DeviceAttribute EndPointID=\"1\" ClusterID=\"1\" AttributeID=\"2\" AttributeType=\"2\" AttributeName=\"\" AttributeValue=\"80012\" Date=\"\" CalibrationID=\"2\" />"
		 * + "</DeviceAttributes>" + "</Connection>"
		 */
		resp += "</Connections>" + "<Tariff ID=\"1\" Name=\"\" />" + "</ACE>";
		return resp;
	}
}