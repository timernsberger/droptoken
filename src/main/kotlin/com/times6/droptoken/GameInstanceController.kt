package com.times6.droptoken

import com.times6.droptoken.model.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/drop_token")
class GameInstanceController(
    private val gameInstanceRepository: GameInstanceRepository,
    private val idGenerator: IdGenerator
) {

    @GetMapping()
    fun getAllGameIds() = gameInstanceRepository.getAllGameIds()

    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createGameInstance(@RequestBody request: CreateGameInstanceRequest): String {
        // TODO: check what we should do if request.players is empty or if columns or rows are < 4
        val gameId = idGenerator.generateId()
        val gameInstance = GameInstance(gameId, request.players, request.columns, request.rows)
        gameInstanceRepository.addGameInstance(gameInstance)
        // TODO: return 201 status?
        return gameId
    }

    @GetMapping("/{gameId}")
    fun getGameInstance(@PathVariable gameId: String): GameInstanceClientView {
        val gameInstance = findGameInstance(gameId)
        return GameInstanceClientView(gameInstance.getPlayers(), gameInstance.gameState, gameInstance.winner)
    }

    @GetMapping("/{gameId}/moves")
    fun getMoves(@PathVariable gameId: String, @RequestParam start: Int?, @RequestParam until: Int?): List<PlayerAction> {
        val gameInstance = findGameInstance(gameId)
        val moves = gameInstance.getMoves()
        return moves.subList(start ?: 0, until ?: moves.size)
    }

    @GetMapping("/{gameId}/moves/{moveNumber}")
    fun getMove(@PathVariable gameId: String, @PathVariable moveNumber: Int): PlayerAction {
        val gameInstance = findGameInstance(gameId)
        val moves = gameInstance.getMoves()
        if(moveNumber < 0 || moveNumber >= moves.size) {
            throw MoveNotFoundException()
        }
        return moves[moveNumber]
    }

    @PostMapping("/{gameId}/{playerId}", consumes = ["application/json"], produces = ["application/json"])
    fun addMove(
            @PathVariable gameId: String,
            @PathVariable playerId: String,
            @RequestBody playerMoveRequest: PlayerMoveRequest
    ) : PlayerMoveResponse {
        val gameInstance = findGameInstance(gameId)
        if(!gameInstance.hasPlayer(playerId)) {
            throw GameInstanceNotFoundException()
        }
        val moveNumber = gameInstance.addMove(PlayerMove(playerId, playerMoveRequest.column))
        // TODO: return 201 status?
        return PlayerMoveResponse("$gameId/moves/$moveNumber")
    }

    @DeleteMapping("/{gameId}/{playerId}")
    fun quitGame(@PathVariable gameId: String, @PathVariable playerId: String): ResponseEntity<Void> {
        // TODO: API does not say to throw 404 here. Catch or remove throw from findGameInstance?
        val gameInstance = findGameInstance(gameId)
        if(!gameInstance.hasPlayer(playerId)) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        gameInstance.removePlayer(playerId)
        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    private fun findGameInstance(gameId: String) =
        gameInstanceRepository.getGameInstance(gameId) ?:
        throw GameInstanceNotFoundException()
}

class PlayerMoveRequest(val column: Int)

class PlayerMoveResponse(val move: String)

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "game not found")
class GameInstanceNotFoundException: java.lang.RuntimeException()

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "move not found")
class MoveNotFoundException: java.lang.RuntimeException()
