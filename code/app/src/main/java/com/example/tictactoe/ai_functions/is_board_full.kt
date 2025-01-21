package com.example.tictactoe.ai_functions

fun isBoardFull(board: List<List<Int>>) : Boolean {
    var count = 0
    for (row in 0..2) {
        for(column in 0..2) {
            if (board[row][column] != 0) {
                count++
            }
        }
    }

    return count == 9
}