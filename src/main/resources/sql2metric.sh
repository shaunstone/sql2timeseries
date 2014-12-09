
#!/bin/bash
#######################################################################
#todo add the ability to stop this for creating a new script every time
#
# There is only one persistet artifact that is created by this script and that is the one which
# stores the timestamp that the last run took place
#
#
#
#
#
#
#
#
#
#
#
#######################################################################
SCRIPTNAME="$0"



function extractJar(){
base64 -d > $EXECFILE << "EOF"
${base64}
EOF

chmod 755 $EXECFILE
}



TMPDIR=`mktemp -d /tmp/sql2metric.XXXXXX`
EXECFILE=$TMPDIR/sql2metric.jar

echo "created $TMPDIR which will hold the executable"
extractJar
#sleep 5;
#echo $(which java)

(
java -jar $EXECFILE "$@"
) </dev/null > /dev/null 2>&1 &
disown

#when exiting cleanup the script
echo "cleaning up $TMPDIR"
trap "rm -rf $TMPDIR" EXIT


