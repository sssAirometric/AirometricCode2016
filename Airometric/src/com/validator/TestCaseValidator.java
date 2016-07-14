package com.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.validator.UrlValidator;

import com.dao.TestConfigDao;
import com.dao.impl.TestConfigDaoImpl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestCaseValidator implements Validator {
	public void validate(FacesContext context, UIComponent component,
      Object value) throws ValidatorException {

	  Pattern pattern;
	  Matcher matcher;
	  final String TESTNAME_PATTERN = "[A-Za-z0-9]+";
	  pattern = Pattern.compile(TESTNAME_PATTERN);
	  FacesMessage message = new FacesMessage();
	  
	  
	  if(component.getId().equals("testName")){
		  String lTestName = (String) value;
		  if(lTestName.length()== 0) {
			  message.setDetail("Please enter test name.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else{
			matcher = pattern.matcher(lTestName);
			boolean valid = matcher.matches();
			if(!valid){
				message.setDetail("Please enter alphaNumeric only.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}else{
				TestConfigDao testConfigDao = new TestConfigDaoImpl();
				if(testConfigDao.getTestNamesInMap().containsKey(lTestName)){
					message.setDetail("Tet Config with name: '"+lTestName+"' already exists");
					message.setSummary("Incorrect");
				    message.setSeverity(FacesMessage.SEVERITY_ERROR);
				    throw new ValidatorException(message);
				}
			}
		  }
	  }else if(component.getId().equals("phoneNumber")){
		  String lPhoneNumber = (String) value;
		  boolean isNum = isInteger(lPhoneNumber);
		  if(isNum){
		  	if(lPhoneNumber.length()== 0) {
		  		message.setDetail("Please enter phone number.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			  }else if(lPhoneNumber.length()!= 10){
				message.setDetail("Please enter 10 digits.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("callDuration")){
		  String lCallDuration = (String) value;
		  boolean isNum = isInteger(lCallDuration);
		  if(isNum){
			int callDuration = Integer.valueOf(lCallDuration);
			context.getExternalContext().getSessionMap().put("tempCallDuration", callDuration); 
		  	if(lCallDuration.length()== 0) {
		  		message.setDetail("Please enter call duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(Integer.valueOf(lCallDuration) > 90){
				message.setDetail("Maximum limit is 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("pauseTime")){
		  String lPauseTime = (String) value;
		  boolean isNum = isInteger(lPauseTime);
		  if(isNum){
			int pauseTime = Integer.valueOf(lPauseTime);
			context.getExternalContext().getSessionMap().put("tempPauseTime", pauseTime);
			String tempCallDuration =  context.getExternalContext().getSessionMap().get("tempCallDuration").toString();
			double pausetime = Integer.valueOf(pauseTime)/60;
			if(pausetime==0.0){
				pausetime = 1.0;
			}
			if(lPauseTime.length()== 0 || Integer.valueOf(lPauseTime) == 0 ) {
		  		message.setDetail("Please enter valid pause time.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if((Integer.valueOf(tempCallDuration)) + (Math.floor(pausetime)) > 90){
				message.setDetail("Total of CallDuration and PauseTime should not exceed 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("testDuration")){
		  String lTestDuration = (String) value;
		  boolean isNum = isInteger(lTestDuration);
		  if(isNum){
			  String tempCallDuration =  context.getExternalContext().getSessionMap().get("tempCallDuration").toString();
			  String tempPauseTime =  context.getExternalContext().getSessionMap().get("tempPauseTime").toString();  
				double pausetime = Integer.valueOf(tempPauseTime)/60;
				if(pausetime==0.0){
					pausetime = 1.0;
				}

			  if(lTestDuration.length()== 0) {
		  		message.setDetail("Please enter test duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if((Integer.valueOf(tempCallDuration) + Math.floor(pausetime)) > Integer.valueOf(lTestDuration)){
				message.setDetail("Test duration should not be lesser than the total of call duration and pause time.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}else if(Integer.valueOf(lTestDuration)>90){
				message.setDetail("TestDuration max limit is 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }	  
	  else if(component.getId().equals("callDurationMt")){
		  String lCallDuration = (String) value;
		  boolean isNum = isInteger(lCallDuration);
		  if(isNum){
			int callDuration = Integer.valueOf(lCallDuration);
			context.getExternalContext().getSessionMap().put("tempMtCallDuration", callDuration);
		  	if(lCallDuration.length()== 0) {
		  		message.setDetail("Please enter call duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(Integer.valueOf(lCallDuration) == 0) {
		  		message.setDetail("Please enter valid call duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(Integer.valueOf(lCallDuration) > 90){
				message.setDetail("Total of call duration and pause time should not exceed 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }
	  
	  else if(component.getId().equals("pauseTimeMt")){
		  String lPauseTime = (String) value;
		  boolean isNum = isInteger(lPauseTime);
		  if(isNum){
			  String tempMtCallDuration =  context.getExternalContext().getSessionMap().get("tempMtCallDuration").toString();
			  context.getExternalContext().getSessionMap().put("tempMtPauseTime", lPauseTime);
			  double pausetime = Integer.valueOf(lPauseTime)/60;
			  if(pausetime==0.0){
					pausetime = 1.0;
			  }
			  if(lPauseTime.length()== 0) {
		  		message.setDetail("Please enter pause time.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(Integer.valueOf(lPauseTime) == 0) {
		  		message.setDetail("Please enter valid pause time.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if((Integer.valueOf(tempMtCallDuration)) + (Math.floor(pausetime)) > 90){
				message.setDetail("Total of call duration and pause time should not exceed 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }
	  
	  else if(component.getId().equals("testDurationMt")){
		  String lMtTestDuration = (String) value;
		  boolean isNum = isInteger(lMtTestDuration);
		  if(isNum){
			 /* String tempMtCallDuration =  context.getExternalContext().getSessionMap().get("tempMtCallDuration").toString();
			  String tempMtPauseTime =  context.getExternalContext().getSessionMap().get("tempMtPauseTime").toString();
			  double pausetime = Integer.valueOf(tempMtPauseTime)/60;
			  if(pausetime==0.0){
					pausetime = 1.0;
			  }
		  	if(lMtTestDuration.length()== 0) {
		  		message.setDetail("Please enter test duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(Integer.valueOf(lMtTestDuration) > 90){
				message.setDetail("TestDuration max limit is 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}else if( (Integer.valueOf(tempMtCallDuration) + Math.floor(pausetime)) > Integer.valueOf(lMtTestDuration)){
				message.setDetail("Test duration should not lesser than the total of call duration and pause time.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}else*/ 
			 if(Integer.valueOf(lMtTestDuration)>90){
				message.setDetail("Test duration should not exceed 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}else if(lMtTestDuration.length()== 0) {
		  		message.setDetail("Please enter test duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(Integer.valueOf(lMtTestDuration) == 0) {
		  		message.setDetail("Please enter valid test duration.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }
	  
	  else if(component.getId().equals("ftpServerURL")){
		  String lFtpServerURL = (String) value;
		  	if(lFtpServerURL.length()== 0) {
		  		message.setDetail("Please enter ftp server url.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("noOfRepeatCycles")){
		  String lNoOfRepeatCycles = (String) value;
		  boolean isNum = isInteger(lNoOfRepeatCycles);
		  if(isNum){
		  	if(lNoOfRepeatCycles.length()== 0) {
		  		message.setDetail("Please enter number of repeat cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
		  if(Integer.valueOf(lNoOfRepeatCycles) == 0) {
		  		message.setDetail("Please enter valid  number of repeat cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		   }
		  if(Integer.valueOf(lNoOfRepeatCycles) > 20) {
		  		message.setDetail("number of repeat cycles. could not exceed more than 20 cycles");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("uploadFilePath")){
		  String lUploadFilePath = (String) value;

		  	if(lUploadFilePath.length()== 0) {
		  		message.setDetail("Please enter upload file path.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("fileDownloadPath")){
		  String lFileDownloadPath = (String) value;

		  	if(lFileDownloadPath.length()== 0) {
		  		message.setDetail("Please enter File to Download.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("ftpUsername")){
		  String lFtpUsername = (String) value;
		  if(lFtpUsername.length()== 0) {
			  message.setDetail("Please enter Ftpusername.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lFtpUsername.length()<4) {
			  message.setDetail("FtpUsername should be between four to ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lFtpUsername.length()>11) {
			  message.setDetail("FtpUsername should not be greater ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(checkSpaces(lFtpUsername)){
			  message.setDetail("FtpUsername can't allow spaces.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("ftpPassword")){
		  String lFtpPassword = (String) value;
		  if(lFtpPassword.length()== 0) {
			  message.setDetail("Please enter Ftppassword.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lFtpPassword.length()<4) {
			  message.setDetail("Ftppassword should be between four to ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lFtpPassword.length()>11) {
			  message.setDetail("Ftppassword should not be greater ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("udpServerURL")){
		  String lUdpServerURL = (String) value;
		  	if(lUdpServerURL.length()== 0) {
		  		message.setDetail("Please enter udp server IP.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("udpServerPort")){
		  String lUdpServerPort = (String) value;
		  	if(lUdpServerPort.length()== 0) {
		  		message.setDetail("Please enter udp server port.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else if(lUdpServerPort.length()>4){
		  		message.setDetail("udp server port length can,t be greater than 4 numbers");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("noOfRepeatCyclesUDP")){
		  String lNoOfRepeatCycles = (String) value;
		  boolean isNum = isInteger(lNoOfRepeatCycles);
		  if(isNum){
		  	if(lNoOfRepeatCycles.length()== 0) {
		  		message.setDetail("Please enter udp number of repeat cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
		  if(Integer.valueOf(lNoOfRepeatCycles) == 0) {
		  		message.setDetail("Please enter valid udp number of repeat cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		   }
		  if(Integer.valueOf(lNoOfRepeatCycles) > 20) {
		  		message.setDetail("udp number of repeat cycles. could not exceed more than 20 cycles");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("serverURL")){
		  String lPingServerUrl = (String) value;
	  	  URL u = null;
		  	if(lPingServerUrl.length()== 0) {
		  		message.setDetail("Please enter server url.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else{
		  	  try {
		  	   u = new URL(lPingServerUrl);
		  	  } catch (MalformedURLException e) {
		  		message.setDetail("PingServerUrl is invalid.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	  }
		  	  try {
		  	   u.toURI();
		  	  } catch (URISyntaxException e) {
		  		message.setDetail("PingServerUrl is invalid.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	  }
		  	}
	  }else if(component.getId().equals("noOfRepeatCyclesPing")){
		  String lNoOfCyclesPing = (String) value;
		  boolean isNum = isInteger(lNoOfCyclesPing);
		  if(isNum){
		  	if(lNoOfCyclesPing.length()== 0) {
		  		message.setDetail("Please enter number of repeat cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }if(Integer.valueOf(lNoOfCyclesPing) == 0) {
		  		message.setDetail("Please enter valid no.of cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		   }
		  if(Integer.valueOf(lNoOfCyclesPing) > 20) {
		  		message.setDetail("No.of cycles could not exceed more than 20 cycles");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }
	  
	  else if(component.getId().equals("filePathToUpload")){
		  String lUploadFilePath = (String) value;

		  	if(lUploadFilePath.length()== 0) {
		  		message.setDetail("Please enter file to upload.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }else if(component.getId().equals("webPageURL")){
		  String lwebPageURL = (String) value;
		  	if(lwebPageURL.length()== 0) {
		  		message.setDetail("Please enter web Page URL.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}else{
		  		UrlValidator urlValidator = new UrlValidator();
			    if (urlValidator.isValid(lwebPageURL)) {
			       System.out.println("url is valid");
			    } else {
			       message.setDetail("web Page URL is Invalid");
			  	   message.setSummary("Incorrect");
			  	   message.setSeverity(FacesMessage.SEVERITY_ERROR);
				   throw new ValidatorException(message);
			    }
		  	}
	  }
	  else if(component.getId().equals("numberofrepeatcyclesInWeb")){
		  String lNoOfCyclesWeb = (String) value;
		  boolean isNum = isInteger(lNoOfCyclesWeb);
		  if(isNum){
		  	if(lNoOfCyclesWeb.length()== 0) {
		  		message.setDetail("Please enter number of repeat cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }if(Integer.valueOf(lNoOfCyclesWeb) == 0) {
		  		message.setDetail("Please enter valid no.of cycles.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  	}
	  }
	  else if(component.getId().equals("testDurationVoip")){
		  String lTestDurationVoip = (String) value;
		  boolean isNum = isInteger(lTestDurationVoip);
		  if(isNum){
			 if(Integer.valueOf(lTestDurationVoip)>90){
				message.setDetail("TestDuration max limit is 90mins.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }else{
		  		message.setDetail("Please enter numeric only.");
		  		message.setSummary("Incorrect");
		  		message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
		  }
	  }	  
	  
	  
	  context.getExternalContext().getSessionMap().remove("createConfigView");  
  	}
	public static boolean isInteger(String str)
	{
	  return str.matches("^[0-9]+$");  //match a number with optional '-' and decimal.
	}
	public boolean checkSpaces(String str){
		boolean spacesConatins = false;
		Pattern whitespace = Pattern.compile("\\s+?");
		Matcher matcher = whitespace.matcher(str);
		while (matcher.find()) {
			spacesConatins = true;
		}
		return spacesConatins;
	}
}



