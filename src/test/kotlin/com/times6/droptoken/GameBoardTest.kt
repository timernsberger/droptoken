package com.times6.droptoken

import com.times6.droptoken.model.ColumnFullException
import com.times6.droptoken.model.ColumnOutOfBoundsException
import com.times6.droptoken.model.GameBoard
import com.times6.droptoken.model.GameStatus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameBoardTest {

    @Test
    fun `rejects negative column index`() {
        val gameBoard = GameBoard(4, 4)
        assertThrows<ColumnOutOfBoundsException> {
            gameBoard.addToken("1", -1)
        }
    }

    @Test
    fun `rejects column index greater than board size`() {
        val gameBoard = GameBoard(5, 5)
        assertThrows<ColumnOutOfBoundsException> {
            gameBoard.addToken("me", 10)
        }
    }

    @Test
    fun `rejects move that would overfill column`() {
        val gameBoard = GameBoard(4, 4)
        gameBoard.addToken("foo", 2)
        gameBoard.addToken("bar", 2)
        gameBoard.addToken("foo", 2)
        gameBoard.addToken("bar", 2)
        assertThrows<ColumnFullException> {
            gameBoard.addToken("bar", 2)
        }
    }

    @Nested
    inner class WinConditions {
        @Test
        fun `nobody wins on empty board`() {
            val gameBoard = GameBoard(4, 6)
            assertThat(gameBoard.winner, nullValue())
            assertThat(gameBoard.gameState, `is`(GameStatus.IN_PROGRESS))
        }

        @Test
        fun `nobody wins after a single round`() {
            val gameBoard = GameBoard(4, 4)
            gameBoard.addToken("a", 0)
            assertThat(gameBoard.winner, nullValue())
            assertThat(gameBoard.gameState, `is`(GameStatus.IN_PROGRESS))
        }

        @Test
        fun `player wins with four consecutive in a column`() {
            val gameBoard = GameBoard(4, 4)
            gameBoard.addToken("a", 0)
            gameBoard.addToken("a", 0)
            gameBoard.addToken("a", 0)
            gameBoard.addToken("a", 0)
            assertThat(gameBoard.winner, equalTo("a"))
            assertThat(gameBoard.gameState, `is`(GameStatus.DONE))
        }

        @Test
        fun `player wins with four consecutive in a row`() {
            val gameBoard = GameBoard(8, 8)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("a", 3)
            gameBoard.addToken("a", 4)
            gameBoard.addToken("a", 5)
            assertThat(gameBoard.winner, equalTo("a"))
            assertThat(gameBoard.gameState, `is`(GameStatus.DONE))
        }

        @Test
        fun `player wins with four consecutive from top left to bottom right`() {
            val gameBoard = GameBoard(6, 4)
            gameBoard.addToken("b", 1)
            gameBoard.addToken("b", 1)
            gameBoard.addToken("b", 1)
            gameBoard.addToken("a", 1)
            gameBoard.addToken("b", 2)
            gameBoard.addToken("b", 2)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("b", 3)
            gameBoard.addToken("a", 3)
            gameBoard.addToken("a", 4)
            assertThat(gameBoard.winner, equalTo("a"))
            assertThat(gameBoard.gameState, `is`(GameStatus.DONE))
        }

        @Test
        fun `player wins with four consecutive from top right to bottom left`() {
            val gameBoard = GameBoard(44, 16)
            gameBoard.addToken("a", 20)
            gameBoard.addToken("b", 20)
            gameBoard.addToken("b", 20)
            gameBoard.addToken("b", 20)
            gameBoard.addToken("a", 20)
            gameBoard.addToken("b", 19)
            gameBoard.addToken("b", 19)
            gameBoard.addToken("b", 19)
            gameBoard.addToken("a", 19)
            gameBoard.addToken("b", 18)
            gameBoard.addToken("b", 18)
            gameBoard.addToken("a", 18)
            gameBoard.addToken("a", 17)
            gameBoard.addToken("a", 17)
            assertThat(gameBoard.winner, equalTo("a"))
            assertThat(gameBoard.gameState, `is`(GameStatus.DONE))
        }

        @Test
        fun `player does not win when there's a gap between pieces`() {
            val gameBoard = GameBoard(10, 12)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("a", 3)
            gameBoard.addToken("a", 4)
            gameBoard.addToken("a", 6)
            gameBoard.addToken("a", 7)
            assertThat(gameBoard.winner, nullValue())
            assertThat(gameBoard.gameState, `is`(GameStatus.IN_PROGRESS))
        }

        @Test
        fun `player does not win with other player in middle of streak`() {
            val gameBoard = GameBoard(1024, 768)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("b", 2)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("a", 2)
            gameBoard.addToken("a", 2)
            assertThat(gameBoard.winner, nullValue())
            assertThat(gameBoard.gameState, `is`(GameStatus.IN_PROGRESS))
        }

        @Test
        fun `filling the board ends in a draw`() {
            val gameBoard = GameBoard(2, 2)
            gameBoard.addToken("a", 0)
            gameBoard.addToken("b", 1)
            gameBoard.addToken("a", 1)
            gameBoard.addToken("b", 0)
            assertThat(gameBoard.winner, nullValue())
            assertThat(gameBoard.gameState, `is`(GameStatus.DONE))
        }
    }
}
