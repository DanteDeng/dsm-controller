var croperId = 'imageCroper';
var croperInitFlag = false;

$(function() {
	includeCroper('croperContainer',croperId);
	$("#cencelUpdateBtn").hide();
	//initCss();
})

function initCss() {
	$("#menuListBlock").css({"overflow-y":"auto","height":"300"});
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
	
	var canvasInfo = $("#"+croperId)[0].contentWindow.getCanvasInfo(30,30);
	$("#menuIcon").remove();
	var image = '<img id="menuIcon" src="'+canvasInfo+'"/>';
	$("#editImageBtn").before('');
	$("#editImageBtn").before(image);
	$("#menuIcon").attr("width","30");
	$("#menuIcon").attr("height","auto");
	$("#menuIcon").attr("margin-right","10px");
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
			aspestRatio:1
	}
	$("#"+croperId)[0].contentWindow.initCroper(croperData);
}

/**
 * 以下为菜单操作相关
 */
var menuList = [];			//全局变量，存储编辑好的菜单列表

var menuUpdating = null;				//全局变量，临时保存修改的menu

var isUpdating = false;		//全局变量，标识是否正在做更新操作

//点击添加
$("#addBtn").click(function(){
	var menuName = $("#menuName").val();
	if(menuName==''||menuName.length>10){
		layer.tips('请填写菜单名称（10个字以内）', '#menuName');
		return;
	}
	var menuLevel = $("#menuLevel").val();
	var parentMenuId = $("#parentMenuId").val();
	var menuUrl = $("#menuUrl").val();
	var menuIcon = $("#menuIcon").attr('src').trim();
	var menuOrder = $("#menuOrder").val();
	var regex = /^[0-9]+$/g
	if(!regex.test(menuOrder)){	//限制必须填写1-9的数字
		layer.tips('请填写数字', '#menuOrder');
		return;
	}
	var menu = {
			menuName:menuName,
			menuLevel:menuLevel,
			parentMenuId:parentMenuId,
			menuUrl:menuUrl,
			menuIcon:menuIcon,
			menuOrder:menuOrder-1,	//计算位置时候使用起来方便，所以减1
			subMenuList:null
	}
	if(checkRepeatName(menu,menuList)){
		layer.tips("菜单名称不能重复！", '#menuName');
		return;
	}
	addMenu(menuList,menu);
	initMenuEditModal(null);
	isUpdating = false;		//修改编辑状态为‘非修改中’
})

//检查是否有重复菜单名称
function checkRepeatName(menu,list){
	for(var i=0;i<list.length;i++){
		if(list[i].menuName==menu.menuName){
			return true;
		}
		var subMenuList = list[i].subMenuList;
		if(subMenuList!=null){	//子集递归
			if(checkRepeatName(menu,subMenuList)){
				return true;
			}
		}
	}
	return false;
}

//改变菜单级别时候获取上级菜单列表
$("#menuLevel").change(function(){
	var menuLevel = $(this).val();
	initParentMenuId(menuLevel);
})



function initParentMenuId(menuLevel) {
	$("#parentMenuId").html('');
	if (menuLevel != null && menuLevel != '1') { // 菜单级别不是一级时候，获取上级菜单列表
		var parentMenuList = [];
		getParentMenuList(menuLevel, menuList, parentMenuList);
		for (var i = 0; i < parentMenuList.length; i++) {
			var menuName = parentMenuList[i].menuName;
			var opt = '<option value="' + menuName + '">' + menuName
					+ '</option>';
			$("#parentMenuId").append(opt);
		}
	}
}

//获取父级菜单列表
function getParentMenuList(level,list,parentMenuList){
	var menuLevel = level-1;
	for(var i=0;i<list.length;i++){
		if(list[i].menuLevel==menuLevel){	//菜单级别满足条件，添加至结果集
			parentMenuList.push(list[i]);
			//alert(JSON.stringify(parentMenuList));
			continue;
		}
		if(list[i].subMenuList!=null){		//递归遍历子集是否满足要求
			getParentMenuList(level,list[i].subMenuList,parentMenuList);
		}
	}
}

