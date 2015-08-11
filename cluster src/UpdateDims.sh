echo ">>>>>>> GMCD Beacon Analysis System <<<<<<<"
echo ">>> Dasher: Begin to Update the DIMs data"
hadoop fs -rm cdrdat/DIM_*
echo ">>> Dasher: Now upload new DIMs to HDFS"
hadoop fs -put Dims/* cdrdat
