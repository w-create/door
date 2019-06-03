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
import com.taiji.tscp.sso.client.SessionUser;
import com.taiji.tscp.sso.client.SessionUtils;

public class AdminInitPageAction extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		SessionUser sessionUser = SessionUtils.getSessionUser(request);
		// 设置登录用户名
		request.setAttribute("userName", sessionUser.getAccount());
		SessionPermission sessionPermission = SessionUtils.getSessionPermission(request);
		// 设置当前登录用户没有的权限，以便控制前台按钮的显示或者隐藏
		request.setAttribute("sessionUserNoPermissions",
				sessionPermission == null ? null : sessionPermission.getNoPermissions());
		// 默认首页
		// model.addAttribute("defaultPage", null);
		request.getRequestDispatcher("/admin.jsp").forward(request, response);
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		
		SessionPermission sessionPermission = SessionUtils.getSessionPermission(request);
		// 如果配置的权限拦截器，则获取登录用户权限下的菜单，没有权限拦截限制的情况下，获取当前系统菜单呈现
		resp.getOutputStream().write(JSONObject.toJSONString(Result.createSuccessResult().setData(
				sessionPermission == null ? ApplicationPermission.getApplicationMenuList() : sessionPermission
						.getMenuList())).getBytes("UTF-8"));
		resp.getOutputStream().flush();
	}
	
	
	
	

}
