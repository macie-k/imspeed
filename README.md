## imspeed
Typing game inspired by bisqwit's "wspeed"

<br>  

## Download

* Download the latest release [here](https://github.com/emermacko/imspeed/releases)  

<br>

## Overview

<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/85625633-0da71b80-b66c-11ea-928f-b1fe94220b2a.jpg">
</p>  
<br>
<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/85625655-1697ed00-b66c-11ea-880a-833b069b29a5.jpg">
</p>

<br>

## Requirements

* [`Java`](https://www.java.com/download/)
* `JAVA_HOME` System Variable leading to JRE root directory.

**If you want to compile the program yourself you'll need JDK with JFX**  

<br>

## General info

* Make sure `words` folder is in the same directory as the game
* **Custom languages** can be added by editing `.words` files
* `fonts` folder is optional, in case the downloading doesn't work
* If something doesn't work use `--log` option when launching
   
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
