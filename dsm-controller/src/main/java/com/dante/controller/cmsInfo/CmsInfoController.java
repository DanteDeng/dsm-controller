package com.dante.controller.cmsInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dante.biz.cmsInfo.service.ICmsInfoService;
import com.dante.common.util.RequestUtil;
import com.dante.controller.BaseController;
import com.dante.model.base.CommonResult;
import com.dante.model.cmsInfo.CmsInfo;
import com.dante.model.cmsInfo.CmsInfoParam;
import com.github.pagehelper.PageInfo;
@Controller
@RequestMapping("cmsInfo")
public class CmsInfoController extends BaseController {
	
	@Autowired
	private ICmsInfoService cmsInfoService;
	
	@RequestMapping("queryCmsInfoList")
	@ResponseBody
	public CommonResult queryCmsInfoList(HttpServletRequest request){
		//获得入参
		CmsInfoParam param = RequestUtil.request2Bean(request, CmsInfoParam.class);
		logger.debug("[cmsInfo-----queryCmsInfoList-----param]"+param);
		CommonResult result = new CommonResult();
		try{
			List<CmsInfo> cmsInfoList = cmsInfoService.queryCmsInfoList(param);
			result.setData(cmsInfoList);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[cmsInfo-----queryCmsInfoList-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[cmsInfo-----queryCmsInfoList-----result]"+result);
		return result;
	}
	
	@RequestMapping("queryCmsInfoDetail")
	@ResponseBody
	public CommonResult queryCmsInfoDetail(HttpServletRequest request){
		//获得入参
		CmsInfo cmsInfo = RequestUtil.request2Bean(request, CmsInfo.class);
		logger.debug("[cmsInfo-----queryCmsInfoDetail-----param]"+cmsInfo);
		CommonResult result = new CommonResult();
		try{
			CmsInfo record = cmsInfoService.getCmsInfoById(cmsInfo);
			result.setData(record);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[cmsInfo-----queryCmsInfoDetail-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[cmsInfo-----queryCmsInfoDetail-----result]"+result);
		return result;
	}
	
	@RequestMapping("pageCmsInfoList")
	@ResponseBody
	public CommonResult pageCmsInfoList(HttpServletRequest request){
		//获得入参
		CmsInfoParam param = RequestUtil.request2Bean(request, CmsInfoParam.class);
		logger.debug("[cmsInfo-----pageCmsInfoList-----param]"+param);
		CommonResult result = new CommonResult();
		try{
			PageInfo<CmsInfo> cmsInfoList = cmsInfoService.pageCmsInfoList(param);
			result.setData(cmsInfoList);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[cmsInfo-----pageCmsInfoList-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[cmsInfo-----pageCmsInfoList-----result]"+result);
		return result;
	}
	
	@RequestMapping("saveCmsInfo")
	@ResponseBody
	public CommonResult saveCmsInfo(HttpServletRequest request){
		//获得入参
		CmsInfo cmsInfo = RequestUtil.request2Bean(request, CmsInfo.class);
		logger.debug("[cmsInfo-----saveCmsInfo-----param]"+cmsInfo);
		CommonResult result = new CommonResult();
		try{
			result = cmsInfoService.saveCmsInfo(cmsInfo);
		}catch(Exception e){
			logger.error("[cmsInfo-----saveCmsInfo-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[cmsInfo-----saveCmsInfo-----result]"+result);
		return result;
	}
	
	@RequestMapping("deleteCmsInfo")
	@ResponseBody
	public CommonResult deleteCmsInfo(HttpServletRequest request){
		//获得入参
		CmsInfo cmsInfo = RequestUtil.request2Bean(request, CmsInfo.class);
		logger.debug("[cmsInfo-----deleteCmsInfo-----param]"+cmsInfo);
		CommonResult result = new CommonResult();
		try{
			int deleteNum = cmsInfoService.deleteCmsInfo(cmsInfo);
			if(deleteNum>0){
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.DELETE_SUCCESS);
			}else{
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.DELETE_FAIL);
			}
		}catch(Exception e){
			logger.error("[cmsInfo-----deleteCmsInfo-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[cmsInfo-----deleteCmsInfo-----result]"+result);
		return result;
	}
	
}
