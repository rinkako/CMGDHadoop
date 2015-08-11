echo ">>>>>>> GMCD Beacon Analysis System <<<<<<<"
echo ">>> Dasher: System Start"
echo ">>> Dasher: Now upload CDR to HDFS"
hadoop fs -rmr cdrdat/cdrdat
hadoop fs -put cdrdat cdrdat
echo ">>> Dasher: Now ETL"
spark-submit ETL.py
echo ">>> Dasher: Now building Cube with Hive"
hive -f LoadAndCube.hql
echo ">>> Dasher: Now loading to HBase"
java -cp .:./../hbase-1.0.1.1/lib/*:./../hadoop-2.6.0/lib/* HbaseIOUtils load
echo ">>> Dasher: All Submissions OK!"
