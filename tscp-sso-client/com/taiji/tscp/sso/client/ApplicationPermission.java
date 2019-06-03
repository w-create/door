package com.taiji.tscp.sso.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.taiji.tscp.sso.rpc.AuthenticationRpcService;

/**
 * 当前应用所有权限
 * 
 * @author Joe
 */
public class ApplicationPermission {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationPermission.class);

	// 应用所有权限URL
	private static Set<String> applicationPermissionSet = null;
	// 应用所有菜单
	private static List<Map<String,Object>> applicationMenuList = null;
	// 并发监控
	private static Object monitor = new Object();

	/**
	 * 1.应用初始化，获取应用所有的菜单及权限 
	 * 2.权限有变动修改，JMS通知重新加载
	 */
	public static void initApplicationPermissions(AuthenticationRpcService authenticationRpcService,
			String ssoAppCode) {
	//	List<RpcPermission> dbList = null;
		List<Map<String,Object>> dbList = null;
		try {
			dbList = authenticationRpcService.findPermissionList(null, ssoAppCode);
		}
		catch (Exception e) {
			dbList = new ArrayList<Map<String,Object>>(0);
			logger.error("无法连接到单点登录服务端,请检查配置sso.server.url", e);
		}

		synchronized (monitor) {
			applicationMenuList = new ArrayList<Map<String,Object>>();
			applicationPermissionSet = new HashSet<String>();
			for (Map<String,Object> menu : dbList) {
				if (menu.get("isMenu") != null && (Boolean)menu.get("isMenu")) {
					applicationMenuList.add(menu);
				}
				if (StringUtils.isNotEmpty((String)menu.get("url"))) {
					applicationPermissionSet.add((String)menu.get("url"));
				}
			}
		}
	}

	public static Set<String> getApplicationPermissionSet() {
		synchronized (monitor) {
			return applicationPermissionSet;
		}
	}

	public static List<Map<String,Object>> getApplicationMenuList() {
		synchronized (monitor) {
			return applicationMenuList;
		}
	}
}
