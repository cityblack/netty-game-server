#Jenkins deploy shell
#Jenkins will kill all of the process
BUILD_ID=DONTKILLME

log="/var/log/game-log"

check_process() {
    if [ $? -eq 1 ]
    then   
        echo "error"
        exit 1  
    fi
}

create_log_path() {

    if [ ! -d ${1} ]
    then
        sudo mkdir ${1}
        sudo chmod -R 777 ${1}
        check_process
    fi
}

echo "maven build"

mvn clean package -DskipTests
check_process

pid=`ps -ef | grep game-starter-1.0-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
echo "pid：$pid"
if [ -n "$pid" ]
then
    kill -9 $pid
    echo "kill $pid"
fi

echo "star app."

create_log_path ${log}

app=`sudo nohup java -jar game-start/target/*.jar >${log}/console.log 2>${log}/error.log &`
echo ${app}
echo "finish.."