package com.taiji.tscp.sso.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.caucho.hessian.client.HessianProxyFactory;
import com.taiji.tscp.sso.rpc.AuthenticationRpcService;
import com.taiji.tscp.sso.server.common.LocalTokenManager;
import com.taiji.tscp.sso.server.common.RedisTokenManager;
import com.taiji.tscp.sso.server.service.impl.AuthenticationRpcServiceImpl;

/**
 * Smart容器中心
 * 
 * @author Joe
 */
public class TscpContainer extends ParamFilter implements Filter {
	
	// 是否服务端，默认为false
	public static boolean isServer = false;

	private ClientFilter[] filters;

	private AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		/*
		 * try { TscpPlatformatManager.main(new String[] {}); } catch (TscpBaseException
		 * e1) { e1.printStackTrace(); }
		 */
		Properties properties = loadProperties();
		isServer = properties.getProperty("isServer")!= null && !"".equals(properties.getProperty("isServer"))?Boolean.parseBoolean(properties.getProperty("isServer")) : false;
		if((properties!= null && properties.getProperty("isServer")!= null && !"".equals(properties.getProperty("isServer")))?Boolean.parseBoolean(properties.getProperty("isServer")):isServer) {
			ssoServerUrl = filterConfig.getServletContext().getContextPath();
			authenticationRpcService = new AuthenticationRpcServiceImpl();
			if("local".equals(properties.get("tokenManager.type"))) {
				((AuthenticationRpcServiceImpl)authenticationRpcService).setTokenManager(LocalTokenManager.getInstance());
			}else if("redis".equals(properties.get("tokenManager.type"))){
				((AuthenticationRpcServiceImpl)authenticationRpcService).setTokenManager(RedisTokenManager.getInstance());
			}else {
				throw new IllegalArgumentException("unknown tokenManager type");
			}
		}else {
			ssoServerUrl = properties.getProperty("sso.server.url");
		}
		
		if (ssoServerUrl == null || "".equals(ssoServerUrl)) {
			throw new IllegalArgumentException("ssoServerUrl不能为空");
		}

		if (authenticationRpcService == null) {
			try {
				authenticationRpcService = (AuthenticationRpcService) new HessianProxyFactory()
						.create(AuthenticationRpcService.class, ssoServerUrl + "/rpc/authenticationRpcService");
			}
			catch (MalformedURLException e) {
				new IllegalArgumentException("authenticationRpcService初始化失败");
			}
		}
		
		

		if (filters == null || filters.length == 0) {
			initFilter(filterConfig,properties);
		}
		for (ClientFilter filter : filters) {
			filter.setSsoServerUrl(ssoServerUrl);
			filter.setAuthenticationRpcService(authenticationRpcService);

			filter.init(filterConfig);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		for (ClientFilter filter : filters) {
			if (matchPath(filter.getPattern(), httpRequest.getServletPath())
					&& !filter.isAccessAllowed(httpRequest, httpResponse)) {
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean matchPath(String pattern, String path) {
		return pattern == null || "".equals(pattern) || pathMatcher.match(pattern, path);
	}
	

	@Override
	public void destroy() {
		if (filters == null || filters.length == 0)
			return;
		for (ClientFilter filter : filters) {
			filter.destroy();
		}
	}

	public void setFilters(ClientFilter[] filters) {
		this.filters = filters;
	}
	
	private void initFilter(FilterConfig filterConfig,Properties properties) {
		filters = new ClientFilter[3];
		filters[0] = new SsoFilter();
		LogoutFilter logout = new LogoutFilter();
		logout.setPattern(properties.getProperty("pattern"));
		logout.setSsoBackUrl(properties.getProperty("backUrl"));
		filters[1] = logout;
		PermissionFilter permission = new PermissionFilter();	
		permission.setSsoAppCode(properties.getProperty("ssoAppCode"));
		filters[2] = permission;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private Properties loadProperties() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getResourceAsStream("/config.properties"));
			return properties;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}