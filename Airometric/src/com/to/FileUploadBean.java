package com.to;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.component.*;

import com.PropertyFileReader;
import com.dao.ReportDao;
import com.dao.impl.ReportDaoImpl;

import jxl.write.WritableWorkbook;

public class FileUploadBean {

	static HashMap<String, String> propertiesFiledata = PropertyFileReader.getProperties();
	private final String UPLOADED_EXTERNAL_FILE_PATH = propertiesFiledata.get("UPLOADED_EXTERNAL_FILE_PATH");
	private final String UPLOADED_MOBILEAPPTEST_FILE_PATH = propertiesFiledata.get("UPLOADED_MOBILEAPPTEST_FILE_PATH");
	private final String QXDM_FILE_PATH = propertiesFiledata.get("QXDM_FILE_PATH");
	private final String FREQUENCY_FILE_PATH = propertiesFiledata.get("FREQUENCY_FILE_PATH");

	private String testCaseName;
	
	private String testName;
	
	private String marketId;
	
	private String fileReportType;
	
	private String deviceId;
	
	private String frequencyPlanName;
	
	private String frequencyToProcess;
	
	private String hiddenReportType;
	
	private String overWritePlan;
	
	static WritableWorkbook writableWorkbook = null;
	
	public String getHiddenReportType() {
		return hiddenReportType;
	}

	public void setHiddenReportType(String hiddenReportType) {
		this.hiddenReportType = hiddenReportType;
	}
	
	public String getFileName(String str) {
		return FilenameUtils.getName(str);
	}

	public String getFrequencyToProcess() {
		return frequencyToProcess;
	}

	public void setFrequencyToProcess(String frequencyToProcess) {
		this.frequencyToProcess = frequencyToProcess;
	}

	public String getFrequencyPlanName() {
		return frequencyPlanName;
	}

