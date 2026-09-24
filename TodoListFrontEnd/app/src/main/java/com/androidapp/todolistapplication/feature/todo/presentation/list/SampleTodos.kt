package com.androidapp.todolistapplication.feature.todo.presentation.list

import com.androidapp.todolistapplication.feature.todo.domain.model.Priority
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoPage
import java.time.Duration
import java.time.Instant

object SampleTodos {

    private val now: Instant = Instant.now()
    private fun days(d: Long): Instant = now.plus(Duration.ofDays(d))
    private fun hours(h: Long): Instant = now.plus(Duration.ofHours(h))

    // Hand-picked items, each one tests a different UI case
    val list: List<Todo> = listOf(
        Todo(
            id = "1",
            title = "Submit MIS assignment",
            description = "Chapter 4: database normalization exercises",
            dueDate = days(1),                 // due tomorrow
            priority = Priority.HIGH,
            completed = false,
            createdAt = days(-3),
            updatedAt = days(-1)
        ),
        Todo(
            id = "2",
            title = "Pay internet bill",
            description = "Monthly payment",
            dueDate = days(-2),                // overdue
            priority = Priority.MEDIUM,
            completed = false,
            createdAt = days(-10),
            updatedAt = days(-10)
        ),
        Todo(
            id = "3",
            title = "Call mom",
            description = null,
            dueDate = hours(3),                // due today, a few hours left
            priority = Priority.LOW,
            completed = false,
            createdAt = hours(-5),
            updatedAt = hours(-5)
        ),
        Todo(
            id = "4",
            title = "Read Clean Architecture book",
            description = null,                // no description
            dueDate = null,                    // no due date
            priority = Priority.MEDIUM,
            completed = false,
            createdAt = days(-7),
            updatedAt = days(-7)
        ),
        Todo(
            id = "5",
            title = "Set up Spring Boot backend",
            description = "Docker + PostgreSQL + JWT auth",
            dueDate = days(-5),
            priority = Priority.HIGH,
            completed = true,                  // completed (should NOT show as overdue)
            createdAt = days(-14),
            updatedAt = days(-6)
        ),
        Todo(
            id = "6",
            title = "Buy groceries",
            description = "Rice, eggs, fish sauce",
            dueDate = null,
            priority = Priority.LOW,
            completed = true,
            createdAt = days(-2),
            updatedAt = days(-1)
        ),
        Todo(
            id = "7",
            title = "Prepare for the banking internship interview and review all Android architecture topics including MVVM, Clean Architecture, Hilt, Retrofit, and Room",
            description = "This is a very long description to check that text overflow, maxLines, and ellipsis work correctly in the list item. It keeps going so it definitely wraps onto several lines on a small phone screen.",
            dueDate = days(7),                 // long text overflow test
            priority = Priority.HIGH,
            completed = false,
            createdAt = days(-1),
            updatedAt = days(-1)
        ),
        Todo(
            id = "8",
            title = "Gym",                         // very short title
            description = null,
            dueDate = days(0),
            priority = Priority.LOW,
            completed = false,
            createdAt = hours(-2),
            updatedAt = hours(-2)
        ),
        Todo(
            id = "9",
            title = "ទិញបន្លែនៅផ្សារ",              // Khmer text rendering test
            description = "ត្រីនិងបន្លែ",
            dueDate = days(2),
            priority = Priority.MEDIUM,
            completed = false,
            createdAt = days(-1),
            updatedAt = days(-1)
        )
    )

    val single: Todo = list.first()
    val active: List<Todo> = list.filter { !it.completed }
    val completed: List<Todo> = list.filter { it.completed }
    val empty: List<Todo> = emptyList()

    // Large generated list for scrolling / pagination tests
    fun generate(count: Int = 50): List<Todo> = List(count) { i ->
        Todo(
            id = "gen-$i",
            title = "Task #${i + 1}",
            description = if (i % 3 == 0) null else "Generated description for task ${i + 1}",
            dueDate = when (i % 4) {
                0 -> null
                1 -> days(-(i % 5 + 1).toLong())   // overdue
                2 -> days(0)                         // today
                else -> days((i % 10 + 1).toLong())  // future
            },
            priority = Priority.entries[i % Priority.entries.size],
            completed = i % 5 == 0,
            createdAt = days(-(count - i).toLong()),
            updatedAt = days(-(count - i).toLong())
        )
    }

    // Simulates backend pages (0-based, like your TodoPage.hasMore logic)
    fun page(index: Int, pageSize: Int = 10, total: Int = 50): TodoPage {
        val chunks = generate(total).chunked(pageSize)
        return TodoPage(
            items = chunks.getOrElse(index) { emptyList() },
            page = index,
            totalPages = chunks.size
        )
    }
}