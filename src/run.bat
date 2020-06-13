@echo off

ECHO.
ECHO Temporarly moving 'jrt-fs.jar' file to AppData. . .
MOVE /Y "%JAVA_HOME%\lib\jrt-fs.jar" "%APPDATA%" > nul

SET CLASSPATH=".\base\*;.\menu\*;..\words\*;"
SET JFX="%JAVA_HOME%\lib"
SET MODULES="javafx.controls,javafx.base,javafx.graphics"

javac --module-path %JFX% --add-modules %MODULES% -cp %CLASSPATH% .\base\Window.java
java --module-path %JFX% --add-modules %MODULES% -cp %CLASSPATH% base.Window

ECHO Restoring 'jrt-fs.jar' file . . .
ECHO.
MOVE /Y "%APPDATA%\jrt-fs.jar" "%JAVA_HOME%\lib" > nul

ECHO Removing class files . . .
ECHO.
ECHO.
del /s /q *.class > nul

pause
