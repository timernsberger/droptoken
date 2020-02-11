package com.times6.droptoken

import com.times6.droptoken.model.GameInstance

interface GameInstanceRepository {
    fun getAllGameIds(): List<String>
    fun addGameInstance(gameInstance: GameInstance)
    fun getGameInstance(gameId: String): GameInstance?
}
