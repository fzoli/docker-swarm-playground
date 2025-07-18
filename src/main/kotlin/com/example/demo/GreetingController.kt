package com.example.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.File
import java.net.InetAddress
import java.time.Instant
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

    private val directoryFile = File("/demo/demo.txt")
    private val standaloneFile = File("/demo.txt")

    @GetMapping("/greeting")
    fun greet(): String {
        if (greetingDelayMs > 0) Thread.sleep(greetingDelayMs)
        val count = counter.incrementAndGet()
        healthService.notify(count)
        val formattedCount = String.format("%04d", count)
        return "[$formattedCount] $greeting from $hostname at ${Instant.now()} '${String(directoryFile.readBytes())}' '${String(standaloneFile.readBytes())}'"
    }

    @GetMapping("/stream")
    fun helloStream(): SseEmitter {
        var counter = 0
        val emitter = SseEmitter()
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            try {
                val formattedCount = String.format("%04d", ++counter)
                emitter.send("[$formattedCount] $greeting from $hostname at ${Instant.now()}")
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
