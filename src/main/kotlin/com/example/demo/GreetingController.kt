package com.example.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

@RestController
class GreetingController(@param:Value("\${GREETING:Hello}") private val greeting: String) {

    private var counter = AtomicInteger(0)

    @GetMapping("/greeting")
    fun greet(): String {
        val count = counter.incrementAndGet()
        return "[$count] $greeting"
    }

}
