## imspeed
Remake of bisqwit's "wspeed" game

<br>

## Overview

<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/80863852-34099580-8c7f-11ea-9833-25b7a4fcdfa6.jpg">
</p>  
<br>
<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/80805576-b11e0780-8bb8-11ea-9c77-81396409e225.jpg">
</p>

<br>

## Requirements

* [`Java`](https://www.java.com/download/)
* `JAVA_HOME` System Variable leading to JRE root directory.

**If you want to compile the program yourself you'll need JDK with JFX**  

<br>  

## General info

<br>

* Make sure `words` folder is in the same directory as the game
* **Custom languages** can be added by editing `.words` files
* `fonts` folder is optional, in case the downloading doesn't work
* Any critical errors should be logged in the `error_log.txt` file
   
<br>

### [ ! ] Scores
File `score.board` stores your scores and it will be created in **EVERY** root folder the game is started from.  
If you want to keep all your scores in one file, **always** start the game from the same location.

*[ in-game scoreboard is still in work, but it will work with your saved scores ].*

<br>

## How to use:
  * Just double click on `.exe` file or:
  * `java -jar imspeed.jar`
  * For self testing use either the IDE launch option or the `run.bat` file: <br>
    ```batch
     javac -cp ".\base\*;.\menu\*;..\words\*;" .\base\Window.java
     java -cp ".\base\*;.\menu\*;..\words\*;" base.Window 2> error_log.txt
    ```
  <br>  
  
**Sidenote:** `.exe` file is in fact a wrapped jar, allowing for the icon and to run the program with a double-click.
<br>
