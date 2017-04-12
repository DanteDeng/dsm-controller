package com.dante.controller.ueditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dante.biz.ueditor.service.IUeditorService;
import com.dante.controller.BaseController;

@Controller
@RequestMapping("ueditor")
public class UeditorController extends BaseController{
	
	@Autowired
	private IUeditorService ueditorService;
	
	@RequestMapping("executeRequest")
	public void executeRequest(HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.info("ueditor---------executeRequest---------start: ");
		request.setCharacterEncoding( "utf-8" );
		response.setHeader("Content-Type" , "text/html");
		response.getWriter().write( ueditorService.executeRequest(request) );
	}
	
}
