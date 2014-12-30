@echo off

rem Get environment information
call bin\rti-environment.bat

rem Put the DLLs on the path
SETLOCAL
set PATH=%RTI_DLL_DIR%;%PATH%

rem Fire up pgauge
bin\cpp\pgauge-win32 -scenario=exmanager %*

ENDLOCAL
