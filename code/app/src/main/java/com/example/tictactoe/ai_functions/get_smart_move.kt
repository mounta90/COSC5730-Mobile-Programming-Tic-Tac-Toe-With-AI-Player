package com.example.tictactoe.ai_functions

import com.example.tictactoe.game_classes.Move
import com.example.tictactoe.game_classes.Player
import com.example.tictactoe.helper_functions.cloneBoard

fun getSmartMove(board: MutableList<MutableList<Int>>, player: Player) : Move {

    // Declare bestMove and bestScore variable at the start...
    // bestMove will hold the best move that is returned from the function...
    // bestScore will help the AI determine the best score of each possible set of moves...
    // If the player = 1, we want to make the bestScore a relatively very low value so the AI has an incentive to play...
    // Same logic for the other player as well, but instead make the bestScore a relatively very high value...
    //      If player = 1 ----> bestScore = 1 * -1 * 100 ----> bestScore = -100
    //      If player = -1 ---> bestScore = -1 * -1 * 100 ---> bestScore = 100
    var bestScore = player.number * -1 * 100
    var bestMove = Move(-1, -1)

    // Get all possible moves from this specific board configuration.
    val moves: List<Move> = getMoves(board)

    // Loop through all moves.
    for (move in moves) {
        // Create a clone of the board, so as to not modify the game board.
        val boardCopy: MutableList<MutableList<Int>> = cloneBoard(board)

        // Play this specific move on the copy board.
        boardCopy[move.row][move.column] = player.number

        // Call the minimax algorithm, after playing this move...
        // Assign the player parameter to be the other player, as it is the other player's turn...
        // The minimax algorithm will return the score of this move.
        val score: Int = when (player) {
            Player.X -> minimax(boardCopy, Player.O)
            Player.O -> minimax(boardCopy, Player.X)
        }

        // Depending on the player...
        //      if player == 1,
        //          set bestScore to the highest score from all positions.
        //          set bestMove to the current move.
        //      if player == -1,
        //          set bestScore to the lowest score from all positions.
        //          set bestMove to the current move.

        when(player) {
            Player.X -> {
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
            Player.O -> {
                if (score < bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
        }

    }

    return bestMove
}