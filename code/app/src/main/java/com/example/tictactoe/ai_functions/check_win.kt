package com.example.tictactoe.ai_functions


fun checkWin(board: List<List<Int>>, playerNumber: Int): Boolean {

    // Check vertical win...
    for (row in 0..2) {
        if (board[row][0] == board[row][1] && board[row][1] == board[row][2] && board[row][2] == playerNumber) {
            return true
        }
    }

    // Check horizontal win...
    for (col in 0..2) {
        if (board[0][col] == board[1][col] && board[1][col] == board[2][col] && board[2][col] == playerNumber) {
            return true
        }
    }

    // Check first diagonal win...
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[2][2] == playerNumber) {
        return true
    }

    // Check second diagonal win...
    if (board[2][0] == board[1][1] && board[1][1] == board[0][2] && board[0][2] == playerNumber) {
        return true
    }

    return false
}