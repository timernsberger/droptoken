package com.times6.droptoken.model

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class GameInstance(
        val id: String,
        initialPlayers: List<String>,
        numColumns: Int,
        numRows: Int
) {
    private val gameBoard = GameBoard(numColumns, numRows)
    private val players = initialPlayers.toMutableList()
    private val moveHistory: MutableList<PlayerAction> = mutableListOf()

    init {
        // TODO: this is the behavior after all but 1 leaves; check if reasonable starting condition
        if(players.size == 1) {
            gameBoard.gameState = GameStatus.DONE
            gameBoard.winner = players[0]
        }
    }

    // TODO: make set as secondary structure to improve lookup efficiency
    fun hasPlayer(playerId: String) = players.contains(playerId)

    fun getPlayers() = players.toList()

    fun getMoves() = moveHistory.toList()

    val gameState
        get() = gameBoard.gameState

    val winner
        get() = gameBoard.winner

    fun removePlayer(playerId: String) {
        players.remove(playerId)
        moveHistory += PlayerQuit(playerId)
        if(players.size == 1) {
            gameBoard.gameState = GameStatus.DONE
            gameBoard.winner = players[0]
        }
    }

    fun addMove(playerMove: PlayerMove): Int {
        val currentPlayerIndex = moveHistory.size % players.size
        if(players[currentPlayerIndex] != playerMove.player) {
            throw NotYourTurnException()
        }
        gameBoard.addToken(playerMove.player, playerMove.column)
        moveHistory += playerMove
        return moveHistory.size - 1
    }
}

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "it's not your turn")
class NotYourTurnException: java.lang.RuntimeException()
