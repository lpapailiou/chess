# README

This is a __chess game__ written in java 8, using a javafx ui.

The goal was to create a user-friendly game which allows the user to play against a computer opponent. 
Also, games can be exported or imported in FEN or PGN.

## Table of Contents
1. [About the game](#about-the-game)<br>
	1.1 [Before you start the game](#before-you-start-the-game)<br>
	1.2 [The game](#the-game)<br>
		1.2.1 [The AI opponent](#the-ai-opponent)<br>
		1.2.2 [Chess notations](#chess-notations)<br>
		1.2.3 [Editing a game](#editing-a-game)<br>
		1.2.4. [Thoughts about this project](#thoughts-about-this-project)<br>
2. [Project structure](#project-structure)<br>
3. [How to get it](#how-to-get-it)<br>
4. [How to run it](#how-to-run-it)<br>
	4.1 [Within the IDE](#within-the-ide)<br>
	4.2 [From a Jar file](#from-a-jar-file)<br>
		4.2.1 [Build Jar in Eclipse IDE](#build-jar-in-eclipse-ide)<br>
		4.2.2 [Build Jar in Intellij IDE](#build-jar-in-intellij-ide)<br>
		4.2.3 [Execute Jar](#execute-jar)<br>


## About the game
### Before you start the game
![screenshot of settings screen](https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/img/chess_screenshots_settings.png)

Before the game starts, you will be able to adjust some parameters:
* <b>Color choice</b>: pieces of chosen color will be initialized at the bottom
* <b>Rule choice</b>: as there are some not-sure-if-I-like-this-rules, specific rules can be deactivated
* <b>Mode choice</b>: choose how players are controlled (by you or AI)
* <b>Difficulty choice</b>: the AI player difficulty can be controlled here. the highest setting is around the difficulty level 5-6 of Chess.com
* <b>Load</b>: here, you can load existing games from FEN or PGN code

### The game
![screenshot of game](https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/img/chess_screenshots_game.png)

The game can be played by clicking or by using drag&drop. For more information about how to handle the application see the <a href="https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/manual.html" target="_blank">manual</a>. If you are not very familiar with the chess game itself, there is an additional file which quickly explains the <a href="https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/rules.html" target="_blank">rules</a>.

#### The AI opponent
The AI opponent was created by using a minimax algorithm with alpha-beta-pruning. This means, that the opponent will simulate a few moves ahead and then choose the move which seems to avoid the worst situation while maximizing the chance to win.

The algorithm takes following to account:
* Recursion depth (steps of simulating moves ahead)
* Heuristics to add recursion depth for the endgame
* Piece values (e.g. a queen is valued higher than a pawn)
* Piece position values (e.g. a knight is valued higher when positioned at the center of the board, as it has more move options there)
* So called 'spasm-parameter', which will generate random moves occasionally to appear more human
* Library of known chess openings to obtain a good starting position
* Heuristics to avoid a a draw by stalemate
* Heuristics to avoid a draw by threefold repetition
* Heuristics to avoid a draw by 50 'moves of no value'

Depending on the rule and difficulty settings, the behavior of the AI opponent will change accordingly.
Example: On the highest difficulty setting we have a recursion depth of 4, while on the lowest difficulty setting we have a no recursion at all.

As the recursion depth has a hughe impact on the speed of the caluclations (which increases exponentially), the maximum depth of 4 seemed a suitable compromise to get a more-or-less smart opponent, without having to wait 100 years for the next move.

#### Chess notations
During the game, the moves are logged to the console and can be exported as html file. Here, the <b>long algebraic notation (LAN)</b> is used.
For importing and exporting games, the <b>FEN</b> and <b>PGN</b> notations are used. These notations are widely used for chess games.<br>
The FEN notation is a very compact one-line-code for a current board situation. It is useful if a board state should be quickly copied to be recreated later and/or in another chess program.<br>
The PGN notation is more complex. It contains metadata about the game (players, location, round, etc.) and the complete list of moves. This means, a whole game can be recreated step by step, which is useful if you want to do analyzes.

#### Editing a game
I really hated to implement this feature, as in a real chess game, steps should not be undone. Still, it's cool for whait-what-happened-moments or analyzing already played games.

#### Thoughts about this project
I did this project for fun and curiosity. The main motivation I was driven by was 'how do I get a nice chess game with a really smart AI' (and so far, I was not able to beat the AI myself).
As you will see in the code, many of the features (e.g. exports, edit mode) grew in in a quite organic way. If I was more serious about it, the architecture should have been restructured accordingly. 
The main issue of the AI opponent is the speed of the calculations, which limits the recursion depth to 4. Recursion depth could be increased if the move generation handling would be closer to machine code (i.e. bit shifting methods). After all, Java might not be the best programming language for that purpose.
Another issue is testability. As the coupling is not loose enough, I was not going too far with unit tests.
Some other time, I will do further work here or start again from scratch.

## Project structure

* ``application``     this package contains the main method (in ``Chess.java``), as well as gui related classes
* ``model``               enums, container classes, data classes
* ``resources``       text and image files
* ``root``        	       contains the 'game engine' (pieces, board, game handling, AI logic)

## How to get it

Clone the repository with:

    git clone https://github.com/lpapailiou/chess your-target-path

The project should automatically build with maven. 

Originally, the project was developped with Eclipse. It also runs with the Intellij IDE.

## How to run it

### Within the IDE
You can directly run it within the IDE.

In case you experience weird UI behavior (buttons look weird), it may be a DPI scaling issue known to occur with Windows 10 notebooks.
To fix it, do following steps:
1. Find the ``java.exe`` the game is running with (check Task Manager)
2. Rightclick on the ``java.exe`` and go to ``Properties``
3. Open the ``Compability`` tab
4. Check ``Override high DPI scaling behavior``
5. Choose ``System`` for ``Scaling performed by:``
6. Run the game again

### From a Jar file
#### Build Jar in Eclipse IDE
1. Right click on the project
2. Choose ``Export``
3. Go to ``Java > Runnable JAR file``
4. Click ``Next``
5. Launch configuration: choose ``Chess`` (here, the main class is in)
6. Export destination: the placee you want to save the Jar
7. Choose ``Extract required libraries into generated JAR``
8. Click ``Finish`` to start the Jar generation

#### Build Jar in Intellij IDE 
1. Go to ``File > Project Structure...``
2. Go to the ``Artifacts`` tab and add a new ``Jar > From module with dependencies`` entry
3. Select the main class ``Chess`` (here, the main class is in)
4. Click ``Ok`` twice
5. Go to ``Build > Build Artifacts...``
6. Select ``Build``
7. The Jar file is now added to the ``target folder`` within the project structure

#### Execute Jar
Double click on the Jar file directly. 
If nothing happens, you might need to add Java to your PATH variable.

Alternatively, you can start the Jar file from the console with:

    java -jar chess.jar
    
Please make sure you execute it from the correct directory. The naming of the Jar file may vary.


