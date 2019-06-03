package com.taiji.tscp.sso.server.controller;

import java.io.IOException;

import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.api.kernel.IReqData;
import com.taiji.tscp.api.kernel.IResData;
import com.taiji.tscp.common.annotation.Action;
import com.taiji.tscp.mvc.util.StringUtils;
import com.taiji.tscp.platform.web.event.TscpRes;
import com.taiji.tscp.sso.client.SessionUtils;
import com.taiji.tscp.sso.server.common.TokenManager;
import com.taiji.tscp.sso.server.util.CookieUtils;
import com.taiji.tscp.sso.server.util.TokenManagerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "单点登出")
@Action
public class LogoutAction {
	

	@ApiOperation("登出")
	public IResData logout(IReqData req) throws IOException, TscpBaseException {
		
		IResData res = new TscpRes();
		String backUrl = (String) req.getAttr("backUrl");
		String token = CookieUtils.getCookie(req.getHttpReq(), TokenManager.TOKEN);
		if (StringUtils.isNotBlank(token)) {
			TokenManagerFactory.getTokenManager().remove(token);
		}
		SessionUtils.invalidate(req.getHttpReq());
		if(StringUtils.isBlank(backUrl)) {
			req.getHttpRes().sendRedirect("http://"+req.getHttpReq().getServerName()+":"+req.getHttpReq().getServerPort()+"/"+req.getHttpReq().getContextPath()+"/admin/admin");
		}else {				req.getHttpRes().sendRedirect(backUrl);
		};
		return res;
		
	}
}