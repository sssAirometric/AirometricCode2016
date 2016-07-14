package com.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.action.TestCaseAction;


public class AssignConfigValidator implements Validator {
	public void validate(FacesContext context, UIComponent component,
      Object value) throws ValidatorException {
	  context.getExternalContext().getSessionMap().remove("userStatus");
	  String userRole =  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
	  FacesMessage message = new FacesMessage();
	  if(component.getId().equals("operator")){
		  String tempOperator = (String) value;
		  context.getExternalContext().getSessionMap().put("tempOperator", tempOperator);
		  if(Integer.valueOf(tempOperator) == 0){
		  message.setDetail("Please select operator.");
		  message.setSummary("Incorrect");
		  message.setSeverity(FacesMessage.SEVERITY_ERROR);
		  throw new ValidatorException(message);
		  }
	 }else if(component.getId().equals("configFile")){
		  String tConfigFile = (String) value;
		  context.getExternalContext().getSessionMap().put("tempConfigFile", tConfigFile);
		  if(null != tConfigFile){
			  if(Integer.valueOf(tConfigFile) == 0){
			  message.setDetail("Please select config file.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
			  }
		  }	  
	 }else if(component.getId().equals("user")){
		 String userId = (String) value;
		 if(null != context.getExternalContext().getSessionMap().get("tempOperator") 
				 && Integer.valueOf(context.getExternalContext().getSessionMap().get("tempOperator").toString()) != 0 
				 && Integer.valueOf(context.getExternalContext().getSessionMap().get("tempConfigFile").toString()) != 0
				 && userRole.equals("superadmin")){
			 if(null != userId){
				 if(Integer.valueOf(userId) == 0){	 
					 message.setDetail("Please select user.");
					 message.setSummary("Incorrect");
					 message.setSeverity(FacesMessage.SEVERITY_ERROR);
					 throw new ValidatorException(message);
				 }
			 }
		 }
	 }
	 else if(component.getId().equals("imei")){
		 if(null != context.getExternalContext().getSessionMap().get("tempOperator") && Integer.valueOf(context.getExternalContext().getSessionMap().get("tempOperator").toString()) != 0){
		 String tImei = (String) value;
		 if(null != tImei){
			 if(Integer.valueOf(tImei) == 0){
				 message.setDetail("Please select IMEI.");
				 message.setSummary("Incorrect");
				 message.setSeverity(FacesMessage.SEVERITY_ERROR);
				 throw new ValidatorException(message);
			 }	
		 }
	 }
   }
	  context.getExternalContext().getSessionMap().remove("assignType");  
  }
}
 




