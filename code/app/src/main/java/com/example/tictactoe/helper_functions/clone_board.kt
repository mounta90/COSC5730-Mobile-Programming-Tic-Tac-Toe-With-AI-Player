package com.example.tictactoe.helper_functions

fun cloneBoard(board: MutableList<MutableList<Int>>) : MutableList<MutableList<Int>> {
    val boardCopy: MutableList<MutableList<Int>> = mutableListOf()
    for (row in 0..2) {
        val rowList: MutableList<Int> = board[row].map{ position -> position }.toMutableList()
        boardCopy.add(rowList)
    }

    return boardCopy
}