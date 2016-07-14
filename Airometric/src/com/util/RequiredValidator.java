package com.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class RequiredValidator implements Validator{

	public void validate(FacesContext context, UIComponent uiComponent, Object vObject)
			throws ValidatorException {
		if (vObject == null || "".equals(vObject.toString().trim())) {
		      FacesMessage message = new FacesMessage();
		      String messageStr = (String)uiComponent.getAttributes().get("message");
		      if (messageStr == null) {
		        messageStr = "Please enter required data";
		      }
		      message.setDetail(messageStr);
		      message.setSummary(messageStr);
		      message.setSeverity(FacesMessage.SEVERITY_ERROR);
		      throw new ValidatorException(message);
		}
	}

}
