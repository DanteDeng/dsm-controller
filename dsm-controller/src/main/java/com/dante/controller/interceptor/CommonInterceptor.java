package com.dante.controller.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.dante.model.permission.Permission;
import com.dante.biz.permission.service.IPermissionService;
import com.dante.model.user.User;
import com.dante.model.base.CommonResult;

/**
 * @author tfj 2014-8-1
 */
public class CommonInterceptor extends HandlerInterceptorAdapter {
	private final Log log = LogFactory.getLog(CommonInterceptor.class);
	public static final String LAST_PAGE = "com.dante.lastPage";
	/*
	 * 利用正则映射到需要拦截的路径
	 * 
	 * private String mappingURL;
	 * 
	 * public void setMappingURL(String mappingURL) { this.mappingURL =
	 * mappingURL; }
	 * 
	 */
	@Autowired
	private IPermissionService permissionService;

	private User getUserFromSession(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute("user");
		if (obj != null && obj instanceof User) {
			User user = (User) obj;
			return user;
		}
		return null;
	}
	
	private List<Permission> getUserPermissionListFromSession(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute("userPermissionList");
		if (obj != null) {
			@SuppressWarnings("unchecked")
			List<Permission> userPermissionList = (List<Permission>) obj;
			return userPermissionList;
		}
		return null;
	}

	/**
	 * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
	 * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
	 * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		/*if ("GET".equalsIgnoreCase(request.getMethod())) {
			RequestUtil.saveRequest();
		}*/
		//log.info("==============执行顺序: 1、preHandle================");
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestUri.substring(contextPath.length());

		log.info("requestUri:" + requestUri);
		log.info("contextPath:" + contextPath);
		log.info("url:" + url);

		User user = getUserFromSession(request);
		String userName = null;
		boolean hasPermission = false;
		if (user != null) {
			userName = user.getLoginName();
			List<Permission> userPermissionList = getUserPermissionListFromSession(request);
			if(userPermissionList==null){
				userPermissionList = permissionService.getUserPermissionList(user);
				request.getSession().setAttribute("userPermissionList", userPermissionList);
			}
			for (Permission permission : userPermissionList) { //遍历用户权限列表，并校验请求路径是否在用户权限范围以内
				//log.debug("preHandle permission : "+permission);
				if(url.equals(permission.getPermissionUrl())){
					hasPermission = true;
				}
			}
		}
		
		if(!hasPermission){
			log.info("Current logined user is ："+userName+"  the requested url is ："+url+"  no permission");
			CommonResult result = new CommonResult();
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.NO_PERMISSION);
			response.getWriter().write(JSONObject.toJSONString(result));
			return false;
		}else{
			log.info("Current logined user is ："+userName+"  the requested url is ："+url+"   permission owner");
			return true;
		}
	}

	/**
	 * 在业务处理器处理请求执行完成后,生成视图之前执行的动作 可在modelAndView中加入数据，比如当前时间
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		/*log.info("==============执行顺序: 2、postHandle================");
		if (modelAndView != null) { // 加入当前时间
			modelAndView.addObject("var", "测试postHandle");
		}*/
	}

	/**
	 * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
	 * 
	 * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//log.info("==============执行顺序: 3、afterCompletion================");
	}

}