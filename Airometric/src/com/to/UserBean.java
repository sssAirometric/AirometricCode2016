package com.to;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.management.relation.Role;
import javax.xml.transform.Result;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.primefaces.component.log.Log;

import com.constants.Roles;
import com.dao.UserDao;
import com.dao.impl.MarketPlaceDaoImpl;
import com.dao.impl.OperatorDaoImpl;
import com.dao.impl.TestConfigDaoImpl;
import com.dao.impl.UserDaoImpl;
import com.validator.TestCaseValidator;
import com.dao.MarketPlaceDao;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
/**
 * @author Mohsin 100787
 *
 */
public class UserBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String loginUserName;
	private String loginPassWord;
	private String userName;
	private String password;
	private String email;
	private String imei;
	private String deviceId;
	private String deviceType;
	private String mobileNumber;
	private String role;
	private String operatorId;
	private String operatorName;
	private String userMessage;
	private String emailMessage;
	private String UserId;
	private List<UserBean> userList;
	private List<UserBean> operatorList;
	boolean enableListUserDiv = false;
	private int userStatus;
	private String userStatusStr;
	private  List<TestConfig> assignListTestConfig;
    private boolean selected;
    private boolean enableLoginUserDetails = false;
    private String slno;
    private int operatorStatus;
    private String operatorStatusStr;
    private String[] marketId;
    private String countryId;
    private String customeRangeId;
    private int superioruser;
    private String imeiLimit;
    private Set<String> userListOfUser;
    private String imei_user;
    private int ImeiCount;
    private final String USER_AGENT = "Mozilla/5.0";
    
	public int getSuperioruser() {
		return superioruser;
	}
	public void setSuperioruser(int superioruser) {
		this.superioruser = superioruser;
	}
	public String getCustomeRangeId() {
		return customeRangeId;
	}
	public void setCustomeRangeId(String customeRangeId) {
		this.customeRangeId = customeRangeId;
	}
	public String getOperatorStatusStr() {
		return operatorStatusStr;
	}
	public void setOperatorStatusStr(String operatorStatusStr) {
		this.operatorStatusStr = operatorStatusStr;
	}
	public List<UserBean> getOperatorList() {
		return operatorList;
	}
	public void setOperatorList(List<UserBean> operatorList) {
		this.operatorList = operatorList;
	}
	public int getOperatorStatus() {
		return operatorStatus;
	}
	public void setOperatorStatus(int operatorStatus) {
		this.operatorStatus = operatorStatus;
	}
	public String getSlno() {
		return slno;
	}
	public void setSlno(String slno) {
		this.slno = slno;
	}

	public boolean isEnableLoginUserDetails() {
		return enableLoginUserDetails;
	}

	public void setEnableLoginUserDetails(boolean enableLoginUserDetails) {
		this.enableLoginUserDetails = enableLoginUserDetails;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	Map<String, String> roleMap = new LinkedHashMap<String, String>();
	Map<String,String> operatorMap = new LinkedHashMap<String,String>();
	Map<String,String> testConfigMap = new LinkedHashMap<String,String>();
	Map<String,String> userMap = new LinkedHashMap<String,String>();
	Map<String,String> testNameMap = new LinkedHashMap<String,String>();
	Map<String, String> marketPlaceMap = new LinkedHashMap<String, String>();
	Map<String, String> countryMap = new LinkedHashMap<String, String>();
	
	private List<DeviceInfoTO>  deviceInfos = new  ArrayList<DeviceInfoTO>();
	
	private List<UserDeviceInfo>  userDeviceInfo = new  ArrayList<UserDeviceInfo>();
	
	private List<UserDeviceInfo>  userDeviceInforesult = new  ArrayList<UserDeviceInfo>();
	
	private List<Integer> marketIdList = new ArrayList<Integer>();
	
	MarketBean marketBean= new MarketBean();
	
	
	
	public MarketBean getMarketBean() {
		return marketBean;
	}
	public void setMarketBean(MarketBean marketBean) {
		this.marketBean = marketBean;
	}
	public List<UserDeviceInfo> getUserDeviceInforesult() {
		return userDeviceInforesult;
	}

	public void setUserDeviceInforesult(List<UserDeviceInfo> userDeviceInforesult) {
		this.userDeviceInforesult = userDeviceInforesult;
	}

	public List<DeviceInfoTO> getDeviceInfos() {
		return deviceInfos;
	}

	public void setDeviceInfos(List<DeviceInfoTO> deviceInfos) {
		this.deviceInfos = deviceInfos;
	}

	private String action;
	
	public String getAction() {
		return action;
	}
    
	public Map<String, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<String, String> userMap) {
		this.userMap = userMap;
	}

	public List<TestConfig> getAssignListTestConfig() {
		return assignListTestConfig;
	}

	public void setAssignListTestConfig(List<TestConfig> assignListTestConfig) {
		this.assignListTestConfig = assignListTestConfig;
	}

	public Map<String, String> getTestConfigMap() {
		return testConfigMap;
	}

	public void setTestConfigMap(Map<String, String> testConfigMap) {
		this.testConfigMap = testConfigMap;
	}

	public List<UserBean> getUserList() {
		return userList;
	}

	public void setUserList(List<UserBean> userList) {
		this.userList = userList;
	}

	public void setAction(String action) {
		this.action = action;
	}
	public List<UserDeviceInfo> getUserDeviceInfo() {
		return userDeviceInfo;
	}
	public void setUserDeviceInfo(List<UserDeviceInfo> userDeviceInfo) {
		this.userDeviceInfo = userDeviceInfo;
	}
	
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	
	public Map<String, String> getRoleMap() {
		return roleMap;
	}
	public void setRoleMap(Map<String, String> roleMap) {
		this.roleMap = roleMap;
	}
	public Map<String, String> getOperatorMap() {
		return operatorMap;
	}
	public void setOperatorMap(Map<String, String> operatorMap) {
		this.operatorMap = operatorMap;
	}

	public boolean isEnableListUserDiv() {
		return enableListUserDiv;
	}
	public void setEnableListUserDiv(boolean enableListUserDiv) {
		this.enableListUserDiv = enableListUserDiv;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getUserMessage() {
		return userMessage;
	}
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	public String getEmailMessage() {
		return emailMessage;
	}
	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}
	public String getLoginUserName() {
		return loginUserName;
	}
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}
	public String getLoginPassWord() {
		return loginPassWord;
	}
	public void setLoginPassWord(String loginPassWord) {
		this.loginPassWord = loginPassWord;
	}
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	UserDeviceInfo userDeviceInfo2 = null;
	
	
	
	public void attrListener(ActionEvent event){
		userDeviceInfo2 = (UserDeviceInfo)event.getComponent().getAttributes().get("userDeviceRm");
	}
	
	public String getUserStatusStr() {
		return userStatusStr;
	}

	public void setUserStatusStr(String userStatusStr) {
		this.userStatusStr = userStatusStr;
	}
	public Map<String, String> getMarketPlaceMap() {
		return marketPlaceMap;
	}
	public void setMarketPlaceMap(Map<String, String> marketPlaceMap) {
		this.marketPlaceMap = marketPlaceMap;
	}
	public String[] getMarketId() {
		return marketId;
	}
	public void setMarketId(String[] marketId) {
		this.marketId = marketId;
	}
	public String getCountryId() {
		return countryId;
	}
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}
	public Map<String, String> getCountryMap() {
		return countryMap;
	}
	public void setCountryMap(Map<String, String> countryMap) {
		this.countryMap = countryMap;
	}
	
	public List<Integer> getMarketIdList() {
		return marketIdList;
	}
	public void setMarketIdList(List<Integer> marketIdList) {
		this.marketIdList = marketIdList;
	}
	/*Imei limit set and get funcion add by ankit 11-09-15
	 * */
	public String getImeiLimit() {
		return imeiLimit;
	}
	public void setImeiLimit(String imeiLimit) {
		this.imeiLimit = imeiLimit;
	}
	public int getImeiCount() {
		return ImeiCount;
	}
	public void setImeiCount(int ImeiCount) {
		this.ImeiCount = ImeiCount;
	}
	public String getIMEI_USER() {
		return imei_user;
	}
	public void setIMEI_USER(String imei_user) {
		this.imei_user = imei_user;
	}
	
	/**
     Add rows for the User Device Info List in the Create User Page.
	 */
	public String addRowAction(){
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		UserDeviceInfo userDev = new UserDeviceInfo();
	    this.userDeviceInfo.add(userDev);
	    userBean.setAction("list");
	    userBean.setUserDeviceInfo(userDeviceInfo);
	    context.getExternalContext().getSessionMap().put("userBean", userBean);
	    return "add";
	}
	/*public String addRowAction(){
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		UserDeviceInfo userDev = new UserDeviceInfo();
		UserDaoImpl udi= new UserDaoImpl();
		int imeiLimit = 0,imeiWithoutUser = 0,adminImeiCount = 0;
		String userRole = null,userid = null; 
		//System.out.println("ROLE :"+userBean.getRole());
		userRole = udi.getUserDetails(userIdLogged).role;
		adminImeiCount = udi.getIMEICountValue(userIdLogged);
		//imeiWithoutUser= udi.getImeiBelongsToUser(userIdLogged).size()-1;
		System.out.println("adminImeiCount : "+adminImeiCount);
		userListOfUser = udi.getUsersOfSuperior(userIdLogged, Roles.ADMIN);
	    userList = udi.listUser(userListOfUser.toString().substring(1, userListOfUser.toString().length()-1),userRole);//
		for(int i=0;i<userList.size();i++)
	    {	
			imeiWithoutUser = udi.getImeiBelongsToUser(userList.get(i).getUserId()).size()+imeiWithoutUser-1;
			if(!userList.get(i).getUserId().equals(userBean.getUserId()))
	    	{
	    	imeiWithoutUser = udi.getImeiBelongsToUser(userList.get(i).getUserId()).size()+imeiWithoutUser-1;
	    	}
			System.out.println("no of devices by userid"+(udi.getImeiBelongsToUser(userList.get(i).getUserId()).size()-1)+"---"+userList.get(i).getUserId());
	    }
	    userBean.setUserDeviceInfo(userDeviceInfo);
	    userBean.setAction("list");
	    context.getExternalContext().getSessionMap().put("userBean", userBean);
	    imeiWithoutUser = this.userDeviceInfo.size()+imeiWithoutUser;
	    System.out.println("total no of rows - "+imeiWithoutUser);
	    if(Integer.parseInt(udi.getUserDetails(userIdLogged).getImeiLimit())<=imeiWithoutUser 
	    		|| Integer.parseInt(udi.getUserDetails(userIdLogged).getImeiLimit())<=adminImeiCount)
			    {
				   context.getExternalContext().getRequestMap().put("LimitReachedmsg", "LimitReached");
			    }
			    else
			    {
			    	this.userDeviceInfo.add(userDev);
			    }
		 return "add";
	}*/
	
	/**
    Add rows for the User Device Info List in the Edit User Page.
	 */
	
	public String addRowEditAction(){
		//System.out.println("Inside this loop");
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		UserDeviceInfo userDevice= new UserDeviceInfo();
	    this.userDeviceInfo.add(userDevice);
	    userBean.setUserDeviceInfo(userDeviceInfo);
	    userBean.setAction("edit");
	    context.getExternalContext().getSessionMap().put("userBean", userBean);
	    return "edit";
	}

	
	/*public String addRowEditAction(){
		//System.out.println("Inside this loop");
		FacesContext context = FacesContext.getCurrentInstance();
		int imeiLimit = 0,imeiWithoutUser = 0,adminImeiCount=0;
		UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		UserDeviceInfo userDevice= new UserDeviceInfo();
		UserDaoImpl udi= new UserDaoImpl();
		String userRole = null,userid = null; 
		//System.out.println("ROLE :"+userBean.getRole());
		userRole = udi.getUserDetails(userBean.getUserId()).role;
		if(userRole.equals("1")){userRole ="admin";
		userid = userBean.getUserId();}
		else if(userRole.equals("2")){userRole ="manager";
		userid = String.valueOf(userBean.getSuperioruser());}
		else if(userRole.equals("3")){userRole ="engineer";
		userid = String.valueOf(udi.getUserDetails(String.valueOf(userBean.getSuperioruser())).superioruser);}
		adminImeiCount = udi.getIMEICountValue(userid);
		userListOfUser = udi.getUsersOfSuperior(userid, Roles.ADMIN);
	    userList = udi.listUser(userListOfUser.toString().substring(1, userListOfUser.toString().length()-1),userRole);//
		for(int i=0;i<userList.size();i++)
	    {	//imeiLimit = udi.getImeiBelongsToUser(userList.get(i).getUserId()).size()+imeiLimit-1;
	    if(!userList.get(i).getUserId().equals(userBean.getUserId()))
	    	{
	    	imeiWithoutUser = udi.getImeiBelongsToUser(userList.get(i).getUserId()).size()+imeiWithoutUser-1;
	    	System.out.println("ye actual size he imeiWithoutUser"+imeiWithoutUser);
	    	}
	    	System.out.println("no of devices by userid"+udi.getImeiBelongsToUser(userList.get(i).getUserId()).size()+"---"+userList.get(i).getUserId());
	    }
	    //System.out.println("total no of devices for an admin - "+imeiLimit);
	    
	    System.out.println("ye actual size he "+this.userDeviceInfo.size());
	    imeiWithoutUser = this.userDeviceInfo.size()+imeiWithoutUser;
	    if(Integer.parseInt(udi.getUserDetails(userid).getImeiLimit())<= adminImeiCount 
	    		|| Integer.parseInt(udi.getUserDetails(userid).getImeiLimit())<=imeiWithoutUser){
		   context.getExternalContext().getRequestMap().put("LimitReachedmsg", "LimitReached");
	    	 }
	    else{
			    this.userDeviceInfo.add(userDevice);
		  }
	    userBean.setAction("edit");
	    userBean.setUserDeviceInfo(userDeviceInfo);
	    context.getExternalContext().getSessionMap().put("userBean", userBean);
	    return "edit";
	}
*/
	
	/**
    Remove rows for the User Device Info List in the Create User Page.
	 */
	public String removeRowAction(){
		String  removedStatus = "false";
		FacesContext context = FacesContext.getCurrentInstance();
		
			for(int i=0; i<userDeviceInfo.size();i++){
				if(userDeviceInfo.size()>1){
					if(userDeviceInfo.get(i).isSelected()){
						removedStatus = removeDeviceRowFromDb(userDeviceInfo.get(i).getDeviceId());
						this.userDeviceInfo.remove(i);
						i--;
					}
				}else{
					context.getExternalContext().getRequestMap().put("message", "Can't remove default value");
				}
			}
			return "remove";
	 }
	/**
    Remove rows for the User Device Info List in the Edit User Page.
	 */
	public String removeRowEditAction(){
		String  removedStatus = "false";
		FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
        String role = userBean.getRole();
        UserDaoImpl userDao = new UserDaoImpl();
        String userid = null;
        if(role.equals("1"))
		{
		userid = userBean.getUserId();
		}
		else if(role.equals("2"))
		{
		userid = String.valueOf(userBean.getSuperioruser());
		}
		else if(role.equals("3") || role.equals("6"))
		{
		userid = String.valueOf(userDao.getUserDetails(String.valueOf(userBean.getSuperioruser())).superioruser);
		}
        
		String imeiLimit = userDao.getUserDetails(userid).imeiLimit;
		int Imeicount = userDao.getIMEICountValue(userid);
		UserDeviceInfo userDevice= new UserDeviceInfo();
			for(int i=0; i<userDeviceInfo.size();i++){
				//if(userDeviceInfo.size()>1){ that condition checks the device for removing atleast one device should needs to be there.
				if(userDeviceInfo.get(i).isSelected()){
					if(userDeviceInfo.get(i).getDeviceType().equals("") || userDeviceInfo.get(i).getMobileNumber().equals("") ||userDeviceInfo.get(i).getImei().equals("")){
						context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
						return "edit";//userCreatedFailed
					}else{
						if( userDeviceInfo.get(i).getMobileNumber().trim().length()>17){
							context.getExternalContext().getRequestMap().put("failuremessage", "Mobile Number Should be equals to 16 characters");
							return "edit";//userCreatedFailed
						}
						if(!TestCaseValidator.isInteger(userDeviceInfo.get(i).getImei()) || userDeviceInfo.get(i).getImei().trim().length()>15){
							context.getExternalContext().getRequestMap().put("failuremessage", "IMEI Should be Numberic and not more than 15 characters");
							return "edit";//userCreatedFailed
						}
					}
						Imeicount = Imeicount-1;
						if(Imeicount<0){
							context.getExternalContext().getRequestMap().put("failuremessage", "IMEI Counter Should not be less then 0.");
							return "edit";//userCreatedFailed
						}
						removedStatus = removeDeviceRowFromDb(userDeviceInfo.get(i).getDeviceId());
						
						userDao.updateIMEICount(userid, Imeicount);
						this.userDeviceInfo.remove(i);
						i--;
					   
						userBean.setAction("edit");
						context.getExternalContext().getSessionMap().put("userBean",userBean);
						context.getExternalContext().getRequestMap().put("message", "Message: Device details deleted successfully");
						context.getExternalContext().getRequestMap().put("failuremessage", "");
					}else if(!userDeviceInfo.get(i).isSelected()){
						userBean.setAction("edit");
						context.getExternalContext().getRequestMap().put("failuremessage", "Message:Please Select a value to remove");
						context.getExternalContext().getSessionMap().put("userBean",userBean);
					}
					
				/*}else{
					if(userDeviceInfo.get(i).isSelected()){
						userBean.setAction("edit");
						context.getExternalContext().getRequestMap().put("failuremessage", "Message:Can't remove default value");
						context.getExternalContext().getSessionMap().put("userBean",userBean);
					}else{
						userBean.setAction("edit");
						context.getExternalContext().getRequestMap().put("failuremessage", "Message:Please Select a value to remove");
						context.getExternalContext().getSessionMap().put("userBean",userBean);
					}
				}*/
			}
			userBean.setImeiLimit(imeiLimit);
			userBean.setImeiCount(Imeicount);
			 this.userDeviceInfo.add(userDevice);
			 userBean.setUserDeviceInfo(userDeviceInfo);
			return "edit";
	 }

	/**
    Remove rows for the User Device Info from the database for both Create/Edit User Page.
	 */
	public String removeDeviceRowFromDb(String deviceid){
		UserDao userDao = new UserDaoImpl();
		String removedFromdb = userDao.removeDeviceFromDb(deviceid);
		return removedFromdb;
	}
	
	/**
       Validate the User that is Login into the Application.
	 */
	public String validateUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		UserDao userDao = new UserDaoImpl();
		UserBean userBean = userDao.validateUserInfo(loginUserName,loginPassWord);
		
		if(userBean != null){
			String role = userBean.getRole();
			if(role.equals("technician") || userBean.getUserStatus() != 1){
				context.getExternalContext().getSessionMap().remove("userBean");
				context.getExternalContext().getRequestMap().put("message", "Access denied");
				return "invalid";
			}
			context.getExternalContext().getSessionMap().put("loggedInUserRoleID", role);
			context.getExternalContext().getSessionMap().put("loggedInUserID", userBean.getUserId());
			if(!role.equals("superadmin")){
				UserBean bean=userDao.validateUserInformation(loginUserName,loginPassWord);
				context.getExternalContext().getSessionMap().put("loggedInOperatorID", bean.getOperatorId());
			}
			
			String login="Welcome"+" "+loginUserName;
			context.getExternalContext().getSessionMap().put("loginName", login);
			context.getExternalContext().getSessionMap().put("userName", loginUserName);
			context.getExternalContext().getSessionMap().remove("userBean");
			context.getExternalContext().getSessionMap().remove("bean");
			return "valid";
		}else{
			context.getExternalContext().getSessionMap().remove("userBean");
			context.getExternalContext().getRequestMap().put("message", "Invalid Credentials");
			return "invalid";
		}
	}
	/**
          Navigation from the Welcome user to the Create User page.
	 */
	public String userPage(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			//marketPlaceMap=marketPlaceDao.getMarketPlaceMap();
			int imeiCount = userDao.getIMEICountValue(userIdLogged);
			String imeiLimit = userDao.getUserDetails(userIdLogged).imeiLimit;
			
			countryMap=marketPlaceDao.getCountryMap();
			//System.out.println("userRole---------"+userRole);
//			if(userRole.equals("superadmin")){	
				System.out.println("inside ifffffffff");
//				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
				Set<String> userNameList =  new UserDaoImpl().getUsersOfSuperior(userIdLogged,userRole);
				userList = userDao.listUser(userNameList.toString().substring(1, userNameList.toString().length()-1),userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged),userIdLogged);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			System.out.println("got the values" + imeiCount  +" "+ imeiLimit);
			context.getExternalContext().getSessionMap().remove("userBean");
			//System.out.println("context.getExternalContext().getSessionMap()--------"+context.getExternalContext().getSessionMap());
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId((String)context.getExternalContext().getSessionMap().get("loggedInOperatorID"));
			userBean.setAction("view");
			userBean.setImeiLimit(imeiLimit);
			userBean.setImeiCount(imeiCount);
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().remove("marketPlaceMapForCountry");
			context.getExternalContext().getSessionMap().remove("marketIdList");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().remove("marketPlaceMapList");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
			//context.getExternalContext().getSessionMap().put("marketPlaceMapList", marketPlaceMap);
			context.getExternalContext().getSessionMap().remove("countryMapList");
			context.getExternalContext().getSessionMap().put("countryMapList", countryMap);
		}catch (Exception e) {
			//System.out.println("exception.................");
			e.printStackTrace();
		}
		//System.out.println("returning........................"+FacesContext.getCurrentInstance().getExternalContext().getSessionMap());
		return "createUserPage";
	}
	
	
	public String operateUser(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//			if(userRole.equals("superadmin")){		
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged),userIdLogged);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			context.getExternalContext().getSessionMap().remove("userBean");
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId("0");
			userBean.setAction("view");
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "operateUserPage";
	}
	
	/**
          Navigation from the Edit user to the Create User page.
     */
	
	public String editToUser(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			int imeiCount = userDao.getIMEICountValue(userIdLogged);
			String imeiLimit = userDao.getUserDetails(userIdLogged).imeiLimit;
//			if(userRole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			context.getExternalContext().getSessionMap().remove("userBean");
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId("0");
			userBean.setAction("view");
			userBean.setImeiLimit(imeiLimit);
			userBean.setImeiCount(imeiCount);
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "editToUserPage";
	}
	
	/**
          Navigation from the Same Edit user page.
    */  
	public String edituserDetails(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			int imeiCount = userDao.getIMEICountValue(userIdLogged);
			String imeiLimit = userDao.getUserDetails(userIdLogged).imeiLimit;
//			if(userRole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			context.getExternalContext().getSessionMap().remove("userBean");
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId("0");
			userBean.setAction("list");
			userBean.setImeiLimit(imeiLimit);
			userBean.setImeiCount(imeiCount);
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "edituserDetailsPage";
	}
	/**
         Navigation from the Welcome user to the Edit User page.
     */
	public String welcomeEditUser(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			countryMap=new MarketPlaceDaoImpl().getCountryMap();
			if(userRole.equals("superadmin")){			
				Set<String> userNameList =  new UserDaoImpl().getUsersOfSuperior(userIdLogged,userRole);
				System.out.println("userNameList size "+userNameList.size());
				userList = userDao.listUser(userNameList.toString().substring(1, userNameList.toString().length()-1),userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
				marketPlaceMap=new MarketPlaceDaoImpl().getMarketPlaceMap();
			}else{
				Set<String> userNameList =  new UserDaoImpl().getUsersOfSuperior(userIdLogged,userRole);
				System.out.println("userNameList size "+userNameList.size());
				userList = userDao.listUser(userNameList.toString().substring(1, userNameList.toString().length()-1),userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
				marketPlaceMap=new MarketPlaceDaoImpl().getMarketPlaceMap();
			}
			context.getExternalContext().getSessionMap().remove("userBean");
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			System.out.println("userDeviceInforesult size"+ userDeviceInforesult.size());
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId("0");
			userBean.setAction("list");
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().remove("marketPlaceMapList");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
			context.getExternalContext().getSessionMap().put("marketPlaceMapList", marketPlaceMap);
			context.getExternalContext().getSessionMap().remove("countryMapList");
			context.getExternalContext().getSessionMap().put("countryMapList", countryMap);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "welcomeEditUserPage";
	}
	
	
	public String operatorEditUser(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//			if(userRole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			context.getExternalContext().getSessionMap().remove("userBean");
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId("0");
			userBean.setAction("list");
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "operatorEditUserPage";
	}
	
	/**
         Navigation from the Edit user to the Welcome User page.
    */
	public String welcomeUserEdit(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("userBean");
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "welcomeUserEditPage";
	}
	/**
         Implementation of Clear Funtionality in the Create user page.
    */
	public String clearUserDetails(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userListdetails");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//			if(userRole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			context.getExternalContext().getSessionMap().remove("userBean");
			UserBean userBean =  new  UserBean();
			UserDeviceInfo userDevice = new UserDeviceInfo();
			this.userDeviceInforesult.add(userDevice);
			userBean.setUserDeviceInfo(userDeviceInforesult);
			userBean.setUserName("");
			userBean.setPassword("");
			userBean.setEmail("");
			userBean.setRole("1");
			userBean.setUserStatus(1);
			userBean.setOperatorId("0");
			userBean.setAction("view");
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "clearUserDetailsPage";
	}
	/**
         Navigation from the map to the Create User page.
    */
	public String mapCreatUser(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userBean");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//			if(userRole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			if(this.userDeviceInfo.size()==0){
				UserDeviceInfo userDevice = new UserDeviceInfo();
				this.userDeviceInfo.add(userDevice);
			}
			UserBean userBean=new UserBean ();
		    userBean.setUserDeviceInfo(userDeviceInfo);
		    context.getExternalContext().getSessionMap().remove("userBean");
		    context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return "mapCreatUserPage";
	}
	
	public String createUserOperator(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		UserDao userDao = new UserDaoImpl();
		operatorList=userDao.getOperatorList();
		context.getExternalContext().getSessionMap().remove("operatorListSize");
		context.getExternalContext().getSessionMap().remove("operatorListdetails");
		context.getExternalContext().getSessionMap().put("operatorListSize", operatorList.size());
		context.getExternalContext().getSessionMap().put("operatorListdetails", operatorList);
		context.getExternalContext().getSessionMap().remove("statusMap");
		context.getExternalContext().getSessionMap().put("statusMap", statusMap);
		return "createUserOperatorPage";
	}
	
	public String editUserOperator(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		UserDao userDao = new UserDaoImpl();
		operatorList=userDao.getOperatorList();
		context.getExternalContext().getSessionMap().remove("operatorListSize");
		context.getExternalContext().getSessionMap().remove("operatorListdetails");
		context.getExternalContext().getSessionMap().put("operatorListSize", operatorList.size());
		context.getExternalContext().getSessionMap().put("operatorListdetails", operatorList);
		context.getExternalContext().getSessionMap().remove("statusMap");
		context.getExternalContext().getSessionMap().put("statusMap", statusMap);
		return "editUserOperatorPage";
	}
	public String operatorWelcome(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		UserDao userDao = new UserDaoImpl();
		operatorList=userDao.getOperatorList();
		context.getExternalContext().getSessionMap().remove("operatorListSize");
		context.getExternalContext().getSessionMap().remove("operatorListdetails");
		context.getExternalContext().getSessionMap().put("operatorListSize", operatorList.size());
		context.getExternalContext().getSessionMap().put("operatorListdetails", operatorList);
		context.getExternalContext().getSessionMap().remove("statusMap");
		context.getExternalContext().getSessionMap().put("statusMap", statusMap);
		return "operatorWelcomePage";
	}
	public String createOperator(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		UserDao userDao = new UserDaoImpl();
		operatorList=userDao.getOperatorList();
		context.getExternalContext().getSessionMap().remove("operatorListSize");
		context.getExternalContext().getSessionMap().remove("operatorListdetails");
		context.getExternalContext().getSessionMap().put("operatorListSize", operatorList.size());
		context.getExternalContext().getSessionMap().put("operatorListdetails", operatorList);
		context.getExternalContext().getSessionMap().remove("statusMap");
		context.getExternalContext().getSessionMap().put("statusMap", statusMap);
		return "createOperatorPage";
	}
	
	public String clearOperatorDetails(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		UserDao userDao = new UserDaoImpl();
		operatorList=userDao.getOperatorList();
		context.getExternalContext().getSessionMap().remove("operatorListSize");
		context.getExternalContext().getSessionMap().remove("operatorListdetails");
		context.getExternalContext().getSessionMap().put("operatorListSize", operatorList.size());
		context.getExternalContext().getSessionMap().put("operatorListdetails", operatorList);
		context.getExternalContext().getSessionMap().remove("userBean");
		context.getExternalContext().getSessionMap().remove("statusMap");
		context.getExternalContext().getSessionMap().put("statusMap", statusMap);
		return "clearOperatorDetailsPage";
	}
	
	public String addOperator(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		boolean status=false;
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		UserDao userDao = new UserDaoImpl();
		status=userDao.checkOperatorExists(operatorName);
		if(status== true){
			context.getExternalContext().getRequestMap().put("failuremessage", "Operator already exists");
			return "addOperatorFailed";
		}
		String operStatus = userDao.addOperator(userId,operatorName,operatorStatus);
		if (operStatus.equalsIgnoreCase("success")) {
			context.getExternalContext().getRequestMap().put("message", "New Operator created.");
		} else {
			context.getExternalContext().getRequestMap().put("failuremessage", "Operator Creation failed");
		}
		operatorList=userDao.getOperatorList();
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		context.getExternalContext().getSessionMap().remove("statusMap");
		context.getExternalContext().getSessionMap().put("statusMap", statusMap);
		context.getExternalContext().getSessionMap().remove("userBean");
		context.getExternalContext().getSessionMap().remove("operatorListSize");
		context.getExternalContext().getSessionMap().remove("operatorListdetails");
		context.getExternalContext().getSessionMap().put("operatorListSize", operatorList.size());
		context.getExternalContext().getSessionMap().put("operatorListdetails", operatorList);
		return "addOperatorSuccess";
	}
	

	/**
         Navigation from the Edit User to the Create User page.
    */
	public String edituserPageDetails(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("userBean");
			UserDao userDao = new UserDaoImpl();
			String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//			if(userRole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userRole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userRole);
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userRole);
			}*/
			UserBean userBean=new UserBean();
			if(this.userDeviceInfo.size() >=1){
				UserDeviceInfo userDevice = new UserDeviceInfo();
				userDevice.setDeviceType("");
				userDevice.setImei("");
				userDevice.setDeviceId("");
				this.userDeviceInforesult.add(userDevice);
			}
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
		    userBean.setUserDeviceInfo(userDeviceInforesult);
		    userBean.setAction("list");
		    
		    context.getExternalContext().getSessionMap().remove("userBean");
		    context.getExternalContext().getSessionMap().put("userBean", userBean);
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap", statusMap);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().remove("roleMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "edituserDetailsPage";
	}
	
	/**
        Implementation of the Edit Functionality for the Created Users.
    */
    public String editCreateUser(){
    	FacesContext context = FacesContext.getCurrentInstance();
		UserDao userDao = new UserDaoImpl();
		UserBean userBeans=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		MarketPlaceDao marketPlaceDao= new  MarketPlaceDaoImpl();
		String[] marketId = new String[marketIdList.size()];
		marketId = marketIdList.toArray(marketId);
		int reportingTo;
		int adminImeiCount = 0,imeilimit = 0;
		//MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
		String userId =  context.getExternalContext().getRequestParameterMap().get("userIdEdit").toString();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		reportingTo = userDao.getUserDetails(userId).superioruser;
		role = userDao.getUserDetails(userId).role;
		System.out.println("userDeviceInfo.size() "+userDeviceInfo.size());
		if(userDeviceInfo.size() == 0){
				context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
				return "edit";//userCreatedFailed
			}/*else if(superioruser==0){
				context.getExternalContext().getRequestMap().put("failuremessage", "Please Select the supervisor");
				return "userCreatedFailed";
			}*/
			else{
				for(int i =0;i< userDeviceInfo.size();i++){
					UserDeviceInfo userDevice = userDeviceInfo.get(i);
					if(userDevice.getDeviceType().equals("") && userDevice.getMobileNumber().equals("") && userDevice.getImei().equals("")){
						this.userDeviceInfo.remove(i);
						i--;
					}else{
						if(userDevice.getDeviceType().equals("") || userDevice.getMobileNumber().equals("") ||userDevice.getImei().equals("")){
							context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
							return "edit";//userCreatedFailed
						}else{
							if( userDevice.getMobileNumber().trim().length()>17){
								context.getExternalContext().getRequestMap().put("failuremessage", "Mobile Number Should be equals to 16 characters");
								return "edit";//userCreatedFailed
							}
							if(!TestCaseValidator.isInteger(userDevice.getImei()) || userDevice.getImei().trim().length()>15){
								context.getExternalContext().getRequestMap().put("failuremessage", "IMEI Should be Numberic and not more than 15 characters");
								return "edit";//userCreatedFailed
							}
						}
					}
				}
			}
				// by ankit 
				int imeiCount = 0;
				String userid = null; 
				if(role.equals("1"))
				{
				userid = userBeans.getUserId();
				}
				else if(role.equals("2") )
				{
				userid = String.valueOf(userBeans.getSuperioruser());
				}
				else if(role.equals("3") || role.equals("6"))
				{
				userid = String.valueOf(userDao.getUserDetails(String.valueOf(userBeans.getSuperioruser())).superioruser);
				}
				adminImeiCount = userDao.getIMEICountValue(userid);
				/*userListOfUser = userDao.getUsersOfSuperior(userid, Roles.ADMIN);
			    userList = userDao.listUser(userListOfUser.toString().substring(1, userListOfUser.toString().length()-1),role);//
				for(int i=0;i<userList.size();i++)
			    {	
					
			    	//System.out.println("no of devices by userid"+userList.get(i).getIMEI_USER()+"---"+userList.get(i).getUserId());
			    }*/
				if(adminImeiCount>0){
				imeiCount = adminImeiCount-(userDao.getImeiBelongsToUser(userId).size()-1);
				}else{
				imeiCount = (userDao.getImeiBelongsToUser(userId).size())-1;
				}
				System.out.println("no of devices imeiCount "+imeiCount+" adminImeiCount "+adminImeiCount+ " ,,,,"+userId+"..."+userDao.getImeiBelongsToUser(userId).size());
			    userBeans.setUserDeviceInfo(userDeviceInfo);
			    imeiCount = userDeviceInfo.size()+imeiCount;
			    imeilimit = Integer.parseInt(userDao.getUserDetails(userid).getImeiLimit());
			   
			    if(imeilimit<imeiCount)
			    {
			    	 //userBeans.setAction("edit");
					 context.getExternalContext().getRequestMap().put("LimitReachedmsg", "LimitReached");
					 return "edit";//userCreatedFailed 
				}
			   
			    if(userrole.equalsIgnoreCase(Roles.SUPERUSER))//&& role.equals("1")
			    {
			    	if(imeiCount>Integer.parseInt(imeiLimit))
					{
						//userBeans.setAction("edit");
						 //context.getExternalContext().getSessionMap().put("userBeans", userBeans);
						context.getExternalContext().getRequestMap().put("resetlimitmsg", "resetlimitmsg");
						return "edit";//userCreatedFailed
					}
			     }
		    
			userDao.editUser(userId,email,role,reportingTo,userStatus,operatorId,userDeviceInfo,password,marketId,countryId,String.valueOf(imeiLimit),imeiCount);
			System.out.println("imeiCount - "+imeiCount);
		    String a = userDao.updateIMEICount(userid,imeiCount);
		    System.out.println("imeiCount update query - "+a);
		    List<Integer> marketIdList= marketPlaceDao.getMarketIDListForUser(userId);
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			//if(userrole.equals("superadmin")){			
			Set<String> userNameList =  new UserDaoImpl().getUsersOfSuperior(userIdLogged,userrole);
			userList = userDao.listUser(userNameList.toString().substring(1, userNameList.toString().length()-1),userrole);				
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userrole);
				marketPlaceMap=new MarketPlaceDaoImpl().getMarketPlaceMap();
				countryMap= new MarketPlaceDaoImpl().getCountryMap();
				
			/*}else{
				userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged), userIdLogged);
				roleMap = userDao.getActiveEditRoleInMap(userrole);
				marketPlaceMap=new MarketPlaceDaoImpl().getMarketPlaceMap();
				countryMap= new MarketPlaceDaoImpl().getCountryMap();
				if(userIdLogged.equals(userId) && userStatus == 0 ){
					FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
					return "userEditLogout";
				}
			}	*/
			String username=userDao.getUserName(userId);
			
			if(this.userDeviceInfo.size()==0){
				UserDeviceInfo userDevice = new UserDeviceInfo();
				this.userDeviceInfo.add(userDevice);
				UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
			    userBean.setUserDeviceInfo(userDeviceInfo);
			    userBean.setAction("edit");
			    userBean.setImeiLimit(String.valueOf(imeilimit));
				userBean.setImeiCount(imeiCount);
			    context.getExternalContext().getSessionMap().remove("userBean");
			    context.getExternalContext().getSessionMap().put("userBean", userBean);
			}else{
				UserBean userBean =  userDao.getUserDetails(userId);
			    userBean.setAction("edit");
			    
			    userBean.setImeiLimit(String.valueOf(imeilimit));
				userBean.setImeiCount(imeiCount);
			    userBean.setMarketIdList(marketIdList);
			    context.getExternalContext().getSessionMap().remove("userBean");
			    context.getExternalContext().getSessionMap().put("userBean", userBean);
			}
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			fetchSuperiorList();
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap",statusMap);
			context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().remove("marketPlaceMapList");
			context.getExternalContext().getSessionMap().put("userDeviceSize", userDeviceInfo.size());
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
			context.getExternalContext().getRequestMap().put("message", "User:" +username+ " Updated Successfully");
			context.getExternalContext().getSessionMap().put("marketPlaceMapList", marketPlaceMap);
			context.getExternalContext().getSessionMap().remove("countryMapList");
			context.getExternalContext().getSessionMap().put("countryMapList", countryMap);
			return "edit"; //editCreateUserPage	
    }
     /**
    Implementation of the Create User Funtionliaty.
 */
	public String createUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		UserDao userDao = new UserDaoImpl();
		//String userId =  context.getExternalContext().getRequestParameterMap().get("userIdEdit").toString();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		UserBean userBean = new UserBean();
		int ImeiCount = 0;
		String Adminuserid = null; 
		userBean.setUserName(userName);
		userBean.setPassword(password);
		userBean.setEmail(email);
		userBean.setRole(role);
		userBean.setAction("list");
		userBean.setMarketId(marketId);
		userBean.setCountryId(countryId);
		
		if(userrole.equals("superadmin")){
			userBean.setImeiLimit(imeiLimit);
			userBean.setOperatorId(operatorId);
		}else{
			
			userBean.setImeiLimit("0");
			userBean.setOperatorId(context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString());
		}
		
	  /*  if(!userrole.equals(Roles.SUPERUSER))
	    {
	    	System.out.println("In if condition is - notSuperUser");
		if(userDeviceInfo.size() == 0){
				context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
				return "userCreatedFailed";
			}else if(!role.equalsIgnoreCase("1")&&superioruser==0){
				context.getExternalContext().getRequestMap().put("failuremessage", "Please Select the supervisor");
				return "userCreatedFailed";
			}
		else{*/
			
	    	if(userDeviceInfo.size() > 0){
	    		for(int i =0;i< userDeviceInfo.size();i++){
	    	
				UserDeviceInfo userDevice = userDeviceInfo.get(i);
				/*if(i==0){
					if(userDevice.getDeviceType().equals("") && userDevice.getMobileNumber().equals("") && userDevice.getImei().equals("")){
						context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
						return "userCreatedFailed";
					}else{
						if(userDevice.getDeviceType().equals("") || userDevice.getMobileNumber().equals("") ||userDevice.getImei().equals("")){
							context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
							return "userCreatedFailed";
						}else{
							if(userDevice.getMobileNumber().trim().length()>17){
								context.getExternalContext().getRequestMap().put("failuremessage", "Mobile Number Should be  equals to 16 characters");
								return "userCreatedFailed";
							}
							if(!TestCaseValidator.isInteger(userDevice.getImei()) || userDevice.getImei().trim().length()>15){
								context.getExternalContext().getRequestMap().put("failuremessage", "IMEI Should be Numberic and not more than 15 characters");
								return "userCreatedFailed";
							}
						}
				   }
				}else{*/
					if(userDevice.getDeviceType().equals("") && userDevice.getMobileNumber().equals("") && userDevice.getImei().equals(""))
					{
						this.userDeviceInfo.remove(i);
						i--;
					}else{
						if(userDevice.getDeviceType().equals("") || userDevice.getMobileNumber().equals("") ||userDevice.getImei().equals("")){
							context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
							return "userCreatedFailed";
						}else{
							if( userDevice.getMobileNumber().trim().length()>17){
								context.getExternalContext().getRequestMap().put("failuremessage", "Mobile Number Should be equals to 16 characters");
								return "userCreatedFailed";
							}
							if(!TestCaseValidator.isInteger(userDevice.getImei()) || userDevice.getImei().trim().length()>15){
								context.getExternalContext().getRequestMap().put("failuremessage", "IMEI Should be Numberic and not more than 15 characters");
								return "userCreatedFailed";
							}
						}
				   }	
		   }
		}
		if(!role.equals("1"))
	    {
	    	
			if(role.equals("2"))
			{
			Adminuserid = String.valueOf(superioruser);
			}
			else if(role.equals("3") || role.equals("6"))
			{
			Adminuserid = String.valueOf(userDao.getSuperiorUserIdbyuserid(String.valueOf(superioruser)));
			}
			else if(role.equals("4"))
			{
			String Manageruserid = String.valueOf(userDao.getSuperiorUserIdbyuserid(String.valueOf(superioruser)));
			Adminuserid = String.valueOf(userDao.getSuperiorUserIdbyuserid(Manageruserid));
			}
			//int adminImeiCount = userDao.getIMEICountValue(Adminuserid);
			System.out.println("Adminuserid- "+Adminuserid);
			//System.out.println("ROLE :"+userBean.getRole());
			//userRole = udi.getUserDetails(userIdLogged).role;
			//ImeiCount = userDao.getIMEICountValue(String.valueOf(userIdLogged));
			ImeiCount = userDao.getIMEICountValue(Adminuserid);
			userBean.setUserDeviceInfo(userDeviceInfo);
		    userBean.setAction("list");
		    ImeiCount = userDeviceInfo.size()+ImeiCount;
		    System.out.println("ImeiCount : "+ImeiCount);
		    if(Integer.parseInt(userDao.getUserDetails(Adminuserid).getImeiLimit())<ImeiCount)
		   		{
				context.getExternalContext().getRequestMap().put("LimitReachedmsg", "LimitReached");
				return "userCreatedFailed";
			 	}
	    }
		//}
		userBean.setUserStatus(userStatus);
		userBean.setUserDeviceInfo(userDeviceInfo);			
		userBean.setUserMessage(userMessage);
		userBean.setEmailMessage(emailMessage);
		//userBean.setUserId(userId);
		userBean.setSuperioruser(superioruser);
		//userBean.setImeiCount(ImeiCount);
		System.out.println("superioruser--------------"+superioruser);
		String status = userDao.createUser(userBean);
		
		String[] statusArray = status.split("&&&");
		userBean.setUserMessage(statusArray[1]);
		//userBean.setEmailMessage(statusArray[2]);
		status = statusArray[0];	
		
