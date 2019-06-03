package com.taiji.tscp.sso.server.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.taiji.tscp.api.kernel.IReqData;
import com.taiji.tscp.api.kernel.IResData;
import com.taiji.tscp.common.annotation.Action;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.platform.web.event.TscpRes;
import com.taiji.tscp.sso.client.ApplicationPermission;
import com.taiji.tscp.sso.client.SessionPermission;
import com.taiji.tscp.sso.client.SessionUtils;

@Action
public class AdminAction {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public IResData menu(IReqData req) {
		
		try {
		SessionPermission sessionPermission = SessionUtils.getSessionPermission(req.getHttpReq());
		IResData res = new TscpRes();
		res.addJsonStringOutWrite(JSONObject.toJSONString(Result.createSuccessResult().setData(
				sessionPermission == null ? ApplicationPermission.getApplicationMenuList() : sessionPermission.getMenuList())));
		return res;
		}catch(Exception e) {
			e.printStackTrace();;
			logger.error("调用异常",e);
			return null;
		}
	}
	
	

}
