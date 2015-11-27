#!/usr/bin/env sh

echo_error() {
    echo "Usage: ${1} [-p property_string]"
    echo "Property strings have the following format:"
    echo "xxx.yyy.zzz=42;aaa.bbb=xyz;"
    exit 1
}

# place of the overrides file
overrides="/usr/local/tomcat/webapps/contacts/WEB-INF/overrides.properties"
props=""

# Parsing opts
while getopts ":p:" OPTION "$@" ; do
    case $OPTION in
        p)
            props="$OPTARG"
            ;;
        \?)
            echo "Unknown option: -$OPTARG"
            echo_error ${0##*/}
            ;;
        :)
            echo "Option requires an argument: -$OPTARG"
            echo_error ${0##*/}
            ;;
    esac
done

# Parsing properties to overrides file
if [ -n "$props" ]; then
    echo > $overrides
    for prop in $(echo $props | tr ";" "\n"); do
        echo "$prop;" >> $overrides
    done
fi

# Uncomment if you wish to start a shell after running
/usr/local/tomcat/bin/catalina.sh run & /usr/bin/env bash
