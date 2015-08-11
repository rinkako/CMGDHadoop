$(function(){
	var mydate = new Date();
	var day = mydate.getDate();
	var month = mydate.getMonth()+1;
	if (month < 10) {
		month = "0" + month;
	};
	month = "month_" + month;
	if (day < 10) {
		day = "0" + day;
	};
	day = "day_" + day;
	// alert(day + " " + month);
	// alert('xml/checkinRecord_2015.xml '+month+' '+day+ ' tr');
	// $("tbody").load('xml/checkinRecord_2015.xml '+month+' '+day+ ' tr');



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
				$(".btn-group-vertical").show();
				$("#checkInRecord").show(100);

				$("#tbody").load('xml/checkinRecord_2015.xml', function(){
			 		var ex1 = new tableSort('table',1,2,999,'up','down','hov');
				});
		};
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


	$("#login-form-a").click(function(){
		$.post("Login",{
			name: encodeURI($("#login-name").val()),
			password: encodeURI($("#login-pass").val()),
			action: encodeURI("login")
		},
		function(data) {
			if (data=="passwordWorng") {
				$("#login-pass").attr("placeholder","Password uncorrect");
				$("#login-pass").parent().addClass("has-error");
			}
			else if (data=="userNameWorng") {
				$("#login-name").attr("placeholder","Username uncorrect");
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
	$("#button_searchRecord").click(function(){
		var text = $(this).parent().prev("input").val();
		var $tr = $("th:contains("+ text + ")").parent();
		// alert(text);
		$("tr").hide();
		$("#thead").show();
		$tr.show();
	})

 	$.get('xml/checkinRecord_2015.xml', function(data) {
          $("#tbody").html($(data).find('tr'));
     })
})
