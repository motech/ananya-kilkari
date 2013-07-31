usage(){
  echo "usage:"
  echo "sh deleteCouchdb.sh <couchdb-host>:<couchdb-port> <db-name> <db-name> ..";
  exit $1;
}

exit_script(){
  echo $1;
  exit $2;
}

if [ $# -lt 1 ]; then
  usage 1;
fi


if [ $# -eq 1 ]; then
  exit_script "Nothing to delete." 0
fi

host=$1;

delete(){
    echo "Deleting couchdb $2 on $1 ...";
    response=$(curl -s -S -X DELETE "$1/$2");
    if [ "$?" -ne "0" ]; then
        echo "Error while deleting couchdb $2 on $1.";
        return;
    fi
    echo $response;
    if [[ "$response" == *true* ]]; then
        echo "Couchdb $2 deleted.";
        return;
    fi
    echo "Couchdb $2 could not be deleted.";
}

shift

deleteAll() {
    for db in "$@"; do
        delete $host $db
    done
}

for db in "$@"; do
    deleteAll $db
done

exit_script "Done."
