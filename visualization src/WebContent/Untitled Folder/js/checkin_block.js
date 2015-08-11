$(function(){
			var mydate = new Date();
	var weekdayNames = ["Sunday", "Monday", "", "Wednesday", "Thursday", "Friday", "Saturday"];
	// var weekdayNames = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
	var workdayBeginhhmm = [735, 950, 1205, 1405, 1620, 1835, 2050, 2220];
	var weekendBeginhhmm = [830, 1130, 1430, 1700, 1930, 2200];

	$.post("schedule",{
		action: encodeURI("init")
	},function(data){
				var arr=new Array();
				arr=data.split(',');
		if (arr[0]=='admin') {
				$("#Login-nav a").empty();
				$("#Login-nav a").html(arr[1] + ' <span class="fui-new"></span> Logout');
				$("#Login-nav a").attr("href","#logout");
				$("#checkInRecord").show();
		}
	})

	function hideCbSelectWrapper(){
		$(".cb_selectWrapper").css("opacity",0)
		$(".cb_selectWrapper").hide(250);
		$(".cb_selectMain").css("border-bottom-width","4px");
	}
	function showSubstitute(){
		$("#name").addClass("subcheckedselect");
		$(".substitute_name").addClass("subcheckedselect").show();
	}
	function hideSubstitute(){
		$("#name").removeClass("subcheckedselect");
		$(".substitute_name").removeClass("subcheckedselect").hide();
	}
	$("#sub_name_list").load("xml/StuffName.xml list option");

	//判断是周末还是工作日
	var shiftFilename = "";
	if (mydate.getDay() < 6 && mydate.getDay() > 0) {
		shiftFilename = "xml/shiftTimeWorkday.xml";
	} 
	else{
		shiftFilename = "xml/shiftTimeWeekend.xml";
	};
	//载入包含该班次员工名字的xml
$("#shift_list").load(shiftFilename+ " list option");
	$.get('xml/shift.xml',function(data){
		var shiftTime = "lalala";
				//处理当前日期格式
				var curtime = mydate.toLocaleTimeString().substring(2).replace(":", "").replace(":", "");
				var ampm = mydate.toLocaleTimeString().substring(0,1);
				if (ampm == "下") {curtime=curtime%120000 + 120000};
				if (mydate.getDay() < 6 && mydate.getDay() > 0) {
					for (var i = 0; i < 7; i++) {
						if (curtime < workdayBeginhhmm[i+1]*100 && curtime > workdayBeginhhmm[i]*100) {
							shiftTime = workdayBeginhhmm[i];
							break;
						}
					}
					if(shiftTime == "lalala"){
						shiftTime = 735;
					}

				}
				else{
					for (var i = 0; i < 5; i++) {
						if (curtime < weekendBeginhhmm[i+1]*100 && curtime > weekendBeginhhmm[i]*100) {
							shiftTime = weekendBeginhhmm[i];
							break;
						}
					}
					if(shiftTime == "lalala"){
						shiftTime = 830;
					}
				};
		//根据当前时间预选择班次
		$selectedoption = $("select#shift_list").find('option[value="t' + shiftTime + '"]');
		$selectedoption.prop('selected', true);
		$("#shift_list").prev('input').attr('value', $selectedoption.text());
		$("#name_list").load('xml/shift.xml '+weekdayNames[mydate.getDay()] + " t" + shiftTime + " option");
    	$("#div_check_in select").selectpicker({style: 'btn-primary', menuStyle: 'dropdown-inverse'});
	});
  $("#shift_list").change(function(){
		$("#name_list").load('xml/shift.xml '+weekdayNames[mydate.getDay()] + " " + $(this).val() + " option");
		$("#shift_list").prev('input').attr('value', $('#shift_list option[selected="selected"]').text());
  })
  $("#Login-nav a").click(function(){
  	if ($(this).attr("href")=="#login") {
  		(!$(".login-form-wrapper").is(":visible"))? $(".login-form-wrapper").show(300) : $(".login-form-wrapper").hide();
  	}
  	else{
  		$.post("Login",{
  			action: encodeURI("#logout")
  		},
  		function(data) {
				$("#Login-nav a").empty();
				$("#Login-nav a").html('<span class="fui-new"></span> Login');
				$("#Login-nav a").attr("href","#login");
				$("#checkInRecord").hide();
		})
  	};
  })
	$("#checkbox").prop('checked', false);
	$("#substitute_checkbox").click(function(){
		(!$(".substitute_name").is(":visible"))? showSubstitute() : hideSubstitute();
	});
	function alertla(){
		alert("sdfsdfsasdfdsfdf");
	}
	$("#login-form-a").click(function(){
		$.post("Login",{
			name: encodeURI($("#login-name").val()),
			password: encodeURI($("#login-pass").val()),
			action: encodeURI("login")
		},
		function(data) {
//			alert(data);
			if (data=="profileWorng") {
				$("#login-name").val("Username or Password uncorrect");
				$("#login-pass").parent().addClass("has-error");
				$("#login-name").parent().addClass("has-error");
			}
			else {
				var arr=new Array();
				arr=data.split(',');
				$("#Login-nav a").empty();
				$("#Login-nav a").html(arr[0] + ' <span class="fui-new"></span> Logout');
				$(".login-form-wrapper").hide();
				$("#Login-nav a").attr("href","#logout");
				$("#checkInRecord").show();
			};
		})
	})
	$("th").click(function(){
		alert($("tr:last-child th:last-child input").attr("name").split("-")[1]);
	})
})
