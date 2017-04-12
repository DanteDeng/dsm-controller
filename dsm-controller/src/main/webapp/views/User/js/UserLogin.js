
$(function () {
})

function userLogin(){
	var loginName = $("#loginName").val();
	var loginPwd = $("#loginPwd").val();
	var url = "/dsm-controller/user/userLogin";
	var data = {
			loginName:loginName,
			loginPwd:loginPwd
	}
	$.ajax({
		url:url,
		data:data,
		type:"POST",
		dataType:"json",
		async:false,
		success:function(result){
			var code = result.code;
			var user = result.data;
			var message = result.message;
			layer.alert(message);
			if(code=='0'){
				parent.showUserInfo(user);
				setTimeout(function(){
					window.location.href="/dsm-controller/views/Iframe.html";
				},1500);
			}
		}
	})
}