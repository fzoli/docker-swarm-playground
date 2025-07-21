package com.example.demo

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component("prestop")
class PreStopHealthIndicator(private val preStopEndpoint: PreStopEndpoint) : HealthIndicator {

    override fun health(): Health {
        return if (!preStopEndpoint.isStopped()) {
            Health.up().build()
        } else {
            Health.down().build()
        }
    }

}
