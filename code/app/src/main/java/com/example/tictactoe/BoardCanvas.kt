package com.example.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import java.util.Random
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.tictactoe.ai_functions.checkWin
import com.example.tictactoe.ai_functions.getSmartMove
import com.example.tictactoe.ai_functions.isBoardFull
import com.example.tictactoe.game_classes.Move
import com.example.tictactoe.game_classes.Player

class BoardCanvas(
    context: Context,
    attributes: AttributeSet? = null
) : View(context, attributes) {
    private val appViewModel = ViewModelProvider(context as ViewModelStoreOwner)[AppViewModel::class.java]

    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap

    private val backgroundColor = context.getColor(R.color.black)
    private val boardColor = context.getColor(R.color.white)
    private lateinit var boardFrame: Rect

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private val padding = 64

    private var boardLeft = 0
    private var boardRight = 0
    private var boardTop = 0
    private var boardBottom = 0

    private var stepSizeX = 0f
    private var firstThirdX = 0f
    private var secondThirdX = 0f

    private var stepSizeY = 0f
    private var firstThirdY = 0f
    private var secondThirdY = 0f

    // --------------------------------------
    // When a player taps on a screen position...
    // these variables need to be updated for...
    // the player shape to be drawn based...
    // on the board tile sizes and positions.
    // --------------------------------------
    private var leftToRightTopCornerX = 0f
    private var leftToRightTopCornerY = 0f
    private var leftToRightBottomCornerX = 0f
    private var leftToRightBottomCornerY = 0f
    private var rightToLeftTopCornerX = 0f
    private var rightToLeftTopCornerY = 0f
    private var rightToLeftBottomCornerX = 0f
    private var rightToLeftBottomCornerY = 0f
    // --------------------------------------

    private val boardPainter = Paint().apply {
        color = boardColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.SQUARE
        strokeJoin = Paint.Join.ROUND
    }

    private val xPainter = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.SQUARE
        strokeJoin = Paint.Join.ROUND
    }

    private val oPainter = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.SQUARE
        strokeJoin = Paint.Join.ROUND
    }

    // ---------------------------------------------------------------
    // Game variables and functions...
    // ---------------------------------------------------------------

    // currentPlayer is initialized with X...
    // Instructions tell us that X always go first.
    private var currentPlayer = Player.X

    private var board : MutableList<MutableList<Int>> = mutableListOf(
        mutableListOf(0, 0, 0),
        mutableListOf(0, 0, 0),
        mutableListOf(0, 0, 0)
    )

    fun resetGame(isVersusAi: Boolean) {

        // ------------------------------------------------
        // Reset game variables
        // ------------------------------------------------
        board[0] = mutableListOf(0, 0, 0)
        board[1] = mutableListOf(0, 0, 0)
        board[2] = mutableListOf(0, 0, 0)

        currentPlayer = Player.X

        appViewModel.setIsGameFinished(false)
        appViewModel.setIsVersusAi(isVersusAi)
        // ------------------------------------------------

        // ------------------------------------------------
        // Reset game view
        // ------------------------------------------------
        if (::bitmap.isInitialized) {
            bitmap.recycle()
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)

        invalidate()
        // ------------------------------------------------

    }
    // ---------------------------------------------------------------

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w
        viewHeight = h

        if (::bitmap.isInitialized) {
            bitmap.recycle()
        }

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)


        // ------------------------------------------
        // Preservation of Aspect Ratio
        // ------------------------------------------
        val aspectRatio = 1f
        val width: Int
        val height: Int

        if (aspectRatio > w/h) {
            width = w
            height = (w / aspectRatio).toInt()
        } else if (aspectRatio < w/h) {
            width = (h * aspectRatio).toInt()
            height = h
        } else {
            width = w
            height = h
        }
        // ------------------------------------------

        boardLeft = padding
        boardTop = padding
        boardRight = width - padding
        boardBottom = height - padding

        stepSizeX = (boardRight - boardLeft).toFloat() / 3f
        firstThirdX = boardLeft + stepSizeX
        secondThirdX = firstThirdX + stepSizeX

        stepSizeY = (boardBottom - boardTop).toFloat() / 3f
        firstThirdY = boardTop + stepSizeY
        secondThirdY = firstThirdY + stepSizeY

        boardFrame = Rect(boardLeft, boardTop, boardRight, boardBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // --------------------------------------
        // Draw outer board frame
        // --------------------------------------
        canvas.drawRect(boardFrame, boardPainter)
        // --------------------------------------

        // -----------------------------------------
        // Draw board vertical lines
        // -----------------------------------------
        canvas.drawLine(
            firstThirdX,
            boardTop.toFloat(),
            firstThirdX,
            boardBottom.toFloat(),
            boardPainter
        )

        canvas.drawLine(
            secondThirdX,
            boardTop.toFloat(),
            secondThirdX,
            boardBottom.toFloat(),
            boardPainter
        )
        // -----------------------------------------

        // -----------------------------------------
        // Draw board horizontal lines
        // -----------------------------------------
        canvas.drawLine(
            boardLeft.toFloat(),
            firstThirdY,
            boardRight.toFloat(),
            firstThirdY,
            boardPainter
        )

        canvas.drawLine(
            boardLeft.toFloat(),
            secondThirdY,
            boardRight.toFloat(),
            secondThirdY,
            boardPainter
        )
        // -----------------------------------------
    }

    private fun mapTouchToRow(touchY: Float) : Int {
        var row = -1

        if (touchY > boardTop && touchY < firstThirdY) {

            row = 0
            mapRowToTileMeasurements(row)

        } else if (touchY > firstThirdY && touchY < secondThirdY) {

            row = 1
            mapRowToTileMeasurements(row)

        } else if (touchY > secondThirdY && touchY < boardBottom) {

            row = 2
            mapRowToTileMeasurements(row)

        }

        return row
    }

    private fun mapTouchToColumn(touchX: Float) : Int {
        var column = -1

        if (touchX > boardLeft && touchX < firstThirdX) {

            column = 0
            mapColumnToTileMeasurements(column)

        } else if (touchX > firstThirdX && touchX < secondThirdX) {

            column = 1
            mapColumnToTileMeasurements(column)

        } else if (touchX > secondThirdX && touchX < boardRight) {

            column = 2
            mapColumnToTileMeasurements(column)

        }

        return column
    }

    fun letAiPlayRandomFirstMove() {

        val aiPlayer = currentPlayer

        // Let AI play a random move since it is the first move...
        val random = Random()
        val randomRow = random.nextInt(3)
        val randomColumn = random.nextInt(3)

        // Let AI play its move on board...
        board[randomRow][randomColumn] = aiPlayer.number

        // Once the move is known, the needed class variables are updated...
        // these variables are needed to know where and how to draw the O shape.
        mapRowToTileMeasurements(randomRow)
        mapColumnToTileMeasurements(randomColumn)

        // AI draws its shape on board
        drawShape(aiPlayer)

        currentPlayer = when (currentPlayer) {
            Player.X -> Player.O
            Player.O -> Player.X
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var boardRow = -1
        var boardColumn = -1

        if (appViewModel.isVersusAi.value!!) {

            if (event!!.action == MotionEvent.ACTION_DOWN && !appViewModel.isGameFinished.value!!) {

                boardRow = mapTouchToRow(event.y)
                boardColumn = mapTouchToColumn(event.x)

                val wasBoardTilePressed = boardRow > -1 && boardColumn > -1
                if (wasBoardTilePressed) {

                    if (board[boardRow][boardColumn] == 0) {

                        // -------------------------------------------------
                        // Human plays...
                        // -------------------------------------------------
                        board[boardRow][boardColumn] = currentPlayer.number
                        drawShape(currentPlayer)

                        if (checkWin(board, currentPlayer.number)) {
                            appViewModel.setWinner(currentPlayer.playerName)

                            when (currentPlayer) {
                                Player.X -> appViewModel.setLoser(Player.O.playerName)
                                Player.O -> appViewModel.setLoser(Player.X.playerName)
                            }

                            appViewModel.setIsGameFinished(true)

                            return true
                        }

                        if (isBoardFull(board)) {
                            appViewModel.setWinner(null)
                            appViewModel.setIsGameFinished(true)

                            return true
                        }
                        // -------------------------------------------------

                        currentPlayer = when (currentPlayer) {
                            Player.X -> Player.O
                            Player.O -> Player.X
                        }

                        // -------------------------------------------------
                        // AI plays...
                        // -------------------------------------------------
                        val aiPlayer = currentPlayer

                        // Let AI calculate next best move...
                        val aiMove: Move = getSmartMove(board, aiPlayer)

                        // Let AI play its move on board...
                        board[aiMove.row][aiMove.column] = aiPlayer.number

                        // Once the move is known, the needed class variables are updated...
                        // these variables are needed to know where and how to draw the O shape.
                        mapRowToTileMeasurements(aiMove.row)
                        mapColumnToTileMeasurements(aiMove.column)

                        // AI draws its shape on board
                        drawShape(aiPlayer)

                        if (checkWin(board, aiPlayer.number)) {
                            appViewModel.setWinner(aiPlayer.playerName)

                            when (currentPlayer) {
                                Player.X -> appViewModel.setLoser(Player.O.playerName)
                                Player.O -> appViewModel.setLoser(Player.X.playerName)
                            }

                            appViewModel.setIsGameFinished(true)

                            return true
                        }

                        if (isBoardFull(board)) {
                            appViewModel.setWinner(null)
                            appViewModel.setIsGameFinished(true)

                            return true
                        }
                        // -------------------------------------------------

                        currentPlayer = when (currentPlayer) {
                            Player.X -> Player.O
                            Player.O -> Player.X
                        }

                    }
                }
            }



        } else {

            if (event!!.action == MotionEvent.ACTION_DOWN && !appViewModel.isGameFinished.value!!) {

                boardRow = mapTouchToRow(event.y)
                boardColumn = mapTouchToColumn(event.x)

                val wasBoardTilePressed = boardRow > -1 && boardColumn > -1
                if (wasBoardTilePressed) {

                    if (board[boardRow][boardColumn] == 0) {

                        board[boardRow][boardColumn] = currentPlayer.number
                        drawShape(currentPlayer)

                        if (checkWin(board, currentPlayer.number)) {
                            appViewModel.setWinner(currentPlayer.playerName)

                            when (currentPlayer) {
                                Player.X -> appViewModel.setLoser(Player.O.playerName)
                                Player.O -> appViewModel.setLoser(Player.X.playerName)
                            }

                            appViewModel.setIsGameFinished(true)

                            return true
                        }

                        if (isBoardFull(board)) {
                            appViewModel.setWinner(null)
                            appViewModel.setIsGameFinished(true)

                            return true
                        }

                        currentPlayer = when (currentPlayer) {
                            Player.X -> Player.O
                            Player.O -> Player.X
                        }

                    }
                }
            }
        }

        return true
    }

    private fun mapRowToTileMeasurements(row: Int) {

        when (row) {
            0 -> {
                leftToRightTopCornerY = boardTop.toFloat()
                rightToLeftTopCornerY = boardTop.toFloat()

                leftToRightBottomCornerY = firstThirdY
                rightToLeftBottomCornerY = firstThirdY
            }
            1 -> {
                leftToRightTopCornerY = firstThirdY
                rightToLeftTopCornerY = firstThirdY

                leftToRightBottomCornerY = secondThirdY
                rightToLeftBottomCornerY = secondThirdY
            }
            2 -> {
                leftToRightTopCornerY = secondThirdY
                rightToLeftTopCornerY = secondThirdY

                leftToRightBottomCornerY = boardBottom.toFloat()
                rightToLeftBottomCornerY = boardBottom.toFloat()
            }
        }

    }

    private fun mapColumnToTileMeasurements(column: Int) {

        when (column) {
            0 -> {
                leftToRightTopCornerX = boardLeft.toFloat()
                rightToLeftBottomCornerX = boardLeft.toFloat()

                leftToRightBottomCornerX = firstThirdX
                rightToLeftTopCornerX = firstThirdX
            }
            1 -> {
                leftToRightTopCornerX = firstThirdX
                rightToLeftBottomCornerX = firstThirdX

                leftToRightBottomCornerX = secondThirdX
                rightToLeftTopCornerX = secondThirdX
            }
            2 -> {
                leftToRightTopCornerX = secondThirdX
                rightToLeftBottomCornerX = secondThirdX

                leftToRightBottomCornerX = boardRight.toFloat()
                rightToLeftTopCornerX = boardRight.toFloat()
            }
        }
    }

    private fun drawShape(player: Player) {

        when (player) {
            Player.X -> {

                canvas.drawLine(leftToRightTopCornerX, leftToRightTopCornerY, leftToRightBottomCornerX, leftToRightBottomCornerY, xPainter)
                canvas.drawLine(rightToLeftTopCornerX, rightToLeftTopCornerY, rightToLeftBottomCornerX, rightToLeftBottomCornerY, xPainter)

            }
            Player.O -> {

                val circleCenterX = (rightToLeftTopCornerX + leftToRightTopCornerX) / 2
                val circleCenterY = (rightToLeftTopCornerY + rightToLeftBottomCornerY) / 2

                // Since the tiles are squares, you can get the radius from the x-axis or the y-axis.
                val radius = (rightToLeftTopCornerX - leftToRightTopCornerX) / 2

                canvas.drawCircle(circleCenterX, circleCenterY, radius, oPainter)

            }
        }

        invalidate()

    }

}