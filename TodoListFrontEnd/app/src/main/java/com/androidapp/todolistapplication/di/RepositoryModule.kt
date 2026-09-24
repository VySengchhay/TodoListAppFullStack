package com.androidapp.todolistapplication.di

import com.androidapp.todolistapplication.feature.auth.data.repository.AuthRepositoryImpl
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import com.androidapp.todolistapplication.feature.todo.data.repository.TodoRepositoryImpl
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTodoRepository(todoRepositoryImpl: TodoRepositoryImpl): TodoRepository
}