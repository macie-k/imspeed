@echo off

javac -cp ".\base\*;.\menu\*;..\words\*;" .\base\Window.java
java -cp ".\base\*;.\menu\*;..\words\*;" base.Window 2> error_log.txt

echo
echo Deleting .class files . . .
echo ----------------------------
del /s /q *.class
echo ----------------------------

pause
