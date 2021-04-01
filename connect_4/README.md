## Connect 4 Protocols:
 Server protocols:
 * MESSAGE S
   * Used for the server to send the client a message
   * S is the string of the message
 * TURN
   * Tell the client to take its turn
 * INVALID
   * Used if the DROP is sent to an invalid column
 * WIN B S
   * Sent when a player wins
   * B is the boolean of whether or not it was you, sent as T for True (you won) and F for False (you lost)
   * S is the string of a message, typically would be the gameboard
 * DRAW S
   * Sent if the game board fills with no winner
   * S is the string of a message, typically would be the gameboard

Client protocols:
 * DROP N
   * Sent to drop a token
   * N is the int of the column to drop into
 * ACK
   * In the case that one side needs to send 2 messages in a row, ACK will be sent in between

 Examples: (only one client shown, {gameboard} would be the full gameboard string)
 
 Example 1 - Game start:
 Sender | Protocol | Full packet
 -------|----------|-------------------------------------------------
 Sever  | MESSAGE  | MESSAGE Welcome to connect 4! You are player 'X'
 Client | ACK      | ACK
 Server | MESSAGE  | MESSAGE Turn 1: Player X's turn:\n{gameboard}
 Client | ACK      | ACK
 Server | TURN     | TURN
 
 Example 2 - Taking turns: 
 Sender | Protocol | Full packet
 -------|----------|-------------------------------------------------
 Server | MESSAGE  | MESSAGE Turn 5: Player X's turn:\n{gameboard}
 Client | ACK      | ACK
 Server | TURN     | TURN
 Client | DROP     | DROP 5
 Server | MESSAGE  | MESSAGE Turn 6: Player O's turn:\n{gameboard}
 Client | ACK      | ACK
 Server | MESSAGE  | MESSAGE Turn 7: Player X's turn:\n{gameboard}
 Client | ACK      | ACK
 Server | TURN     | TURN
 Client | DROP     | DROP 10
 Server | INVALID  | INVALID
 Client | DROP     | DROP 2
 Server | MESSAGE  | MESSAGE Turn 8: Player O's turn:\n{gameboard}
 
 Example 3 - Game End: 
 Sender | Protocol | Full packet
 -------|----------|-------------------------------------------------
 Server | MESSAGE  | MESSAGE Turn 6: Player O's turn:\n{gameboard}
 Client | ACK      | ACK
 Server | MESSAGE  | MESSAGE Turn 7: Player X's turn:\n{gameboard}
 Client | ACK      | ACK
 Server | TURN     | TURN
 Client | DROP     | DROP 0
 Server | WIN      | WIN T {gameboard}
