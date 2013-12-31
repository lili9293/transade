#!/bin/sh
# Project name: transade
# @author Patrick Meppe (tapmeppe@gmail.com)
# Description:
#  An algorithm for the transfer of selected/adapted data
#  from one repository to another.
#
# Date: 1/1/14
# Time: 12:00 AM
#
# This script is used to compile and execute scala class on a linux operating system.

# PARAMS
# - transfer doc directory
# - transfer bin directory
# - .jar files or ".jar" if no .jar file required
# - .scala file name
# - bin directory of the Scala framework provided by the TRANSADé software
# - suffix required to run the Scala framework provided by the TRANSADé software

# STEPS
# - go to the directory where the .scala file is located 
# - check the availability of the Scala framework on the OS
#   (http://stackoverflow.com/questions/7522712/how-to-check-if-command-exists-in-a-shell-script)
# - IF available THEN use it as Scala framework ELSE use the Scala framework provided by the TRANSADé software
# - generate the .class files
# - go to the directory where they are located 
# - run them & exit

cd $1
goToBin(){ cd $2; }

if ["$3" -eq ".jar"]; then
    JARS=
elif ["$3" -ne ""]; then
    JARS= -cp "$3"
else
    exit 1
fi

if type scalac >/dev/null; then #scalac is available on the machine
    scalac -deprecation -feature -d $2${JARS} $4.scala
    goToBin
    scala $4;
else #scalac isn't supported by th machine yet
    $5scalac.$6 -deprecation -feature -d $2${JARS} $4.scala
    goToBin
    $5scala.$6 $4
fi

exit