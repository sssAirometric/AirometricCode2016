package com.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.to.UserBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateUserValidator implements Validator {
	public void validate(FacesContext context, UIComponent component,
      Object value) throws ValidatorException {

	  Pattern pattern;
	  Matcher matcher;
	  final String TESTNAME_PATTERN = "[A-Za-z0-9]+";
	  pattern = Pattern.compile(TESTNAME_PATTERN);
	  FacesMessage message = new FacesMessage();
	  UserBean  userBean=(UserBean)context.getExternalContext().getSessionMap().get("userBean");
	  
	  if(component.getId().equals("userName")){
		  String lUserName = (String) value;
		  if(lUserName.length()== 0) {
			  message.setDetail("Please enter username.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lUserName.length()<4) {
			  message.setDetail("Username should not less than 4 characters");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lUserName.length()>51) {
			  message.setDetail("Username should not be greater 50 characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(checkSpaces(lUserName)){
			  message.setDetail("Username can't allow spaces.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("password")){
		  String lPassword = (String) value;
		  if(lPassword.length()== 0) {
			  message.setDetail("Please enter password.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lPassword.length()<4) {
			  message.setDetail("Password should not less than 4 characters");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(lPassword.length()>11) {
			  message.setDetail("Password should not be greater ten characters.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }else if (component.getId().equals("email")){
		  String lEmail= (String) value;
		  String validEmail = echeck(lEmail);
		  if(lEmail.length()== 0) {
			  message.setDetail("Please enter email address.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }else if(validEmail.equals("invalid")){
			  message.setDetail("Invalid email address.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("operatorId")){
		  System.out.println("value----------------"+value);
		  String lOperator = (String) value;
		  if(Integer.valueOf(lOperator) == 0){
			  message.setDetail("Please select operator.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }else if(component.getId().equals("country")){
		  String countryId = (String) value;
		  if(Integer.valueOf(countryId) == 0){
			  message.setDetail("Please select a country.");
			  message.setSummary("Incorrect");
			  message.setSeverity(FacesMessage.SEVERITY_ERROR);
			  throw new ValidatorException(message);
		  }
	  }
	  userBean.setAction("list");
	  context.getExternalContext().getSessionMap().put("userBean",userBean);
	}  
	
	
	
	public String echeck(String emailAddress) {
		String validEmail = "false";
		String emailRegEx = "^[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+(\\.[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.([A-Za-z]{2,})$";
		if (!emailAddress.matches(emailRegEx)) {
			validEmail = "invalid";
		} else {
			validEmail = "valid";
		}
		return validEmail;
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



