$(function(){
	// function bindClickEvent() {
	$("#btn_addNewRec").click(function(){
		$.post("suAdmin",{
			action: encodeURI("addNewRec"),
			username: encodeURI($("#newUsername").val()),
			password: encodeURI($("#newPassword").val())
		},
			function(data){
				alert(data);
				$("#modifiableAccount").load("suAdmin",{"action":"getAccountRec"});
				$("#newUsername").val("");
				$("#newPassword").val("");
		})
	});
	function bindClickEvent() {
	$(".apllyMod").click(function(){
				// alert($(this).parent().prev().prev().prev().children("input").val());
				// alert($(this).text());
			var name = $(this).parent().prev().prev().children("input").val();
			var password = $(this).parent().prev().children("input").val();
		$.post("suAdmin",{
			action: encodeURI("apllyMod"),
			username: encodeURI(name),
			password: encodeURI(password)
		},
			function(data){
				alert(data);
				$("#modifiableAccount").load("suAdmin",{"action":"getAccountRec"},function(data){bindClickEvent()});
		})
	});
	$(".btnDel").click(function(){
			var name = $(this).parent().prev().prev().prev().children("input").val();
			var password = $(this).parent().prev().prev().children("input").val();
				// alert($(this).text());
		$.post("suAdmin",{
			action: encodeURI("btnDel"),
			username: encodeURI(name),
			password: encodeURI(password)
		},
			function(data){
				alert(data);
				$("#modifiableAccount").load("suAdmin",{"action":"getAccountRec"},function(data){bindClickEvent()});
		})
	});
	};
	// alert("init");
	// bindClickEvent();
	$.post("suAdmin",{
		action: encodeURI("init")
	},function(data){
				var arr=new Array();
				arr=data.split(',');
		if (arr[0]=='suAdmin') {
				$("#Login-nav a").empty();
				$("#Login-nav a").html(arr[1] + ' <span class="fui-new"></span> Logout');
				$("#Login-nav a").attr("href","#logout");
				$("#checkInRecord").show();
		}
	})

	$("#modifiableAccount").load("suAdmin",{"action":"getAccountRec"},function(data){
		bindClickEvent();
	});
});