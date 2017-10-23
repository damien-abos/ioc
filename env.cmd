@echo off

rem define HOME dir for tools
set "TOOLS=D:\Outils"
set "GOW_HOME=%TOOLS%\Gow"
set "JAVA_HOME=%TOOLS%\Java\jdk1.8.0_91"
set "GIT_HOME=%TOOLS%\PortableGit"

rem define NEWPATH
set NEWPATH=%JAVA_HOME%\bin;%GIT_HOME%\bin;%GOW_HOME%\bin;
set OLDPATH=%PATH%

set PATH=%NEWPATH%;%OLDPATH%;