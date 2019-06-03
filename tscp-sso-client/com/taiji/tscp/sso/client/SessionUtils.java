package com.taiji.tscp.sso.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 当前已登录用户Session工具
 * 
 * @author Joe
 */
public class SessionUtils {
	/**
	 * 用户信息
	 */
	public static final String SESSION_USER = "_sessionUser";

	/**
	 * 用户权限
	 */
	public static final String SESSION_USER_PERMISSION = "_sessionUserPermission";

	public static SessionUser getSessionUser(HttpServletRequest request) {
		
		if(request == null) {
			throw new IllegalArgumentException();
		}
		HttpSession session = request.getSession(false);
		return (SessionUser)(session != null ? session.getAttribute(SESSION_USER) : null);
	}

	public static void setSessionUser(HttpServletRequest request, SessionUser sessionUser) {
		
		if(request == null) {
			throw new IllegalArgumentException();
		}
        if (sessionUser != null) {
            request.getSession().setAttribute(SESSION_USER, sessionUser);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_USER);
            }
        }
		
	}

	public static SessionPermission getSessionPermission(HttpServletRequest request) {
		
		if(request == null) {
			throw new IllegalArgumentException();
		}
		HttpSession session = request.getSession(false);
		return (SessionPermission)(session != null ? session.getAttribute(SESSION_USER_PERMISSION) : null);
		
	}

	/**
	 * 
	 * @param request
	 * @param sessionPermission
	 */
	public static void setSessionPermission(HttpServletRequest request, SessionPermission sessionPermission) {
		
		if(request == null) {
			throw new IllegalArgumentException();
		}
        if (sessionPermission != null) {
            request.getSession().setAttribute(SESSION_USER_PERMISSION, sessionPermission);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_USER_PERMISSION);
            }
        }
	}
	
	
	/**
	 * 
	 * @param request
	 */
	public static void removeSessionUser(HttpServletRequest request) {
		
		if(request == null) {
			throw new IllegalArgumentException();
		}
		HttpSession session = request.getSession(false);
		if(session == null) {
			return;
		}
		session.removeAttribute(SESSION_USER);
		session.removeAttribute(SESSION_USER_PERMISSION);
	}
	
	public static void invalidate(HttpServletRequest request){
		setSessionUser(request, null);
		setSessionPermission(request, null);
	}
}