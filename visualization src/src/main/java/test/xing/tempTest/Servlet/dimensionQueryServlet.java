package test.xing.tempTest.Servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

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
		
		Map<String, String> columnValueMap = new LinkedHashMap<String, String>();

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
							columnValueMap.put("ct:", city.replaceAll(", ","|"));
						}
						break;
					case 'a':
						if (!apn.equals("None selected")) {
							columnValueMap.put("ap:", apn.replaceAll(", ","|"));
						}
						break;
					case 'p':
						if (!datetime.equals("None selected")) {
							columnValueMap.put("pr:", datetime.replaceAll(":00-(\\d){0,2}:00","").replaceAll(", ","|"));
						}
						break;
					case 's':
						if (!service.equals("None selected")) {
							columnValueMap.put("sv:", service.replaceAll(", ","|"));
						}
						break;
					case 'f':
						if (!firmType.equals("None selected")) {
							columnValueMap.put("ft:", firmType.replaceAll(", ","|"));
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
				     value = columnValueMap.get(key); 
				     myregex += key + "(" + value + ")\\$";
				}   
				myregex += "#)$";
			}
			else {
				myregex = "^(.*)$";
			}
		}
		else {
			Writer writer = response.getWriter();
			writer.write("null selection!");
			writer.close();
			return;
		}
		System.out.println(myregex);
		
//		myregex = "^(ct:(梅州市|汕尾市|河源市)\\$ap:(cmnet|cmwap)\\$sv:[^$]*\\$#)$";
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String temp = "";
		ArrayList<Map<String, String>> kvMapsArray = null;  
				kvMapsArray = HbaseIOUtils.getResultArrayMapsByRegex("hbase_naive_cube", myregex);
		
		if (kvMapsArray != null) {
			Iterator<Map<String, String>> kvMapArrIt = kvMapsArray.iterator();
			   for(int i=1; kvMapArrIt.hasNext();i++){
				   Map<String, String> kvMap = kvMapArrIt.next();
//				   int downByte = Integer.parseInt(kvMap.get("downbyte"));
//				   int transtime = Integer.parseInt(kvMap.get("transtime"));
//				   kvMap.remove("downbyte");
//				   kvMap.remove("transtime");
//				   kvMap.put("avgSpeed", (transtime == 0)? 0+"" : (int)(downByte/transtime)+"" );
				   jsonArray.put(kvMap);
			   }
			   System.out.println(jsonArray.toString());
		}
		  
//		System.out.println("select_datetime: "+ datetime + 
//				"\nselect_city: " + city +
//				"\nselect_service: " + service +
//				"\nselect_APN: " + apn + 
//				"\nselect_terminal: " + firmType +
//				"\ncheckboxs: " + checkboxs
//		);
	//	jsonObj = new JSONObject("{\"2\":[{\"totalbyte\":\"223056\",\"avgSpeed\":\"0\",\"ct\":\"汕尾市\"}],\"1\":[{\"totalbyte\":\"1213760\",\"avgSpeed\":\"0\",\"ct\":\"惠州市\"}],\"inx\":[{\"totalbyte\":\"166345\",\"avgSpeed\":\"0\",\"ct\":\"河源市\"}]}");
//		jsonArray = new JSONArray("[{\"totalbyte\":\"1665\",\"avgSpeed\":\"0\",\"ap\":\"cmwap\",\"sv\":\"金融理财\",\"ft\":\"朵唯_TD-S2\",\"pr\":\"15\",\"ct\":\"韶关市\"}]");
		Writer writer = response.getWriter();
		writer.write(jsonArray.toString());
		writer.close();
		return;
	}


}
