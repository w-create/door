package com.taiji.tscp.sso.server.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.sso.client.ApplicationPermission;
import com.taiji.tscp.sso.client.SessionPermission;
import com.taiji.tscp.sso.client.SessionUtils;

public class AdminMenuAction extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SessionPermission sessionPermission = SessionUtils.getSessionPermission(request);
		// 如果配置的权限拦截器，则获取登录用户权限下的菜单，没有权限拦截限制的情况下，获取当前系统菜单呈现
		
		response.getOutputStream().write(JSONObject.toJSONString(Result.createSuccessResult().setData(sessionPermission == null ? ApplicationPermission.getApplicationMenuList() : sessionPermission
						.getMenuList())).toString().getBytes("UTF-8"));
		response.getOutputStream().close();
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}
	
	
	
	

}
