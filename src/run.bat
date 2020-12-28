@echo off

SET CLASSPATH=".\base\*;.\menu\*;..\words\*;"
SET JFX="%JAVA_HOME%\lib"
SET MODULES="javafx.controls,javafx.base,javafx.graphics"

javac --module-path %JFXX% --add-modules javafx.controls,javafx.base,javafx.graphics -cp %CLASSPATH% .\base\Window.java
java --module-path %JFXX% --add-modules javafx.controls,javafx.base,javafx.graphics -cp %CLASSPATH% base.Window

echo.
echo Deleting .class files . . .
echo ----------------------------
del /s /q *.class
echo ----------------------------

pause