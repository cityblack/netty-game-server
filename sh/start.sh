#Jenkins deploy shell
#Jenkins will kill all of the process
BUILD_ID=DONTKILLME
# sudo password
passwd="as123456"
# log path "/var/log/game-log"
log="./log"

check_process() {
    if [ $? -eq 1 ]
    then
        echo "Process error..."
        exit 1
    fi
}

create_log_path() {

    if [ ! -d ${1} ]
    then
        echo ${passwd} | sudo mkdir -p ${1}
        echo ${passwd} | sudo chmod -R 777 ${1}
        check_process
    fi
}

echo "maven build"

mvn clean package -DskipTests
check_process

pid=`ps -ef | grep game-starter-*.jar | grep -v grep | awk '{print $2}'`
echo "pid：$pid"
if [ -n "$pid" ]
then
    kill -9 $pid
    echo "kill $pid"
fi

echo "star app."

create_log_path ${log}

app=`echo ${passwd} | sudo nohup java -jar game-start/target/*.jar -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector >${log}/console.log 2>${log}/error.log &`
echo ${app}
echo "finish.."
