## imspeed
Remake of bisqwit's "wspeed" game

<br>

## Overview

<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/80281643-f059dd80-870c-11ea-9e04-38ca190bef60.jpg">
</p>

<br>

<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/78953222-6f79cf80-7ad8-11ea-95b2-eb9ccda45d55.jpg">
</p>

<br>

* Custom languages can be added by editing `.words` files

<br>

 ## How to use
 
### The .exe file requires [`Java JDK`](https://bit.ly/imspeedJDK2) to run

Preferably version 10, as it's the last version with **JavaFX** built in.
* Make sure `words` folder is in the same directory as the game.

<br>

### For Windows:

* Run `setup.bat` to create a system environment variable (only required when using `.exe` file)
* After launching, it will set `%JAVA_JDK%` as system environment variable leading to provided JDK folder <br>
<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/79079528-febcf800-7d0f-11ea-8b74-f6a81d506af0.jpg">
</p>

### For Linux:

* [`JDK`](https://bit.ly/imspeedJDK2) with JavaFX is the only requirement for Unix based systems
* Launch as shown below

<br>

* **Launch:**
  * Just double click on `.exe` file or:
  * `java -jar imspeed.jar` - running `setup` is not required when using this option
  * For self testing use either the IDE launch option or the `run.bat` file: <br>
    ```batch
     javac -cp ".\base\*;.\menu\*;..\words\*;" .\base\Window.java
     java -cp ".\base\*;.\menu\*;..\words\*;" base.Window 2> error_log.txt
    ```
  <br>
  
**Sidenote:** `.exe` file is in fact wrapped jar, allowing for the icon and to run the program with double-click

<br>
