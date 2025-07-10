package com.example.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class StartupHealthIndicator(@param:Value("\${UP_DELAY_MS:30000}") private val upDelayMs: Long) : HealthIndicator {

    private val startupTime = System.currentTimeMillis()

    override fun health(): Health {
        val elapsed = System.currentTimeMillis() - startupTime
        return if (elapsed >= upDelayMs) {
            Health.up().withDetail("elapsedMillis", elapsed).build()
        } else {
            Health.down().withDetail("elapsedMillis", elapsed).build()
        }
    }

}
