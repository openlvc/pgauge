@echo off
rem NOTE: This script is an an adapdation of the bash version.
rem       You can find that in "codebase/ant".

set DEVELOPMENT_HOME=.
set ANT_HOME=%DEVELOPMENT_HOME%\system\ant\apache-ant-1.9.4
set ANT_LIB=%DEVELOPMENT_HOME%\system\ant-optional\

"%ANT_HOME%\bin\ant.bat" %* -lib %ANT_LIB%