	public void setFrequencyPlanName(String frequencyPlanName) {
		this.frequencyPlanName = frequencyPlanName;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getFileReportType() {
		return fileReportType;
	}
	public void setFileReportType(String fileReportType) {
		this.fileReportType = fileReportType;
	}

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getoverWritePlan() {
		return overWritePlan;
	}

	public void setoverWritePlan(String overWritePlan) {
		this.overWritePlan = overWritePlan;
	}
	
	public static Connection conn = null;
	
	public String handleFileUpload(FileUploadEvent event) {
		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			String testCaseName = null;
			String testName = null;
			String deviceId=null;
			String marketId = null;
			Map map = context.getExternalContext().getRequestParameterMap();
			Iterator it = map.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        String key = pairs.getKey().toString();
		        //System.out.println("key--------"+key);
		        //System.out.println("pairs--------"+pairs.getValue().toString());
		        if(key.indexOf(":testCaseName") != -1||key.indexOf("testCaseName") != -1){
		        	testCaseName = pairs.getValue().toString();
		        }
		        if(key.indexOf(":testName") != -1||key.indexOf("testName") != -1){
		        	testName = pairs.getValue().toString();
		        }
		        if(key.indexOf(":deviceId") != -1||key.indexOf("deviceId") != -1){
		        	deviceId = pairs.getValue().toString();
		        }
		        if(key.indexOf(":hiddenReportType") != -1||key.indexOf("hiddenReportType") != -1){
		        	fileReportType = pairs.getValue().toString();
		        	//System.out.println("fileReportType:"+fileReportType);
		        }
	        	if(key.indexOf(":marketId") != -1||key.indexOf("marketId") != -1){
		        	marketId = pairs.getValue().toString();
		        	//System.out.println("marketId:"+marketId);
		        }
	        	if(key.indexOf(":frequencyToProcess") != -1||key.indexOf("frequencyToProcess") != -1){
	        		frequencyToProcess = pairs.getValue().toString();
		        	//System.out.println("frequencyToProcess:"+frequencyToProcess);
		        }
	        	if(key.indexOf(":frequencyPlanName") != -1||key.indexOf("frequencyPlanName") != -1){
	        		frequencyPlanName = pairs.getValue().toString();
		        	//System.out.println("frequencyPlanName:"+frequencyPlanName);
		        }
	        	if(key.indexOf(":overWritePlan") != -1||key.indexOf("overWritePlan") != -1){
	        		overWritePlan = pairs.getValue().toString();
		        	//System.out.println("overWritePlan:"+overWritePlan);
		        }	
	        }
		    
		    if(fileReportType.equals("VQTFile")){
		    	//System.out.println("inside vqt");
		    	File targetFolder = new File(UPLOADED_EXTERNAL_FILE_PATH);
				if (!targetFolder.exists()) {
					targetFolder.mkdir();
				}
				String fileName = getFileName(event.getFile().getFileName());
				int fileSize = (int) event.getFile().getSize();
				InputStream inputStream = event.getFile().getInputstream();
				if(!marketId.equals("0") && null != testName && !testName.equals("") && !deviceId.equals("0")){
					//System.out.println("inside if...");
					//System.out.println("fileName---");
					if (null != testName && !testName.equals("") ) {
						String[] fileNameStr = fileName.split("\\.");
						fileName = fileNameStr[0]+"&"+marketId+"&"+deviceId +"."+ fileNameStr[1];
					}
					OutputStream out = new FileOutputStream(new File(targetFolder,fileName));
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = inputStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					inputStream.close();
					out.flush();
					out.close();
					
					FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage("File upload failed.Please enter test name and select device and select market. ");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
		    }else if(fileReportType.equals("LogFile")){
		    	//System.out.println("inside LogFile");
		    	File targetFolder = new File(UPLOADED_MOBILEAPPTEST_FILE_PATH);
				if (!targetFolder.exists()) {
					targetFolder.mkdir();
				}
				//System.out.println("UPLOADED_MOBILEAPPTEST_FILE_PATH----"+UPLOADED_MOBILEAPPTEST_FILE_PATH);
				String fileName = getFileName(event.getFile().getFileName());
				int fileSize = (int) event.getFile().getSize();
				InputStream inputStream = event.getFile().getInputstream();
				if( null != testCaseName && !testCaseName.equals("")){
					if (null != testCaseName && !testCaseName.equals("")) {
						String[] fileNameStr = fileName.split("\\.");
						fileName = fileNameStr[0] + "."
								+ fileNameStr[1];
					}
					OutputStream out = new FileOutputStream(new File(targetFolder,
							fileName));
					//System.out.println("targetFolder--------"+targetFolder);
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = inputStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					inputStream.close();
					out.flush();
					out.close();
					FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage("File upload failed.Please enter test name and select market.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
		    }else if(fileReportType.equals("QXDMFile")){
		    	//System.out.println("inside QXDMFile");
		    	File targetFolder = new File(QXDM_FILE_PATH);
				if (!targetFolder.exists()) {
					targetFolder.mkdir();
				}
				String fileName = getFileName(event.getFile().getFileName());
				int fileSize = (int) event.getFile().getSize();
				InputStream inputStream = event.getFile().getInputstream();
				if(null != testCaseName && !testCaseName.equals("")){
					if (null != testCaseName && !testCaseName.equals("")) {
						String[] fileNameStr = fileName.split("\\.");
						fileName = fileNameStr[0]  + "."
								+ fileNameStr[1];
					}
				    File f = new File(QXDM_FILE_PATH+"//"+fileName);
					  if(f.exists()){
						//System.out.println("File existed");
						f.delete();
						OutputStream out = new FileOutputStream(new File(targetFolder,fileName));
						int read = 0;
						byte[] bytes = new byte[1024];
						while ((read = inputStream.read(bytes)) != -1) {
							out.write(bytes, 0, read);
						}
						inputStream.close();
						out.flush();
						out.close();
						FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					  }else{
						  //System.out.println("File not found!");
						  OutputStream out = new FileOutputStream(new File(targetFolder,
									fileName));
							int read = 0;
							byte[] bytes = new byte[1024];
							while ((read = inputStream.read(bytes)) != -1) {
								out.write(bytes, 0, read);
							}
							inputStream.close();
							out.flush();
							out.close();
							FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
							FacesContext.getCurrentInstance().addMessage(null, msg);
					  }
				}else{
					FacesMessage msg = new FacesMessage("File upload failed.Please enter test name and select market.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				} 
		    }else if(fileReportType.equals("FrequencyPlans")){
		    	
		    	System.out.println("inside............................... :FrequencyPlans : "+overWritePlan);
		    	ReportDao reportDao = new ReportDaoImpl();
		    	File targetFolder = new File(FREQUENCY_FILE_PATH);
		    	boolean overWrite = false;
		    	//System.out.println("targetFolder :FrequencyPlans : "+targetFolder);
				if (!targetFolder.exists()) {
					targetFolder.mkdir();
				}
				String fileName = getFileName(event.getFile().getFileName());
				int fileSize = (int) event.getFile().getSize();
				InputStream inputStream = event.getFile().getInputstream();

				String fileExtension = "";
				String[] fileNameStr = fileName.split("\\.");
				fileName = fileNameStr[0]  + "." + fileNameStr[1];
				fileExtension = fileNameStr[1];
				//context.getExternalContext().getSessionMap().put("PlanNameExist","");
				if( !fileExtension.equalsIgnoreCase("xls") && !fileExtension.equalsIgnoreCase("xlsx")){
					FacesMessage msg = new FacesMessage("File upload failed.Please select only xls or xlsx file.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}else if (null != frequencyPlanName && !frequencyPlanName.equals("")) 
					{
					 if(overWritePlan.equals("") || overWritePlan.equals("overWritePlan"))
					 {
					 overWrite = true;
					
					 File f = new File(FREQUENCY_FILE_PATH+"//"+fileName);
						 if(f.exists()){
						//System.out.println("File existed");
						f.delete();
						OutputStream out = new FileOutputStream(new File(targetFolder,fileNameStr[0]+"&"+frequencyPlanName+ "." + fileNameStr[1]));
						int read = 0;
						byte[] bytes = new byte[1024];
						while ((read = inputStream.read(bytes)) != -1) {
							out.write(bytes, 0, read);
						}
						inputStream.close();
						out.flush();
						out.close();
						/*Comment by ankit on 01/02/16
						 * FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
						FacesContext.getCurrentInstance().addMessage(null, msg);*/
						 }else{
						  //System.out.println("File not found!");
						  OutputStream out = new FileOutputStream(new File(targetFolder,
								  fileNameStr[0]+"&"+frequencyPlanName+ "." + fileNameStr[1]));
							int read = 0;
							byte[] bytes = new byte[1024];
							while ((read = inputStream.read(bytes)) != -1) {
								out.write(bytes, 0, read);
							}
							inputStream.close();
							out.flush();
							out.close();
							/*Comment by ankit on 01/02/16
							 * FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
							FacesContext.getCurrentInstance().addMessage(null, msg);*/
					  }
					  //new code to insert values of frequency band in freq_band table directly without using Mule.
						
					String status = reportDao.ReadExcelFile(fileNameStr[0]+"&"+frequencyPlanName+ "." + fileNameStr[1],frequencyPlanName
							,overWrite);
					  if(status.equals("Success")){
						  FacesMessage msg = new FacesMessage("File:", fileName+ " is uploaded successfully.");
						  FacesContext.getCurrentInstance().addMessage(null, msg);
					  }
					  else if(status.equals("HeadingError")){
						 new File(FREQUENCY_FILE_PATH+"/"+fileNameStr[0]+"&"+frequencyPlanName+ "." + fileNameStr[1]).delete();
						FacesMessage msg = new FacesMessage("Incorrect file. Please contact the administrator.");
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
					 }else{
						 //overWrite = false;
						 RequestContext.getCurrentInstance().execute("ExistPlanName()");
						// return "uploadSuccess";
					 }
					}else{
				FacesMessage msg = new FacesMessage("File upload failed.Please select Frequency plan and enter Frequency Plan name.");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} 
		}
	} catch (IOException e) {
			e.printStackTrace();
		}
		return "uploadSuccess";
	}
	
	  public void devicecaseListener(AjaxBehaviorEvent event) {
	        String result = "called by " + event.getComponent().getClass().getName();
	        Map<String, String> device = new HashMap<String, String>();
			FacesContext context = FacesContext.getCurrentInstance();
			String newValue =  (String) ((UIOutput)event.getSource()).getValue();
			ReportDao reportDao=new ReportDaoImpl();
			device = reportDao.getDeviceForTest(newValue);
			//System.out.println("device-------------------"+device);
			context.getExternalContext().getSessionMap().remove("deviceSelectNameMap");
			context.getExternalContext().getSessionMap().put("deviceSelectNameMap", device);
	    }
	  //for that checks frequency plan name already exist or not. 
	  public void devicecaseListener1(AjaxBehaviorEvent event) {
	        
			FacesContext context = FacesContext.getCurrentInstance();
			ReportDao reportDao=new ReportDaoImpl();
			System.out.println("devicecaseListener1-------------------called listener");
			if(!reportDao.CheckFreqBandPlan(frequencyPlanName))
			{
			context.getExternalContext().getSessionMap().remove("PlanNameExist");
			context.getExternalContext().getSessionMap().put("PlanNameExist","PlanNameAlreadyExist");
			RequestContext.getCurrentInstance().execute("ExistPlanName()");
			}else{
			context.getExternalContext().getSessionMap().remove("PlanNameExist");
			context.getExternalContext().getSessionMap().put("PlanNameExist","");
			}
	    }
}
