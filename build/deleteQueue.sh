cookies_file=activemq.cookies;

usage(){
  echo "usage:"
  echo "sh deleteQueue.sh <activemq-host>:<activemq-port> <queue-name>";
  exit $1;
}

exit_script(){
  rm -f $cookies_file;
  echo $1;
  exit $2;
}

if [ $# -ne 2 ]; then
  usage 1;
fi

response=$(curl -b $cookies_file -c $cookies_file -s -S "$1/admin/queues.jsp");

if [ "$?" -ne "0" ]; then
  exit_script "Error while connecting to activemq admin on $1." 1;
fi 

url=$(echo "$response" | grep -i \=$2\&.*secret.*delete | tail -1);

if [ "$url" == "" ]; then
  exit_script "Queue $2 not found." 0
fi

url=${url#*\"};
url=${url%\"*};

curl -b $cookies_file -c $cookies_file -s -S "$1/admin/$url";
if [ "$?" -ne "0" ]; then
  exit_script "Error while connecting to activemq admin on $1" 1;
fi

exit_script "Queue $2 deleted." 0;
