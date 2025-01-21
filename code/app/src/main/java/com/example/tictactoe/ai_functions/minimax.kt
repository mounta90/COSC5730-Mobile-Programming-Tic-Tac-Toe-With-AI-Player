package com.example.tictactoe.ai_functions

import com.example.tictactoe.game_classes.Player
import com.example.tictactoe.helper_functions.cloneBoard

fun minimax(board: MutableList<MutableList<Int>>, player: Player) : Int {

    // -----------------------------------
    // BASE CASES
    // -----------------------------------
    if (checkWin(board, player.number)) {
        return player.number
    }

    if (checkWin(board, -player.number)) {
        return -player.number
    }

    if (isBoardFull(board)) {
        return 0
    }

    // -----------------------------------
    // RECURSIVE PART OF MINIMAX
    // -----------------------------------

    // Declare bestScore variable at the start...
    // This will help the AI determine the best score of each possible move...
    // If the player = 1, we want to make the bestScore a relatively very low value so the AI has an incentive to play...
    // Same logic for the other player as well, but instead make the bestScore a relatively very high value...
    //      If player = 1 ----> bestScore = 1 * -1 * 100 ----> bestScore = -100
    //      If player = -1 ---> bestScore = -1 * -1 * 100 ---> bestScore = 100
    var bestScore = player.number * -1 * 100

    // Get all possible moves from a specific board configuration.
    val moves = getMoves(board)

    // Loop through all possible moves ...
    // Recursively call minimax algorithm until a score is returned for each possible move...
    // Return :
    //      if player = 1 ----> the highest possible value
    //      if player = -1 ---> the lowest possible value
    for (move in moves) {

        // Create a copy of the board, so to not modify the game board.
        val boardCopy: MutableList<MutableList<Int>> = cloneBoard(board)

        // Play this specific move.
        boardCopy[move.row][move.column] = player.number

        // Call the minimax algorithm, after playing this move...
        // Assign the player parameter to be the other player, as it is the other player's turn...
        // The minimax algorithm will return the score of this move.
        val score: Int = when (player) {
            Player.X -> minimax(boardCopy, Player.O)
            Player.O -> minimax(boardCopy, Player.X)
        }

        // Depending on the player...
        //      if player == 1, set bestScore to the highest score from all positions.
        //      if player == -1, set bestScore to the lowest score from all positions.
        when (player) {
            Player.X -> {
                if (score > bestScore) bestScore = score
            }
            Player.O -> {
                if (score < bestScore) bestScore = score
            }
        }

    }

    return bestScore
}