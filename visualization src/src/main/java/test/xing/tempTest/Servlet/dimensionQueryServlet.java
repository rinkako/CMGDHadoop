package test.xing.tempTest.Servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avro.data.Json;
import org.json.JSONArray;
import org.json.JSONObject;

import sun.tools.tree.NewArrayExpression;
import test.xing.tempTest.util.HbaseIOUtils;

/**
 * Servlet implementation class dimensionQuery
 */
@WebServlet("/dimensionQuery")
public class dimensionQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public dimensionQueryServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();

		String myregex = "";
		
		String datetime = request.getParameter("select_datetime");
		String city = request.getParameter("select_city");
		String service = request.getParameter("select_service");
		String apn = request.getParameter("select_APN");
		String firmType = request.getParameter("select_terminal");
		String checkboxs = request.getParameter("all");
		
		String[] array_ckbox = checkboxs.split(" ");
		if (array_ckbox.length > 0) {
			for (String paramName : array_ckbox) {
				if (paramName.replace("\\s", "").length() > 0) {
					switch (paramName.charAt(0)) {
					case 'c':
						if (!city.equals("None selected")) {
							columnValueMap.put("ct", city.replaceAll(", ","|"));
						}
						break;
					case 'a':
						if (!apn.equals("None selected")) {
							columnValueMap.put("ap", apn.replaceAll(", ","|"));
						}
						break;
					case 'p':
						if (!datetime.equals("None selected")) {
							columnValueMap.put("pr", datetime.replaceAll(":00-(\\d){0,2}:00","").replaceAll(", ","|"));
						}
						break;
					case 's':
						if (!service.equals("None selected")) {
							columnValueMap.put("sv", service.replaceAll(", ","|"));
						}
						break;
					case 'f':
						if (!firmType.equals("None selected")) {
							columnValueMap.put("ft", firmType.replaceAll(", ","|"));
						}
						break;
					default:
						break;
					}
				}
			}
			if (!columnValueMap.isEmpty()) {
				myregex = "^(";
				Iterator<String> it=columnValueMap.keySet().iterator();
				while(it.hasNext()){    
				     String key;    
				     String value;    
				     key = it.next().toString();    
				     value = (String)columnValueMap.get(key); 
				     myregex += key + ":(" + value + ")\\$";
				}   
				myregex += "#)$";
			}
			else {
				myregex = "^(#)$";
			}
		}
		else {
			Writer writer = response.getWriter();
			writer.write("null selection!");
			writer.close();
			return;
		}
		System.out.println(myregex);
		
		for(String key:columnValueMap.keySet()){
			columnValueMap.put(key, new LinkedHashMap<String,Double[]>());
		}

//		Map<String, Object> barChartMap = new LinkedHashMap<String, Object>();
//		barChartMap = columnValueMap;
//		myregex = "^(ct:(梅州市|汕尾市|河源市)\\$ap:(cmnet|cmwap)\\$sv:[^$]*\\$#)$";
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray_table = new JSONArray();
		String temp = "";
		ArrayList<Map<String, String>> kvMapsArray = null;  
				kvMapsArray = HbaseIOUtils.getResultArrayMapsByRegex("hbase_naive_cube", myregex);
		
		if (kvMapsArray != null) {
			Iterator<Map<String, String>> kvMapArrIt = kvMapsArray.iterator();
			   for(int i=1; kvMapArrIt.hasNext();i++){
				   Map<String, String> kvMap = kvMapArrIt.next();
				   for(String dimension : columnValueMap.keySet()){
					   Double[] data = ((Map<String,Double[]>)columnValueMap.get(dimension)).get(kvMap.get(dimension));
					   Double preSumByte;
					   Double preAvgDown;
					   Double preAvgSmall;
					   Double count;
					   if (data == null) {
						   preSumByte=preAvgDown=preAvgSmall=count=0.0;
					   } else {
						   preSumByte=data[0];
						   preAvgDown=data[1];
						   preAvgSmall=data[2];
						   count=data[3];
					   }
//					   System.out.println("preSumByte:"+preSumByte.toString());
					   ((Map<String,Double[]>)columnValueMap.get(dimension)).put(kvMap.get(dimension),
							   new Double[]{Double.parseDouble(kvMap.get("totalbyte"))+preSumByte,
											   Double.parseDouble(kvMap.get("avgdowntime"))+preAvgDown,
											   Double.parseDouble(kvMap.get("avgsmalltime"))+preAvgSmall,
											   count+1});
				   }
//				   System.out.println("columnValueMap:"+columnValueMap);
				   jsonArray_table.put(kvMap);
			   }
//			   System.out.println(jsonArray.toString());
		}
