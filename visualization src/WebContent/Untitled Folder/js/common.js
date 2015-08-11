$(function(){
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
				$(".btn-group-vertical").hide();
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
				$(".btn-group-vertical").show();
				$("#checkInRecord").show();
			};
		})
	})
})