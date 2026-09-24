package com.todoauth.repository

import com.todoauth.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface TodoRepository : JpaRepository<Todo, UUID> {

    fun findByUserId(userId: UUID, pageable: Pageable): Page<Todo>

    fun findByUserIdAndCompleted(userId: UUID, completed: Boolean, pageable: Pageable): Page<Todo>

    fun findByIdAndUserId(id: UUID, userId: UUID): Optional<Todo>

    fun deleteByIdAndUserId(id: UUID, userId: UUID): Long
}
