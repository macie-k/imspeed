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


## How to use

<br>

**The .exe file requires [`Java JDK`](https://bit.ly/imspeedJDK2) to run**

Preferably version 10, as it's the last version with **JavaFX** built in.

<br>

* Make sure `words` folder is in the same directory as the game
* **Custom languages** can be added by editing `.words` files
* `fonts` folder is optional, in case the downloading doesn't work
* Any critical errors should be logged in the `error_log.txt` file
* `setup.bat` file is required **only** when using an `.exe` file

<br>

### [ ! ] Scoreboard
File `score.board` stores your scores and it will be created in **EVERY** root folder the game is started from.  
If you want to keep all your scores in one file, **always** start the game from the same location.

*[ in-game scoreboard is still in work, but it will work with your saved scores ].*

<br>

### For Windows:

* Run `setup.bat` to create a system environment variable (**only required** when using an `.exe` file)
* After launching, it will set `%JAVA_JDK%` as system environment variable leading to the provided JDK folder <br>
<p align="center">
  <img width="auto" height="auto" src="https://user-images.githubusercontent.com/25122875/79079528-febcf800-7d0f-11ea-8b74-f6a81d506af0.jpg">
</p>

### For Linux:

* [`JDK`](https://bit.ly/imspeedJDK2) with JavaFX is the only requirement for the Unix based systems
* Launch as shown below

<br>

### Launching:
  * Just double click on `.exe` file or:
  * `java -jar imspeed.jar` - running `setup` is not required when using this option
  * For self testing use either the IDE launch option or the `run.bat` file: <br>
    ```batch
     javac -cp ".\base\*;.\menu\*;..\words\*;" .\base\Window.java
     java -cp ".\base\*;.\menu\*;..\words\*;" base.Window 2> error_log.txt
    ```
  <br>
  
**Sidenote:** `.exe` file is in fact a wrapped jar, allowing for the icon and to run the program with a double-click.

<br>
