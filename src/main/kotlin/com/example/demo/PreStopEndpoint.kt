package com.example.demo

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.boot.actuate.endpoint.annotation.Selector
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component


@Component
@Endpoint(id = "prestop")
class PreStopEndpoint {

    @Volatile
    private var preStop = false

    @ReadOperation
    fun status(): ResponseEntity<String> {
        if (preStop) {
            return ResponseEntity.status(503).build()
        }
        return ResponseEntity.ok("")
    }

    @ReadOperation
    fun preStop(@Selector delay: Long): ResponseEntity<String> {
        preStop = true
        Thread.sleep(delay)
        return ResponseEntity.ok("")
    }

}
