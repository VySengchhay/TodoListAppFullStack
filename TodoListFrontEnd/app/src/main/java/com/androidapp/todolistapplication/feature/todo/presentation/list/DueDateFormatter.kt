package com.androidapp.todolistapplication.feature.todo.presentation.list

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

object DueDateFormatter {

    private val sameYear: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    private val otherYear: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

    fun format(
        dueDate: Instant,
        clock: Clock = Clock.systemDefaultZone()
    ): String {
        val zone: ZoneId = clock.zone
        val today = LocalDate.now(clock)
        val due = dueDate.atZone(zone).toLocalDate()

        return when (ChronoUnit.DAYS.between(today, due)) {
            0L -> "Today"
            1L -> "Tomorrow"
            -1L -> "Yesterday"
            in 2L..6L -> due.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            else -> if (due.year == today.year) sameYear.format(due) else otherYear.format(due)
        }
    }

    /**
     * Fuller wording for the add/edit form, where the exact day matters:
     * "Today · Sep 21", "Tomorrow · Sep 22", "Wed, Sep 23".
     */
    fun formatWithDate(
        dueDate: Instant,
        clock: Clock = Clock.systemDefaultZone()
    ): String {
        val today = LocalDate.now(clock)
        val due = dueDate.atZone(clock.zone).toLocalDate()
        val absolute = if (due.year == today.year) sameYear.format(due) else otherYear.format(due)

        return when (ChronoUnit.DAYS.between(today, due)) {
            0L -> "Today · $absolute"
            1L -> "Tomorrow · $absolute"
            -1L -> "Yesterday · $absolute"
            else -> "${due.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, $absolute"
        }
    }

    fun isOverdue(
        dueDate: Instant,
        completed: Boolean,
        clock: Clock = Clock.systemDefaultZone()
    ): Boolean {
        if (completed) return false
        val due = dueDate.atZone(clock.zone).toLocalDate()
        return due.isBefore(LocalDate.now(clock))
    }
}
