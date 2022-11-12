#!/usr/bin/env bash
#
# Author: wangweining
# Update: 2022-10-05
# Description: 启动 java 进程脚本

# set -x
version="0.0.1"
base_dir=/opt/soft/marketgo/web
heap_dump_path=/opt/soft/marketgo/logs/web/jvmlog
gc_log=$heap_dump_path/jvm_gc.log
app=$base_dir/mktgo-web-0.0.1.jar

java_opts="
-server
-Xmx512m
-Xms512m
-Xmn256m
-XX:SurvivorRatio=8
-XX:MaxMetaspaceSize=256m 
-XX:MetaspaceSize=256m
-XX:+UseParNewGC
-XX:+UseConcMarkSweepGC 
-XX:+UseCMSInitiatingOccupancyOnly 
-XX:+UseCMSCompactAtFullCollection 
-XX:CMSFullGCsBeforeCompaction=0
-XX:CMSInitiatingOccupancyFraction=70 
-XX:+CMSClassUnloadingEnabled 
-XX:+ExplicitGCInvokesConcurrent 
-XX:+DisableExplicitGC 
-XX:+TieredCompilation 
-XX:CICompilerCount=8 
-XX:+ParallelRefProcEnabled
-Xloggc:$gc_log 
-Dcatalina.home=$base_dir 
-verbose:gc 
-XX:+PrintGC
-XX:+PrintGCDetails 
-XX:+PrintGCDateStamps  
-XX:+PrintGCTimeStamps 
-XX:+PrintHeapAtGC
-XX:+PrintTenuringDistribution 
-XX:+PrintGCApplicationStoppedTime
-XX:+UseGCLogFileRotation  
-XX:NumberOfGCLogFiles=10 
-XX:GCLogFileSize=1G 
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=$heap_dump_path
"

function help() {
  echo "Version: $version"
  echo "Usage: $(basename "$0") variable"
  echo "  version: Display version"
  echo "  start: Startup"
  echo "  stop: Stop"
  echo "  reload: Reload"
  echo "  help: Print help message"
}

function pre_start() {
  mkdir -p $heap_dump_path
  touch $gc_log
  if [ ! -e $app ];
  then
    red "$app not exist!"
    exit 1
  fi
}

function start() {
  pre_start
  blue "启动进程......"
  nohup java $java_opts -jar $app &
}
function stop() {
  green "开始 停止 web 服务"
  local result=success
  if [ "$result" == "success" ];
  then
    green "web 服务 停止成功"
    sleep 2
    local pid
    pid=$(jps |grep mktgo-web | awk '{print $1}')
    green "停止 web 应用服务"
    kill "$pid"
    sleep 5
    while jps |grep mktgo-web
    do
      red "发现进程没有停掉，将采用 kill -9 $pid 强制结束进程！！！"
      kill -9 "$pid"
      red "kill -9 $pid 结束进程！！！"
    done
    green "停止 web 应用成功"
  fi
}

function reload() {
    stop
    start
}

function red(){
    echo -e "\033[31m\033[01m<--------------------- $1 --------------------->\033[0m"
}

function blue(){
    echo -e "\033[34m<--------------------- $1 --------------------->\033[0m"
}

function green(){
    echo -e "\033[32m<--------------------- $1 --------------------->\033[0m"
}

function main() {
  case $1 in
    help)
      help
      ;;
    version)
      echo "Version: $version"
      ;;
    start)
      start
      ;;
    stop)
      stop
      ;;
    reload)
      reload
      ;;
    *)
      help
  esac
}
main $1
