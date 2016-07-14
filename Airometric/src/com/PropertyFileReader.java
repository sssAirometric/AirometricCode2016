package com;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertyFileReader {
	private static HashMap<String,String>  propertiesMap = null;
	private static Locale currentLocale = new Locale("es");
	public static ResourceBundle resourceBundle = ResourceBundle.getBundle("Resource",currentLocale);

	public static PropertyFileReader propertyManagerOBJ =null;
	public static Properties props = new Properties();
	private PropertyFileReader() {
		loadProperties();
	}
	public synchronized static HashMap<String,String>  getProperties() {
		try {
			/*FacesContext context = FacesContext.getCurrentInstance();
		    ResourceBundle resourceBundle = context.getApplication().evaluateExpressionGet(context, "#{props}", ResourceBundle.class);
		    String propertiesFilesPath = resourceBundle.getString("PROPERTIES_FILE_PATH");*/
			props.load(new FileInputStream(resourceBundle.getString("PROPERTIES_FILE_PATH")));
        	
    		if(propertiesMap == null) {
				propertiesMap = new HashMap<String,String>(50);
    		}	
    		
    		String environment=  props.getProperty("ENVIRONMENT"," ").toString();
    		propertiesMap.put("ENVIRONMENT",environment);
    		
			propertiesMap.put(environment+"_DATABASE_URL", props.getProperty(environment+"_DATABASE_URL",""));
			propertiesMap.put(environment+"_DATABASE_USERNAME",props.getProperty(environment+"_DATABASE_USERNAME",""));
			propertiesMap.put(environment+"_DATABASE_PASSWORD", props.getProperty(environment+"_DATABASE_PASSWORD",""));
			propertiesMap.put("UPLOADED_FILE_PATH", props.getProperty("UPLOADED_FILE_PATH"));
			propertiesMap.put("DOWNLOAD_FILE_PATH", props.getProperty("DOWNLOAD_FILE_PATH"));	
			propertiesMap.put("UPLOADED_EXTERNAL_FILE_PATH", props.getProperty("UPLOADED_EXTERNAL_FILE_PATH"));	
			propertiesMap.put("SIGNALSTRENGTH_GSM", props.getProperty("SIGNALSTRENGTH_GSM"));	
			propertiesMap.put("SIGNALSTRENGTH_GSM1", props.getProperty("SIGNALSTRENGTH_GSM1"));	
			propertiesMap.put("SIGNALSTRENGTH_LTE", props.getProperty("SIGNALSTRENGTH_LTE"));	
			propertiesMap.put("THROUGHPUT", props.getProperty("THROUGHPUT"));
			propertiesMap.put("REPORTS_URL", props.getProperty("REPORTS_URL"));	
			propertiesMap.put("ServerPort", props.getProperty("ServerPort"));	
			propertiesMap.put("QXDM_FILE_PATH", props.getProperty("QXDM_FILE_PATH"));	
			propertiesMap.put("UPLOADED_MOBILEAPPTEST_FILE_PATH", props.getProperty("UPLOADED_MOBILEAPPTEST_FILE_PATH"));	
			propertiesMap.put("REPORTS_GENERATE_URL", props.getProperty("REPORTS_GENERATE_URL"));	
			propertiesMap.put("REPORTS_FOLDER_PATH", props.getProperty("REPORTS_FOLDER_PATH"));	
			propertiesMap.put("AIROMETRIC_LOGO_PATH", props.getProperty("AIROMETRIC_LOGO_PATH"));	
			propertiesMap.put("FREQUENCY_FILE_PATH", props.getProperty("FREQUENCY_FILE_PATH"));
			propertiesMap.put("ANALYTICS_REPORTS_FOLDER_PATH", props.getProperty("ANALYTICS_REPORTS_FOLDER_PATH"));
			propertiesMap.put("CONFIG_FILE_NAME", props.getProperty("CONFIG_FILE_NAME"));
	    }
		catch(IOException e) {
			  e.printStackTrace();
		}
		return propertiesMap;
	}
	private void loadProperties() {
		try {
			props.load((this.getClass().getClassLoader().getResourceAsStream( "Resource.properties" ) ));
			loadPropertiesDataToHashMap();
		}
		catch (IOException e) {
			System.out.println("Exception while loading properties file ");
		}
	}
	private synchronized static void loadPropertiesDataToHashMap() {
		try {
			String propertyValue = "";
			String propertyKey = "";
			Enumeration<Object>  propertiesKeyEnum  = props.keys();
			while(propertiesKeyEnum.hasMoreElements()){
				propertyKey = propertiesKeyEnum.nextElement().toString();
				propertyValue = props.getProperty(propertyKey);
				propertiesMap.put(propertyKey, propertyValue);
			}
	    }
		catch(Exception e) {
			System.out.println("Exception while loading properties file data to hash map ");
		}
	}
	public synchronized static String getProperty(String propertyKey) {
		String propertyValue = "";
		System.out.println("Property Key requested for value is : "+propertyKey);
		System.out.println("Size of Hash Map that contains properties file data : "+propertiesMap.size());
		try {
			if(propertiesMap.size()>0){
				propertyValue = propertiesMap.get(propertyKey).toString();
			}
		} catch (Exception e) {
			System.out.println("Exception while getting value from hashMap in PropertyManager class");
		}
		System.out.println("Property Value sent is : "+propertyValue);
		return propertyValue;
	}
}
