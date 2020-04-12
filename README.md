## imspeed
Remake of bisqwit's "wspeed" game

<br>

## Overview

<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/78953211-67ba2b00-7ad8-11ea-8477-dec07b537580.jpg">
</p>

<br>

<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/78953222-6f79cf80-7ad8-11ea-95b2-eb9ccda45d55.jpg">
</p>

<br>

* Custom languages can be added by editing `.words` files

<br>

 ## How to use
 
### The .exe file requires [`Java JDK`](https://bit.ly/imspeedJDK) to run

Preferably version 10, as it's the last version with **JavaFX** built in.

<br>

* Make sure `words` folder is in the same directory as the game.

<br>

* Run `setup.bat` to create a system environment variable (only required when using `.exe` file)
* After launching, it will set `%JAVA_JDK%` as system environment variable leading to provided JDK folder
<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/79075444-379aa400-7cf3-11ea-882b-ee42316ba8ce.jpg">
</p>

<br>

* **Launch:**
  * Just double click on `.exe` file or:
  * `java -jar imspeed.jar` - running `setup` is not required when using this option
  * For self testing use either IDE launch option or `run.bat` file: <br>
    ```batch
     @echo off
     javac -cp ".\base\*;.\menu\*;..\words\*;" .\base\Window.java
     java -cp ".\base\*;.\menu\*;..\words\*;" base.Window 2> error_log.txt
    ```
  <br>
  
**Sidenote:** `.exe` file is in fact wrapped jar, allowing for the icon and to run the program with double-click
