$(function(){
   $.fn.scrollablecombo = function(options) {
		return this.each(function() {
			$this = $(this);
			
			function makeScrollable($wrapper, $container){
				var extra 			= 50;
				var wrapperHeight 	= $wrapper.height();
				$wrapper.css({overflow: "hidden"});
				$wrapper.scrollTop(0);
				$wrapper.unbind("mousemove").bind("mousemove",function(e){
					var ulHeight 	= $container.outerHeight() + 2*extra ;
					var top 		= (e.pageY - $wrapper.offset().top) * (ulHeight-wrapperHeight ) / wrapperHeight - extra;
					$wrapper.scrollTop(top);
				});
			}
			
			/**
			* let"s build our element structure
			*/
			var $ul_e 		= $this.find(".cb_ul");
			var $a_e	 	= $ul_e.find("a");
			var $wrapper_e 	= $this.find(".cb_selectWrapper");
			var $control_e 	= $this.find(".cb_selectMain");
			var $select_e 	= $this.find(".cb_select");
			var $selected	= $ul_e.find(".selected");
			
			function openCombo(){
				$(".cb_selectWrapper").css("opacity",1);
				$wrapper_e.show();
				$control_e.addClass("cb_up").removeClass("cb_down");
				$control_e.css("border-bottom-width",0);
				makeScrollable($wrapper_e,$ul_e);
			}
			function closeCombo(){
				$wrapper_e.hide();
				$control_e.addClass("cb_down").removeClass("cb_up");
				$control_e.css("border-bottom-width", 4);
			}
			$control_e.html($selected.find("a").html())
					  .bind("click",function(){
						  (!$wrapper_e.is(":visible"))? openCombo() : closeCombo();
					  }
			);
			$selected.hide();
			
			$a_e.click(function(e){
				var $this 		= $(this);
				$control_e.html($this.html());
				var $selected	= $ul_e.find(".selected");
				$selected.show().removeClass("selected");
				$this.parent().addClass("selected").hide();
				closeCombo();
				e.preventDefault();
			});
		});
	};

})