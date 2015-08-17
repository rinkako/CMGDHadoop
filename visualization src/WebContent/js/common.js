
        /**
            “图”标签页中图表的生成
        */
        $.fn.createPi=function (title,date_input) {
            $(this).highcharts({
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false,
                    type: 'pie',
                   // margin: ["0","0","0","0"]
                },
                title: {
                    text: title
                },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                            style: {
                                color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                            }
                        }
                    }
                },
                credits: {
                    enabled: false
                },
                // center: ["10%","10%"],
                series: [{
                    name: "Brands",
                    colorByPoint: true,
                    data: date_input
                }]
            });
        }
        

        $.fn.createBar=function (title,categories,data1,data2) {
	        $(this).highcharts({
	            chart: {
	                zoomType: 'xy'
	            },
	            title: {
	                text: title
	            },
	            subtitle: {
	                text: ' '
	            },
	            xAxis: [{
//	                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
//	                    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
	            	categories: categories,
	                crosshair: true
	            }],
	            yAxis: [{ // Primary yAxis
	                labels: {
	                    format: '{value}KB/s',
	                    style: {
	                        color: Highcharts.getOptions().colors[1]
	                    }
	                },
	                title: {
	                    text: '大包下载均速',
	                    style: {
	                        color: Highcharts.getOptions().colors[1]
	                    }
	                }
	            }, { // Secondary yAxis
	                title: {
	                    text: '小包平均时延',
	                    style: {
	                        color: Highcharts.getOptions().colors[0]
	                    }
	                },
	                labels: {
	                    format: '{value} ms',
	                    style: {
	                        color: Highcharts.getOptions().colors[0]
	                    }
	                },
	                opposite: true
	            }],
	            tooltip: {
	                shared: true,
//                    pointFormat: '{point.y}'
	            },
	            legend: {
	                layout: 'vertical',
	                align: 'left',
	                x: 120,
	                verticalAlign: 'top',
	                y: 100,
	                floating: true,
	                backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
	            },
                credits: {
                    enabled: false
                },
	            series: [{
	                name: '小包平均时延',
	                type: 'column',
	                yAxis: 1,
//	                data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4],
	                data: data2,
	                tooltip: {
	                    valueSuffix: ' ms'
	                }
	
	            }, {
	                name: '大包下载均速',
	                type: 'column',
//	                data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6],
	                data: data1,
	                tooltip: {
	                    valueSuffix: ' KB/s'
	                }
	            }]
	        });
        }
        /**
            调用插件提供的函数使下拉菜单可以多选
        */

        $.fn.buildMultiselect2=function () {
                    $(this).multiselect({
                        includeSelectAllOption: true,
                        enableFiltering: true,
                        maxHeight: 400,
                        buttonWidth: '80px'
                    });
        }
        $.fn.buildMultiselect=function () {
                    $(this).multiselect({
                        includeSelectAllOption: true,
                        enableFiltering: true,
                        maxHeight: 400,
                        buttonWidth: '80px'
                    });
                    $(this).next('div').find('input').change(function(){
                    	if($(this).is(':checked')){
                    		if($(this).val()=="multiselect-all"){
                    			$(this).parentsUntil('ul').parent().prev().attr('title',"[^$]*");
                    		}
                    	}
                    })
                    //.find('li')
                    if($(this).attr('id')=='select_terminal'){
                        $('#select_terminal').next('div').find('input').change(function(){
                        	if($(this).is(':checked')){
                        		if($(this).val()=="multiselect-all"){
                        			$('#select_terminal_2').empty();
                            		$('#select_terminal_2').load('xml/options_ft2.xml?id='+Math.random()+' optgroup',function(){
                            			$('#select_terminal_2').multiselect('rebuild');
//                            			alert($(this).prev().children().attr('title','.*'));
                            		});
                        		}
                        		else{
                            		$opt = $('<optgroup label="'+$(this).val()+'"></optgroup>');
                            		$opt.load('xml/options_ft2.xml?id='+Math.random()+' optgroup[label='+$(this).val().replace("(","\\(").replace(")","\\)")+'] option',function(){
                            			$('#select_terminal_2').append($opt);
                            			$('#select_terminal_2').multiselect('rebuild');
                            		});
                        		}
                        	}
                        	else{
                        		if($(this).val()=="multiselect-all"){
                        			$('#select_terminal_2').empty();
                        		}
                        		else{
                            		$('#select_terminal_2').children('optgroup[label='+$(this).val().replace("(","\\(").replace(")","\\)")+']').remove();
                        		}
                        	};
                			$('#select_terminal_2').multiselect('rebuild');
                        });
//                        $('ul').children('li:first').find('input').change(function(){
//                        	alert("changed");
//                        });
                    }
        }

        /**
            使用返回的json数组构造展示查询结果的表格
        */
        $.fn.buildResultTable=function (jsonObj) {
                 // alert(jsonObj[0].pr);
                 // buildResultTable(jsonObj);
                var $thead = $('<thead id="thead"></thead>');
                var $tbody = $('<tbody id="tbody"></tbody>');
                    $(this).append($thead);
                    $(this).append($tbody);
                var $tr = $("<tr></tr>");   
                $th = $("<th>#</th>");

                    /**
                        构造表头
                    */
                        $tr.append($th);
                        if (!(typeof(jsonObj[0].pr)=="undefined")){      
                            $th = $("<th>时段</th>");
                            $tr.append($th);
                            // alert(info["pr"]) 
                        }
                        if (!(typeof(jsonObj[0].ct)=="undefined")){         
                            $th = $("<th>地市</th>");
                            $tr.append($th);
                        }
                        if (!(typeof(jsonObj[0].sv)=="undefined")){             
                            $th = $("<th>业务</th>");
                            $tr.append($th);  
                        }
                        if (!(typeof(jsonObj[0].ap)=="undefined")){             
                            $th = $("<th>APN</th>");
                            $tr.append($th);  
                        }
                        if (!(typeof(jsonObj[0].ft)=="undefined")){              
                            $th = $("<th>终端类型</th>");
                            $tr.append($th); 
                        }
                        if (!(typeof(jsonObj[0].totalbyte)=="undefined")){            
                            $th = $("<th>流量 Bytes</th>");
                            $tr.append($th);   
                        }
                        if (!(typeof(jsonObj[0].avgdowntime)=="undefined")){            
                            $th = $("<th>大包下载均速 KB/s</th>");
                            $tr.append($th);   
                        }
                        if (!(typeof(jsonObj[0].avgsmalltime)=="undefined")){            
                            $th = $("<th>小包平均时延 ms</th>");
                            $tr.append($th);   
                        }
                $thead.append($tr);

                    /**
                        遍历json数组构造表的内容
                    */
                 $.each(jsonObj, function(index, info){
                    $tr = $("<tr></tr>");  
                    $th = $("<th></th>");
                    $th.text(index+1);

                    $tr.append($th);
                        if (!(typeof(info["pr"])=="undefined")){      
                            $th = $("<th></th>");
                            $th.text(info["pr"])
                            $tr.append($th);
                        }
                        if (!(typeof(info["ct"])=="undefined")){         
                            $th = $("<th></th>");
                            $th.text(info["ct"])
                            $tr.append($th);
                        }
                        if (!(typeof(info["sv"])=="undefined")){             
                            $th = $("<th></th>");
                            $th.text(info["sv"])
                            $tr.append($th);  
                        }
                        if (!(typeof(info["ap"])=="undefined")){             
                            $th = $("<th></th>");
                            $th.text(info["ap"])
                            $tr.append($th);  
                        }
                        if (!(typeof(info["ft"])=="undefined")){              
                            $th = $("<th></th>");
                            $th.text(info["ft"])
                            $tr.append($th); 
                        }
                        if (!(typeof(info["totalbyte"])=="undefined")){            
                            $th = $("<th></th>");
                            $th.text(info["totalbyte"])
                            $tr.append($th);   
                        }
                        if (!(typeof(info["avgdowntime"])=="undefined")){            
                            $th = $("<th></th>");
                            $th.text(info["avgdowntime"])
                            $tr.append($th);   
                        }
                        if (!(typeof(info["avgsmalltime"])=="undefined")){            
                            $th = $("<th></th>");
                            $th.text(info["avgsmalltime"])
                            $tr.append($th);   
                        }
                    $tbody.append($tr);
                })
                
        }

 $(document).ready(function() {

	 var loadReady = 0;
    $.ajaxSetup({
      contentType: "application/x-www-form-urlencoded; charset=utf-8"
    });
 

    /**
        多选下拉框
    */
    $('select').hide();
    $('#select_datetime').buildMultiselect();
    $('#select_terminal_2').buildMultiselect();
    $('#select_APN').buildMultiselect();

    $('#select_service').load('xml/options_sv.xml?id='+Math.random(),function(data){
        $(this).buildMultiselect();
    });
    $('#select_terminal').load('xml/options_ft2.xml?id='+Math.random()+' brands option',function(data){
        $(this).buildMultiselect();
    });
    $('#select_city').load('xml/options_ct.xml?id='+Math.random(),function(data){
        $(this).buildMultiselect();
    });
    

    /**
        "提交按钮"单击事件，获取下拉菜单及下拉菜单前复选框选中内容，
        提交到地址“dimensionQuery”，
    */
    $('#btn_apply_search').click(function(){
        var checkbox_all = "";
        $('select[multiple="multiple"]').each(function(){
            if($(this).prev('span').children('input[name="all"]').is(':checked')){
                checkbox_all += $(this).prev('span').children('input[name="all"]').val()+ " ";
            }
            else{
            }
        });
        $.ajax({
            url:'dimensionQuery',
            type: "POST",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            /**
                select_datetime : 时段
                select_city： 地市
                select_service ： 业务
                select_APN ： APN
                select_terminal ： 终端类型
                all ： 选择需要cube中为all的维度
            */
            data: {select_datetime : $('#select_datetime').next('div').children('button').attr('title'),
                    select_city : $('#select_city').next('div').children('button').attr('title'),
                    select_service : $('#select_service').next('div').children('button').attr('title'),
                    select_APN : $('#select_APN').next('div').children('button').attr('title'),
                    select_terminal : $('#select_terminal_2').next('div').children('button').attr('title'),
                    all : checkbox_all
            },
            success: function(data){
                $('#table_search_result').empty();
                 var jsonObj = $.parseJSON(data);
//                 if(jsonObj.chart.c);
                 (typeof(jsonObj.chart_pi.ct)!="undefined")?$('#chart_ct').show().createPi('各市总流量',jsonObj.chart_pi.ct): $('#chart_ct').hide();
                 (typeof(jsonObj.chart_pi.ap)!="undefined")?$('#chart_ap').show().createPi('各APN总流量',jsonObj.chart_pi.ap): $('#chart_ap').hide();
                 (typeof(jsonObj.chart_pi.sv)!="undefined")?$('#chart_sv').show().createPi('各服务总流量',jsonObj.chart_pi.sv): $('#chart_sv').hide();
                 (typeof(jsonObj.chart_pi.ft)!="undefined")?$('#chart_ft').show().createPi('各终端总流量',jsonObj.chart_pi.ft): $('#chart_ft').hide();
                 (typeof(jsonObj.chart_pi.pr)!="undefined")?$('#chart_pr').show().createPi('各时段总流量',jsonObj.chart_pi.pr): $('#chart_pr').hide();
                 (typeof(jsonObj.chart_bar.ct)!="undefined")?
                		 $('#bar_ct').show().createBar('各市大包下载均速及小包平均时延',jsonObj.chart_bar.ct.categories,jsonObj.chart_bar.ct.data1,jsonObj.chart_bar.ct.data2)
                		 : $('#bar_ct').hide();
                (typeof(jsonObj.chart_bar.sv)!="undefined")?
                		$('#bar_sv').show().createBar('各服务大包下载均速及小包平均时延',jsonObj.chart_bar.sv.categories,jsonObj.chart_bar.sv.data1,jsonObj.chart_bar.sv.data2)
               		 : $('#bar_sv').hide();
                (typeof(jsonObj.chart_bar.pr)!="undefined")?
                		$('#bar_pr').show().createBar('各时段大包下载均速及小包平均时延',jsonObj.chart_bar.pr.categories,jsonObj.chart_bar.pr.data1,jsonObj.chart_bar.pr.data2)
               		 : $('#bar_pr').hide();
                (typeof(jsonObj.chart_bar.ap)!="undefined")?
                		$('#bar_ap').show().createBar('各APN大包下载均速及小包平均时延',jsonObj.chart_bar.ap.categories,jsonObj.chart_bar.ap.data1,jsonObj.chart_bar.ap.data2)
               		 : $('#bar_ap').hide();
//                 $('#bar_ct').createBar('各市大包下载均速及小包平均时延',jsonObj.chart_bar.ct.categories,jsonObj.chart_bar.ct.data1,jsonObj.chart_bar.ct.data2);
//                 $('#bar_sv').createBar('各市大包下载均速及小包平均时延',jsonObj.chart_bar.sv.categories,jsonObj.chart_bar.sv.data1,jsonObj.chart_bar.sv.data2);
//                 $('#bar_pr').createBar('各市大包下载均速及小包平均时延',jsonObj.chart_bar.pr.categories,jsonObj.chart_bar.pr.data1,jsonObj.chart_bar.pr.data2);
//                 $('#bar_ap').createBar('各市大包下载均速及小包平均时延',jsonObj.chart_bar.ap.categories,jsonObj.chart_bar.ap.data1,jsonObj.chart_bar.ap.data2);
//                 div_charts_search_result
                 $('#table_search_result').buildResultTable(jsonObj.table);
//                 alert(jsonObj.table[0].totalbyte);
                ex1 = new tableSort('table_search_result',1,2,999,'up','down','hov');
            },
            error: function(data){
                 alert("error");
            }
        });
    });


        var chartdata = [{
                        name: "key1",
                        y: 20
                    }, {
                        name: "Chrome",
                        y: 24.030000000000005,
                        sliced: true,
                        selected: true
                    }, {
                        name: "Firefox",
                        y: 10.38
                    }, {
                        name: "Safari",
                        y: 4.77
                    }, {
                        name: "Opera",
                        y: 0.9100000000000001
                    }, {
                        name: "Proprietary or Undetectable",
                        y: 0.2
                    }];
         // $('#chart2').createChart();
//        $('#container').createBar();
});
                    
