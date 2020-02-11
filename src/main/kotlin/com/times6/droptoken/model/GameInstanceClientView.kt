package com.times6.droptoken.model

import com.fasterxml.jackson.annotation.JsonInclude

class GameInstanceClientView(
        val players: List<String>,
        val status: GameStatus,
        @JsonInclude(JsonInclude.Include.NON_NULL) val winner: String?
)
