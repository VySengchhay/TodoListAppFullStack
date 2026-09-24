package com.todoauth.service

import com.todoauth.dto.CreateTodoRequest
import com.todoauth.dto.PagedTodoResponse
import com.todoauth.dto.TodoResponse
import com.todoauth.dto.UpdateTodoRequest
import com.todoauth.entity.Todo
import com.todoauth.exception.TodoNotFoundException
import com.todoauth.exception.UserNotFoundException
import com.todoauth.repository.TodoRepository
import com.todoauth.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class TodoService(
	private val todoRepository: TodoRepository,
	private val userRepository: UserRepository
) {

	@Transactional
	fun create(userId: UUID, request: CreateTodoRequest): TodoResponse {
		val user = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

		val todo = Todo(
			user = user,
			title = request.title.trim(),
			description = request.description,
			dueDate = request.dueDate,
			priority = request.priority
		)

		return todoRepository.save(todo).toResponse()
	}

	@Transactional(readOnly = true)
	fun list(userId: UUID, completed: Boolean?, pageable: Pageable): PagedTodoResponse {
		val page = if (completed != null) {
			todoRepository.findByUserIdAndCompleted(userId, completed, pageable)
		} else {
			todoRepository.findByUserId(userId, pageable)
		}

		return PagedTodoResponse(
			items = page.content.map { it.toResponse() },
			page = page.number,
			size = page.size,
			totalItems = page.totalElements,
			totalPages = page.totalPages
		)
	}

	@Transactional(readOnly = true)
	fun getOne(userId: UUID, todoId: UUID): TodoResponse =
		todoRepository.findByIdAndUserId(todoId, userId)
			.orElseThrow { TodoNotFoundException() }
			.toResponse()

	@Transactional
	fun update(userId: UUID, todoId: UUID, request: UpdateTodoRequest): TodoResponse {
		val todo = todoRepository.findByIdAndUserId(todoId, userId)
			.orElseThrow { TodoNotFoundException() }

		todo.title = request.title.trim()
		todo.description = request.description
		todo.dueDate = request.dueDate
		todo.priority = request.priority
		todo.completed = request.completed
		todo.updatedAt = Instant.now()

		return todoRepository.save(todo).toResponse()
	}

	@Transactional
	fun toggleComplete(userId: UUID, todoId: UUID): TodoResponse {
		val todo = todoRepository.findByIdAndUserId(todoId, userId)
			.orElseThrow { TodoNotFoundException() }

		todo.completed = !todo.completed
		todo.updatedAt = Instant.now()

		return todoRepository.save(todo).toResponse()
	}

	@Transactional
	fun delete(userId: UUID, todoId: UUID) {
		val deletedCount = todoRepository.deleteByIdAndUserId(todoId, userId)
		if (deletedCount == 0L) {
			throw TodoNotFoundException()
		}
	}

	private fun Todo.toResponse() = TodoResponse(
		id = id!!,
		title = title,
		description = description,
		dueDate = dueDate,
		priority = priority,
		completed = completed,
		createdAt = createdAt,
		updatedAt = updatedAt
	)
}
