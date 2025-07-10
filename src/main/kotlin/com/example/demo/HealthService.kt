package com.example.demo

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class HealthService(
    @param:Value("\${UP_DELAY_MS:30000}") private val upDelayMs: Long,
    @param:Value("\${DOWN_DELAY_MS:120000}") private val downDelayMs: Long,
    @param:Value("\${MAX_COUNT:200}") private val maxCount: Int,
) {

    private val startupTime = System.currentTimeMillis()

    @Volatile
    private var damaged = false

    fun isHealthy(): Pair<Boolean, Long> {
        val elapsed = System.currentTimeMillis() - startupTime
        return Pair(!damaged && elapsed >= upDelayMs, elapsed)
    }

    @PostConstruct
    fun initialize() {
        if (downDelayMs <= 0) return
        Thread.ofVirtual().start {
            Thread.sleep(downDelayMs)
            damaged = true
        }
    }

    fun notify(count: Int) {
        if (maxCount <= 0) return
        if (count >= maxCount) {
            damaged = true
        }
    }

}
