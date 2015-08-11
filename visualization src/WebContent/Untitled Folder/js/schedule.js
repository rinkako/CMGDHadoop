$(function(){
	function constructNewTr(row){
		var $newtr = $(" <tr id=' ' class='ul_inputable'><th><input name='0-" + row + "' value=' '/></th> <th><textarea name='1-" + row + "'></textarea></th> <th><textarea name='2-" + row + "'></textarea></th> <th><textarea name='3-" + row + "'></textarea></th> <th><textarea name='4-" + row + "'></textarea></th> <th><textarea name='5-" + row + "'></textarea></th> <th><input name='6-" + row + "' value=' '/></th> <th><textarea name='7-" + row + "'></textarea></th> <th><textarea name='8-" + row + "'></textarea></th> </tr>");
		return $newtr;
	}
	function trigger2th(element, name){
		element.children().replaceWith("<textarea class='textarea' name='" + name + "'></textarea>");
		element.children().addClass("col-md-12 col-xs-12");
		element.css("background-color","#FFF");
	}
	function trigger2input(element, name){
		element.children().replaceWith("<input class='input' name='" + name + "' value=''/>");
		element.css("background-color","#E74C3C");
		element.children().css("background-color","#E74C3C");
		element.children().addClass("col-md-12 col-xs-12");
	}
	constructRightClickTh();
	//响应鼠标右击事件
	function constructRightClickTh(){
	$('th').contextMenu('sysMenu',{
					bindings:{
						//增加一行
						'newRow' : function(th){
							var row = $(th).children().attr("name").split("-")[1]/1 +0.1;
							$(th).parent().after(constructNewTr(row));
							constructRightClickTh();
							addClassfun();

								for (var j = 0; j < $("tr").length ; j++) {
									for (var i = 0; i < 9; i++) {
										var $th = $("tr:eq("+ j+")").children("th:eq("+i+")").children();
										$th.attr("name",i+"-"+j);
									}
								};
							$("#rowNum").attr("value",$("tr").length + "");
						},
						//切换类型，有用，但使用会引起bug未解决，勿用
						'triggerType' : function(th){
							var name = $(th).children().attr("name");
							$(th).children().is('input')? trigger2th($(th),name): trigger2input($(th),name);
						},
						//删除一行
						'delRow' : function(th){
							$(th).parent().remove();
								for (var j = 0; j < $("tr").length ; j++) {
									for (var i = 0; i < 9; i++) {
										var $th = $("tr:eq("+ j+")").children("th:eq("+i+")").children();
										$th.attr("name",i+"-"+j);
									}
								};
							$("#rowNum").attr("value",$("tr").length + "");
						}
					},
                  menuStyle: {	'height':'0px',
  								'border-radius': '4px',
								'-webkit-box-shadow':'none',
								'box-shadow':'none',
  								'border':'none'}
                  ,itemStyle: {
                  		 width: "100%",'height':'0px',
							  border:'0px'
                  }
                  ,itemHoverStyle: {
							  background: '#f3f4f5',
							  border:'0px'},
              		onShowMenu:function(e, menu){
						$("#jqContextMenu ul").attr("style","");
						$("#jqContextMenu li").attr("style","");
					return menu
              		}});
	}
	// $("#jqContextMenu").attr("style","");
	// $("#jqContextMenu ul").attr("style","");
	// $("#jqContextMenu li").attr("style","");

//copied
addClassfun();
function addClassfun(){
		$("tr").addClass("container-fluid");
		$("textarea").addClass("col-md-12 col-xs-12");
		$("tr:eq(0) input").addClass("col-md-12 col-xs-12");
		$("th:first-child").css("background-color", "#E74C3C");
		$("th:first-child input").css("background-color", "#E74C3C");
		$("th:first-child input").addClass("col-md-12 col-xs-12");
		$("th:nth-child(7)").css("background-color", "#E74C3C");
		$("th:nth-child(7) input").css("background-color", "#E74C3C");
		$("th:nth-child(7) input").addClass("col-md-12 col-xs-12");
		$("tr th:eq(0)").css("background-color", "#C0392B");
		$("tr:eq(0) th textarea").css("height", "25px");
		$("tr th:eq(6)").css("background-color", "#C0392B");
		$("tr:first-child").css("background-color", "#C0392B");
		$("table").addClass("redBorderTable");
}

	
//	$("table").hide();
	$("table").load("xml/schedule.xml",function(){
		addClassfun();
		$("table").show(100);
		constructRightClickTh();
		// $("#rowNum").value($("tr").length);
							// 	$("tr").click(function(){
							// 	for (var j = 0; j < $("tr").length ; j++) {
							// 		for (var i = 0; i < 9; i++) {
							// 			var $th = $("tr:eq("+ j+")").children("th:eq("+i+")").children();
							// 			// $th.attr("name",i+"-"+j);
							// 			alert($th.attr("name"));
							// 		};
							// 	};
							// })
	});
	$.post("schedule",{
		action: encodeURI("init")
	},function(data){
				var arr=new Array();
				arr=data.split(',');
		if (arr[0]=='admin') {
			$("textarea").removeAttr("readonly");
				$("#Login-nav a").empty();
				$("#Login-nav a").html(arr[1] + ' <span class="fui-new"></span> Logout');
				$("#Login-nav a").attr("href","#logout");
				$("#buttonGrp").show();
				$("#checkInRecord").show(100);
		}
		else if (arr[0]==' ') {
			$("#buttonGrp").hide();
			$("textarea").attr("readonly","readonly");
		}
		else{
			$("#buttonGrp").hide();
			$("textarea").attr("readonly","readonly");
			// alert('.attr(readonly)');
		};
	})
	$("#reset").click(function(){
		$("table").load("xml/schedule.xml",function(){
			$("tr").addClass("container-fluid");
			$("textarea").addClass("col-md-12 col-xs-12");
			// $("textarea").attr("onpropertychange","this.style.height=this.scrollHeight+'px';");
			// $("textarea").attr("oninput","this.style.height=this.scrollHeight+'px';");
			// $("tr th").addClass("col-md-2 col-xs-3");
			$("tr:eq(0) th").addClass("col-md-1 col-xs-1");
			$("th:first-child").css("background-color", "#E74C3C");
			$("th:nth-child(7)").css("background-color", "#E74C3C");
			$("tr th:eq(0)").css("background-color", "#C0392B");
			$("tr th:eq(6)").css("background-color", "#C0392B");
			$("tr:first-child").css("background-color", "#C0392B");
			$("table").addClass("redBorderTable");
		});
	})

})
