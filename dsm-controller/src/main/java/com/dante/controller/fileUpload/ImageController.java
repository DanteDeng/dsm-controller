package com.dante.controller.fileUpload;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dante.model.fileUpload.Image;
import com.dante.biz.fileUpload.service.IImageService;
import com.dante.controller.BaseController;
import com.dante.common.util.RequestUtil;
import com.dante.model.base.CommonResult;
import com.dante.model.base.PageParam;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("image")
public class ImageController extends BaseController{
	
	@Autowired
	private IImageService imageService;
	
	@RequestMapping("queryImageDetail")
	@ResponseBody
	public CommonResult queryimageDetail(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Image i = RequestUtil.requestToBean(request, Image.class);
		logger.debug("/image/queryImageDetail ------>params : "+i);
		try {
			Image image = imageService.getImageById(i);
			result.setData(image);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
			logger.error("/image/queryImageDetail ------>params : ",e);
		}
		logger.debug("/image/queryImageDetail ------>Exception : "+result);
		return result;
	}
	
	@RequestMapping("queryImageList")
	@ResponseBody
	public CommonResult queryImageList(HttpServletRequest request){
		CommonResult result = new CommonResult();
		PageParam params = new PageParam();
		logger.debug("/image/queryImageList ------>params : "+params);
		try {
			List<Image> imageList = imageService.queryImageList(params);
			result.setData(imageList);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
			logger.error("/image/queryImageList ------>params : ",e);
		}
		logger.debug("/image/queryImageList ------>Exception : "+result);
		return result;
	}
	
	@RequestMapping("pageImageList")
	@ResponseBody
	public CommonResult pageImageList(HttpServletRequest request){
		CommonResult result = new CommonResult();
		String pn = request.getParameter("pageNum");
		String ps = request.getParameter("pageSize");
		int pageNum = 0;
		int pageSize = 0;
		try{
			pageNum = Integer.parseInt(pn);
		}catch(Exception e){}
		try{
			pageSize = Integer.parseInt(ps);
		}catch(Exception e){}
		PageParam params = new PageParam();
		params.setPageNum(pageNum);
		params.setPageSize( pageSize);
		logger.debug("/image/pageimageList ------>params : "+params);
		try {
			PageInfo<Image> page = imageService.pageImageList(params);
			result.setData(page);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/image/pageimageList ------>Exception : ", e);
		}
		logger.debug("/image/pageimageList ------>result : "+result);
		return result;
	}
	
	@RequestMapping("editImage")
	@ResponseBody
	public CommonResult editImage(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Image image = RequestUtil.requestToBean(request, Image.class);
		logger.debug("/Image/editImage -----> Image: "+image);
		try {
			if(image.getImageId()==null||"".equals(image.getImageId())){
				imageService.addImage(image);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.INSERT_SUCCESS);
			}else{
				imageService.updateImage(image);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.UPDATE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("/Image/editImage -----> Exception: ", e);
		}
		logger.debug("/Image/editImage -----> result: "+result);
		return result;
	}
	
	@RequestMapping("deleteImage")
	@ResponseBody
	public CommonResult deleteImage(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Image image = RequestUtil.requestToBean(request, Image.class);
		logger.debug("/Image/deleteImage -----> Image: "+image);
		try {
			if(image.getImageId()==null||"".equals(image.getImageId())){
				result.setCode(CommonResult.RESULT_FAIL);
				result.setMessage(CommonResult.DELETE_NO_VALUE);
			}else{
				imageService.deleteImage(image);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("/Image/deleteImage -----> Exception: ", e);
		}
		logger.debug("/Image/deleteImage -----> result: "+result);
		return result;
	}

}
