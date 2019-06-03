package com.taiji.tscp.sso.server.controller.admin;

import java.util.ArrayList;
import java.util.List;


import com.taiji.tscp.api.kernel.IReqData;
import com.taiji.tscp.api.kernel.IResData;
import com.taiji.tscp.common.annotation.Action;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.mvc.model.ResultCode;
import com.taiji.tscp.platform.web.event.TscpRes;
import com.taiji.tscp.sso.client.SessionUtils;
import com.taiji.tscp.sso.client.StringUtils;
import com.taiji.tscp.sso.server.common.LoginUser;
import com.taiji.tscp.sso.server.controller.common.BaseController;
import com.taiji.tscp.sso.server.provider.PasswordProvider;
import com.taiji.tscp.sso.server.util.TokenManagerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "个人中心")
@Action
public class ProfileAction extends BaseController {


	@ApiOperation("初始页")
	public IResData profile(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			LoginUser loginUser = TokenManagerFactory.getTokenManager().validate(SessionUtils.getSessionUser(req.getHttpReq()).getToken());
			if (loginUser != null) {
				req.getHttpReq().setAttribute("user", TscpServiceUtil.callService("getUserById", loginUser.getUserId()));
			}
			req.getHttpReq().getRequestDispatcher("/admin/profile.jsp").forward(req.getHttpReq(), req.getHttpRes());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			return res;
		}
	}

	@ApiOperation("修改密码提交")
	public IResData savePassword(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			String newPassword = (String)req.getAttr("newPassword");
			String confirmPassword = (String)req.getAttr("confirmPassword");
			if(StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(confirmPassword) || !newPassword.equals(confirmPassword)) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR).setMessage("修改失败"));
				return res;
			}
			LoginUser loginUser = TokenManagerFactory.getTokenManager().validate(SessionUtils.getSessionUser(req.getHttpReq()).getToken());
			if (loginUser != null) {
				List<Integer> userIds = new ArrayList<Integer>();
				userIds.add(loginUser.getUserId());
				TscpServiceUtil.callService("resetPassword",PasswordProvider.encrypt(newPassword), userIds);
				res.addJsonObjectOutWrite(Result.createSuccessResult().setMessage("修改成功"));
			}else {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR).setMessage("修改失败"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR).setMessage("修改失败"));
		}
		return res;
	}
}