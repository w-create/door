package com.taiji.tscp.sso.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.taiji.tscp.mvc.model.Pagination;
import com.taiji.tscp.sso.server.model.App;
import com.taiji.tscp.sso.server.model.Permission;
import com.taiji.tscp.sso.server.model.Role;
import com.taiji.tscp.sso.server.model.RolePermission;
import com.taiji.tscp.sso.server.model.User;
import com.taiji.tscp.sso.server.model.UserRole;

public class POUtils {
	
	
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static User mapConvertUser(Map<String,Object> map) {
	
		if(map == null || map.isEmpty()) {
			return null;
		}
		
		//user.setCreateTime((map.get("account") instanceof String ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String)map.get("account")):(Date)map.get("account")));
		User user = new User();
		user.setAccount((String)map.get("account"));
		user.setCreateTime((Date)map.get("createtime"));
		user.setLoginCount((Integer)map.get("logincount"));
		user.setId((Integer)map.get("id"));
		user.setIsEnable((map.get("isenable") == null || (Integer)map.get("isenable") == 0) ? false : true);
		user.setLastLoginIp((String)map.get("lastloginip"));
		user.setLastLoginTime((Date)map.get("lastlogintime"));
		user.setPassword((String)map.get("password"));
		return user;

	}
	
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static UserRole mapConvertUserRole(Map<String,Object> map) {
		
		if(map == null || map.isEmpty()) {
			return null;
		}
		UserRole userRole = new UserRole();
		userRole.setId((Integer)map.get("id"));
		userRole.setRoleId((Integer)map.get("roleid"));
		userRole.setUserId((Integer)map.get("userid"));
		return userRole;
	}
	
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static Role mapConvertRole(Map<String,Object> map) {
		
		if(map == null || map.isEmpty()) {
			return null;
		}
		
		Role role = new Role();
		role.setId((Integer)map.get("id"));
		role.setName((String)map.get("name"));
		role.setSort((Integer)map.get("sort"));
		role.setIsEnable((map.get("isenable") == null || (Integer)map.get("isenable") == 0) ? false : true);
		return role;
	}
	
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static App mapConvertApp(Map<String,Object> map) {
		
		if(map == null || map.isEmpty()) {
			return null;
		}
		App app = new App();
		app.setId((Integer)map.get("id"));
		app.setName((String)map.get("name"));
		app.setCode((String)map.get("code"));
		app.setCreateTime((Date)map.get("createtime"));
		app.setSort((Integer)map.get("setSort"));
		app.setIsEnable((map.get("isenable") == null || (Integer)map.get("isenable") == 0)?false:true);
		return app;
	}
	
	public static Permission mapConvertPermission(Map<String,Object> map) {
		
		if(map == null || map.isEmpty()) {
			return null;
		}
		Permission permission = new Permission();
		permission.setId((Integer)map.get("id"));
		permission.setName((String)map.get("name"));
		permission.setAppId((Integer)map.get("appid"));
		permission.setSort((Integer)map.get("sort"));
		permission.setUrl((String)map.get("url"));
		permission.setIcon((String)map.get("icon"));
		permission.setIsMenu((map.get("ismenu") == null || (Integer)map.get("ismenu") == 0)?false:true);
		permission.setIsEnable((map.get("isenable") == null || (Integer)map.get("isenable") == 0)?false:true);
		permission.setParentId((Integer)map.get("parentid"));
		return permission;
	}
	
	
	public static RolePermission mapConvertRolePermission(Map<String,Object> map) {
		
		if(map == null || map.isEmpty()) {
			return null;
		}
		RolePermission rp = new RolePermission();
		rp.setId((Integer)map.get("id"));
		rp.setAppId((Integer)map.get("appid"));
		rp.setPermissionId((Integer)map.get("permissionid"));
		rp.setRoleId((Integer)map.get("roleid"));
		return rp;
	}
	
	
	
	/**
	 * 
	 * @param lisMap
	 * @param pageNo
	 * @param pageSize
	 * @param totalSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T>List<T> generateList(List<Map<String,Object>> lisMap,Class<T> clazz){
		
		if(lisMap == null || lisMap.isEmpty()) {
			return null;
		}

		List<T> list = new ArrayList<T>();
		for(Map<String,Object> map : lisMap) {
			T po = null;
			if(User.class.getName().equals(clazz.getName())) {
				po = (T) POUtils.mapConvertUser(map);
			}else if(Role.class.getName().equals(clazz.getName())) {
				po = (T) POUtils.mapConvertRole(map);
			}else if(App.class.getName().equals(clazz.getName())){
				po = (T) POUtils.mapConvertApp(map);
			}else if(Permission.class.getName().equals(clazz.getName())){
				po = (T) POUtils.mapConvertPermission(map);
			}else if(RolePermission.class.getName().equals(clazz.getName())){
				po = (T) POUtils.mapConvertRolePermission(map);
			}else{
				throw new IllegalArgumentException("unkown class type");
			}
			list.add(po);
		}
		return list;
	}
	
	
	
	/**
	 * 
	 * @param lisMap
	 * @param pageNo
	 * @param pageSize
	 * @param totalSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T>Pagination<T> generatePagination(List<Map<String,Object>> lisMap,int pageNo,int pageSize,int totalSize,Class<T> clazz){
		
		Pagination<T> pagination = new Pagination<T>();
		if(lisMap == null || lisMap.isEmpty()) {
			return pagination;
		}

		List<T> list = new ArrayList<T>();
		for(Map<String,Object> map : lisMap) {
			T po = null;
			if(User.class.getName().equals(clazz.getName())) {
				po = (T) POUtils.mapConvertUser(map);
			}else if(Role.class.getName().equals(clazz.getName())) {
				po = (T) POUtils.mapConvertRole(map);
			}else if(App.class.getName().equals(clazz.getName())){
				po = (T) POUtils.mapConvertApp(map);
			}else {
				throw new IllegalArgumentException("unkown class type");
			}
			list.add(po);
		}
		
		if(list != null && list.size() > 0) {
			pagination.setList(list);
		}
		pagination.setPageNo(pageNo);
		pagination.setPageSize(pageSize);
		pagination.setRowCount(totalSize);
		return pagination;
		
	}
	
	

}
