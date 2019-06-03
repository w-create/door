package com.taiji.tscp.sso.server.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;

@BeanContainer
public class SysLogServiceImpl {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@Service(name="recordSystemLog")
	public void recordSystemLog(String user,String action) throws TscpBaseException {
		
		try {
			Date date = new Date();
			DBParam param = new DBParam();
			param.addParam(user);
			param.addParam(action);
			param.addParam(date);
			param.addParam(date);
			TscpPersistenceUtils.getPersistenceService().insertDataByKey("insertSysLog", param);
		}catch(Exception e) {
			//吃掉异常，日志记录不影响正常业务
			logger.error("记录日志信息失败 user:{},action:{}",user,action,e);
		}
	}
	
	

}
