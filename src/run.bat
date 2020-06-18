@echo off

ECHO.

SET CLASSPATH=".\base\*;.\menu\*;..\words\*;"

javac -cp %CLASSPATH% .\base\Window.java
java -cp %CLASSPATH% base.Window

ECHO Removing class files . . .
ECHO.
ECHO.
del /s /q *.class > nul

pause
