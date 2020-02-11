package com.times6.droptoken

import com.times6.droptoken.model.GameInstance
import org.springframework.stereotype.Service

@Service
class GameInstanceRepositoryInMemory: GameInstanceRepository {
    private val gameInstances: MutableMap<String, GameInstance> = mutableMapOf()

    override fun getAllGameIds() = gameInstances.keys.toList()

    override fun addGameInstance(gameInstance: GameInstance) {
        gameInstances[gameInstance.id] = gameInstance
    }

    override fun getGameInstance(gameId: String) = gameInstances[gameId]
}