//		barChartMap = null;
//		   System.out.println("columnValueMap:"+columnValueMap);
//		   System.out.println("barChartMap:"+barChartMap);
		
/**
		System.out.println("select_datetime: "+ datetime + 
				"\nselect_city: " + city +
				"\nselect_service: " + service +
				"\nselect_APN: " + apn + 
				"\nselect_terminal: " + firmType +
				"\ncheckboxs: " + checkboxs
		);
*/
		jsonObj.put("table", jsonArray_table);
		
		JSONObject piChartJson = new JSONObject();
		JSONObject barChartJson = new JSONObject();
		
		JSONArray jsonArray_pi = null;
		for (Map.Entry<String,Object> entry : columnValueMap.entrySet()) {
			LinkedHashMap<String, Double[]> tempMap = (LinkedHashMap<String, Double[]>)entry.getValue();
			jsonArray_pi = new JSONArray();
			
			JSONObject JSONObject_bar = new JSONObject();
			String[] strArr = tempMap.keySet().toArray(new String[0]);
			int len = tempMap.keySet().size();
			JSONObject_bar.put("categories", strArr);
			Double[] data1 = new Double[len];
//			Double[] count = new Double[len];
			Double[] data2 = new Double[len--];
			int lenLeft = len;
			
			for (Map.Entry<String,Double[]> tempEntry : tempMap.entrySet()) {
				jsonArray_pi.put(new JSONObject("{name:" + tempEntry.getKey()+ ",y:" + tempEntry.getValue()[0] + "}" ));
				Double count = tempEntry.getValue()[3];
//				System.out.println(tempEntry.getValue()[0]);
				data1[len-lenLeft] = tempEntry.getValue()[1]/count;
				data2[len-(lenLeft--)] = tempEntry.getValue()[2]/count;
			}
//			System.out.println(data1[1]);
			JSONObject_bar.put("data1", data1);
			JSONObject_bar.put("data2", data2);
			barChartJson.put(entry.getKey(), JSONObject_bar);
			piChartJson.put(entry.getKey(), jsonArray_pi);
		}

		jsonObj.put("chart_bar", barChartJson);
		jsonObj.put("chart_pi", piChartJson);
		System.out.println("chart_bar: "+barChartJson);
//		   System.out.println("jsonObj: "+jsonObj.toString());
	//	jsonObj = new JSONObject("{\"2\":[{\"totalbyte\":\"223056\",\"avgSpeed\":\"0\",\"ct\":\"汕尾市\"}],\"1\":[{\"totalbyte\":\"1213760\",\"avgSpeed\":\"0\",\"ct\":\"惠州市\"}],\"inx\":[{\"totalbyte\":\"166345\",\"avgSpeed\":\"0\",\"ct\":\"河源市\"}]}");
//		jsonArray = new JSONArray("[{\"totalbyte\":\"1665\",\"avgSpeed\":\"0\",\"ap\":\"cmwap\",\"sv\":\"金融理财\",\"ft\":\"朵唯_TD-S2\",\"pr\":\"15\",\"ct\":\"韶关市\"}]");
		Writer writer = response.getWriter();
		writer.write(jsonObj.toString());
		writer.close();
		return;
	}


}
