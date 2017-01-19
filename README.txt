Sunny Patel
README:

*** All the code was written in one file to meet the instructors request ***

This was a class project for Computational Game Theory.
Python scripts were written by instructor to run our agent. FourInARow is a computerized agent capable of winning against a random player. 

Game: 109 piece game board
Objective: Get 4 of your pieces in a row
Moves: Can only move 1-2 spaces from current piece and cannot stack pieces 


This folder contains 4 files. They are:
The environment files that will run your programs:p2env_mac.py & p2env_win_linux.py
A random player: someRandomPlayer.py
A text file that should contain the player(s) file names: players.txt

Requirments:
1- If your OS is windows you should run the python script from a bash-like terminal. You could install cygwin: https://cygwin.com/install.html
2- If your OS is mac, you should install coreutils so that you could have a timeout like feature to stop each process after 1 sec: http://stackoverflow.com/questions/3504945/timeout-command-on-mac-os-x 
3- You should also always flush whatever you print in the standard output! This is very critical since the environment is killing the processes after 1 second and we don't want the memory management capabilities of java, python, c++ to prevent your output to be printed in the output file. http://stackoverflow.com/questions/2340106/what-is-the-purpose-of-flush-in-java-streams
4- you need to have python (versions after 2.7 all should be fine)


How run your program(s):
step 1) you should update the players.txt file and add your programs name there. Right now it contains a random player. If you want to play vs a random player either keep the random player and add your player in another line or simply just add your player in place of the random player. The environment itself has a built-in random player that is activated if the number of players in the players.txt file is 1.
If you want to play against a friend of yours you should add both of your players in the folder and update the players.txt file to include your players. Each player in a seperate line. The order does matter, the first player is the player (blue) that is added to the first line.
step 2) in terminal, cygwin run the environment: python p2env_mac.py for mac terminal or python p2env_win_linux.py in cygwin for windows or for linux.

Some info about the outputs:
If your player is making a wrong move, this will be reported in the standard ouput and you can see it in terminal but the game does not stop. The environment make a valid random move for you so that the game continues.