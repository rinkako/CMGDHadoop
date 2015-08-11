DROP TABLE IF EXISTS naivebuffer;
DROP TABLE IF EXISTS naivecube;
CREATE TABLE naivebuffer (city string, apn string, period string, service string, firmtype string, sumbyte bigint, bigdownbyte bigint, smallpack bigint, bigtranstime bigint, smalltranstime bigint) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' STORED AS TEXTFILE;
LOAD DATA LOCAL INPATH '/home/hadoop/gmcc/result.txt' OVERWRITE INTO TABLE naivebuffer;
CREATE TABLE naivecube LIKE naivebuffer;
INSERT OVERWRITE TABLE naivecube SELECT city, apn, period, service, firmtype, SUM(sumbyte), SUM(bigdownbyte), SUM(smallpack), SUM(bigtranstime), SUM(smalltranstime) FROM naivebuffer GROUP BY city, apn, period, service, firmtype WITH CUBE;
INSERT OVERWRITE LOCAL DIRECTORY '/home/hadoop/gmcc/hbb' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' SELECT concat_ws("$", concat("ct:", city), concat("ap:", apn), concat("pr:", period), concat("sv:", service), concat("ft:", firmtype), "#"), ROUND(sumbyte, 4), IF(bigdownbyte = 0, 0, ROUND(bigdownbyte/bigtranstime*1000*1000/1024, 4)), IF(smallpack = 0, 0, ROUND(smalltranstime/smallpack, 4)) FROM naivecube;
