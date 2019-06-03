package com.taiji.tscp.sso.client;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taiji.tscp.sso.rpc.RpcUser;
import com.taiji.tscp.sso.server.common.TokenManager;

/**
 * 单点登录及Token验证Filter
 * 
 * @author zhengchenglei
 */
public class SsoFilter extends ClientFilter {

	// sso授权回调参数token名称
	public static final String SSO_TOKEN_NAME = "__vt_param__";

	@Override
	public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = getLocalToken(request);
		boolean isNeedToken = true;
		if (token == null) {
			token = request.getParameter(SSO_TOKEN_NAME);
			if (token != null) {
				if(invokeAuthInfoInSession(request, token,response)) {
					if(TscpContainer.isServer) {
						return true;
					}
					response.sendRedirect(getRemoveTokenBackUrl(request));
					return false;
				}else {
					isNeedToken = false;
				}
			}
		}
		else if (authenticationRpcService.validate(token)) {// 验证token是否有效
			return true;
		}
		redirectLogin(request, response,isNeedToken);
		return false;
	}

	/**
	 * 获取Session中token
	 * 
	 * @param request
	 * @return
	 */
	private String getLocalToken(HttpServletRequest request) {
		SessionUser sessionUser = SessionUtils.getSessionUser(request);
		return sessionUser == null ? null : sessionUser.getToken();
	}

	/**
	 * 存储sessionUser
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private boolean invokeAuthInfoInSession(HttpServletRequest request, String token, HttpServletResponse response) throws IOException {
		RpcUser rpcUser = authenticationRpcService.findAuthInfo(token);
		if (rpcUser != null) {
			SessionUtils.setSessionUser(request, new SessionUser(token, rpcUser.getAccount()));
			addTokenInCookie(token,request,response);
			return true;
		}
		return false;
	}
	
	
	private void addTokenInCookie(String token, HttpServletRequest request, HttpServletResponse response) {
		// Cookie添加token
		Cookie cookie = new Cookie(TokenManager.TOKEN, token);
		cookie.setPath("/");
		if ("https".equals(request.getScheme())) {
			cookie.setSecure(true);
		}
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

	/**
	 * 跳转登录
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void redirectLogin(HttpServletRequest request, HttpServletResponse response,boolean isNeedToken) throws IOException {
		if (isAjaxRequest(request)) {
			responseJson(response, SsoResultCode.SSO_TOKEN_ERROR, "未登录或已超时");
		}
		else {
			SessionUtils.invalidate(request);

			String ssoLoginUrl = new StringBuilder().append(ssoServerUrl)
					.append("/login?backUrl=").append(URLEncoder.encode(getBackUrl(request,isNeedToken), "utf-8")).toString();

			response.sendRedirect(ssoLoginUrl);
		}
	}

	/**
	 * 去除返回地址中的token参数
	 * @param request
	 * @return
	 */
	private String getRemoveTokenBackUrl(HttpServletRequest request) {
		String backUrl = getBackUrl(request,true);
		return backUrl.substring(0, backUrl.indexOf(SSO_TOKEN_NAME) - 1);
	}

	/**
	 * 返回地址
	 * @param request
	 * @return
	 */
	private String getBackUrl(HttpServletRequest request,boolean isNeedToken) {
		return new StringBuilder().append(request.getRequestURL())
				.append((!isNeedToken || request.getQueryString() == null) ? "" : "?" + request.getQueryString()).toString();
	}
}