//添加菜单
function addMenu(list,menu){
	formatMenuList(list,menu);
	resetMenuListTable(list);
}

//在列表table中增加新行tr
function formatNewRow(menu,menuNo,menuOrder) {
	menuNo['menuNo'] = menuNo['menuNo']+1;
	menu['menuOrder']=menuOrder+1;	//修改菜单位置，保持为连续数字
	var rowId = "row"+ menuNo['menuNo'];
	var tr = '<tr id="'+rowId+'">tdcontent</tr>';
	tr = tr.replace('tdcontent','<td>'+menuNo['menuNo']+'</td>tdcontent');
	tr = tr.replace('tdcontent','<td>'+menu.menuName+'</td>tdcontent');
	tr = tr.replace('tdcontent','<td>'+menu.menuLevel+' 级</td>tdcontent');
	tr = tr.replace('tdcontent','<td>'+menu.parentMenuId+'</td>tdcontent');
	tr = tr.replace('tdcontent','<td><div class="muCss">'+menu.menuUrl+'</div></td>tdcontent');
	tr = tr.replace('tdcontent','<td><img class="menuIcon" src="'+menu.menuIcon+'"/></td>tdcontent');
	tr = tr.replace('tdcontent','<td>'+menu.menuLevel+'_'+menu.menuOrder+'</td>tdcontent');
	tr = tr.replace('tdcontent','<td><a class="btn btn-primary updateBtn">修改</a><a class="btn btn-primary deleteBtn">删除</a></td>');
	$("#menuListTable tbody").append(tr.replace(/null|undefined/g,''));		//暂时做成直接添加至列表
}

//重新排序并展示列表
function resetMenuListTable(list){
	$("#menuListTable tbody").html('');
	showSameLevelMenuList(list,{'menuNo':0});
}

//展示列表主要方法
function showSameLevelMenuList(list,menuNo) {
	if(list==null){
		return;
	}
	for(var i=0;i<list.length;i++){
		formatNewRow(list[i],menuNo,i);
		showSameLevelMenuList(list[i].subMenuList,menuNo);
	}
}

//根据属性比较
function compare(property){
    return function(a,b){
        var v1 = a[property];
        var v2 = b[property];
        if(v1>v2){
        	return 1;
        }else if(v1<v2){
        	return -1;
        }else{
        	return 0;
        }
    }
}

//形成菜单列表(在原有菜单列表的基础上新增一个菜单至列表)
function formatMenuList(list,menu,oprateType){
	if(menu.menuLevel=='1'&&oprateType!='delete'){	//一级菜单，添加至一级菜单列表
		list.push(menu);
		list.sort(compare('menuOrder'));
		return;
	}else if(list!=null){
		var menuName = menu.parentMenuId;
		for(var i=0;i<list.length;i++){
			if(oprateType=='delete'){	//执行删除元素逻辑
				if(list[i].menuName==menu.menuName){
					list.splice(i,1);	//删除这个位置的这个元素
					return;
				}
			}else{	//执行新增元素逻辑
				var subMenuList = list[i].subMenuList;
				if(list[i].menuName==menuName){		//添加至相应父级菜单的子菜单
					if(subMenuList==null){
						list[i].subMenuList=[];
					}
					list[i].subMenuList.push(menu);
					list[i].subMenuList.sort(compare('menuOrder'));
					return;
				}
			}
			//本级没有找到满足条件的菜单，去子集找
			formatMenuList(list[i].subMenuList,menu,oprateType);
		}
	}
}

