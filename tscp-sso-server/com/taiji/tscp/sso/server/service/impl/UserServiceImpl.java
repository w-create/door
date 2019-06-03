package com.taiji.tscp.sso.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.mvc.model.Pagination;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.mvc.model.ResultCode;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.client.StringUtils;
import com.taiji.tscp.sso.server.enums.TrueFalseEnum;
import com.taiji.tscp.sso.server.model.User;
import com.taiji.tscp.sso.server.util.POUtils;

@BeanContainer
public class UserServiceImpl {
	
	
	@Service(name="login")
	public Result login(String ip, String account, String password) throws TscpBaseException {
		Result result = Result.createSuccessResult();
		DBParam param = new DBParam();
		param.addParam(account);
		param.addParam(password);
		Map<String,Object> dataMap = TscpPersistenceUtils.getPersistenceService().queryMapByKey("findByAccount", param);
		if (dataMap == null || dataMap.isEmpty()) {
			result.setCode(ResultCode.ERROR).setMessage("登录名或密码不不正确");
		}
		else if (TrueFalseEnum.FALSE.getValue().equals(dataMap.get("isenable"))) {
			result.setCode(ResultCode.ERROR).setMessage("已被用户禁用");
		}
		else {
			param.clear();
			param.addParam(ip);
			param.addParam(new Date());
			param.addParam((Integer)dataMap.get("logincount") + 1);
			param.addParam(account);
			TscpPersistenceUtils.getPersistenceService().updateDataByKey("updateUserStatus", param);
			result.setData(dataMap);
		}
		return result;
	}
	
	
	@Service(name="getUserById")
	public User getUserById(Integer id) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(id);
		Map<String,Object> dataMap = TscpPersistenceUtils.getPersistenceService().queryMapByKey("getUserById", param);
		User user = POUtils.mapConvertUser(dataMap);
		return user;
		
	}

	@Service(name="enableUser")
	public void enableUser(Boolean isEnable, List<Integer> idList) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam((isEnable == null || !isEnable)?0:1);
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("enableUser", param);
		
	}
	

	@Service(name = "resetPassword")
	public void resetPassword(String password, List<Integer> idList) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(password);
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("resetPassword", param);
	}

	@Service(name="findUserPaginationByAccount")
	public Pagination<User> findUserPaginationByAccount(String account, Integer pageNo,Integer pageSize) throws TscpBaseException {
		
		DBParam param = new DBParam();
		List<Map<String,Object>> result = null;
		Long totalSize = 0L;
		if(StringUtils.isEmpty(account)) {
			param.addParam(pageNo-1);
			param.addParam(pageSize);
			result = TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findUserPaginationByAccountAll", param);
			totalSize = (Long)TscpPersistenceUtils.getPersistenceService().queryMapByKey("findUserPaginationByAccountAllSize",null).get("num");
		}else {
			param.clear();
			param.addParam(account);
			param.addParam(pageNo-1);
			param.addParam(pageSize);
			result = TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findUserPaginationByAccount", param);
			param.clear();
			param.addParam(account);
			totalSize = (Long)TscpPersistenceUtils.getPersistenceService().queryMapByKey("findUserPaginationByAccountSize",param).get("num");
		}
		return POUtils.generatePagination(result,pageNo,pageSize,totalSize == null ? 0 : totalSize.intValue(),User.class);
		
	}
	
	@Service(name="findUserByAccount")
	public User findUserByAccount(String account) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(account);
		param.addParam(0);
		param.addParam(1);
		return POUtils.mapConvertUser(TscpPersistenceUtils.getPersistenceService().queryMapByKey("findUserPaginationByAccount", param));
	}
	
	
	@Service(name="deleteUserByIds")
	public void deleteById(List<Integer> idList) throws TscpBaseException {
		
		if(idList == null || idList.isEmpty()) {
			return;
		}
		DBParam param = new DBParam();
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteUserByIds", param);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteUserRoleByUserId", param);
	}
	

	/**
	 * 
	 * @param id
	 * @param newPassword
	 * @throws TscpBaseException
	 */
	public void updatePassword(Integer id, String newPassword) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(id);
		param.addParam(newPassword);
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("updatePassword", param);
	}
	

	@Service(name="saveUserService")
	public void save(User user, List<Integer> roleIdList) throws TscpBaseException {
		
		if(user.getId() == null) {
			insertUser(user);
		}else {
			updateUser(user);
		}
		
		List<DBParam> userRoleList = new ArrayList<DBParam>();
		DBParam param;
		for (Integer roleId : roleIdList) {
			param = new DBParam();
			param.addParam(user.getId());
			param.addParam(roleId);
			userRoleList.add(param);
		}
		TscpServiceUtil.callService("addUserRole", user.getId(),userRoleList);
	}
	
	
	/**
	 * 
	 * @param user
	 * @throws TscpBaseException
	 */
	private void insertUser(User user) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(user.getAccount());
		param.addParam(user.getPassword());
		param.addParam(user.getLastLoginIp());
		param.addParam(user.getLastLoginTime());
		param.addParam(user.getLoginCount());
		param.addParam(user.getCreateTime());
		param.addParam((user.getIsEnable() == null || !user.getIsEnable()) ? 0 : 1);
		TscpPersistenceUtils.getPersistenceService().insertDataByKey("insertUser", param);
	}
	
	
	/**
	 * 
	 * @param user
	 * @throws TscpBaseException
	 */
	private void updateUser(User user) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(user.getAccount());
		param.addParam(user.getLastLoginIp());
		param.addParam(user.getLastLoginTime());
		param.addParam(user.getLoginCount());
		param.addParam(user.getCreateTime());
		param.addParam((user.getIsEnable() == null || !user.getIsEnable()) ? 0 : 1);
		param.addParam(user.getId());
		TscpPersistenceUtils.getPersistenceService().insertDataByKey("updateUser", param);
	}
	
	
}
