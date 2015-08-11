#! /usr/bin/env python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import codecs
from operator import add
from pyspark import SparkConf, SparkContext

# Main
sconf = SparkConf().setMaster("local").setAppName("CMGD_Beancon_ETLProcess") 
sc = SparkContext(conf=sconf)
rawrdd = sc.textFile("DIM_SERVICE_SDK")

def mapper(line):
  item = line.split(',')
  return item[0]

if rawrdd != None:
  # split input
  resrdd = rawrdd.map(mapper).distinct()
  file_object = codecs.open('ser_result.txt', 'w', "utf-8")
  for ritem in resrdd.collect():
    file_object.write("{0}\n".format(ritem))
  file_object.close()


