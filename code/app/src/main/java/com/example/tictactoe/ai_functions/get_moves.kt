package com.example.tictactoe.ai_functions

import com.example.tictactoe.game_classes.Move

fun getMoves(board: MutableList<MutableList<Int>>) : List<Move> {

    val moves: MutableList<Move> = mutableListOf()

    for(row in 0..2) {
        for(column in 0..2) {

            if (board[row][column] == 0) {
                val move = Move(row, column)
                moves.add(move)
            }

        }
    }

    return moves
}