package com.androidapp.todolistapplication.di

import com.androidapp.todolistapplication.fake.FakeAuthRepository
import com.androidapp.todolistapplication.fake.FakeTodoRepository
import com.androidapp.todolistapplication.feature.auth.domain.repository.AuthRepository
import com.androidapp.todolistapplication.feature.todo.domain.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    @Provides
    @Singleton
    fun provideFakeAuthRepository(): FakeAuthRepository = FakeAuthRepository()

    @Provides
    fun provideAuthRepository(fake: FakeAuthRepository): AuthRepository = fake

    @Provides
    @Singleton
    fun provideTodoRepository(): TodoRepository = FakeTodoRepository()
}
