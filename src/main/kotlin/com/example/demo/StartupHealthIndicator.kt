package com.example.demo

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class StartupHealthIndicator : HealthIndicator {

    private val startupTime = System.currentTimeMillis()

    override fun health(): Health {
        val elapsed = System.currentTimeMillis() - startupTime
        return if (elapsed >= 30000) {
            Health.up().withDetail("elapsedMillis", elapsed).build()
        } else {
            Health.down().withDetail("elapsedMillis", elapsed).build()
        }
    }

}
