package com.to;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

public class TestConfig implements Serializable{
	private static final long serialVersionUID = 1L;
	private String testConfigId;
	private String testConfigName;
	private String testConfigXml;
	
	private boolean enableTestCaseDiv = true;
	private boolean enableMoTestCaseDiv = false;
	private boolean enableMtTestCaseDiv = false;
	private boolean enableFtpTestCaseDiv = false;

	private boolean enableUdpTestCaseDiv = false;
	private boolean enableDataTestCaseDiv = false;
	private boolean enableWebpageTestCaseDiv = false;	
	private boolean enableVoipTestCaseDiv = false;
	private boolean enableSubmitDiv = false;
		
	private String operator_Name;
	private String user_Name;
	private String device_Type;
	private String date;
	private String slNO;
	private String created_Date;
	private String modified_Date;
	private String Status;
	private String imei;
	private boolean selected;

	
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getCreated_Date() {
		return created_Date;
	}

	public void setCreated_Date(String createdDate) {
		created_Date = createdDate;
	}

	public String getModified_Date() {
		return modified_Date;
	}

	public void setModified_Date(String modifiedDate) {
		modified_Date = modifiedDate;
	}

	public String getSlNO() {
		return slNO;
	}

	public void setSlNO(String slNO) {
		this.slNO = slNO;
	}

	public String getOperator_Name() {
		return operator_Name;
	}

	public void setOperator_Name(String operatorName) {
		operator_Name = operatorName;
	}

	public String getUser_Name() {
		return user_Name;
	}

	public void setUser_Name(String userName) {
		user_Name = userName;
	}

	public String getDevice_Type() {
		return device_Type;
	}

	public void setDevice_Type(String deviceType) {
		device_Type = deviceType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTestConfigId() {
		return testConfigId;
	}
	
	public void setTestConfigId(String testConfigId) {
		this.testConfigId = testConfigId;
	}

	public void setTestConfigName(String testConfigName) {
		this.testConfigName = testConfigName;
	}

	public String getTestConfigName() {
		return testConfigName;
	}

	public String getTestConfigXml() {
		return testConfigXml;
	}

	public void setTestConfigXml(String testConfigXml) {
		this.testConfigXml = testConfigXml;
	}
	public boolean isEnableTestCaseDiv() {
		return enableTestCaseDiv;
	}

	public void setEnableTestCaseDiv(boolean enableTestCaseDiv) {
		this.enableTestCaseDiv = enableTestCaseDiv;
	}
    public boolean isEnableMoTestCaseDiv() {
		return enableMoTestCaseDiv;
	}
	public void setEnableMoTestCaseDiv(boolean enableMoTestCaseDiv) {
		this.enableMoTestCaseDiv = enableMoTestCaseDiv;
	}
    public boolean isEnableMtTestCaseDiv() {
		return enableMtTestCaseDiv;
	}

	public void setEnableMtTestCaseDiv(boolean enableMtTestCaseDiv) {
		this.enableMtTestCaseDiv = enableMtTestCaseDiv;
	}

	public boolean isEnableFtpTestCaseDiv() {
		return enableFtpTestCaseDiv;
	}

	public void setEnableFtpTestCaseDiv(boolean enableFtpTestCaseDiv) {
		this.enableFtpTestCaseDiv = enableFtpTestCaseDiv;
	}

	public boolean isEnableSubmitDiv() {
		return enableSubmitDiv;
	}

	public void setEnableSubmitDiv(boolean enableSubmitDiv) {
		this.enableSubmitDiv = enableSubmitDiv;
	}

	public boolean isEnableUdpTestCaseDiv() {
		return enableUdpTestCaseDiv;
	}

	public void setEnableUdpTestCaseDiv(boolean enableUdpTestCaseDiv) {
		this.enableUdpTestCaseDiv = enableUdpTestCaseDiv;
	}

	public boolean isEnableDataTestCaseDiv() {
		return enableDataTestCaseDiv;
	}

	public void setEnableDataTestCaseDiv(boolean enableDataTestCaseDiv) {
		this.enableDataTestCaseDiv = enableDataTestCaseDiv;
	}
	public boolean isEnableWebpageTestCaseDiv() {
		return enableWebpageTestCaseDiv;
	}

	public void setEnableWebpageTestCaseDiv(boolean enableWebpageTestCaseDiv) {
		this.enableWebpageTestCaseDiv = enableWebpageTestCaseDiv;
	}

	public boolean isEnableVoipTestCaseDiv() {
		return enableVoipTestCaseDiv;
	}

	public void setEnableVoipTestCaseDiv(boolean enableVoipTestCaseDiv) {
		this.enableVoipTestCaseDiv = enableVoipTestCaseDiv;
	}

	@PostConstruct
	public void reSetMainDivProperty() {
		enableTestCaseDiv = true;
		enableSubmitDiv = false;
	}	
	
	public void enterMoTestCases() {
		enableTestCaseDiv = false;
		enableMoTestCaseDiv = true;
	}
	
	public void enterMtTestCases() {
		enableTestCaseDiv = true;
		enableMoTestCaseDiv = false;
	}	
	
	public void enterFtpTestCases() {
		enableMoTestCaseDiv = false;
		enableMtTestCaseDiv = true;
	}	

	public void enterPreviousFtp() {
		enableMoTestCaseDiv = true;
		enableMtTestCaseDiv = false;

	}	
	
	public void enterFinalTestCases() {
		enableMtTestCaseDiv = false;
		enableFtpTestCaseDiv = true;
	}	
	public void enterTestCases() {
		enableFtpTestCaseDiv = false;
		enableMtTestCaseDiv = true;
	}	
	public void nextFtpTest(){
		enableFtpTestCaseDiv = false;
		enableUdpTestCaseDiv = true;
	}
	public void previousUdpTest(){
		enableFtpTestCaseDiv = true;
		enableUdpTestCaseDiv = false;
	}	
	public void nextUdpTest(){
		enableUdpTestCaseDiv = false;
		enableDataTestCaseDiv = true;
	}
	public void previousDataPingTest(){
		enableUdpTestCaseDiv = true;
		enableDataTestCaseDiv = false;
	}
	public void nextDataPingTest(){
		enableDataTestCaseDiv = false;
		enableWebpageTestCaseDiv = true;
	}	
	public void previousWebpageTest(){
		enableWebpageTestCaseDiv = false;
		enableDataTestCaseDiv = true;
	}
	public void nextWebpageTest(){
		enableWebpageTestCaseDiv = false;
		enableVoipTestCaseDiv = true;
	}
	public void previousVoipTest(){
		enableWebpageTestCaseDiv = true;
		enableVoipTestCaseDiv = false;
	}	
	public void enterSubmitTestCases() {
		enableVoipTestCaseDiv = false;
		enableSubmitDiv = true;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}	
	
}
