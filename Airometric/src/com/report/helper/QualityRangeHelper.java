package com.report.helper;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.model.DBUtil;
import com.report.dao.impl.DataConnectivityDaoImpl;
import com.to.QualityRangeTo;

public class QualityRangeHelper {

	public QualityRangeTo getNonLteQualityrange(String costomId) {
		QualityRangeTo qualityTo = new QualityRangeTo();
		try {
			// Statement st = DataConnectivityDaoImpl.st;
			Statement st = DBUtil.getConnection().createStatement();

			String query = "SELECT * FROM MASTER_QUALITY_RANGE WHERE CUSTOMRANGE_ID = '"
					+ costomId + "' AND NETWORK_TYPE = 'NONLTE'";
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				String qualtiyName = rs.getString("QUALITY_NAME");
				int lowerRange = rs.getInt("MIN_VALUE");
				int upperRange = rs.getInt("MAX_VALUE");
				getQuality(qualtiyName, lowerRange, upperRange, qualityTo);

			}
			//System.out.println(qualityTo.getLowerRangeForAvg());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return qualityTo;
	}

	public QualityRangeTo getLteQualityrange(String costomId) {
		QualityRangeTo qualityTo = new QualityRangeTo();
		try {
			Statement st = DBUtil.getConnection().createStatement();
			// Statement st = DataConnectivityDaoImpl.st;

			String query = "SELECT * FROM MASTER_QUALITY_RANGE WHERE CUSTOMRANGE_ID = '"
					+ costomId + "' AND NETWORK_TYPE = 'LTE'";
			//System.out.println("query----------" + query);
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				String qualtiyName = rs.getString("QUALITY_NAME");
				int lowerRange = rs.getInt("MIN_VALUE");
				int upperRange = rs.getInt("MAX_VALUE");
				getQuality(qualtiyName, lowerRange, upperRange, qualityTo);

			}
			//System.out.println(qualityTo.getLowerRangeForAvg());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return qualityTo;
	}

	private void getQuality(String qualityName, int lowerRange, int upperRange,
			QualityRangeTo qualityTo) {
		//System.out.println("qualityName---------" + qualityName);
		if (qualityName.equals("GOOD")) {
			qualityTo.setLowerRangeForGood(lowerRange);
			qualityTo.setUpperRangeForGood(upperRange);
		}

		if (qualityName.equals("AVERAGE")) {
			qualityTo.setLowerRangeForAvg(lowerRange);
			qualityTo.setUpperRangeForAvg(upperRange);
		}

		if (qualityName.equals("POOR")) {
			qualityTo.setLowerRangeForPoor(lowerRange);
			qualityTo.setUpperRangeForPoor(upperRange);
		}
	}
	
	private void getQuality(String qualityName, int lowerRange, int upperRange,
			QualityRangeTo qualityTo,String networkType) {
		//System.out.println("qualityName---------" + qualityName);
		if (qualityName.equals("GOOD")) {
			if(networkType.equalsIgnoreCase("nonlte")){
				qualityTo.setLowerRangeForGood(lowerRange);
				qualityTo.setUpperRangeForGood(upperRange);
			}
			else{
				qualityTo.setLtelowerRangeForGood(lowerRange);
				qualityTo.setLteupperRangeForGood(upperRange);
			}
		}

		if (qualityName.equals("AVERAGE")) {
			if(networkType.equalsIgnoreCase("nonlte")){
				qualityTo.setLowerRangeForAvg(lowerRange);
				qualityTo.setUpperRangeForAvg(upperRange);
			}
			else{
				qualityTo.setLtelowerRangeForAvg(lowerRange);
				qualityTo.setLteupperRangeForAvg(upperRange);
			}
		}

		if (qualityName.equals("POOR")) {
			if(networkType.equalsIgnoreCase("nonlte")){
			qualityTo.setLowerRangeForPoor(lowerRange);
			qualityTo.setUpperRangeForPoor(upperRange);
			}
			else{
				qualityTo.setLtelowerRangeForPoor(lowerRange);
				qualityTo.setLteupperRangeForPoor(upperRange);
			}
		}
	}

	public QualityRangeTo getConfigNonLteQualityrange(String config_id) {
		QualityRangeTo qualityTo = new QualityRangeTo();
		try {
			Statement st = DataConnectivityDaoImpl.st;

			String query = "SELECT * FROM configsaved_quality_range WHERE CONFIG_ID = '"
					+ config_id + "' AND NETWORK_TYPE = 'NONLTE'";
			//System.out.println("query----------" + query);
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				String qualtiyName = rs.getString("QUALITY_NAME");
				int lowerRange = rs.getInt("MIN_VALUE");
				int upperRange = rs.getInt("MAX_VALUE");
				getQuality(qualtiyName, lowerRange, upperRange, qualityTo);

			}
			//System.out.println(qualityTo.getLowerRangeForAvg());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return qualityTo;
	}

