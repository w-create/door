package com.taiji.tscp.sso.server.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.mvc.config.ConfigUtils;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.mvc.model.ResultCode;
import com.taiji.tscp.sso.client.SsoFilter;
import com.taiji.tscp.sso.server.captcha.CaptchaHelper;
import com.taiji.tscp.sso.server.common.LocalTokenManager;
import com.taiji.tscp.sso.server.common.LoginUser;
import com.taiji.tscp.sso.server.common.RedisTokenManager;
import com.taiji.tscp.sso.server.common.TokenManager;
import com.taiji.tscp.sso.server.provider.IdProvider;
import com.taiji.tscp.sso.server.provider.PasswordProvider;
import com.taiji.tscp.sso.server.util.CookieUtils;

import io.swagger.annotations.Api;

/**
 * @author Joe
 */
@Api(tags = "单点登录管理")
public class LoginAction extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
		
	private TokenManager tokenManager;
	
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		
		String token = CookieUtils.getCookie(request, TokenManager.TOKEN);
		if (StringUtils.isNotEmpty(token) && tokenManager.validate(token) != null && request.getParameter("backUrl")!= null) {
		    
			resp.sendRedirect(authBackUrl((String)request.getParameter("backUrl"), token));
		}
		else {
			goLoginPath((String)request.getParameter("backUrl"), request);
			request.getRequestDispatcher("/login.jsp").forward(request, resp);
		}
		
		
	}
	
	
	private String authBackUrl(String backUrl, String token) {
		StringBuilder sbf = new StringBuilder(backUrl);
		if (backUrl.indexOf("?") > 0) {
			sbf.append("&");
		}
		else {
			sbf.append("?");
		}
		sbf.append(SsoFilter.SSO_TOKEN_NAME).append("=").append(token);
		return sbf.toString();
	}
	
	
	private String goLoginPath(String backUrl, HttpServletRequest request) {
		if(backUrl == null || "".equals(backUrl)) {
			return "/login";
		}
		request.setAttribute("backUrl", backUrl);
		return "/login";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String backUrl = request.getParameter("backUrl");
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String captcha = request.getParameter("captcha");
		try {
		if(StringUtils.isEmpty(backUrl) || StringUtils.isEmpty(account) || 
				StringUtils.isEmpty(password)|| StringUtils.isEmpty(captcha)) {
			goLoginPath(backUrl, request);
			response.sendRedirect("http://"+request.getServerName()+":"+request.getServerPort()+"/"+request.getContextPath()+"/admin/admin");
			return;
		}
		
		
		if (!CaptchaHelper.validate(request, captcha)) {
			request.setAttribute("errorMessage", "验证码不正确");
			goLoginPath(backUrl, request);
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
		Result result = (Result) TscpServiceUtil.callService("login", getIpAddr(request),account,PasswordProvider.encrypt(password));
		if (!result.getCode().equals(ResultCode.SUCCESS)) {
			request.setAttribute("errorMessage", result.getMessage());
			goLoginPath(backUrl, request);
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
		else {
			Map<String,Object> user = (Map<String,Object>) result.getData();
			LoginUser loginUser = new LoginUser((Integer)user.get("id"), (String)user.get("account"));
			String token = CookieUtils.getCookie(request, TokenManager.TOKEN);
			if (StringUtils.isBlank(token) || tokenManager.validate(token) == null) {// 没有登录的情况
				token = createToken(loginUser);
				addTokenInCookie(token, request, response);
			}

			// 跳转到原请求
			backUrl = URLDecoder.decode(backUrl, "utf-8");
			response.sendRedirect(authBackUrl(backUrl, token));
			TscpServiceUtil.callService("recordSystemLog", account,new StringBuilder("[SSO-人员登录成功]").append("user:[").append(account).
					append("],url:[").append(backUrl).append("]").toString());
		}
		}catch(Exception e) {
			try {
				TscpServiceUtil.callService("", account,new StringBuilder("[SSO-人员登录失败]").append("user:[").append(account).
						append("],url:[").append(backUrl).append("]").toString());
			} catch (TscpBaseException e1) {
				logger.error("记录日志失败",e);
			}
			logger.error("用户登录失败 account：{},password:{},url:{}",account,password,backUrl,e);
		}
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
	
	
	@Override
	public void init() throws ServletException {
	
	    try {
			ConfigUtils.processProperties(this.getClass().getResourceAsStream("/config.properties"));
			if("local".equals(ConfigUtils.getProperty("tokenManager.type")) || ConfigUtils.getProperty("tokenManager.type") == null){
			   tokenManager = LocalTokenManager.getInstance();
			}else if("redis".equals(ConfigUtils.getProperty("tokenManager.type"))){
			    tokenManager = RedisTokenManager.getInstance();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	protected String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			}
			else {
				return ip;
			}
		}
		else {
			return request.getRemoteAddr();
		}
	}
	
	
	
	private String createToken(LoginUser loginUser) {
		// 生成token
		String token = IdProvider.createUUIDId();

		// 缓存中添加token对应User
		tokenManager.addToken(token, loginUser);
		return token;
	}
	
	
	
}