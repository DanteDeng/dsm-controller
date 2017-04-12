$(function () {
    $(".content-left,.content-right").height($(window).height() - 130);
})
function changeIframe(obj) {
	var srcVal = $(obj).find("input").val();
	$("#subBody").attr("src",srcVal);
    $(".active").html($(obj).find(".left-body").text());
}

$(window).resize(function () {
    $(".content-left,.content-right").height($(window).height() - 130);
})

function toLogin(){
	$("#subBody").attr("src","User/UserLogin.html");
}

function initMenu(menuList){
	$("#collapseOne").empty();
	for(var i in menuList){
		var menu ='<div class="accordion-inner" onclick="javascript:changeIframe(this)">'
			+'<img class="left-icon-child" src="../images/32/4962_sitemap.png" /><span'
			+' class="left-body"><input type="hidden"'
			+' class="subModuleVal" value="'+menuList[i].permissionUrl+'" /> '+menuList[i].menuName+'</span>'
			+'</div>';
		$("#collapseOne").append(menu.replace(/null/g,''));
	}
}

function showUserInfo(user){
	$("#userId").val(user.userId);
	$("#userText").html("欢迎您，"+user.userName);
	if(user.permissionList!=null){
		initMenu(user.permissionList);
	}
	
}

function logout(){
	var userId = $("#userId").val();
	if(userId!=''){
		$.ajax({
			url:"/dsm-controller/user/userLogout",
			data:{userId:userId},
			type:"POST",
			dataType:"json",
			async:false,
			success:function(result){
				var code = result.code;
				var message = result.message;
				layer.alert(message);
				if(code=='0'){
					$("#userId").val('');
					$("#userText").html('请<a href="javascript:toLogin()">登录</a>');
					$("#collapseOne").empty();
					$("#subBody").attr("src","Iframe.html");
				}
			}
		})
	}else{
		layer.alert("请先登录");
	}
}

/**
 *下面是一些常用util方法，封装放这里供iframe的页面调用
 */
function formatDate(date, format) {
	if(Number.isInteger(date)){
		date = new Date(date);
	}else if(date==null){
		return null;
	}
	var o = {
		"M+" : date.getMonth() + 1, 					// month
		"d+" : date.getDate(), 							// day
		"h+" : date.getHours(), 						// hour
		"m+" : date.getMinutes(), 						// minute
		"s+" : date.getSeconds(), 						// second
		"q+" : Math.floor((date.getMonth() + 3) / 3), 	// quarter
		"S"  : date.getMilliseconds()					// millisecond
	}

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (date.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}

	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
}