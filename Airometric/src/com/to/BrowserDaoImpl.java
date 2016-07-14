package com.to;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.model.DBUtil;
import com.report.helper.ReportHelper;
import com.util.DBHelper;

public class BrowserDaoImpl extends DBHelper {

	public List<BrowserBean> getBrowserDetails(String masterDeviceName,
			String masterMarketId, String jsonTestName, String marketName) {
		// TODO Auto-generated method stub
		// System.out.println("inside getBrowserDetails..............................................................starts..........................");
		openConn();
		BrowserBean browserBean = new BrowserBean();
		List<BrowserBean> browserValueList = new ArrayList<BrowserBean>();
		String httpAvgTotalLatency = null;
		String httpAvgReadRate = null;
		String httpAvgConnct = null;
		String timeStamp = null;

		// System.out.println("masterDeviceName:"+masterDeviceName+":masterMarketId:"+masterMarketId+":jsonTestName:"+jsonTestName);
		// masterDeviceName:LG-D801:masterMarketId:22:jsonTestName:g2and
		// NR.Networktype like '%LTE%'

		try {
			String browserQuery = "SELECT AVG(NR.HTTPAvgTotalLatency) AS HTTPAvgTotalLatency, "
					+ "AVG(NR.HTTPAvgReadRate) AS HTTPAvgReadRate,AVG(NR.HTTPAvgConnectLatency) AS HTTPAvgConnectLatency, "
					+ "AVG(LC.TEST_IDENTIFIER_TIMESTAMP) AS TIME_STAMP "
					+ "FROM stg_net_results NR , stg_log_cat_info LC "
					+ "WHERE NR.netSpeedtest='HTTP' AND LC.test_type='browser'  "
					+ "AND (LC.EVENT_NAME = 'Current RX bytes' OR LC.EVENT_NAME = 'Current RXSegments' "
					+ "OR LC.EVENT_NAME = 'PageStart' OR LC.EVENT_NAME = 'PageFinished') "
					+ "AND NR.market_id='"
					+ masterMarketId
					+ "' AND NR.TEST_NAME='"
					+ jsonTestName
					+ "' AND LC.TEST_NAME LIKE '" + jsonTestName + "%'";

//			System.out
//					.println("=====getBrowserDetails QUERY===" + browserQuery);
			BrowserBean finalBean = getPageDownloadThroughPut(jsonTestName, masterMarketId);
			rs = st.executeQuery(browserQuery);
			browserValueList.add(browserBean);
			while (rs.next()) {
			    browserBean = new BrowserBean();
				httpAvgTotalLatency = rs.getString("HTTPAvgTotalLatency");
				httpAvgReadRate = rs.getString("HTTPAvgReadRate");
				httpAvgConnct = rs.getString("HTTPAvgConnectLatency");
				timeStamp = rs.getString("TIME_STAMP");
				boolean isValuepresent = false;
				if (null == httpAvgTotalLatency) {
					browserBean.setHttpAvgTotalLatency("0.00");
				} else {
					isValuepresent = true;
					browserBean.setHttpAvgTotalLatency(httpAvgTotalLatency);
				}
				if (null == httpAvgReadRate) {
					browserBean.setHttpAvgReadRate("0.00");
				} else {
					isValuepresent = true;
					browserBean.setHttpAvgReadRate(httpAvgReadRate);
				}
				if (null == httpAvgConnct) {
					browserBean.setHttpAvgConnct("0.00");
				} else {
					isValuepresent = true;
					browserBean.setHttpAvgConnct(httpAvgConnct);
				}
				if (null == timeStamp) {
					browserBean.setTimeStamp("0.00");
				} else {
					isValuepresent = true;
					browserBean.setTimeStamp(timeStamp);
				}
				browserBean.setDeviceName(masterDeviceName);
				browserBean.setMarketName(marketName);
				
				browserBean.setPageDownloadSpeed(finalBean.getPageDownloadSpeed());
				browserBean.setPageDownloadStartTime(finalBean.getPageDownloadStartTime());
				browserBean.setPageDownloadEndTime(finalBean.getPageDownloadEndTime());
				if (isValuepresent) {
//					browserValueList.add(browserBean);
					browserValueList.set(0, browserBean);
				}
			}
			BrowserBean tempBrowserBean = browserValueList.get(0);
			tempBrowserBean.setDeviceName(masterDeviceName);
			tempBrowserBean.setMarketName(marketName);
			tempBrowserBean.setTestName(jsonTestName);
			tempBrowserBean.setPageDownloadSpeed(finalBean.getPageDownloadSpeed());
			tempBrowserBean.setPageDownloadStartTime(finalBean.getPageDownloadStartTime());
			tempBrowserBean.setPageDownloadEndTime(finalBean.getPageDownloadEndTime());
			tempBrowserBean.setAvgTime(finalBean.getAvgTime());
			browserValueList.set(0, tempBrowserBean);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				closeConn();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
//		System.out.println("browserValueList size :" + browserValueList.size()
//				+ " : browserValueList :" + browserValueList);
		return browserValueList;
	}

	public List<BrowserBean> getBrowserDetailsForLte(String masterDeviceName,
			String masterMarketId, String jsonTestName, String marketName) {
		// TODO Auto-generated method stub
		// System.out.println("inside getBrowserDetails..............................................................starts..........................");
		openConn();
		List<BrowserBean> browserValueListLte = new ArrayList<BrowserBean>();
		String httpAvgTotalLatency = null;
		String httpAvgReadRate = null;
		String httpAvgConnct = null;
		String timeStamp = null;

		// System.out.println("masterDeviceName:"+masterDeviceName+":masterMarketId:"+masterMarketId+":jsonTestName:"+jsonTestName);
		// masterDeviceName:LG-D801:masterMarketId:22:jsonTestName:g2

		try {
			String browserQuery = "SELECT AVG(NR.HTTPAvgTotalLatency) AS HTTPAvgTotalLatency, "
					+ "AVG(NR.HTTPAvgReadRate) AS HTTPAvgReadRate,AVG(NR.HTTPAvgConnectLatency) AS HTTPAvgConnectLatency, "
					+ "AVG(LC.TEST_IDENTIFIER_TIMESTAMP) AS TIME_STAMP "
					+ "FROM stg_net_results NR , stg_log_cat_info LC "
					+ "WHERE NR.netSpeedtest='HTTP' AND LC.test_type='browser'  "
					+ "AND (LC.EVENT_NAME = 'Current RX bytes' OR LC.EVENT_NAME = 'Current RXSegments' "
					+ "OR LC.EVENT_NAME = 'PageStart' OR LC.EVENT_NAME = 'PageFinished') "
					+ "AND NR.market_id='"
					+ masterMarketId
					+ "' AND NR.TEST_NAME='"
					+ jsonTestName
					+ "' AND LC.TEST_NAME LIKE '"
					+ jsonTestName
					+ "%' and NR.Networktype like '%LTE%' ";

//			System.out.println("=====getBrowserDetails QUERY==Lte===="
//					+ browserQuery);
			rs = st.executeQuery(browserQuery);
			while (rs.next()) {
				BrowserBean browserBean = new BrowserBean();
				httpAvgTotalLatency = rs.getString("HTTPAvgTotalLatency");
				httpAvgReadRate = rs.getString("HTTPAvgReadRate");
				httpAvgConnct = rs.getString("HTTPAvgConnectLatency");
				timeStamp = rs.getString("TIME_STAMP");
				if (null == httpAvgTotalLatency) {
					browserBean.setHttpAvgTotalLatency("0.00");
				} else {
					browserBean.setHttpAvgTotalLatency(httpAvgTotalLatency);
				}
				if (null == httpAvgReadRate) {
					browserBean.setHttpAvgReadRate("0.00");
				} else {
					browserBean.setHttpAvgReadRate(httpAvgReadRate);
				}
				if (null == httpAvgConnct) {
					browserBean.setHttpAvgConnct("0.00");
				} else {
					browserBean.setHttpAvgConnct(httpAvgConnct);
				}
				if (null == timeStamp) {
					browserBean.setTimeStamp("0.00");
				} else {
					browserBean.setTimeStamp(timeStamp);
				}
				browserBean.setDeviceName(masterDeviceName);
				browserBean.setMarketName(marketName);
				browserValueListLte.add(browserBean);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				closeConn();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
//		System.out.println("browserValueList size----------- :"
//				+ browserValueListLte.size()
//				+ " : browserValueListLte----------- :" + browserValueListLte);
		return browserValueListLte;
	}

	public BrowserBean getPageDownloadThroughPut(String testName,
			String marketId) {
		List<BrowserBean> pageDownLoadThroughputList = new ArrayList<BrowserBean>();
		Connection conn = DBUtil.getConnection();
		try {
			String query = "SELECT DISTINCT SLC.* FROM STG_DEVICE_INFO SDI,"
					+ "(SELECT * FROM STG_LOG_CAT_INFO WHERE TEST_TYPE = 'browser' and TEST_NAME LIKE '"
					+ testName
					+ "-%') "
					+ "SLC WHERE SLC.TEST_IDENTIFIER_TIMESTAMP = SDI.TEST_IDENTIFIER_TIMESTAMP AND SDI.MARKET_ID = '"
					+ marketId
					+ "' AND SLC.EVENT_NAME IN "
					+ "('PageStart','PageFinished','Current RX bytes')  order by slc.time_stamp";
			Statement st = conn.createStatement();
//			System.out.println("query-------"+query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				BrowserBean browserBean = new BrowserBean();
				String timeStamp = rs.getString("TIME_STAMP");
				String eventName = rs.getString("EVENT_NAME");
				String eventValue = rs.getString("EVENT_VALUE");
				String testname = rs.getString("TEST_NAME");
				browserBean.setEvent_name(eventName);
				browserBean.setEvent_value(eventValue);
				browserBean.setTimeStamp(timeStamp);
				browserBean.setTestName(testname);
				pageDownLoadThroughputList.add(browserBean);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
//		getThroughPut(pageDownLoadThroughputList);
//		System.out.println("pageDownLoadThroughputList---------"+pageDownLoadThroughputList);
		return getThroughPut(pageDownLoadThroughputList);
	}

	private BrowserBean  getThroughPut(List<BrowserBean> pageDownLoadThroughputList){
		List<BrowserBean> pageDownLoadFilterList = new ArrayList<BrowserBean>();
		for(int i=0;i<pageDownLoadThroughputList.size();i++){
			BrowserBean browserBean =  pageDownLoadThroughputList.get(i);
			String eventName = browserBean.getEvent_name();
			if(eventName.equalsIgnoreCase("PageStart")){
				pageDownLoadFilterList.add(pageDownLoadThroughputList.get(i+2));
				i++;
				i++;
			}
			if(eventName.equalsIgnoreCase("PageFinished")){
				pageDownLoadFilterList.add(pageDownLoadThroughputList.get(i-1));
			}
		}
		
		return getThroughPutFinal(pageDownLoadFilterList);
	}
	
	private BrowserBean  getThroughPutFinal(List<BrowserBean> pageDownLoadThroughputList){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		List<Float> throughputAvgList = new ArrayList<Float>();
		List<Float> timeAvgList = new ArrayList<Float>();
		BrowserBean finalBean = new BrowserBean();
		String currentTestName = "";
		String prevTestName = "";
		List<Float> cycleTimeStamp = new ArrayList<Float>();
		try{
//			System.out.println("pageDownLoadThroughputList---------"+pageDownLoadThroughputList);
			for(int i=0;i<pageDownLoadThroughputList.size();i++){
				BrowserBean browserBean =  pageDownLoadThroughputList.get(i);
				currentTestName = browserBean.getTestName();
				i++;
				BrowserBean nextbrowserBean = pageDownLoadThroughputList.get(i);
//				System.out.println(browserBean.getTimeStamp()+"-------browserBean-------"+browserBean.getTestName());
//				System.out.println(nextbrowserBean.getTimeStamp()+"-------------nextbrowserBean-------"+nextbrowserBean.getTestName());
			
				Float eventValue = new Float(nextbrowserBean.getEvent_value());
				Date currentTimeStamp = newSdf.parse(browserBean.getTimeStamp());
				Date nextTimeStamp = newSdf.parse(nextbrowserBean
						.getTimeStamp());
				c.setTime(currentTimeStamp);
				long currentSecs = (c.get(Calendar.SECOND)
						+ c.get(Calendar.MINUTE) * 60 + c
						.get(Calendar.HOUR) * 3600);

				c.setTime(nextTimeStamp);
				long nextSecs = (c.get(Calendar.SECOND)
						+ c.get(Calendar.MINUTE) * 60 + c
						.get(Calendar.HOUR) * 3600);
				float timeDiff = nextSecs-currentSecs;
				if(!prevTestName.endsWith(currentTestName)){
					cycleTimeStamp.add(timeDiff);
				}
				timeAvgList.add(timeDiff);
				throughputAvgList.add((eventValue*8)/(1024*(nextSecs-currentSecs)));
				prevTestName = currentTestName;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(pageDownLoadThroughputList.size()>0){
			finalBean.setPageDownloadSpeed(new ReportHelper().getAverageList(throughputAvgList));
			finalBean.setPageDownloadStartTime(pageDownLoadThroughputList.get(0).getTimeStamp());
			finalBean.setPageDownloadEndTime(pageDownLoadThroughputList.get(pageDownLoadThroughputList.size()-1).getTimeStamp());
			finalBean.setAvgTime((new ReportHelper().getAverageList(cycleTimeStamp)).toString());
		}
		else{
			finalBean.setPageDownloadSpeed(new Float(0));
			finalBean.setPageDownloadStartTime("0");
			finalBean.setPageDownloadEndTime("0");
			finalBean.setAvgTime("0");
		}
		
		return finalBean;
	}
	public static void main(String[] args) {
		new BrowserDaoImpl().getPageDownloadThroughPut("g2app","22");
	}

}
