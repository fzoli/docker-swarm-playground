package com.example.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RestController
class GreetingController(
    @param:Value("\${GREETING:Hello}") private val greeting: String,
    @param:Value("\${GREETING_DELAY_MS:0}") private val greetingDelayMs: Long,
    private val healthService: HealthService,
) {

    private val hostname: String = InetAddress.getLocalHost().hostName
    private var counter = AtomicInteger(0)

    @GetMapping("/greeting")
    fun greet(): String {
        if (greetingDelayMs > 0) Thread.sleep(greetingDelayMs)
        val count = counter.incrementAndGet()
        healthService.notify(count)
        val formattedCount = String.format("%04d", count)
        return "[$formattedCount] $greeting from $hostname"
    }

    @GetMapping("/stream")
    fun helloStream(): SseEmitter {
        var counter = 0
        val emitter = SseEmitter()
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            try {
                val formattedCount = String.format("%04d", ++counter)
                emitter.send("[$formattedCount] $greeting from $hostname")
            } catch (ex: Exception) {
                emitter.completeWithError(ex)
            }
        }, 0, 1, TimeUnit.SECONDS)
        emitter.onCompletion { executor.shutdown() }
        emitter.onTimeout {
            emitter.complete()
            executor.shutdown()
        }
        return emitter
    }

}
