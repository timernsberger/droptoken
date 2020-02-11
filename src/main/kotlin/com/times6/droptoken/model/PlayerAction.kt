package com.times6.droptoken.model

enum class PlayerActionType {
    MOVE,
    QUIT
}

abstract class PlayerAction(val player: String, val type: PlayerActionType)

class PlayerMove(player: String, val column: Int): PlayerAction(player,
    PlayerActionType.MOVE
)

class PlayerQuit(player: String) : PlayerAction(player,
    PlayerActionType.QUIT
)
