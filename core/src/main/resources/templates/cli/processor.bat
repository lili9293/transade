@echo off
REM copyright...
REM file generated for %TRANSFER_CLASS%.scala

:: PARAMS
:: - transfer doc directory
:: - transfer bin directory
:: - .jar files or ".jar" if no .jar file required
:: - .scala file name
:: - bin directory of the Scala framework provided by Deburnat
:: - suffix required to run the Scala framework provided by Deburnat

:: STEPS: 
:: - go to the directory where the .scala file is located 
:: - check the availability of the Scala framework on the OS (http://superuser.com/questions/175466/determine-if-command-is-recognized-in-a-batch-file)
:: -   IF available THEN use it as Scala framework ELSE use the Scala framework provided by the Deburnat software
:: - generate the .class files
:: - go to the directory where they are located 
:: - run them & exit

cd %1
set GOTOBIN=cd %2
if "%3"==".jar" (
  set JARS=
) else if not "%3"=="" (
  set JARS= -cp "%3"
) else (
  exit
)
::scala %4

scalac >nul 2>&1 && (
  scalac -deprecation -feature -d %2%JARS% %4.scala
  %GOTOBIN%
  
  exit
) || (
  %5scalac.%6 -deprecation -feature -d %2%JARS% %4.scala
  %GOTOBIN%
  %5scala.%6 %4
  exit
)
