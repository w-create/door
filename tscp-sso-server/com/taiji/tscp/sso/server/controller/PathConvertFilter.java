package com.taiji.tscp.sso.server.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathConvertFilter implements Filter {

	public static final String SSO_TOKEN_NAME = "__vt_param__";
	@Override
	public void destroy() {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		if(req.getServletContext().getContextPath() == null ||
				((HttpServletRequest)req).getRequestURL().toString().endsWith(req.getServletContext().getContextPath()+"/")) {
			((HttpServletResponse)resp).sendRedirect(((HttpServletRequest)req).getRequestURL()+"/admin/admin");
			return ;
		}
		chain.doFilter(req, resp);
	}

	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	
	
	

}