//		if(userrole.equals("superadmin")){			
			userList = userDao.listUser(userIdLogged,userrole);
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			roleMap = userDao.getActiveRoleInMap(userrole);
		/*}else{
			userList = userDao.listUserBasedOnOperator(userBean.getOperatorId(),userIdLogged);
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			roleMap = userDao.getActiveRoleInMap(userrole);
		}*/	
		if(!status.equals("failure")){
			//if(userrole.equals(Roles.ADMIN)){
				//update imei counter if devices added for that user.
			if(!role.equals("1"))
		    {
				userDao.updateIMEICount(Adminuserid, ImeiCount);
		    }
			//}
			context.getExternalContext().getSessionMap().remove("userBean");
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
			context.getExternalContext().getRequestMap().put("message", "User:" +userName+ " Created Successfully");
			userDeviceInfo.clear();
			if(this.userDeviceInfo.size()==0){
				UserDeviceInfo userDevice = new UserDeviceInfo();
				this.userDeviceInfo.add(userDevice);
				UserBean userBean2=new UserBean();
				userBean2.setUserDeviceInfo(userDeviceInfo);
			    context.getExternalContext().getSessionMap().put("userBean", userBean2);
			}
			return "userCreatedSuccess";
		}else{
			if(null != userBean.getUserMessage()){
				context.getExternalContext().getRequestMap().put("failuremessage", userBean.getUserMessage());
			}else{
				context.getExternalContext().getRequestMap().put("failuremessage", "User Created Failed");
			}
			return "userCreatedFailed";
		}	
	}
 

	
	public String welcomeOpearatorUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "welcomeOpearatorUserPage";
	}
	
	
	  /**
    Implementation of the Same Welcome Page.
    */
	public String welcomeUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "welcomePage";
	}
	  /**
           Navigation from the map to the Welcome User  page
    */
	public String welcomeMaptoUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		ReportBean reportBean = (ReportBean)context.getExternalContext().getSessionMap().get("reportBean");
		reportBean.setTestCaseName("");
		reportBean.setMapReportType("signalStrength");
		context.getExternalContext().getSessionMap().put("reportBean",reportBean);
		return "welcomeMaptoUserPage";
	}
	
	  /**
         Navigation from the Create User  to the Welcome User  page
    */
	public String welcomeCreateUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("userBean");
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "welcomeCreateUserPage";
	}
	 /**
         Navigation from the Create Configuration to Welcome page
    */
	public String createConfigToManagement(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "createConfigToManagementPage";
	}
	/**
        Navigation from the Assign Configuration to Welcome page
    */
	public String assignConfigToManagement(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "assignConfigToManagementPage";
	}
	/**
         Navigation from the Edit Configuration to Welcome page
    */
	public String editConfigToManagement(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "editConfigToManagementPage";
	}
	/**
       Navigation from the Upload  to Welcome page
    */
	public String uploadConfigToManagement(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "uploadConfigToManagementPage";
	}
	/**
        Implementation of the logout Functionality 
    */
	public String logoutUser(){
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "valid";
	}
	/**
       Implementation of the Session timed out Functionality 
    */
	public String logoutSessionUser(){
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "sessionValid";
	}
	/**
       Implementation of the edit and the Delete user Functionality
    */
	public String goToeditUser(){
		//System.out.println("=============================23================");
		FacesContext context = FacesContext.getCurrentInstance();
		String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		UserDao userDao = new UserDaoImpl();
		MarketPlaceDao marketPlaceDao= new  MarketPlaceDaoImpl();
		if(userRole.equals("superadmin")){			
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			roleMap = userDao.getActiveRoleInMap(userRole);
		}else{
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			roleMap = userDao.getActiveEditRoleInMap(userRole);
		}
		String editUserId =  context.getExternalContext().getRequestParameterMap().get("editUserId").toString();
		UserBean userBean =  userDao.getUserDetails(editUserId);
		String userId=userBean.getUserId();
		List<Integer> marketIdList= marketPlaceDao.getMarketIDListForUser(userId);
//		userBean=user
		String cntryId= userBean.getCountryId();
		//System.out.println("marketIdList==="+marketIdList);
		getMarketForCountry(cntryId);
		if(userBean.getUserDeviceInfo().size()==0){
			UserDeviceInfo userDevice = new UserDeviceInfo();
			userBean.getUserDeviceInfo().add(userDevice);
		}
		String userid = null;
		String role = userBean.role;
		if(role.equals("1"))
		{
		userid = userBean.getUserId();
		}
		else if(role.equals("2"))
		{
		userid = String.valueOf(userBean.getSuperioruser());
		}
		else if(role.equals("3") || role.equals("6"))
		{
		userid = String.valueOf(userDao.getUserDetails(String.valueOf(userBean.getSuperioruser())).superioruser);
		}
		int imeiCount = userDao.getIMEICountValue(userid);
		String imeiLimit = userDao.getUserDetails(userid).imeiLimit;
		userBean.setImeiLimit(imeiLimit);
		userBean.setImeiCount(imeiCount);
		System.out.println("edit user page userRole "+userRole + "userid" +userid+" count " +imeiCount+" getSuperioruser "+ userBean.getSuperioruser());
		Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(String.valueOf(userBean.getSuperioruser()),userBean.getRole());
		System.out.println("userNameList"+userNameList.toString());
		context.getExternalContext().getSessionMap().put("superiorRoleUsers",new UserDaoImpl().getSuperiorRoleUsersMap(userRole,role,userBean.operatorId,userNameList.toString().substring(1, userNameList.toString().length()-1)));
		  
		userBean.setUserId(editUserId);
		userBean.setAction("edit");
		userBean.setMarketIdList(marketIdList);
		context.getExternalContext().getSessionMap().remove("marketIdList");
		context.getExternalContext().getSessionMap().remove("operatorMaplist");
		context.getExternalContext().getSessionMap().remove("roleMaplist");
		context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
		context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
		context.getExternalContext().getSessionMap().put("userBean", userBean);
		context.getExternalContext().getSessionMap().put("userDeviceSize", userBean.getUserDeviceInfo().size());
		context.getExternalContext().getSessionMap().put("marketIdList", marketIdList);
		return "success";
	}
	/**
          Implementation of the Pop up alert Message before deleting the user 
    */
	public String goToDeleteUserStatus(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getRequestMap().put("removedStatus", "true");
		context.getExternalContext().getRequestMap().put("popUpmessage", "Are you sure want to delete the user?");	
		String editUserId =  context.getExternalContext().getRequestParameterMap().get("editUserId").toString();
		context.getExternalContext().getSessionMap().remove("editUserId");
		context.getExternalContext().getSessionMap().put("editUserId",editUserId);
		UserBean userBean =  new UserBean();
		userBean.setAction("list");
		UserDeviceInfo userDevice = new UserDeviceInfo();
		this.userDeviceInforesult.add(userDevice);
		userBean.setUserDeviceInfo(userDeviceInforesult);
		context.getExternalContext().getSessionMap().remove("userBean");
		context.getExternalContext().getSessionMap().put("userBean", userBean);
		return "deletePopPage";
	}
	
	/**
        Implementation of the Delete user Functionality
    */
	public String goToDeleteUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("userListdetails");
		String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		UserDao userDao = new UserDaoImpl();
		boolean deleteStatus=false;
		String editUserId =  context.getExternalContext().getSessionMap().get("editUserId").toString();
		String userName=userDao.getUserName(editUserId);
		UserBean userBean =  new UserBean();
		deleteStatus=userDao.deleteUser(editUserId);
		if(userBean.getUserDeviceInfo().size()==0){
			UserDeviceInfo userDevice = new UserDeviceInfo();
			userBean.getUserDeviceInfo().add(userDevice);
		}
		userBean.setUserId(editUserId);
		userBean.setAction("list");
		if(deleteStatus == true){
			context.getExternalContext().getRequestMap().put("failuremessage", "User: "+userName+ " Deleted Successfully");
		}else{
			context.getExternalContext().getRequestMap().put("failuremessage", "User Deleted UnSuccessFully");
		}
		String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//		if(userRole.equals("superadmin")){			
			userList = userDao.listUser(userIdLogged,userRole);
			roleMap = userDao.getActiveRoleInMap(userRole);
		/*}else{
			roleMap = userDao.getActiveRoleInMap(userRole);
			userList =userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged),userIdLogged);
		}*/
		context.getExternalContext().getSessionMap().put("userBean", userBean);
		context.getExternalContext().getSessionMap().remove("editUserId");
		context.getExternalContext().getSessionMap().put("userListdetails", userList);
		return "deleteSuccess";
	}
	/**
        Navigation from the Header page to the welcome page
    */
	public String welcomePage(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("userBean");
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "welcomePage";
	}
	/**
        Display the Current User Logged Details 
    */
	public String loginuserPage(){
		enableLoginUserDetails = true;
		MarketPlaceDao marketPlaceDao=new MarketPlaceDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		UserDao userDao = new UserDaoImpl();
		String userid = null; 
		
		if(userRole.equals("superadmin")){			
			roleMap = userDao.getActiveRoleInMap(userRole);
			countryMap= new MarketPlaceDaoImpl().getCountryMap();
		}else{
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			roleMap = userDao.getActiveRoleInMap(userRole);
			countryMap= new MarketPlaceDaoImpl().getCountryMap();
		}
		Map<String, String> statusMap = new LinkedHashMap<String, String>();
		statusMap.put("Active", "1");
		statusMap.put("Inactive", "0");
		String editUserId =  context.getExternalContext().getRequestParameterMap().get("editUserId").toString();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		UserBean userBean =  userDao.getLoginUserDetails(editUserId,userrole);
		String userId=userBean.getUserId();
		System.out.println("userRole==="+userRole);
		if(userRole.equals(Roles.ADMIN))
		{
		userid = userId;
		}
		else if(userRole.equals(Roles.MANAGER))
		{
		userid = String.valueOf(userDao.getUserDetails(userId).superioruser);
		}
		else if(userRole.equals(Roles.ENGINEER))
		{
		userid = String.valueOf(userDao.getUserDetails(String.valueOf(userDao.getUserDetails(userId).superioruser)).superioruser);
		}System.out.println("userid==="+userid);
		int adminCount = userDao.getIMEICountValue(userid);
		String ImeiLimit = userDao.getUserDetails(userid).getImeiLimit();
		List<Integer> marketIdList= marketPlaceDao.getMarketIDListForUser(userId);
		String cntryId= userBean.getCountryId();
		//System.out.println("marketIdList==="+marketIdList);
		getMarketForCountry(cntryId);
		if(userBean.getUserDeviceInfo().size()==0){
			UserDeviceInfo userDevice = new UserDeviceInfo();
			userBean.getUserDeviceInfo().add(userDevice);
		}
		userBean.setEnableLoginUserDetails(enableLoginUserDetails);
		userBean.setUserId(editUserId);
		userBean.setAction("edit");
		userBean.setImeiLimit(ImeiLimit);
		userBean.setImeiCount(adminCount);
		userBean.setMarketIdList(marketIdList);
		context.getExternalContext().getSessionMap().put("statusMap",statusMap);
		context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
		context.getExternalContext().getSessionMap().put("roleMaplist", roleMap);
		context.getExternalContext().getSessionMap().put("countryMapList", countryMap);
		context.getExternalContext().getSessionMap().put("userBean", userBean);
		context.getExternalContext().getSessionMap().put("userDeviceSize", userBean.getUserDeviceInfo().size());
		context.getExternalContext().getSessionMap().put("marketIdList", marketIdList);
		return "loginuserPagesuccess";		
	}	
	/**
         Edit the Current User Logged Details 
    */
    public String editLoginUser(){
    	//System.out.println("Inside the funtion-=-----------------------");
    	FacesContext context = FacesContext.getCurrentInstance();
    	UserBean userBeans=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		UserDao userDao = new UserDaoImpl();
		MarketPlaceDao marketPlaceDao= new MarketPlaceDaoImpl();
		String[] marketId = new String[marketIdList.size()];
		System.out.println("marketIdList "+marketIdList.size());
		marketId = marketIdList.toArray(marketId);
		String userId =  context.getExternalContext().getRequestParameterMap().get("userIdEdit").toString();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			if(userDeviceInfo.size() == 0){
				context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
				return "loginUserEditSuccess";//userCreatedFailed
			}else{
				for(int i =0;i< userDeviceInfo.size();i++){
					UserDeviceInfo userDevice = userDeviceInfo.get(i);
					if(userDevice.getDeviceType().equals("") && userDevice.getMobileNumber().equals("") && userDevice.getImei().equals("")){
						this.userDeviceInfo.remove(i);
						i--;
					}else{
					/*if(userDevice.getDeviceType().equals("") || userDevice.getMobileNumber().equals("") ||userDevice.getImei().equals("")){
						context.getExternalContext().getRequestMap().put("failuremessage", "Please Enter Device Type,IMEI and Mobile Number");
						return "loginUserEditSuccess";
					}else{*/
						if( userDevice.getMobileNumber().trim().length()>17){
							context.getExternalContext().getRequestMap().put("failuremessage", "Mobile Number Should be equals to 16 characters");
							return "loginUserEditSuccess";
						}
						if(!TestCaseValidator.isInteger(userDevice.getImei()) || userDevice.getImei().trim().length()>15){
							context.getExternalContext().getRequestMap().put("failuremessage", "IMEI Should be Numberic and not more than 15 characters");
							return "loginUserEditSuccess";
						}
					}
				}
			}
			
			// by ankit 
			int imeiCount = 0,adminImeiCount = 0,imeilimit = 0;
			String userid = null; 
			System.out.println("getSuperioruser "+userBeans.getSuperioruser());
			if(userrole.equals(Roles.ADMIN))
			{
			userid = userId;
			}
			else if(userrole.equals(Roles.MANAGER))
			{
			userid = String.valueOf(userDao.getUserDetails(userId).superioruser);
			}
			else if(userrole.equals(Roles.ENGINEER))
			{
			userid = String.valueOf(userDao.getUserDetails(String.valueOf(userDao.getUserDetails(userId).superioruser)).superioruser);
			}
			adminImeiCount = userDao.getIMEICountValue(userid);
			imeiCount = adminImeiCount-(userDao.getImeiBelongsToUser(userId).size()-1);
			System.out.println("no of devices userid "+userid +" imeiCount "+imeiCount+" adminImeiCount "+adminImeiCount+ " ,,,,"+userId+"..."+userDao.getImeiBelongsToUser(userId).size());
		    userBeans.setUserDeviceInfo(userDeviceInfo);
		    imeiCount = userDeviceInfo.size()+imeiCount;
		    imeilimit = Integer.parseInt(userDao.getUserDetails(userid).getImeiLimit());
		   
		    if(imeilimit<imeiCount)
		    {
		    	 context.getExternalContext().getRequestMap().put("LimitReachedmsg", "LimitReached");
				 return "loginUserEditSuccess"; 
			}
		   
		    System.out.println("imeiCount - "+imeiCount);
		    userDao.profileEditUser(userId,email,role,userStatus,operatorId,userDeviceInfo,password,userrole,marketId,countryId);
			String a = userDao.updateIMEICount(userid,imeiCount);
		    System.out.println("imeiCount update query - "+a);
			userBeans.setImeiLimit(String.valueOf(imeilimit));
			userBeans.setImeiCount(imeiCount);
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
//			if(userrole.equals("superadmin")){			
				userList = userDao.listUser(userIdLogged,userrole);
				operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
				roleMap = userDao.getActiveRoleInMap(userrole);
			/*}else{
				userList = userDao.listUserBasedOnOperator(new UserDaoImpl().getOperator(userIdLogged),userIdLogged);
				roleMap = userDao.getActiveRoleInMap(userrole);
				if(userIdLogged.equals(userId) && userStatus == 0 ){
					FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
					return "userEditLogout";
				}
			}*/	
			//userDeviceInfo.clear();
			if(this.userDeviceInfo.size()==0){
				UserDeviceInfo userDevice = new UserDeviceInfo();
				this.userDeviceInfo.add(userDevice);
				UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
			    userBean.setUserDeviceInfo(userDeviceInfo);
			    userBean.setAction("edit");
			    context.getExternalContext().getSessionMap().put("userBean", userBean);
			}else{
				UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
			    userBean.setUserDeviceInfo(userDeviceInfo);
			    userBean.setAction("edit");
			    context.getExternalContext().getSessionMap().put("userBean", userBean);
			}
			Map<String, String> statusMap = new LinkedHashMap<String, String>();
			statusMap.put("Active", "1");
			statusMap.put("Inactive", "0");
			context.getExternalContext().getSessionMap().remove("statusMap");
			context.getExternalContext().getSessionMap().put("statusMap",statusMap);
			context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
			context.getExternalContext().getSessionMap().remove("userListSize");
			context.getExternalContext().getSessionMap().remove("userListdetails");
			context.getExternalContext().getSessionMap().put("userListSize", userList.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userList);
			context.getExternalContext().getRequestMap().put("message", "User Updated Successfully");
			return "loginUserEditSuccess"; 	
    }	
    
    /** Function to Add the User Device info in the Profile Screen Page
     * 
     */
    public String addLoginUserRowAction(){
		FacesContext context = FacesContext.getCurrentInstance();
		UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
		UserDeviceInfo userDev = new UserDeviceInfo();
	    this.userDeviceInfo.add(userDev);
	    userBean.setAction("edit");
	    userBean.setUserDeviceInfo(userDeviceInfo);
	    context.getExternalContext().getSessionMap().put("userBean", userBean);
	    return "addRow";
	}
    
    /** Function to remove  the User Device info from the list in the Profile Screen Page
     * 
     */
    
    public String loginRemoveRowAction(){
    	String  removedStatus = "false";
    	FacesContext context = FacesContext.getCurrentInstance();
    		for(int i=0; i<userDeviceInfo.size();i++){
    			if(userDeviceInfo.size()>1){
    				if(userDeviceInfo.get(i).isSelected()){
    					removedStatus = removeDeviceRowFromDb(userDeviceInfo.get(i).getDeviceId());
    					this.userDeviceInfo.remove(i);
    					i--;
    				}
    			}else{
    				context.getExternalContext().getRequestMap().put("message", "Can't remove default value");
    			}
    		}
    		
    		UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
    	    userBean.setAction("edit");
    	    userBean.setUserDeviceInfo(userDeviceInfo);
    	    context.getExternalContext().getSessionMap().put("userBean", userBean);		
    		return "removeRow";
    	}
	public String welcomeMarketUser(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		return "welcomeMarketUserPage";
	}
	  public void ajaxListener(AjaxBehaviorEvent event) {
		  //System.out.println("ajaxListener===");
	        String result = "called by " + event.getComponent().getClass().getName();
	        Map<String, String> marketPlaceMapForCountry = new LinkedHashMap<String, String>();
	        
			FacesContext context = FacesContext.getCurrentInstance();
			String newValue =  (String) ((UIOutput)event.getSource()).getValue();
			MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
			marketPlaceMapForCountry = marketPlaceDao.getMarketPlaceMapForCountry(newValue);
			String otherMarket=marketPlaceDao.getOtherMarketId(countryId);
			if(otherMarket!=null){
				String otherMarketId[]=otherMarket.split(",");
				UserBean userBean=(UserBean) context.getExternalContext().getSessionMap().get("userBean");
				userBean.setMarketId(otherMarketId);
				context.getExternalContext().getSessionMap().put("userBean", userBean);
			}
			context.getExternalContext().getSessionMap().put("marketPlaceMapForCountry",marketPlaceMapForCountry);
	    }
	  public void getMarketForCountry(String countryId) {
		  //System.out.println("getMarketForCountry===");
	        //String result = "called by " + event.getComponent().getClass().getName();
	        Map<String, String> marketPlaceMapForCountry = new LinkedHashMap<String, String>();
			FacesContext context = FacesContext.getCurrentInstance();
			//String newValue =  (String) ((UIOutput)event.getSource()).getValue();
			MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
			marketPlaceMapForCountry = marketPlaceDao.getMarketPlaceMapForCountry(countryId);
			context.getExternalContext().getSessionMap().put("marketPlaceMapForCountry",marketPlaceMapForCountry);
	    }
	  
	  public void fetchSuperiorList(){
		  FacesContext context = FacesContext.getCurrentInstance();
		  Map<String,String> superiorUsersMap = new HashMap<String, String>();
		  //System.out.println("herrrrrrrrrrrrr"+role);
		  String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		  //System.out.println("operatorId----------"+operatorId);
		  String userId =(String) context.getExternalContext().getSessionMap().get("loggedInUserID");
		  //System.out.println("userId--++---"+userRole);
		  Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(userId,userRole);
		  context.getExternalContext().getSessionMap().put("superiorRoleUsers",new UserDaoImpl().getSuperiorRoleUsersMap(userRole,role,operatorId,userNameList.toString().substring(1, userNameList.toString().length()-1)));
		  
	  }
	  
	  public void GCMNotifications(){
		  
		  try {
				
				System.out.println("Testing 1 - Send Http GET request");
				sendGet();
				//System.out.println("\nTesting 2 - Send Http POST request");
				//sendPost();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		 /* String url = "http://www.google.com/search?q=httpClient";

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);

			// add request header
			request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response = client.execute(request);

			System.out.println("Response Code : " 
		                + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}*/
		  
		  
	    	 /*try {
              //Please add here your project API key: "Key for browser apps (with referers)".
              //If you added "API key Key for server apps (with IP locking)" or "Key for Android apps (with certificates)" here
              //then you may get error responses.
              Sender sender = new  Sender("AIzaSyC0eBPYpEU0lewirMoAEKDsU_9WJzlCD38");
              
              // use this to send message with payload data
              Message message = new Message.Builder()
              .collapseKey("price")
              .timeToLive(3)
              .delayWhileIdle(true)
              .addData("price", "START_TEST") //you can get this message on client side app
              .build();  

              //Use this code to send notification message to a single device
              Result result = sender.send(message,
                      "APA91bHoCksS5KSWXlHlufmHvfCNxSiWS_a6ZiCFtGnGQo9nFMf9kVtn84RnDvAYTzsJmprq84XGeOEJn6mwokMhr-bgSox6H7HQRxeAiXJY-UtczKHe0wE",
                      1);
              System.out.println("Message Result: "+result.toString()); //Print message result on console
               
              //Use this code to send notification message to multiple devices
              ArrayList<String> devicesList = new ArrayList<String>();
              //add your devices RegisterationID, one for each device                
              devicesList.add("APA91bHoCksS5KSWXlHlufmHvfCNxSiWS_a6ZiCFtGnGQo9nFMf9kVtn84RnDvAYTzsJmprq84XGeOEJn6mwokMhr-bgSox6H7HQRxeAiXJY-UtczKHe0wE");    
              //devicesList.add("APA91bEVcqKmPnESzgnGpEstHHymcpOwv52THv6u6u2Rl-PaMI4mU3Wkb9bZtuHp4NLs4snBl7aXXVkNn-IPEInGO2jEBnBI_oKEdrEoTo9BpY0i6a0QHeq8LDZd_XRzGRSv_R0rjzzZ1b6jXY60QqAI4P3PL79hMg");    

              //Use this code for multicast messages    
              MulticastResult multicastResult = sender.send(message, devicesList, 0);
              System.out.println("Message Result: "+multicastResult.toString());//Print multicast message result on console

          } catch (Exception e) {
              e.printStackTrace();
          }
    	*/
    }
	// HTTP GET request
		private void sendGet() throws Exception {

			String url = "http://ssism.org/TestAndroid/gcm_server_php/send_message.php?message=START_TEST&regId=APA91bHoCksS5KSWXlHlufmHvfCNxSiWS_a6ZiCFtGnGQo9nFMf9kVtn84RnDvAYTzsJmprq84XGeOEJn6mwokMhr-bgSox6H7HQRxeAiXJY-UtczKHe0wE";
			System.out.println("is it right?"+url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());

		}
		
		// HTTP POST request
		private void sendPost() throws Exception {

			String url = "https://selfsolve.apple.com/wcResults.do";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			System.out.println(response.toString());

		}
	  
	  
}
