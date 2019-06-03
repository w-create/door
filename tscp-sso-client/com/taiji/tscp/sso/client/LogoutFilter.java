package com.taiji.tscp.sso.client;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.taiji.tscp.sso.server.common.TokenManager;
import com.taiji.tscp.sso.server.util.TokenManagerFactory;


/**
 * 单点退出Filter
 * 
 * @author Joe
 */
public class LogoutFilter extends ClientFilter {

	// 单点退出成功后跳转页(配置当前应用上下文相对路径，不设置或为空表示项目根目录)
	private String ssoBackUrl;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.pattern = this.pattern == null ? filterConfig.getInitParameter("pattern") : this.pattern;
		this.ssoBackUrl = this.ssoBackUrl == null ? filterConfig.getInitParameter("ssoBackUrl") : this.ssoBackUrl;
		if (StringUtils.isEmpty(pattern)) {
			throw new IllegalArgumentException("pattern不能为空");
		}
		if (StringUtils.isEmpty(ssoBackUrl)) {
			throw new IllegalArgumentException("ssoBackUrl不能为空");
		}
	}

	public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		SessionUtils.invalidate(request);
		String logoutUrl = null;
		String token = CookieUtils.getCookie(request, TokenManager.TOKEN);
		if (token != null && !"".equals(token) && TscpContainer.isServer ) {
			TokenManagerFactory.getTokenManager().remove(token);
			logoutUrl = new StringBuilder().append(ssoServerUrl)
					.append("/logout?backUrl=").append(new StringBuffer().append(getLocalUrl(request)).append(ssoBackUrl).toString()).toString();
		}else {
			if( token != null && !"".equals(token) && !authenticationRpcService.remove(token)){
				throw new RuntimeException("logout failed");
			}
			logoutUrl = new StringBuilder().append(ssoServerUrl)
					.append("/login?backUrl=").append(URLEncoder.encode(getLocalUrl(request), "utf-8")).toString();
		}
		

		response.sendRedirect(logoutUrl);
		return false;
	}
	
	

	/**
	 * 获取当前上下文路径
	 * 
	 * @param request
	 * @return
	 */
	private String getLocalUrl(HttpServletRequest request) {
		return new StringBuilder().append(request.getScheme()).append("://").append(request.getServerName()).append(":")
				.append(request.getServerPort() == 80 ? "" : request.getServerPort()).append(request.getContextPath()).append(ssoBackUrl)
				.toString();
	}

	public void setSsoBackUrl(String ssoBackUrl) {
		this.ssoBackUrl = ssoBackUrl;
	}
}