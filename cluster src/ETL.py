#! /usr/bin/env python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import codecs
import random
from operator import add
from pyspark import SparkConf, SparkContext

randomer = [1001, 1002, 1322, 1320,\
1321, 1018, 1019, 1014,\
1015, 1016, 1017, 1010,\
1011, 1012, 1013, 1009,\
1007, 1006, 1005, 1004,\
1003, 1002, 1001]

def clarifyMapper(line):
  item = line.split(",")
  # data unpack
  a_endtime = item[2]
  a_imei = item[6].encode('utf-8') 
  a_lac = item[9].encode('utf-8')
  a_cellid = item[11].encode('utf-8')
  a_apn = item[16].encode('utf-8')
  a_upbyte = int(item[22])
  a_downbyte = int(item[23])
  a_transtime = int(item[26])
  a_service = int(random.choice(randomer))
  a_ishttp = item[35]
  # calc time period
  period_preptr = a_endtime.find(" ")
  period_postptr = a_endtime.find(":")
  period = int(a_endtime[period_preptr + 1 : period_postptr])
  if period == 0:
    period = 24
  if a_apn == ' ':
    a_apn = 'noapn'
  # clear not http and bad period, then pack for join
  if a_ishttp != '0' and period >= 7:
    prpr = ((a_lac, a_cellid), (a_apn, a_upbyte, a_downbyte, period, a_transtime, a_service, a_imei[0:8]))
    return (prpr[1][5], prpr)
  else:
    return (None, None)

def cityMapper(line):
  item = line.split(",")
  ct_lac = item[0].encode('utf-8')
  ct_cellid = item[1].encode('utf-8')
  ct_city = item[3].encode('utf-8')
  return ((ct_lac, ct_cellid), ct_city)

def serviceMapper(line):
  item = line.split(",")
  sv_groupsdk = int(item[0])
  sv_groupname = item[2].encode('utf-8')
  return (sv_groupsdk, sv_groupname)

def imeiMapper(line):
  item = line.split(",")
  i_imei = item[0].encode('utf-8')
  i_firm = item[1].encode('utf-8')
  i_type = item[2].encode('utf-8')
  return (i_imei, i_firm + "_" + i_type)

def unpackMapper1(x):
  originKV = x[1][0]
  a_apn, a_upb, a_downb, period, a_transtime, a_service, a_imei = originKV[1]
  a_service = x[1][1]
  prpr = (originKV[0], (a_apn, a_upb, a_downb, period, a_transtime, a_service, a_imei))
  return (prpr[1][6], prpr)
  

def unpackMapper2(x):
  originKV = x[1][0]
  a_apn, a_upb, a_downb, period, a_transtime, a_service, a_imei = originKV[1]
  a_firm = x[1][1]
  return (originKV[0], (a_apn, a_upb, a_downb, period, a_transtime, a_service, a_firm))

def finalMapper(x):
  fcity = x[1][1]
  f_apn, f_upbyte, f_downbyte, f_period, f_transtime, f_service, f_firm = x[1][0]
  f_totalbyte = f_upbyte + f_downbyte
  f_bigpack = 0
  f_smallpack = 0
  f_bigtranstime = 0
  f_smalltranstime = 0
  # big pack filter
  if f_downbyte > 500 * 1024:
    f_bigpack = f_downbyte
    f_bigtranstime = f_transtime
  elif f_downbyte < 30 * 1024:
    f_smallpack = 1
    f_smalltranstime = f_transtime
  return ((fcity, f_apn, f_period, f_service, f_firm), (f_totalbyte, f_bigpack, f_smallpack, f_bigtranstime, f_smalltranstime))

def finalReducer(x, y):
  return (x[0] + y[0], x[1] + y[1], x[2] + y[2], x[3] + y[3], x[4] + y[4])

########################### Main ###########################
sconf = SparkConf().setMaster("spark://192.168.1.128:7077").setAppName("GMCC_Beacon_ETLProcess")
sc = SparkContext(conf=sconf)
rawrdd = sc.textFile("hdfs://192.168.1.128:9000/user/hadoop/cdrdat/cdrdat")
cellrdd = sc.textFile("hdfs://192.168.1.128:9000/user/hadoop/cdrdat/DIM_CELL")
servrdd = sc.textFile("hdfs://192.168.1.128:9000/user/hadoop/cdrdat/DIM_SERVICE_SDK")
imeirdd = sc.textFile("hdfs://192.168.1.128:9000/user/hadoop/cdrdat/DIM_IMEI")

if rawrdd != None and cellrdd != None and servrdd != None:
  # split input
  # function distinct is essential for reduce great amount of duplicated ones
  prerdd = rawrdd.map(clarifyMapper).filter(lambda t : t[1] != None)
  lacrdd = cellrdd.map(cityMapper).distinct()
  srgrdd = servrdd.map(serviceMapper).distinct()
  imerdd = imeirdd.map(imeiMapper).distinct()
  # do join
  # note that inner join will be applied to key, hence some pack and unpack is needed
  serrdd = prerdd.join(srgrdd).map(unpackMapper1).join(imerdd).map(unpackMapper2)
  resrdd = serrdd.join(lacrdd).map(finalMapper).reduceByKey(finalReducer)
  # collect and dash
  # using UTF-8, the chinese character ought to be print individual in format function
  file_object = codecs.open('result.txt', 'w', "utf-8")
  for ritem in resrdd.collect():
    file_object.write("{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\n".format(ritem[0][0], ritem[0][1], ritem[0][2], ritem[0][3], ritem[0][4], ritem[1][0], ritem[1][1], ritem[1][2], ritem[1][3], ritem[1][4]))
  file_object.close()
# ~EOF~
