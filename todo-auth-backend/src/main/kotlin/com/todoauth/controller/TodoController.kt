package com.todoauth.controller

import com.todoauth.dto.CreateTodoRequest
import com.todoauth.dto.PagedTodoResponse
import com.todoauth.dto.TodoResponse
import com.todoauth.dto.UpdateTodoRequest
import com.todoauth.exception.UserNotFoundException
import com.todoauth.service.TodoService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/todos")
class TodoController(
	private val todoService: TodoService
) {

	// The JwtAuthenticationFilter stashes the authenticated user's id as a
	// request attribute - see AuthController.me() for the same pattern.
	private fun currentUserId(request: HttpServletRequest): UUID =
		request.getAttribute("userId") as? UUID ?: throw UserNotFoundException()

	@PostMapping
	fun create(
		httpRequest: HttpServletRequest,
		@Valid @RequestBody request: CreateTodoRequest
	): ResponseEntity<TodoResponse> =
		ResponseEntity.status(HttpStatus.CREATED).body(
			todoService.create(currentUserId(httpRequest), request)
		)

	@GetMapping
	fun list(
		httpRequest: HttpServletRequest,
		@RequestParam(required = false) completed: Boolean?,
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int,
		@RequestParam(defaultValue = "createdAt") sortBy: String,
		@RequestParam(defaultValue = "desc") direction: String
	): ResponseEntity<PagedTodoResponse> {
		val sortDirection = if (direction.equals("asc", ignoreCase = true)) {
			Sort.Direction.ASC
		} else {
			Sort.Direction.DESC
		}
		val pageable = PageRequest.of(page, size.coerceAtMost(100), Sort.by(sortDirection, sortBy))

		return ResponseEntity.ok(todoService.list(currentUserId(httpRequest), completed, pageable))
	}

	@GetMapping("/{id}")
	fun getOne(httpRequest: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<TodoResponse> =
		ResponseEntity.ok(todoService.getOne(currentUserId(httpRequest), id))

	@PutMapping("/{id}")
	fun update(
		httpRequest: HttpServletRequest,
		@PathVariable id: UUID,
		@Valid @RequestBody request: UpdateTodoRequest
	): ResponseEntity<TodoResponse> =
		ResponseEntity.ok(todoService.update(currentUserId(httpRequest), id, request))

	@PatchMapping("/{id}/toggle-complete")
	fun toggleComplete(httpRequest: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<TodoResponse> =
		ResponseEntity.ok(todoService.toggleComplete(currentUserId(httpRequest), id))

	@DeleteMapping("/{id}")
	fun delete(httpRequest: HttpServletRequest, @PathVariable id: UUID): ResponseEntity<Void> {
		todoService.delete(currentUserId(httpRequest), id)
		return ResponseEntity.noContent().build()
	}
}
