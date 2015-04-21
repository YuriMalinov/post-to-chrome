package ru.smarty.post_to_chrome

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

SpringBootApplication
open public class PostToChromeApplication {
    companion object {
        public fun main(args: Array<String>) {
            SpringApplication.run(javaClass<PostToChromeApplication>(), *args)
        }
    }
}

fun main(args: Array<String>) = PostToChromeApplication.main(args)