//根据menuName找到相应menu数据
function findMenuByName(list,name){
	if(list==null){		//空指针避免
		return null;
	}else{
		for(var i=0;i<list.length;i++){		//遍历列表找出满足条件的内容
			if(list[i].menuName==name){
				return list[i];
			}
			menu = findMenuByName(list[i].subMenuList,name);	//遍历子列表找出满足条件的内容
			if(menu!=null){
				return menu;
			}
		}
		return null;
	}
}

//初始化菜单编辑模块
function initMenuEditModal(menu){
	var maxMenuLevel = getMaxMenuLevel();
	$("#menuLevel").html('');
	$("#parentMenuId").html('');
	for(var i=1;i<=maxMenuLevel;i++){		//添加菜单级别选项
		var opt = '<option value="'+i+'">'+i+' 级</option>';
		$("#menuLevel").append(opt);
	}
	if(menu==null){		//置空编辑模块
		//编辑框数据置空
		$("#menuName").val('');
		$("#menuUrl").val('');
		$("#menuIcon").attr('src',' ');
		$("#menuOrder").val('');
	}else{				//回显编辑数据
		$("#menuName").val(menu.menuName);
		var menuLevel=menu.menuLevel;
		$("#menuLevel").val(menuLevel);
		initParentMenuId(menuLevel);
		$("#parentMenuId").val(menu.parentMenuId);
		$("#menuUrl").val(menu.menuUrl);
		var srcVal = menu.menuIcon==''?' ':menu.menuIcon;
		$("#menuIcon").attr('src',srcVal);
		$("#menuOrder").val(menu.menuOrder);
	}
}

//获取最大菜单级别
function getMaxMenuLevel(){
	return eachMenuLevel(menuList,1);
}

//获取已有菜单级别
function eachMenuLevel(list,level){
	var levelCount = level;
	var listLength =list.length;
	if(listLength>0){
		levelCount++;
	}
	for(var i=0;i<list.length;i++){
		var subMenuList = list[i].subMenuList;
		if(subMenuList!=null){
			//遍历子菜单，从而找到菜单的最大级别
			var levelCountTemp = eachMenuLevel(subMenuList,levelCount);
			if(levelCountTemp>levelCount){
				levelCount = levelCountTemp;
			}
		}
	}
	return levelCount;
}

//点击修改按钮
$("#menuListTable").on('click','.updateBtn',function(){
	if(isUpdating){		//更新操作过程中，不允许修改操作
		return;
	}
	isUpdating = true;
	var menuName = $(this).parent().siblings().eq(1).text().trim();
	menuUpdating = findMenuByName(menuList,menuName);
	formatMenuList(menuList,menuUpdating,'delete'); 	//删除数据
	resetMenuListTable(menuList);				//展示列表重排
	$(".updateBtn,.deleteBtn").removeClass("btn-primary");		//样式控制
	$("#cencelUpdateBtn").show();
	initMenuEditModal(menuUpdating);
})

//点击删除按钮
$("#menuListTable").on('click','.deleteBtn',function(){
	if(isUpdating){		//更新操作过程中，不允许删除操作
		return;
	}
	var menuName = $(this).parent().siblings().eq(1).text().trim();
	var menu ={};			//入参设置
	menu['menuName']=menuName;
	formatMenuList(menuList,menu,'delete'); 	//删除数据
	resetMenuListTable(menuList);				//展示列表重排
	initMenuEditModal(null);						//初始化编辑模块
})

//重置编辑项
$("#resetBtn").click(function(){
	initMenuEditModal(null);
})

//取消修改
$("#cencelUpdateBtn").click(function(){
	$("#cencelUpdateBtn").hide();
	isUpdating = false;
	menuUpdating['menuOrder'] = menuUpdating['menuOrder']-1;		//纠正排序位置
	formatMenuList(menuList,menuUpdating);					//数据还原
	resetMenuListTable(menuList);				//展示列表重排
	initMenuEditModal(null);						//初始化编辑模块
})

function showMenuListJson(){
	$("#tab3").html(JSON.stringify(menuList));
}
