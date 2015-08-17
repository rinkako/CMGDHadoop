package test.xing.tempTest.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseIOUtils {
    // 声明静态配置
    static Configuration conf = null;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "localhost");
    }

    /*
     * 创建表
     * 
     * @tableName 表名
     * 
     * @family 列族列表
     */
    public static void createTable(String tableName, String[] family)
            throws IOException {
        HBaseAdmin admin = new HBaseAdmin(conf);
        HTableDescriptor desc = new HTableDescriptor(tableName);
        for (int i = 0; i < family.length; i++) {
            desc.addFamily(new HColumnDescriptor(family[i]));
        }
        if (admin.tableExists(tableName)) {
            System.out.println("table Exists!");
            System.exit(0);
        } else {
            admin.createTable(desc);
            System.out.println("create table Success!");
        }
    }
    //HColumnDescriptor[] ihcolumnFamilies = ihtable.getTableDescriptor() // 获取所有的列族
    //            .getColumnFamilies();


    /*
     * 为表添加数据（适合知道有多少列族的固定表）
     * 
     * @rowKey rowKey
     * 
     * @tableName 表名
     * 
     * @column1 第一个列族列表
     * 
     * @value1 第一个列的值的列表
     * 
     * @column2 第二个列族列表
     * 
     * @value2 第二个列的值的列表
     */
    public static void addData(String rowKey, String tableName,
            String[] column1, String[] value1)
            throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey
        
        HTable ihtable = new HTable(conf, Bytes.toBytes("hbase_naive_cube"));// HTabel负责跟记录相关的操作如增删改查等

        //for (int i = 0; i < ihcolumnFamilies.length; i++) {
        //    String familyName = ihcolumnFamilies[i].getNameAsString(); // 获取列族名
        for (int j = 0; j < column1.length; j++) {
            put.add(Bytes.toBytes("dimensions"),
                    Bytes.toBytes(column1[j]), Bytes.toBytes(value1[j]));
        }
        //}
        ihtable.put(put);
        //System.out.println("add data Success!");
    }



    /*
     * 根据rwokey查询
     * 
     * @rowKey rowKey
     * 
     * @tableName 表名
     */
    public static Result getResult(String tableName, String rowKey)
            throws IOException {
        Get get = new Get(Bytes.toBytes(rowKey));
        HTable table = new HTable(conf, Bytes.toBytes(tableName));// 获取表
        Result result = table.get(get);
        for (KeyValue kv : result.list()) {
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out
                    .println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
        return result;
    }

    /*
     * 遍历查询hbase表
     * 
     * @tableName 表名
     */
    public static void getResultScann(String tableName) throws IOException {
        Scan scan = new Scan();
        ResultScanner rs = null;
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        try {
            rs = table.getScanner(scan);
            for (Result r : rs) {
                for (KeyValue kv : r.list()) {
                    System.out.println("row:" + Bytes.toString(kv.getRow()));
                    System.out.println("family:"
                            + Bytes.toString(kv.getFamily()));
                    System.out.println("qualifier:"
                            + Bytes.toString(kv.getQualifier()));
                    System.out
                            .println("value:" + Bytes.toString(kv.getValue()));
                    System.out.println("timestamp:" + kv.getTimestamp());
                    System.out
                            .println("-------------------------------------------");
                }
            }
        } finally {
            rs.close();
        }
    }

    /*
     * 遍历查询hbase表
     * 
     * @tableName 表名
     */
    public static void getResultScann(String tableName, String start_rowkey,
            String stop_rowkey) throws IOException {
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(start_rowkey));
        scan.setStopRow(Bytes.toBytes(stop_rowkey));
        ResultScanner rs = null;
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        try {
            rs = table.getScanner(scan);
            for (Result r : rs) {
                for (KeyValue kv : r.list()) {
                    System.out.println("row:" + Bytes.toString(kv.getRow()));
                    System.out.println("family:"
                            + Bytes.toString(kv.getFamily()));
                    System.out.println("qualifier:"
                            + Bytes.toString(kv.getQualifier()));
                    System.out
                            .println("value:" + Bytes.toString(kv.getValue()));
                    System.out.println("timestamp:" + kv.getTimestamp());
                    System.out
                            .println("-------------------------------------------");
                }
            }
        } finally {
            rs.close();
        }
    }

    /*
     * 查询表中的某一列
     * 
     * @tableName 表名
     * 
     * @rowKey rowKey
     */
    public static void getResultByColumn(String tableName, String rowKey,
            String familyName, String columnName) throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // 获取指定列族和列修饰符对应的列
        Result result = table.get(get);
        for (KeyValue kv : result.list()) {
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out
                    .println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
    }

    /*
     * 更新表中的某一列
     * 
     * @tableName 表名
     * 
     * @rowKey rowKey
     * 
     * @familyName 列族名
     * 
     * @columnName 列名
     * 
     * @value 更新后的值
     */
    public static void updateTable(String tableName, String rowKey,
            String familyName, String columnName, String value)
            throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(familyName), Bytes.toBytes(columnName),
                Bytes.toBytes(value));
        table.put(put);
        System.out.println("update table Success!");
    }

    /*
     * 查询某列数据的多个版本
     * 
     * @tableName 表名
     * 
     * @rowKey rowKey
     * 
     * @familyName 列族名
     * 
     * @columnName 列名
     */
    public static void getResultByVersion(String tableName, String rowKey,
            String familyName, String columnName) throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
        get.setMaxVersions(5);
        Result result = table.get(get);
        for (KeyValue kv : result.list()) {
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out
                    .println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
        /*
         * List<?> results = table.get(get).list(); Iterator<?> it =
         * results.iterator(); while (it.hasNext()) {
         * System.out.println(it.next().toString()); }
         */
    }

    /*
     * 删除指定的列
     * 
     * @tableName 表名
     * 
     * @rowKey rowKey
     * 
     * @familyName 列族名
     * 
     * @columnName 列名
     */
    public static void deleteColumn(String tableName, String rowKey,
            String falilyName, String columnName) throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
        deleteColumn.deleteColumns(Bytes.toBytes(falilyName),
                Bytes.toBytes(columnName));
        table.delete(deleteColumn);
        System.out.println(falilyName + ":" + columnName + "is deleted!");
    }

    /*
     * 删除指定的列
     * 
     * @tableName 表名
     * 
     * @rowKey rowKey
     */
    public static void deleteAllColumn(String tableName, String rowKey)
            throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
        table.delete(deleteAll);
        System.out.println("all columns are deleted!");
    }

    /*
     * 删除表
     * 
     * @tableName 表名
     */
    public static void deleteTable(String tableName) throws IOException {
        try {
            HBaseAdmin admin = new HBaseAdmin(conf);
            if (admin.tableExists(tableName) == true) {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
                System.out.println(tableName + "is deleted!");
            }
        }
        catch (IOException e) {
        }
    }


    public static void splitInput(String filename) throws IOException {

        // 创建表
        String tableName = "hbase_naive_cube";
        String[] family = { "dimensions" };
        deleteTable(tableName);
        createTable(tableName, family);

        File f = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String tempstring = null;
        int counter = 0;
        while ((tempstring = reader.readLine()) != null ) {
            String[] splitItem = tempstring.split("\t");
            if (splitItem.length != 4) { continue; }
            String[] tcolumn = { "totalbyte", "downbyte", "transtime" };
            String[] tvalue = { splitItem[1], splitItem[2], splitItem[3] };
            addData(splitItem[0], tableName, tcolumn, tvalue);
            System.out.println("Insert OK : " + String.valueOf(++counter));
        }
        reader.close();

    }





    public static void getResultByRegex(String tableName, String regex)
            throws IOException {
        try {
            int encounter = 0;
            HTable table = new HTable(conf, tableName);
            Filter filter1 = new RowFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator(regex));
            Scan s = new Scan();
            s.setFilter(filter1);
            ResultScanner rs = table.getScanner(s);
            for (Result r : rs) {
                System.out.println("rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {
                    System.out.println("列族: " + new String(keyValue.getFamily())
                            + "\t列: " + new String(keyValue.getQualifier()) + ":"
                            + new String(keyValue.getValue()));
                }
                encounter++;
            }
            System.out.println("一共DASH了:" + String.valueOf(encounter));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static ArrayList<Map<String, String>> getResultArrayMapsByRegex(String tableName, String regex)
            throws IOException {
        long a=System.currentTimeMillis();
        HTable htable = null;
    	ArrayList<Map<String, String>> kvMapsArray = new ArrayList<Map<String, String>>();
        try {
        	htable = new HTable(conf, tableName);
            int encounter = 0;
            Filter filter1 = new RowFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator(regex));
            Scan s = new Scan();
            s.setFilter(filter1);
            ResultScanner rs = htable.getScanner(s);
            for (Result r : rs) {
            	Map<String,String> kvMap = new LinkedHashMap<String, String>();
            	String[] rowKeys = (new String(r.getRow())).replace("$#", "").split("\\$");
            	for (String rowKey : rowKeys) {
					String[] tempKV = rowKey.split(":");
//					String tempValue ;
//					tempValue=(tempKV.length == 1)? rowKey:tempKV[1];
	            	kvMap.put(tempKV[0], (tempKV.length == 1)? rowKey:tempKV[1]);
				}
                for (KeyValue keyValue : r.raw()) {
                	kvMap.put(new String(keyValue.getQualifier()), new String(keyValue.getValue()));
                }
            	kvMapsArray.add(kvMap);
                encounter++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        	if (htable != null) {
            	htable.close();
			}
        }
        System.out.println("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒");
        return kvMapsArray;
    }




    public static void addDataByBlock(String filename) throws IOException {
        // 创建表
        String tableName = "hbase_naive_cube";
        String[] family = { "dimensions" };
        deleteTable(tableName);
        createTable(tableName, family);

        HTable ihtable = new HTable(conf, Bytes.toBytes(tableName));
        ArrayList<Put> arvec = new ArrayList<Put>();
        File f = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String tempstring = null;
        int counter = 0;
        while ((tempstring = reader.readLine()) != null ) {
            String[] splitItem = tempstring.split("\t");
            if (splitItem.length != 4) { continue; }
            String[] tcolumn = { "totalbyte", "avgdowntime", "avgsmalltime" };
            String[] tvalue = { splitItem[1], splitItem[2], splitItem[3] };

            Put put = new Put(Bytes.toBytes(splitItem[0]));
            for (int j = 0; j < tcolumn.length; j++) {
                put.add(Bytes.toBytes("dimensions"), Bytes.toBytes(tcolumn[j]), Bytes.toBytes(tvalue[j]));
            }
            arvec.add(put);
            if (arvec.size() >= 5000) {
                ihtable.put(arvec);
                arvec.clear();
                System.out.println("Insert Block OK: " + String.valueOf(++counter));
            }
        }
        if (arvec.size() != 0) {
            ihtable.put(arvec);
            arvec.clear();
            System.out.println("Insert Final Block: " + String.valueOf(counter));
        }
        reader.close();
    }


    public static void main(String[] args) throws Exception {
        long a=System.currentTimeMillis();
        // 读入数据
//        addDataByBlock("/home/xing/gmcc/hbb/000000_0");
//        System.out.println("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒");
//        System.out.println("Dash OK");
        //getResult("hbase_naive_cube", "ap:cmnet$pr:15$ft:飞利浦_TD-T3500$#");
//        String myregex = "^(ct:(梅州市|汕尾市|河源市)\\$ap:(cmnet|cmwap)\\$sv:[^$]*\\$#)$";
        String myregex = "^(#)$";
        getResultByRegex("hbase_naive_cube", myregex);
        System.out.println("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒");
        
    }/**


    // 为表添加数据

    String[] column1 = { "title", "content", "tag" };
    String[] value1 = {
            "Head First HBase",
            "HBase is the Hadoop database. Use it when you need random, realtime read/write access to your Big Data.",
            "Hadoop,HBase,NoSQL" };
    String[] column2 = { "name", "nickname" };
    String[] value2 = { "nicholas", "lee" };
    addData("rowkey1", "blog2", column1, value1, column2, value2);
    addData("rowkey2", "blog2", column1, value1, column2, value2);
    addData("rowkey3", "blog2", column1, value1, column2, value2);

    // 遍历查询
    getResultScann("blog2", "rowkey4", "rowkey5");
    // 根据row key范围遍历查询
    getResultScann("blog2", "rowkey4", "rowkey5");

    // 查询
    getResult("blog2", "rowkey1");

    // 查询某一列的值
    getResultByColumn("blog2", "rowkey1", "author", "name");

    // 更新列
    updateTable("blog2", "rowkey1", "author", "name", "bin");

    // 查询某一列的值
    getResultByColumn("blog2", "rowkey1", "author", "name");

    // 查询某列的多版本
    getResultByVersion("blog2", "rowkey1", "author", "name");

    // 删除一列
    deleteColumn("blog2", "rowkey1", "author", "nickname");

    // 删除所有列
    deleteAllColumn("blog2", "rowkey1");

    // 删除表
    //deleteTable("blog2");
*/
}
