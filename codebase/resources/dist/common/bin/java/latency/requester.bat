@echo off

rem Get environment information
call bin\rti-environment.bat

SETLOCAL
set PATH=%RTI_DLL_DIR%;%PATH%

rem java -Dcom.sun.management.jmxremote -cp %RTI_JAR%;lib\pgauge.jar org.portico.pgauge.latency.Requester %*
java -cp %RTI_JAR%;lib\pgauge.jar org.portico.pgauge.latency.Requester %*

ENDLOCAL
