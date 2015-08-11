import java.io.IOException;
import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
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
     * @tableName 表名
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

    /*
     * 为表添加数据（适合知道有多少列族的固定表）
     * @rowKey rowKey
     * @tableName 表名
     * @column1 第一个列族列表
     * @value1 第一个列的值的列表
     * @column2 第二个列族列表
     * @value2 第二个列的值的列表
     */
    public static void addData(String rowKey, String tableName,
            String[] column1, String[] value1)
            throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey
        // HTabel负责跟记录相关的操作如增删改查等
        HTable ihtable = new HTable(conf, Bytes.toBytes("hbase_naive_cube"));

        //for (int i = 0; i < ihcolumnFamilies.length; i++) {
        //    String familyName = ihcolumnFamilies[i].getNameAsString(); // 获取列族名
        for (int j = 0; j < column1.length; j++) {
            put.add(Bytes.toBytes("dims"),
                    Bytes.toBytes(column1[j]), Bytes.toBytes(value1[j]));
        }
        //}
        ihtable.put(put);
        //System.out.println("add data Success!");
    }

    /*
     * 根据rwokey查询
     * @rowKey rowKey
     * @tableName 表名
     */
    public static Result getResult(String tableName, String rowKey)
            throws IOException {
        Get get = new Get(Bytes.toBytes(rowKey));
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Result result = table.get(get);
        for (KeyValue kv : result.list()) {
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
        return result;
    }

    /*
     * 遍历查询hbase表
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
                    System.out.println("family:" + Bytes.toString(kv.getFamily()));
                    System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
                    System.out.println("value:" + Bytes.toString(kv.getValue()));
                    System.out.println("timestamp:" + kv.getTimestamp());
                    System.out.println("-------------------------------------------");
                }
            }
        } finally {
            rs.close();
        }
    }

    /*
     * 遍历查询hbase表
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
                    System.out.println("family:" + Bytes.toString(kv.getFamily()));
                    System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
                    System.out.println("value:" + Bytes.toString(kv.getValue()));
                    System.out.println("timestamp:" + kv.getTimestamp());
                    System.out.println("-------------------------------------------");
                }
            }
        } finally {
            rs.close();
        }
    }

    /*
     * 查询表中的某一列
     * @tableName 表名
     * @rowKey rowKey
     */
    public static void getResultByColumn(String tableName, String rowKey,
            String familyName, String columnName) throws IOException {
        HTable table = new HTable(conf, Bytes.toBytes(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
        Result result = table.get(get);
        for (KeyValue kv : result.list()) {
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
    }

    /*
     * 更新表中的某列
     * @tableName 表名
     * @rowKey rowKey
     * @familyName 列族名
     * @columnName 列名
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
     * @tableName 表名
     * @rowKey rowKey
     * @familyName 列族名
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
            System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
    }

    /*
     * 删除指定的列
     * @tableName 表名
     * @rowKey rowKey
     * @familyName 列族名
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
     * @tableName 表名
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

    /*
     * 按条插入
     * @filename Cube文件名
     */
    public static void splitInput(String filename) throws IOException {
        // 创建表
        String tableName = "hbase_naive_cube";
        String[] family = { "dimensions" };
        deleteTable(tableName);
        createTable(tableName, family);
        // 读入Cube
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

    /*
     * 正则查询
     * @tableName 表名
     * @regex 正则式
     */
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

    /*
     * 分页插入
     * @fileName Cube文件
     */
    public static void addDataByBlock(String filename) throws IOException {
        // 创建表
        String tableName = "hbase_naive_cube";
        String[] family = { "dims" };
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
            String[] tcolumn = { "ttb", "bps", "sad" };
            String[] tvalue = { splitItem[1], splitItem[2], splitItem[3] };
            Put put = new Put(Bytes.toBytes(splitItem[0]));
            for (int j = 0; j < tcolumn.length; j++) {
                put.add(Bytes.toBytes("dims"), Bytes.toBytes(tcolumn[j]), Bytes.toBytes(tvalue[j]));
            }
            arvec.add(put);
            if (arvec.size() >= 5000) {
                ihtable.put(arvec);
                arvec.clear();
                System.out.println("Insert Block OK: " + String.valueOf(counter++));
            }
        }
        // 清空缓冲区
        if (arvec.size() != 0) {
            ihtable.put(arvec);
            arvec.clear();
            System.out.println("Insert Final Block: " + String.valueOf(counter));
        }
        reader.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Bad arguments!");
            return;
        }
        long a=System.currentTimeMillis();
        if (args[0].equals("load")) {
            addDataByBlock("hbb/000000_0");
        }
        else if (args[0].equals("selecttest")) {
            //String myregex = "^(ct:[^$]*\\$ap:[^$]*\\$#)$"; // 组合
            String myregex = "^(sv:[^$]*\\$#)$";
            //String myregex = "^([^$]+(TD-MX3_M356)\\$#)$";  // 只查型号
            getResultByRegex("hbase_naive_cube", myregex);
        }
        System.out.println("\nHBaseIOUtils OK, 执行耗时: "+ (System.currentTimeMillis() - a) / 1000f + " 秒");
    }
}
