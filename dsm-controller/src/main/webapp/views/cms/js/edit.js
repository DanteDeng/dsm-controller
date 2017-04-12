var croperId = 'imageCroper';
var croperInitFlag = false;

var saveUrl = "/dsm-controller/cmsInfo/saveCmsInfo";

$(function() {
	includeCroper('croperContainer',croperId);
	// initCss();
	UE.getEditor('editor', {
		initialFrameHeight : '360',
		initialFrameWidth : '60%',
		scaleEnabled:true
	});
	$("#saveBtn").attr("onclick","javascript:save();");
	$("#backBtn").attr("onclick","javascript:back();");
	$("#cmsImage").attr({"width":"30","height":"auto"});
})

function initCss() {
	$(".block").css("border", "solid blue 2px");
	var topHeight = $(".navbar-fixed-top")[0].clientHeight;// +$(".navbar-fixed-top").css("margin-top");
	var bottomHeight = $(".navbar-fixed-bottom")[0].clientHeight;// +$(".navbar-fixed-bottom").css("margin-bottom");
	var windowHeight = document.body.scrollHeight;
	$(".block")[0].clientHeight = windowHeight - topHeight - windowHeight;
	var blockHeight = windowHeight - topHeight - bottomHeight;
	alert("windowHeight" + windowHeight + "blockHeight" + blockHeight);
	$(".block").css("height", blockHeight);
	$(".block").css("overflow", "scroll");
}

function includeCroper(containerId,croperId) {
	var height = window.innerHeight;
	var width = window.innerWidth;
	var iframeSrc = '../croper/croper.html';
	var iframe = '<iframe id="'+croperId+'" src=' + iframeSrc + '></iframe>'
	$("#" + containerId).html(iframe);
	$("#"+croperId).attr("width", "80%");
	$("#"+croperId).attr("height", window.innerHeight * 0.76);
	$("#"+croperId).css({
		"border" : "0"
	});
}

//完成截取点击事件
$("#finishCrop").click(function(e){
	var canvasInfo = $("#"+croperId)[0].contentWindow.getCanvasInfo();
	$("#cmsImage").remove();
	var image = '<img id="cmsImage" src="'+canvasInfo+'"/>';
	$("#editImageBtn").before('');
	$("#editImageBtn").before(image);
	$("#cmsImage").attr("width","40");
	$("#cmsImage").attr("height","auto");
	$("#cmsImage").attr("margin-right","10px");
})

//初始化
$("#editImageBtn").click(function(){
	if(croperInitFlag){
		return;
	}
	croperInitFlag = true;
	initCroperInIframe();
})

function initCroperInIframe(){
	//初始化iframe中的croper
	var croperData = {
			options :{"resizable":false,"movable":false,preview: '.img-preview'},
			aspestRatio:4/3
	}
	$("#"+croperId)[0].contentWindow.initCroper(croperData);
}

/**
 * 以下为与后台交互的逻辑
 */
//数据保存
function save(){
	var data = getData();
	if(data==null){
		return;
	}
	$.ajax({
		url : saveUrl,
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : data,
		success : function(result) {
			layer.alert(result.message);
		},
		error : function(error){
			layer.alert("出错啦！");
		}
	})
}
//获取数据
function getData(){
	var cmsId = $("#cmsId").val();
	var cmsTitle = $("#cmsTitle").val();
	if(cmsTitle==''){
		layer.tips("请填写标题","#cmsTitle");
		return null;
	}
	var cmsAuthor = $("#cmsAuthor").val();
	if(cmsAuthor==''){
		layer.tips("请填写作者","#cmsAuthor");
		return null;
	}
	var cmsImageUrl = $("#cmsImage").attr('src').trim();
	if(cmsImageUrl==''){
		layer.tips("请设置图标","#editImageBtn");
		return null;
	}
	var cmsContent = UE.getEditor('editor').getContent();
	if(cmsContent==''){
		layer.alert("请填写内容");
		return null;
	}
	var data = {
			cmsId:cmsId,
			cmsTitle:cmsTitle,
			cmsAuthor:cmsAuthor,
			cmsImageUrl:cmsImageUrl,
			cmsContent:cmsContent
	}
	return data;
}
//返回
function back(){
	window.location.back();
}