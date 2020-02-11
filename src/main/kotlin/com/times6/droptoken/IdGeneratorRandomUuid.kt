package com.times6.droptoken

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IdGeneratorRandomUuid: IdGenerator {
    override fun generateId() = UUID.randomUUID().toString()
}
