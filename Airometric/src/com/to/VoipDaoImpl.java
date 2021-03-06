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
import com.util.DBHelper;

public class VoipDaoImpl extends DBHelper {

	public List<VoipBean> getVoipDetails(String masterDeviceName,
			String masterMarketId, String jsonTestName, String marketName) {
		// TODO Auto-generated method stub
		// System.out.println("inside getVoipDetails..............................................................starts..........................");
		List<VoipBean> voipValueList = new ArrayList<VoipBean>();
		String voipSipRegister = null;
		String voipDownAvgJitter = null;
		String voipUpAvgJitter = null;
		String voipSipBye = null;
		String voipSipInvite = null;
		openConn();
		// System.out.println("masterDeviceName:"+masterDeviceName+":masterMarketId:"+masterMarketId+":jsonTestName:"+jsonTestName);
		// masterDeviceName:LG-D801:masterMarketId:22:jsonTestName:g2

		try {
			String voipQuery = "SELECT AVG(VOIPSIPRegister) AS VOIPSIPRegister, "
					+ "AVG(VOIPDownstreamAvgJitter) AS VOIPDownstreamAvgJitter, "
					+ "AVG(VoIPUpstreamAvgJitter) AS VoIPUpstreamAvgJitter, AVG(VoIPSIPBye) AS VoIPSIPBye,AVG(VoIPSIPInvite) AS VoIPSIPInvite "
					+ "FROM stg_net_results "
					+ "WHERE netSpeedtest='VoIP' AND market_id='"
					+ masterMarketId + "' AND test_name='" + jsonTestName + "'";

//			System.out.println("=====getVoipDetails QUERY===" + voipQuery);
			rs = st.executeQuery(voipQuery);
			while (rs.next()) {
//				System.out.println("inside if--------------");
				VoipBean voipBean = new VoipBean();
				voipSipRegister = rs.getString("VOIPSIPRegister");
				voipDownAvgJitter = rs.getString("VOIPDownstreamAvgJitter");
				voipUpAvgJitter = rs.getString("VoIPUpstreamAvgJitter");
				voipSipBye = rs.getString("VoIPSIPBye");
				voipSipInvite = rs.getString("VoIPSIPInvite");
				if (null == voipSipRegister) {
					voipBean.setVoipSipRegister("0.00");
				} else {
					voipBean.setVoipSipRegister(voipSipRegister);
				}
				if (null == voipDownAvgJitter) {
					voipBean.setVoipDownAvgJitter("0.00");
				} else {
					voipBean.setVoipDownAvgJitter(voipDownAvgJitter);
				}
				if (null == voipUpAvgJitter) {
					voipBean.setVoipUpAvgJitter("0.00");
				} else {
					voipBean.setVoipUpAvgJitter(voipUpAvgJitter);
				}
				if (null == voipSipBye) {
					voipBean.setVoipSipBye("0.00");
				} else {
					voipBean.setVoipSipBye(voipSipBye);
				}
				if (null == voipSipInvite) {
					voipBean.setVoipSipInvite("0.00");
				} else {
					voipBean.setVoipSipInvite(voipSipInvite);
				}
				voipBean.setDeviceName(masterDeviceName);
				voipBean.setMarketName(marketName);
				Float[] throughPutVals = getVoipThroughPut(jsonTestName, masterMarketId);
				voipBean.setVoipRxThroughPut(throughPutVals[0]);
				voipBean.setVoipTxThroughPut(throughPutVals[1]);
				voipValueList.add(voipBean);
			}
//			System.out.println("voipValueList----------" + voipValueList);
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
//		System.out.println("vopiValueList size :" + voipValueList.size()
//				+ " : vopiValueList :" + voipValueList);
		return voipValueList;
	}

	public Float[] getVoipThroughPut(String testName, String marketId) {
		Float voipTxThroughPut = new Float(0);
		Float voipRxThroughPut = new Float(0);
		Float[] throughputVals = new Float[2];
		Connection conn = DBUtil.getConnection();
		Calendar c = Calendar.getInstance();
		Date currentTimeStamp = null;
		Date nextTimeStamp = null;
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		String query = "SELECT EVENT_NAME, AVG(EVENT_VALUE) AS AVG_VAL FROM STG_LOG_CAT_INFO SLC,"
				+ "(	SELECT TEST_IDENTIFIER_TIMESTAMP FROM STG_DEVICE_INFO WHERE MARKET_ID='"+marketId+"') SDI WHERE SLC.TEST_TYPE='voip' AND "
				+ "SLC.TEST_NAME LIKE '"
				+ testName
				+ "-%' AND SLC.EVENT_NAME IN('CURRENT RX BYTES','CURRENT TX BYTES') AND "
				+ "SLC.TEST_IDENTIFIER_TIMESTAMP = SDI.TEST_IDENTIFIER_TIMESTAMP GROUP BY EVENT_NAME";
		
		
		try {
			Statement st = conn.createStatement();
//			System.out.println("query---------"+query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String eventName = rs.getString("EVENT_NAME");
				String eventValue = rs.getString("AVG_VAL");
				if(eventName.equalsIgnoreCase("Current TX bytes")){
					voipTxThroughPut = new Float(eventValue);
				}
				else{
					voipRxThroughPut = new Float(eventValue);
				}
			}
			
			String getTimeStamp =  "SELECT time_stamp  FROM STG_LOG_CAT_INFO SLC,"
				+ "(	SELECT TEST_IDENTIFIER_TIMESTAMP FROM STG_DEVICE_INFO WHERE MARKET_ID='"+marketId+"') SDI WHERE SLC.TEST_TYPE='voip' AND "
				+ "SLC.TEST_NAME LIKE '"
				+ testName
				+ "-%' AND SLC.EVENT_NAME IN('CURRENT RX BYTES','CURRENT TX BYTES') AND "
				+ "SLC.TEST_IDENTIFIER_TIMESTAMP = SDI.TEST_IDENTIFIER_TIMESTAMP order by time_stamp";
//			System.out.println(getTimeStamp);
			 rs = st.executeQuery(getTimeStamp);
			while (rs.next()) {
				currentTimeStamp = newSdf.parse(rs.getString("time_stamp"));
				rs.last();
				nextTimeStamp = newSdf.parse(rs.getString("time_stamp"));
				
				c.setTime(currentTimeStamp);
				long currentSecs = (c.get(Calendar.SECOND)
						+ c.get(Calendar.MINUTE) * 60 + c
						.get(Calendar.HOUR) * 3600);

				c.setTime(nextTimeStamp);
				long nextSecs = (c.get(Calendar.SECOND)
						+ c.get(Calendar.MINUTE) * 60 + c
						.get(Calendar.HOUR) * 3600);
//				System.out.println("voipRxThroughPut-------"+voipRxThroughPut);
//				System.out.println("(nextSecs-currentSecs)-------"+(nextSecs-currentSecs));
				throughputVals[0] = voipRxThroughPut/(nextSecs-currentSecs);
				throughputVals[1] = voipTxThroughPut/(nextSecs-currentSecs);
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
//		System.out.println("throughputVals-----------"+throughputVals[0]);
//		System.out.println("throughputVals-----------"+throughputVals[1]);
		return throughputVals;
	}
	
	public static void main(String[] args) {
		new VoipDaoImpl().getVoipThroughPut("g2","33");
	}
}
