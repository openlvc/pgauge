@echo off

REM If you want to use Portico, make sure the following is uncommented
set RTI_HOME=[rtihome]
set RTI_JAR=%RTI_HOME%\lib\portico.jar
REM set JAVA_HOME=<your java install here>
set RTI_DLL_DIR=%RTI_HOME%\bin;%JAVA_HOME%\jre\bin\client
set RTI_RID_FILE=etc\portico\RTI.rid

REM If you want to use RTI-NG, make sure the following is uncommented and ensure that
REM RTI_HOME and RTI_BUILD_TYPE are also set appropriately
REM set RTI_HOME=C:\Program Files\DMSO\RTI1.3NG-V6
REM set RTI_BUILD_TYPE=Win2000-VC6
REM set RTI_JAR="%RTI_HOME%\%RTI_BUILD_TYPE%\apps\javaBinding\javalib\RTI-1.3NGv6.jar"
REM set RTI_DLL_DIR=%RTI_HOME%\%RTI_BUILD_TYPE%\bin
REM set RTI_RID_FILE=etc\ng6\RTI.rid
