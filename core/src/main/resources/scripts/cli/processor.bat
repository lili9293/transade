@echo off
:: An algorithm for data transfer.
:: Project name: transade
:: Date: 10/2/13
:: Time: 1:00 AM
:: @author Patrick Meppe (tapmeppe@gmail.com)
:: This script is used to compile and execute scala class on a windows operating system.

:: PARAMS
:: - transfer doc directory
:: - transfer bin directory
:: - .jar files or ".jar" if no .jar file required
:: - .scala file name
:: - bin directory of the Scala framework provided by the TRANSADé software
:: - suffix required to run the Scala framework provided by the TRANSADé software

:: STEPS
:: - go to the directory where the .scala file is located 
:: - check the availability of the Scala framework on the OS
::   (http://superuser.com/questions/175466/determine-if-command-is-recognized-in-a-batch-file)
:: - IF available THEN use it as Scala framework ELSE use the Scala framework provided by the TRANSADé software
:: - generate the .class files
:: - go to the directory where they are located 
:: - run them & exit

cd %1
set goToBin=cd %2

if "%3" == ".jar" (
  set JARS=
) else if not "%3" == "" (
  set JARS= -cp "%3"
) else (
  exit 1
)

scalac >nul 2>&1 && (
  scalac -deprecation -feature -d %2%JARS% %4.scala
  %goToBin%
  scala %4
  exit
) || (
  %5scalac.%6 -deprecation -feature -d %2%JARS% %4.scala
  %goToBin%
  %5scala.%6 %4
  exit
)
