package com.times6.droptoken.model

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class GameBoard(private val numColumns: Int, private val numRows: Int) {
    var winner: String? = null
    var gameState = GameStatus.IN_PROGRESS
    private val columns = mutableMapOf<Int, Column>()

    fun addToken(player: String, columnIndex: Int) {
        if(columnIndex < 0 || columnIndex >= numColumns) {
            throw ColumnOutOfBoundsException()
        }
        if(!columns.containsKey(columnIndex)) {
            columns[columnIndex] = Column()
        }
        val column = columns[columnIndex]!!
        val rowIndex = column.addToken(player)
        checkForWinner(columnIndex, rowIndex, player)
    }

    private fun checkForWinner(columnIndex: Int, rowIndex: Int, player: String) {
        // TODO: possibly make this async. Can clear out winner/isGameOver and restrict access to them
        // with Futures
        if(playerWonColumn(columnIndex, rowIndex, player) ||
                playerWonRow(columnIndex, rowIndex, player) ||
                playerWonDiagonalTopLeftBottomRight(columnIndex, rowIndex, player) ||
                playerWonDiagonalBottomLeftTopRight(columnIndex, rowIndex, player)
        ) {
            gameState = GameStatus.DONE
            winner = player
        } else if(columns.size == numColumns &&
                columns.values.all { it.tokens.size == numRows }) {
            gameState = GameStatus.DONE
            // draw - no winner
        }
    }

    private fun playerWonColumn(columnIndex: Int, rowIndex: Int, player: String): Boolean {
        // We're checking the latest move. Since the token goes in the top spot in the column
        // we know there's nothing above and can take a shortcut by only checking below.
        val indexes = ((rowIndex - 1) downTo (rowIndex - 3)).map { Pair(columnIndex, it) }
        return countConsecutive(indexes, player) >= 3
    }

    private fun playerWonRow(columnIndex: Int, rowIndex: Int, player: String): Boolean {
        val consecutiveCount =
                countConsecutiveInRow(columnIndex - 1 downTo columnIndex - 3, rowIndex, player) +
                countConsecutiveInRow((columnIndex + 1)..(columnIndex + 3), rowIndex, player) +
                1
        return consecutiveCount >= 4
    }

    private fun playerWonDiagonalTopLeftBottomRight(columnIndex: Int, rowIndex: Int, player: String): Boolean {
        val leftIndexes = (columnIndex - 1 downTo columnIndex - 3).zip((rowIndex + 1)..(rowIndex + 3))
        val rightIndexes = ((columnIndex + 1)..(columnIndex + 3)).zip(rowIndex - 1 downTo rowIndex - 3)
        val consecutiveCount =
                countConsecutive(leftIndexes, player) +
                countConsecutive(rightIndexes, player) +
                1
        return consecutiveCount >= 4
    }

    private fun playerWonDiagonalBottomLeftTopRight(columnIndex: Int, rowIndex: Int, player: String): Boolean {
        val leftIndexes = (columnIndex - 1 downTo columnIndex - 3).zip(rowIndex - 1 downTo rowIndex - 3)
        val rightIndexes = ((columnIndex + 1)..(columnIndex + 3)).zip((rowIndex + 1)..(rowIndex + 3))
        val consecutiveCount =
                countConsecutive(leftIndexes, player) +
                countConsecutive(rightIndexes, player) +
                1
        return consecutiveCount >= 4
    }

    private fun countConsecutiveInRow(range: IntProgression, rowIndex: Int, player: String): Int {
        return countConsecutive(range.map { Pair(it, rowIndex) }, player)
    }

    private fun countConsecutive(indexes: Iterable<Pair<Int, Int>>, player: String): Int {
        var consecutiveCount = 0
        for((columnIndex, rowIndex) in indexes) {
            val columnTokens = columns[columnIndex]?.tokens ?: break
            if(rowIndex < 0 || rowIndex >= columnTokens.size) {
                break
            }
            val token = columnTokens[rowIndex]
            if(token != player) {
                break
            }
            consecutiveCount++
        }
        return consecutiveCount
    }

    inner class Column {
        val tokens = mutableListOf<String>()
        fun addToken(player: String): Int {
            if(tokens.size >= numColumns) {
                throw ColumnFullException()
            }
            tokens += player
            return tokens.size - 1
        }
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "column index out of range")
class ColumnOutOfBoundsException: RuntimeException()

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "column is full")
class ColumnFullException: RuntimeException()