	public QualityRangeTo getConfigLteQualityrange(String config_id) {
		QualityRangeTo qualityTo = new QualityRangeTo();
		try {
			// Statement st =DBUtil.getConnection().createStatement();
			Statement st = DataConnectivityDaoImpl.st;

			String query = "SELECT * FROM configsaved_quality_range WHERE CONFIG_ID = '"
					+ config_id + "' AND NETWORK_TYPE = 'LTE'";
			//System.out.println("query----------" + query);
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				String qualtiyName = rs.getString("QUALITY_NAME");
				int lowerRange = rs.getInt("MIN_VALUE");
				int upperRange = rs.getInt("MAX_VALUE");
				getQuality(qualtiyName, lowerRange, upperRange, qualityTo);

			}
			//System.out.println(qualityTo.getLowerRangeForAvg());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return qualityTo;
	}

	public void createRanges(int upperRange, int lowerrangeValue,
			String quality, String networkType,int customId) {
		QualityRangeTo qualityTo = new QualityRangeTo();
		try {
			Statement st = DBUtil.getConnection().createStatement();
			// Statement st = DataConnectivityDaoImpl.st;
			
			String query = "INSERT INTO master_quality_range SET MAX_VALUE ='"
					+ lowerrangeValue + "',MIN_VALUE='" + upperRange + "' "
					+ "WHERE QUALITY_NAME = '" + quality
					+ "' AND NETWORK_TYPE = '" + networkType + "'";
			String insertQuery = "INSERT INTO master_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CUSTOMRANGE_ID) VALUES ( '"+networkType+"', '"+quality+"', '"
					+ upperRange
					+ "', '"
					+ lowerrangeValue
					+ "', '"
					+ customId
					+ "')";
//			System.out.println("query----------" + insertQuery);
			st.executeUpdate(insertQuery);

			//System.out.println(qualityTo.getLowerRangeForAvg());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public int insertRangeName(String rangeName, String userId, Statement st) {
		int rangeId = 0;
		String inserQuery = "INSERT into custom_range_usermapping (USER_ID,CUSTOME_RANGE_NAME) VALUES('"
				+ userId + "','" + rangeName + "')";
		String getCustomId = "SELECT MAPPING_ID FROM custom_range_usermapping WHERE CUSTOME_RANGE_NAME = '"
				+ rangeName + "'";
		try {
			st.executeUpdate(inserQuery);
			ResultSet rs = st.executeQuery(getCustomId);
			while (rs.next()) {
				rangeId = rs.getInt("MAPPING_ID");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return rangeId;
	}

	public Map<String, String> getUserRanges() {
		Map<String, String> rangeMap = new LinkedHashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		Connection conn = (Connection) DBUtil.getConnection();
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM custom_range_usermapping WHERE USER_ID='"
					+ userId + "' OR USER_ID='1' ORDER BY MAPPING_ID";
//			System.out.println("query--------------" + query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String custom_id = rs.getString("MAPPING_ID");
				String customName = rs.getString("CUSTOME_RANGE_NAME");
				rangeMap.put(customName, custom_id);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return rangeMap;
	}

	public List<QualityRangeTo> getAllUserRanges() {
		List<QualityRangeTo> allRangesToList = new ArrayList<QualityRangeTo>();
		String currentName = "";
		String prevName = "";
		QualityRangeTo qto = new QualityRangeTo();
		FacesContext context = FacesContext.getCurrentInstance();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		String roleId = context.getExternalContext().getSessionMap().get(
		"loggedInUserRoleID").toString();
		String query = "SELECT CRU.*,MQR.* FROM master_quality_range MQR , "
				+ "(SELECT * FROM custom_range_usermapping WHERE  USER_ID='1' OR USER_ID='"
				+ userId
				+ "')CRU "
				+ "WHERE MQR.CUSTOMRANGE_ID = CRU.MAPPING_ID  GROUP BY CUSTOME_RANGE_NAME,NETWORK_TYPE,QUALITY_NAME ORDER  BY CRU.MAPPING_ID";
//		System.out.println("query-----------"+query);
		Connection conn = (Connection) DBUtil.getConnection();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				currentName = rs.getString("CUSTOME_RANGE_NAME");
				String qualtiyName = rs.getString("QUALITY_NAME");
				String customId = rs.getString("MAPPING_ID");
				String networkType = rs.getString("NETWORK_TYPE");
				int lowerRange = rs.getInt("MIN_VALUE");
				int upperRange = rs.getInt("MAX_VALUE");
				
				if(!prevName.equalsIgnoreCase(currentName)){
					
					qto = new QualityRangeTo();
					allRangesToList.add(qto);
				}
				if(currentName.equalsIgnoreCase("Default")){
					qto.setEditable("true");
				}
				else{
					qto.setEditable("false");
				}
				qto.setCustomName(currentName);
				qto.setCustomId(customId);
				getQuality(qualtiyName,lowerRange,upperRange,qto,networkType);
				
				prevName = currentName;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return allRangesToList;
	}

	public boolean updateRanges(int lowerRangeForGood, int upperRangeForGood, int lowerRangeForPoor,int upperRangeForPoor,int lowerRangeForAvg, int upperRangeForAvg,
			int ltelowerRangeForGood, int lteupperRangeForGood,int ltelowerRangeForPoor, int lteupperRangeForPoor,int ltelowerRangeForAvg, int lteupperRangeForAvg,String customId){
		PreparedStatement brandTranPst = null;
		Connection conn = (Connection) DBUtil.getConnection();
		String updateQuery = "UPDATE master_quality_range SET MIN_VALUE =?, MAX_VALUE = ? WHERE CUSTOMRANGE_ID=? AND NETWORK_TYPE=? AND QUALITY_NAME =?";
		try {
			brandTranPst = conn
			.prepareStatement(updateQuery);
			
			brandTranPst.setInt(1, lowerRangeForGood);
			brandTranPst.setInt(2, upperRangeForGood);
			brandTranPst.setString(3, customId);
			brandTranPst.setString(4, "NONLTE");
			brandTranPst.setString(5, "GOOD");
			//System.out.println("brandTranPst---------"+brandTranPst);
			brandTranPst.addBatch();

			brandTranPst.setInt(1, lowerRangeForPoor);
			brandTranPst.setInt(2, upperRangeForPoor);
			brandTranPst.setString(3, customId);
			brandTranPst.setString(4, "NONLTE");
			brandTranPst.setString(5, "POOR");
			brandTranPst.addBatch();
			
			brandTranPst.setInt(1, lowerRangeForAvg);
			brandTranPst.setInt(2, upperRangeForAvg);
			brandTranPst.setString(3, customId);
			brandTranPst.setString(4, "NONLTE");
			brandTranPst.setString(5, "AVERAGE");
			brandTranPst.addBatch();
			
			brandTranPst.setInt(1, ltelowerRangeForGood);
			brandTranPst.setInt(2, lteupperRangeForGood);
			brandTranPst.setString(3, customId);
			brandTranPst.setString(4, "LTE");
			brandTranPst.setString(5, "GOOD");
			brandTranPst.addBatch();

			brandTranPst.setInt(1, ltelowerRangeForPoor);
			brandTranPst.setInt(2, lteupperRangeForPoor);
			brandTranPst.setString(3, customId);
			brandTranPst.setString(4, "LTE");
			brandTranPst.setString(5, "POOR");
			brandTranPst.addBatch();
			
			brandTranPst.setInt(1, ltelowerRangeForAvg);
			brandTranPst.setInt(2, lteupperRangeForAvg);
			brandTranPst.setString(3, customId);
			brandTranPst.setString(4, "LTE");
			brandTranPst.setString(5, "AVERAGE");
			brandTranPst.addBatch();
			
			int[] count = brandTranPst.executeBatch();
			brandTranPst.execute("COMMIT");
			//System.out.println("count----------"+count);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean isInserted = true;;
		return isInserted;
	}
	public static void main(String[] args) {
		QualityRangeTo qualityTo = new QualityRangeHelper()
				.getNonLteQualityrange("1");
		File  myFold = new File ("C:\\MobileAppTests\\toproccessfiles\\m3sed");
		File[] myFiles = myFold.listFiles();
		for(File myFile : myFiles){
			File newFile = new File(myFile.getName().substring(myFile.getName().indexOf("deviceinfo")));
			myFile.renameTo(newFile);
		}
		String fileNAme = "13-08-14_1407956151648_ftp_013925000002975_M3SED-1_1407956060182";
//		System.out.println(fileNAme.substring(fileNAme.indexOf("deviceinfo")));

/*//		//System.out.println("getLowerRangeForAvg---"
////				+ qualityTo.getLowerRangeForAvg());
//		//System.out.println("getUpperRangeForAvg---"
//				+ qualityTo.getUpperRangeForAvg());
//		//System.out.println("getLowerRangeForGood---"
//				+ qualityTo.getLowerRangeForGood());
//		//System.out.println("getUpperRangeForGood---"
//				+ qualityTo.getUpperRangeForGood());
//		//System.out.println("getLowerRangeForPoor---"
//				+ qualityTo.getLowerRangeForPoor());
//		//System.out.println("getUpperRangeForPoor---"
//				+ qualityTo.getUpperRangeForPoor());
*/	}
}
