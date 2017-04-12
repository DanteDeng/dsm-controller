var curr = 1;
$(function() {
	load(curr);
	getAllRole();
})

function load(curr) {
	$
			.ajax({
				url : "/dsm-controller/user/pageUserList",
				timeout : 300000,
				dataType : "json",
				type : "post",
				data : {
					"flag" : "load",
					"pageNum" : curr,
					"pageSize" : 5
				},
				success : function(result) {
					var code = result.code;
					if (code != '0') {
						layer.alert(result.message);
						return;
					}
					var html = "";
					$.each(
									result.data.list,
									function(i, item) {
										html += " <tr> "
												+ " <td>"
												+ item.userName
												+ "</td> "
												+ " <td>"
												+ item.userType
												+ "</td> "
												+ " <td>"
												+ item.loginName
												+ "</td> "
												+ " <td>"
												+ item.loginPwd
												+ "</td> "
												+ " <td><a class=\"btn btn-info\" onclick='openedt(\""
												+ item.userId
												+ "\");'>修改</a>&nbsp;&nbsp;<a class=\"btn btn-warning\" onclick='del(\""
												+ item.userId
												+ "\");'>删除</a></td> "
												+ " </tr>";
									})
					$("#tbody").html(html.replace(/null/g, ''));
					initPage("page", result.data)
				}
			})
}

function openadd() {
	$("#myModalLabel").text("新增用户");
	$("#addModal input").val("");
	$("#addModal select option").each(function(index, element) {
		element.selected=false;
	})
	$("#roleList [name='role']").each(function(index, element) {
		element.checked=false;
	})
	$("#addModal").modal("show");
	$("#add").show();
	$("#edt").hide();
}

function add() {
	if ($("#userName").val() == "") {
		layer.tips('不能为空', '#userName');
		return;
	}
	if ($("#userType").val() == "") {
		layer.tips('不能为空', '#userType');
		return;
	}
	if ($("#loginName").val() == "") {
		layer.tips('不能为空', '#loginName');
		return;
	}
	if ($("#loginPwd").val() == "") {
		layer.tips('不能为空', '#loginPwd');
		return;
	}
	var formdata = {
		flag : "add",
		userId : $("#userId").val(),
		userName : $("#userName").val(),
		userType : $("#userType").val(),
		loginName : $("#loginName").val(),
		loginPwd : $("#loginPwd").val(),
		roleJson : getRoleJson()
	}
	$.ajax({
		url : "/dsm-controller/user/editUser",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : formdata,
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			$("#addModal").modal("hide");
			var message = result.message;
			layer.alert(message);
			//$("#addModal input").val("");
			load(curr);
		}
	})
}

function openedt(userId) {
	$.ajax({
		url : "/dsm-controller/user/queryUserDetail",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : {
			"flag" : "getUser",
			"userId" : userId
		},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var data = result.data;
			$("#myModalLabel").text("修改成绩");
			$("#userId").val(userId);
			$("#userName").val(data.userName);
			$("#userType").val(data.userType);
			$("#loginName").val(data.loginName);
			$("#loginPwd").val(data.loginPwd);
			$("#roleList [name='role']").each(function(index, element) {
				element.checked=false;
			})
			var roleList = data.roleList;
			if(roleList!=null){
				for(var i in roleList){
					$("#roleList [name='role']").each(function(index, element) {
						var roleId = $(element).val();
						if(roleId==roleList[i].roleId){
							element.checked=true;
						}
					})
				}
			}
			$("#edt").show();
			$("#add").hide();
			$("#addModal").modal("show");
		}
	})
}

function edt() {
	if ($("#userName").val() == "") {
		layer.tips('不能为空', '#userName');
		return;
	}
	if ($("#Chinese").val() == "") {
		layer.tips('不能为空', '#Chinese');
		return;
	}
	if ($("#Math").val() == "") {
		layer.tips('不能为空', '#Math');
		return;
	}
	if ($("#English").val() == "") {
		layer.tips('不能为空', '#English');
		return;
	}
	var formdata = {
		flag : "edt",
		userName : $("#userName").val(),
		Chinese : $("#Chinese").val(),
		Math : $("#Math").val(),
		English : $("#English").val()
	}
	$.ajax({
		url : "../Json/Index.aspx",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : formdata,
		success : function(data) {
			$("#addModal").modal("hide");
			layer.alert(data.msg);
			$("input").val("");
			load(curr);
		}
	})
}

function del(userId) {
	// 询问框
	layer.confirm('您确定要删除？', {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		$.ajax({
			url : "/dsm-controller/user/deleteUser",
			timeout : 300000,
			dataType : "json",
			type : "post",
			data : {
				"flag" : "del",
				"userId" : userId
			},
			success : function(result) {
				var code = result.code;
				if (code != '0') {
					layer.alert(result.message);
					return;
				}
				var msg = result.message;
				layer.alert(msg);
				load(curr);
			}
		})
	}, function() {
		layer.close();
	});

}

function editRole() {
	$("#roleModalLabel").text("角色编辑");
	$("#addModal").modal("hide");
	$("#roleModal").modal("show");
}

function closeRoleEdit() {
	$("#addModal").modal("show");
	$("#roleModal").modal("hide");
}

function initRoleUl(roleList) {
	$("#roleList").empty();
	if (roleList != null) {
		for ( var i in roleList) {
			var op = '<li><input type="checkBox" name="role" value="'
					+ roleList[i].roleId + '">' + roleList[i].roleName
					+ '</option></li>'
			$("#roleList").append(op);
		}
	}

}

function getAllRole() {
	$.ajax({
		url : "/dsm-controller/role/queryRoleList",
		timeout : 300000,
		dataType : "json",
		type : "post",
		async : false,
		data : {},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var roleList = result.data;
			initRoleUl(roleList);
		}
	})
}

function getRoleJson() {
	var roleList = [];
	$("#roleList [name='role']:checked").each(function(index, element) {
		roleList.push({
			roleId : element.value
		});
	})
	return JSON.stringify(roleList);
}