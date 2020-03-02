# README

This is a __chess game__ written in java 8, using a javafx ui.

The goal was to create a user-friendly game which allows the user to play against a computer opponent. 
Also, games can be exported or imported in FEN or PGN.

## Table of Contents
1. [About the game](#about-the-game)  
	1.1 [Before you start the game](#before-you-start-the-game)  
	1.2 [The game](#the-game)  
		1.2.1 [The AI opponent](#the-ai-opponent)  
		1.2.2 [Chess notations](#chess-notations)  
		1.2.3 [Editing a game](#editing-a-game)  
		1.2.4. [Thoughts about this project](#thoughts-about-this-project)  
2. [Project structure](#project-structure)  
3. [How to get it](#how-to-get-it)  
	3.1 [How to import it to Intellij IDE](#how-to-import-it-to-intellij-ide)   
	3.2 [How to import it to Eclipse IDE](#how-to-import-it-to-eclipse-ide)   
4. [How to run it](#how-to-run-it)  
	4.1 [Within the IDE](#within-the-ide)  
	4.2 [From a Jar file](#from-a-jar-file)  
		4.2.1 [Build Jar in Intellij IDE](#build-jar-in-intellij-ide)    
		4.2.2 [Build Jar in Eclipse IDE](#build-jar-in-eclipse-ide)  
		4.2.3 [Execute Jar](#execute-jar)  


## About the game
### Before you start the game
![screenshot of settings screen](https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/img/chess_screenshots_settings.png)

Before the game starts, you will be able to adjust some parameters:
* __Color choice__: pieces of chosen color will be initialized at the bottom
* __Rule choice__: as there are some not-sure-if-I-like-this-rules, specific rules can be deactivated
* __Mode choice__: choose how players are controlled (by you or AI)
* __Difficulty choice__: the AI player difficulty can be controlled here. the highest setting is around the difficulty level 5-6 of Chess.com
* __Load__: here, you can load existing games from FEN or PGN code

### The game
![screenshot of game](https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/img/chess_screenshots_game.png)

The game can be played by clicking or by using drag&drop. For more information about how to handle the application see the [manual](https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/manual.html). If you are not very familiar with the chess game itself, there is an additional file which quickly explains the [rules](https://github.com/lpapailiou/chess/blob/master/src/com/chess/resources/rules.html).

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

As the recursion depth has a huge impact on the speed of the caluclations (which increases exponentially), the maximum depth of 4 seemed a suitable compromise to get a more-or-less smart opponent, without having to wait 100 years for the next move.

#### Chess notations
During the game, the moves are logged to the console and can be exported as html file. Here, the __long algebraic notation (LAN)__ is used.  
Example for LAN: 

     e2-e4

For importing and exporting games, the __FEN__ and __PGN__ notations are used. These notations are widely used for chess games.    
  
The __FEN__ notation is a very compact one-line-code for a current board situation. It is useful if a board state should be quickly copied to be recreated later and/or in another chess program.  
Example for FEN: 

    rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

The __PGN__ notation is more complex. It contains metadata about the game (players, location, round, etc.) and the complete list of moves. This means, a whole game can be recreated step by step, which is useful if you want to do analyzes.  
Example for PGN:

    [Event "Casual waste of time"]
    [Site "Your cave, SWITZERLAND"]
    [Date "2019.12.08"]
    [Round "2"]
    [White "Thought, Deep"]
    [Black "Blue, Deep"]
    [Result "0:1"]
    
    1. f4 d5 2. Nc3 d4 3. Nb5 a6 4. Na3 Bg4 5. h3 Bh5 6. d3 Nd7
    7. Bd2 e6 8. Nc4 Qh4+ 9. g3 Qxg3# 0:1

#### Editing a game
I really hated to implement this feature, as in a real chess game, steps should not be undone. Still, it's cool for whait-what-happened-moments or analyzing already played games.

#### Thoughts about this project
I did this project for fun and curiosity. The main motivation I was driven by was 'how do I get a nice chess game with a really smart AI' (and so far, I was not able to beat the AI myself).
As you will see in the code, many of the features (e.g. exports, edit mode) grew in in a quite organic way. If I was more serious about it, the architecture should have been restructured accordingly. 
The main issue of the AI opponent is the speed of the calculations, which limits the recursion depth to 4. Recursion depth could be increased if the move generation handling would be closer to machine code (i.e. bit shifting methods). After all, Java might not be the best programming language for that purpose.
Another issue is testability. As the coupling is not loose enough, I was not going too far with unit tests.
Some other time, I will do further work here or start again from scratch.

## Project structure

* ``com.chess.application``     this package contains the main method (in ``Chess.java``), as well as gui related classes
* ``com.chess.model``               enums, container classes, data classes
* ``com.chess.resources``        text and image files
* ``com.chess.root``        	       contains the 'game engine' (pieces, board, game handling, AI logic)

## How to get it

Clone the repository with:

    git clone https://github.com/lpapailiou/chess your-target-path

Originally, the project was developped with Eclipse. It also runs with the Intellij IDE.

### How to import it to Intellij IDE
1. Go to ``File > New``
2. Pick ``Maven > Project from Existing Sources...``
3. Now, navigate to the directory you cloned it to
4. Select the ``pom.xml`` file and click ``OK``
5. The project will be opened and build

### How to import it to Eclipse IDE
1. Go to ``File > Import``
2. Pick ``Maven > Existing Maven Project``
3. Now, navigate to the directory you cloned it to
4. Pick the root directory ``chess`` and click ``Finish``
5. The project will be opened and build

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
You can download the Jar file directly from the [artifacts folder](https://github.com/lpapailiou/chess/tree/master/classes/artifacts/chess_jar). Alternatively, you can build it yourself.

#### Build Jar in Intellij IDE 
1. Go to ``File > Project Structure...``
2. Go to the ``Artifacts`` tab and add a new ``Jar > From module with dependencies`` entry
3. Select the main class ``Chess`` (here, the main class is in)
4. Click ``Ok`` twice
5. Go to ``Build > Build Artifacts...``
6. Select ``Build``
7. The Jar file is now added to the ``target`` folder within the project structure

#### Build Jar in Eclipse IDE
1. Right click on the project
2. Choose ``Export``
3. Go to ``Java > Runnable JAR file``
4. Click ``Next``
5. Launch configuration: choose ``Chess`` (here, the main class is in)
6. Export destination: the placee you want to save the Jar
7. Choose ``Extract required libraries into generated JAR``
8. Click ``Finish`` to start the Jar generation

#### Execute Jar
Double click on the Jar file directly. 
If nothing happens, you might need to add Java to your PATH variable.

Alternatively, you can start the Jar file from the console with:

    java -jar chess.jar
    
Please make sure you execute it from the correct directory. The naming of the Jar file may vary.


