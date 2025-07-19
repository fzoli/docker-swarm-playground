package com.example.demo

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.InetAddress
import kotlin.system.exitProcess

@Service
class HealthService(
    @param:Value("\${DIE_MODE:false}") private val dieMode: Boolean,
    @param:Value("\${DIE_EXIT_CODE:1}") private val dieExitCode: Int,
    @param:Value("\${UP_DELAY_MS:30000}") private val upDelayMs: Long,
    @param:Value("\${DOWN_DELAY_MS:120000}") private val downDelayMs: Long,
    @param:Value("\${DOWN_SLOT:}") private val downSlot: String,
    @param:Value("\${MAX_COUNT:200}") private val maxCount: Int,
) {

    private val hostname: String = InetAddress.getLocalHost().hostName
    private val startupTime = System.currentTimeMillis()

    @Volatile
    private var damaged = false

    fun isHealthy(): Pair<Boolean, Long> {
        val elapsed = System.currentTimeMillis() - startupTime
        return Pair(!damaged && elapsed >= upDelayMs, elapsed)
    }

    @PostConstruct
    fun initialize() {
        if (downSlot.isNotBlank() && !hostname.endsWith(downSlot)) return
        if (downDelayMs <= 0) return
        Thread.ofVirtual().start {
            Thread.sleep(downDelayMs)
            boom()
        }
    }

    fun notify(count: Int) {
        if (maxCount <= 0) return
        if (count >= maxCount) {
            boom()
        }
    }

    private fun boom() {
        if (dieMode) {
            exitProcess(dieExitCode)
        }
        damaged = true
    }

}
