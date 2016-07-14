package com.util;

import java.util.Map;

import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

public class RequestInterceptor implements  PhaseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void afterPhase(PhaseEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		 String servlet_path=((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString(); 
		System.out.println(servlet_path+"-+++--After phase-++- " + context.getExternalContext().getSessionMap().get("loggedInUserRoleID"));
		
		 NavigationHandler nh = context.getApplication().getNavigationHandler();
	/*	 try {
			 ViewHandler viewHandler = context.getApplication().getViewHandler();
             UIViewRoot viewRoot = viewHandler.createView(context, "/Pages/login.xhtml");
             context.setViewRoot(viewRoot);
             context.renderResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public void beforePhase(PhaseEvent event) {
		 FacesContext context = event.getFacesContext();
		 Map reqMap = context.getExternalContext().getRequestMap();
		 String loginUserName= null;
		 String servlet_path = null;
		 if(null != context.getExternalContext().getSessionMap().get("loginName")){
			 loginUserName =  context.getExternalContext().getSessionMap().get("loginName").toString();
		 }
		 if(null != reqMap.get("javax.servlet.forward.servlet_path")){
			 servlet_path= reqMap.get("javax.servlet.forward.servlet_path").toString();
		 }
		 if(null != servlet_path && servlet_path.equals("/index.jsp")){
			// return "";
		 }else if(null == loginUserName){
			 NavigationHandler nh = context.getApplication().getNavigationHandler();
			 try {
				 ViewHandler viewHandler = context.getApplication().getViewHandler();
	             UIViewRoot viewRoot = viewHandler.createView(context, "/Pages/login.xhtml");
	             context.setViewRoot(viewRoot);
	             context.renderResponse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
	}

	public PhaseId getPhaseId() {
		 return PhaseId.RENDER_RESPONSE;
	}

}
