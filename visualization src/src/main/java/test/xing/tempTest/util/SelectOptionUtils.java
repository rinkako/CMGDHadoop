package test.xing.tempTest.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Iterator;

public class SelectOptionUtils {
    static String firmList = "<brands>\n";

	public static void main(String[] args) {
		PrintWriter pw = null;
    	SortedNodeTree firmTypeTree = getSortedNodeTree();
		        BufferedReader reader = null;
		        try {
		        	pw = new PrintWriter(new File("/home/xing/gmcc/options_ft2.xml"),"UTF-8");
		            pw.write("<xml>\n");
		            reader = new BufferedReader(new FileReader(new File("/home/xing/gmcc/result_ft.txt")));
		            String firmType = "";
		            while ((firmType = reader.readLine()) != null) {
		            	firmTypeTree.addFirmType(firmType);
//		            	System.out.println((i++)+","+firmType);
		            }
		            iterateTreeForFullXmlfile(firmTypeTree, pw);
		            firmList += "</brands>\n";
		            pw.printf(firmList+ "</xml>");
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (reader != null) {
		                    try {
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
		            }
		            if (pw != null) {
		            	pw.close();
		            }
		        }
		        System.out.print("done");
	}
	

    private static void iterateTreeForFullXmlfile(SortedNodeTree root, PrintWriter pw){  
    	SortedNodeTree lChild = root.getLChild();
    	SortedNodeTree rChild = root.getRChild();
    	String firm = root.getFirm();
    	ArrayList<String> typeArray = root.getType();
        if(lChild!=null){  
        	iterateTreeForFullXmlfile(lChild, pw);
        }  
        firmList += "<option value=\"" + firm +"\">" + firm + "</option>\n";
        System.out.println(firmList);
        pw.printf("<optgroup label=\"%s\">\n", firm);
		Iterator<String> typeArrIt = typeArray.iterator();
		while(typeArrIt.hasNext()){
			String firmtype = typeArrIt.next();
	        pw.printf("<option value=\"%s\">%s</option>\n", firmtype,firmtype);
//	        System.out.println(firmtype);
		}
        pw.print("</optgroup>\n");
        if(rChild!=null){
        	iterateTreeForFullXmlfile(rChild, pw);
        }
    }
	
	private static SortedNodeTree getSortedNodeTree() {
		return (new SelectOptionUtils()).new SortedNodeTree();
	}
	
	class SortedNodeTree {
	    private String firm;
	    private ArrayList<String> typeArray;
	    private SortedNodeTree lChild;
	    private SortedNodeTree rChild;
	    
	    public SortedNodeTree(){
	    	
	    }
	    
	    public SortedNodeTree(String firm, ArrayList<String> typeArray, SortedNodeTree lChild, SortedNodeTree rChild){
	    	this.firm = firm;
	    	this.typeArray = typeArray;
	    	this.lChild = lChild;
	    	this.rChild = rChild;
	    }
	    public String getFirm() {  
	        return firm;  
	    }  
	    public ArrayList<String> getType() {  
	        return typeArray;  
	    }  
	    public SortedNodeTree getRChild() {  
	        return rChild;  
	    }  
	    public SortedNodeTree getLChild() {  
	        return lChild;  
	    } 

	    public void addFirmType(String firmType){
	    	String firm = firmType.split("_")[0];
//	    	System.out.println(firm);
		    RuleBasedCollator collator = (RuleBasedCollator)Collator.getInstance(java.util.Locale.CHINA);
		    if (firmType != null) {
		    	if (this.firm == null) {
		    		this.firm = firm;
	        		this.typeArray = new ArrayList<String>();
		    		this.typeArray.add(firmType);
//			        System.out.println("firm == null"+firmType);
				}
				else {
					int cmprst= collator.compare(this.firm,firm);
					if(cmprst > 0){  
			            if(lChild!=null){  
			                lChild.addFirmType(firmType);  
			            }  
			            else{  
			            	ArrayList<String> firmTypeArray = new ArrayList<String>();
			            	firmTypeArray.add(firmType);
//					        System.out.println("lChild"+firmType);
//			            	firmTypeArray.add("<option value="+firmType+">"+firmType+"</option>");
			                lChild = new SortedNodeTree(firm, firmTypeArray, null, null);  
			              //  System.out.println(child.getTitle());
			            }
			        }  
			        else if(cmprst < 0){  
			            if(rChild!=null){  
			                rChild.addFirmType(firmType);  
			            }  
			            else{  
			            	ArrayList<String> firmTypeArray = new ArrayList<String>();
			            	firmTypeArray.add(firmType);
//					        System.out.println("rChild"+firmType);
//			            	firmTypeArray.add("<option value="+firmType+">"+firmType+"</option>");
			                rChild = new SortedNodeTree(firm, firmTypeArray, null, null); 
			               // System.out.println(child.getTitle());
			            }  
			        }  
			        else {
			    		this.typeArray.add(firmType);
//				        System.out.println(firmType);
					}
				}
			}
	    }  
	}
}
