package com.example.demo

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class StartupHealthIndicator(private val healthService: HealthService) : HealthIndicator {

    override fun health(): Health {
        val (up, elapsed) = healthService.isHealthy()
        return if (up) {
            Health.up().withDetail("elapsedMillis", elapsed).build()
        } else {
            Health.down().withDetail("elapsedMillis", elapsed).build()
        }
    }

}
