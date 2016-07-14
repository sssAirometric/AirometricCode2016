package com.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginUserValidator implements Validator {
	public void validate(FacesContext context, UIComponent component,
      Object value) throws ValidatorException {

	  Pattern pattern;
	  Matcher matcher;
	  final String USERNAME_PATTERN = "[A-Za-z0-9]+";
	  pattern = Pattern.compile(USERNAME_PATTERN);
	  FacesMessage message = new FacesMessage();
	  
	  
	  if(component.getId().equals("loginUserName")){
		  String tLoginUserName = (String) value;
		  if(tLoginUserName.length()== 0) {
			  message.setDetail("Please enter user name.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(tLoginUserName.length()<4 || tLoginUserName.length()>10) {
			  message.setDetail("Username should be between four to ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(tLoginUserName.length()>11) {
			  message.setDetail("Username should not be greater ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }/*else{
			matcher = pattern.matcher(tLoginUserName);
			boolean valid = matcher.matches();
			if(!valid){
				message.setDetail("Please enter alphaNumeric only.");
				message.setSummary("Incorrect");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			    throw new ValidatorException(message);
			}
		  }*/
	  }else if(component.getId().equals("loginPassWord")){
		  String tLoginPassWord = (String) value;
		  if(tLoginPassWord.length()== 0) {
			  message.setDetail("Please enter password.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(tLoginPassWord.length()<4 || tLoginPassWord.length()>10) {
			  message.setDetail("Password should be between four to ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(tLoginPassWord.length()>11) {
			  message.setDetail("Password should not be greater ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
		  /*else{
				matcher = pattern.matcher(tLoginPassWord);
				boolean valid = matcher.matches();
				if(!valid){
					message.setDetail("Please enter alphaNumeric only.");
					message.setSummary("Incorrect");
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
				    throw new ValidatorException(message);
				}
		 }*/
	  }

	}  
}



