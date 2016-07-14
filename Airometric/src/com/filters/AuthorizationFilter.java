package com.filters;

import java.io.IOException;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;

@WebFilter("/Airometric/*")
public class AuthorizationFilter implements Filter {

/*    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {    
        HttpServletRequest req = (HttpServletRequest) request;

        if (auth != null && auth.isLoggedIn()) {
            // User is logged in, so just continue request.
            chain.doFilter(request, response);
        } else {
            // User is not logged in, so redirect to index.
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendRedirect(req.getContextPath() + "/index.xhtml");
        }
    }*/
	
	  public void init(FilterConfig filterConfig) throws ServletException {
	    }
	  public void doFilter(ServletRequest request, ServletResponse response,
	                         FilterChain filterChain)
	    throws IOException, ServletException {
	    	 HttpServletRequest req = (HttpServletRequest) request;
	    	 
//	    	 System.out.println("in this page");
	    }

	    public void destroy() {
	    }

    // You need to override init() and destroy() as well, but they can be kept empty.
}
