package com.preprocessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.model.DBUtil;
import com.preprocessorhelpers.SummaryDetailsStorer;
import com.preprocessorhelpers.VoiceConnectivityProccesorHelper;
import com.preprocessorhelpers.VoiceQualityHelper;
import com.preprocessorhelpers.Voice_DataPreprocessorHelper;

public class PreprocessorTrigger {

	String testNames;
	List<String> markets;
	String testType;
	
	public static Connection conn = null;
	public static Statement st = null;
	
	public PreprocessorTrigger(String testNames) {
//		new AssignSnapShot().main(null);
		this.testNames = testNames;
		this.markets = new ArrayList<String>();
		populateMarkets(testNames);
		deletePreProcesstest(testNames);
		getTestTypes(testNames);
	}

	public void populateMarkets(String testName){
		openConn();
		String getAllmarkets = "SELECT DISTINCT MARKET_ID FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"+testName+"%'";
		try{
			ResultSet rs = st.executeQuery(getAllmarkets);
//			System.out.println("getAllmarkets---------"+getAllmarkets);
			while(rs.next()){
				String marketId = rs.getString("MARKET_ID");
				markets.add(marketId);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		closeConn();
		
	}
	
	public void getTestTypes(String testName){
		openConn();
		String query = "SELECT DISTINCT TEST_TYPE FROM STG_DEVICE_INFO WHERE TEST_NAME like '%"+testName+"%'";
		try{
			ResultSet rs = st.executeQuery(query );
//			System.out.println("getAllmarkets---------"+query );
			while(rs.next()){
				testType = rs.getString("TEST_TYPE");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		closeConn();
		
	}
	
	public String getTestType(String testName){
		openConn();
		String query = "SELECT DISTINCT TEST_TYPE FROM STG_DEVICE_INFO WHERE TEST_NAME like '%"+testName+"%'";
		try{
			ResultSet rs = st.executeQuery(query );
//			System.out.println("getAllmarkets---------"+query );
			while(rs.next()){
				testType = rs.getString("TEST_TYPE");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return testType;
		
	}
	
	
	public void deletePreProcesstest(String testName){
		openConn();
		try{
			String deleteQuery = "DELETE FROM ftp_throughput WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM ftpcalculationtable WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_cal_callretention WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_cal_callretention_1 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_cal_voicequality_1 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_cal_voicequality_2 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calc_voiceconnectivity WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calc_voiceconnectivity_1 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calc_voiceconnectivity_2 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calculation_voicedata_1 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calculation_voicedata_2 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM precalculated_tests WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calculation_TCP_level1 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
			deleteQuery = "DELETE FROM pre_calculation_udp_level1 WHERE TEST_NAME LIKE '"+testName+"%'";
			st.execute(deleteQuery);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public void triggerPreprocessin(){
		openConn();
		FtpPreproccesor ftp = new FtpPreproccesor();
		VoiceConnectivityProccesorHelper vcph = new VoiceConnectivityProccesorHelper();
//		VoiceConnectivityProccesorHelper2 vcph2 = new VoiceConnectivityProccesorHelper2();
		Voice_DataPreprocessorHelper vdp = new Voice_DataPreprocessorHelper();
		SummaryDetailsStorer sds = new SummaryDetailsStorer();
		VoiceQualityHelper vq = new VoiceQualityHelper();
		NetTestPreprocessor netPreprocesor = new NetTestPreprocessor();
		for(int i=0;i<markets.size();i++){
			String marketId = markets.get(i);

			vq.prepopulateVQData(testNames,marketId);
			System.out.println("VQ...1");
//			if(testType.equalsIgnoreCase("externaltest"))
//				vcph2.prepopulateVCData(testNames,marketId,testType);
//			else
			vcph.prepopulateVCData(testNames,marketId,testType);
			System.out.println("VC...2");
			ftp.prepopulateFtpData(testNames,marketId);
			System.out.println("FTP...3");
			vdp.preCalculateVoiceData(testNames,marketId);
			System.out.println("VDP...4");
			//Comment by ankit on 11/04/16
			//netPreprocesor.populateTcpLevel1Table(testNames, marketId);
			netPreprocesor.populateTcpLevel1Table(testNames, marketId,testType);
			System.out.println("TCP...5");
			//Comment by ankit on 11/04/16
			//netPreprocesor.populateUdpLevel1Table(testNames, marketId);
			netPreprocesor.populateUdpLevel1Table(testNames, marketId,testType);
			System.out.println("UDP...6");
			insertTestPreprocessed(testNames);
			System.out.println("INSERT...7");
			sds.getVoiceSummary(testNames,marketId);
			System.out.println("VS...8");
			closeConn();
			
		}
	}
	
	private void insertTestPreprocessed(String testName){
		String query = "INSERT INTO PRECALCULATED_TESTS VALUES('"+testName+"','COMPLETED')";
		try {
			st.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openConn(){
		try{
			conn= DBUtil.getConnection();
			st = conn.createStatement();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void closeConn(){
		try{
			st.close();
			conn.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PreprocessorTrigger ppt = new PreprocessorTrigger("nokiatestmo1");
		ppt.triggerPreprocessin();
//		List<Integer> testList = new ArrayList<Integer>();
//		testList.add(1);
//		testList.add(2);
//		String test = testList.toString();
		//System.out.println(test.substring(1,test.length()-1));
		
	}
}
