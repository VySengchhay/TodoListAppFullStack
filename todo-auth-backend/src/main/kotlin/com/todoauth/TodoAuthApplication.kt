package com.todoauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TodoAuthApplication

fun main(args: Array<String>) {
	runApplication<TodoAuthApplication>(*args)
}
