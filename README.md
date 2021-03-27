# Compatability Games
This is a personal project where I work to better my understanding of various programming languages.
The idea is that I write a game in many different languages using a client-server setup
and the goal is to make it so that they can all interact at once.
For example, the server can be run using the Java version and the clients that connect are a 
Python version and a C++ version, but everything works as intended.

## Games
* Connect 4

## Languages
* Python

## Currently under development
* Connect 4/Python

 ## Network protocols for each game (uniform across languages)
 ### Connect 4
 * DROP N
   * Sent to drop a token
   * N is the int of the column to drop into
 * INVALID
   * Used if the DROP is sent to an invalid column
 * WIN N
   * Sent when a player wins
   * N is the int of the winning player number
 * DRAW
   * Sent if the game board fills with no winner
 * BOARD [N]
   * A package containing the new board layout
   * [N] is a series of ints formatted as "N N N N" representing the board
 * ACK
   * In the case that one side needs to send 2 messages in a row, ACK will be sent in between

 Example: (taken from the middle of a game)
 Sender | Protocol
 -------|---------
 Sever  | BOARD [N]
 Client | DROP N
 Server | WIN N
 Client | ACK
 Server | BOARD [N]
