package com.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.to.MarketPlaceBean;
import com.to.UserBean;

public class MarketUserValidator implements Validator {
	public void validate(FacesContext context, UIComponent component,
		      Object value) throws ValidatorException {
			  Pattern pattern;
			  Matcher matcher;
			  final String TESTNAME_PATTERN = "[A-Za-z0-9]+";
			  pattern = Pattern.compile(TESTNAME_PATTERN);
			  FacesMessage message = new FacesMessage();
			  //UserBean  userBean=(UserBean)context.getExternalContext().getSessionMap().get("userBean");
			  MarketPlaceBean marketPlaceBean =(MarketPlaceBean)context.getExternalContext().getSessionMap().get("marketPlaceBean");
			  
			  if(component.getId().equals("marketName")){
				  String lmarketName = (String) value;
				  if(lmarketName.length()== 0) {
					  message.setDetail("Please enter Market Name.");
					  message.setSummary("Incorrect");
					  message.setSeverity(FacesMessage.SEVERITY_ERROR);
					  throw new ValidatorException(message);
				  }else if(lmarketName.length()>20) {
					  message.setDetail("Market Name should not be greater 20 characters.");
					  message.setSummary("Incorrect");
					  message.setSeverity(FacesMessage.SEVERITY_ERROR);
					  throw new ValidatorException(message);
			   }
         }
			context.getExternalContext().getSessionMap().put("marketPlaceBean",marketPlaceBean);
	}
	
}
