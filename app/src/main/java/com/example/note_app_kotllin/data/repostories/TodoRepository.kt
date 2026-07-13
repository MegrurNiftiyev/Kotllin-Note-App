package com.example.note_app_kotllin.data.repostories

import com.example.note_app_kotllin.core.extensions.addLocalBanner
import com.example.note_app_kotllin.core.extensions.isLocal
import com.example.note_app_kotllin.data.datasoruces.local.TodoLocalDataSource
import com.example.note_app_kotllin.data.datasoruces.local.room.entities.TodoEntity
import com.example.note_app_kotllin.data.datasoruces.remote.datasources.TodoRemoteDataSource
import com.example.note_app_kotllin.domain.models.Todo
import com.example.note_app_kotllin.domain.repositories.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoRemoteDataSource: TodoRemoteDataSource,
    private val todoLocalDataSource: TodoLocalDataSource
) : ITodoRepository {

    override fun getAllTodos(): Flow<List<Todo>> {
        return todoLocalDataSource.getAllTodos()
            .map { entities -> entities.map { it.toDomainTodo() } }
    }

    override suspend fun syncTodos(): Result<Unit> {
        return try {
            val locallyDeletedTodos = todoLocalDataSource.getLocallyDeletedTodos()
            locallyDeletedTodos.forEach { localTodo ->
                try {
                    val isNewTodo = localTodo.id.isLocal()

                    if (isNewTodo) {
                        todoLocalDataSource.deleteTodoById(localTodo.id)
                    } else {
                        todoRemoteDataSource.deleteTodoById(localTodo.id)
                        todoLocalDataSource.deleteTodoById(localTodo.id)
                    }
                } catch (e: Exception) {

                }
            }

            val unsyncedTodos = todoLocalDataSource.getAllTodos().first().filter { !it.isSynced }
            unsyncedTodos.forEach { localTodo ->
                try {
                    val isNewTodo = localTodo.id.isLocal()

                    if (isNewTodo) {
                        val response = todoRemoteDataSource.createTodo(localTodo.description, localTodo.isCompleted)
                        val serverTodo = response.data.todo
                        todoLocalDataSource.deleteTodoById(localTodo.id)
                        todoLocalDataSource.insertTodo(serverTodo.toEntityTodo(isSynced = true).copy(createdAt = localTodo.createdAt))
                    } else {
                        val response = todoRemoteDataSource.updateTodo(localTodo.id, localTodo.description, localTodo.isCompleted)
                        val serverTodo = response.data.todo
                        todoLocalDataSource.insertTodo(serverTodo.toEntityTodo(isSynced = true).copy(createdAt = localTodo.createdAt))
                    }
                } catch (e: Exception) {

                }
            }

            val remoteResponse = todoRemoteDataSource.getAllTodos()

            val entities = remoteResponse.data.todos.map { remoteTodo ->
                val localExisting = todoLocalDataSource.getTodoById(remoteTodo.id)
                remoteTodo.toEntityTodo(isSynced = true).copy(
                    createdAt = localExisting?.createdAt ?: System.currentTimeMillis()
                )
            }
            todoLocalDataSource.insertTodos(entities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTodoById(id: String): Result<Todo> {
        return try {
            val response = todoRemoteDataSource.getTodoById(id)
            val todoDto = response.data.todo
            val localExisting = todoLocalDataSource.getTodoById(id)

            todoLocalDataSource.insertTodo(
                todoDto.toEntityTodo(isSynced = true).copy(
                    createdAt = localExisting?.createdAt ?: System.currentTimeMillis()
                )
            )
            Result.success(todoDto.toDomainTodo())
        } catch (e: Exception) {
            val localTodo = todoLocalDataSource.getTodoById(id)
            if (localTodo != null) {
                Result.success(localTodo.toDomainTodo())
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun createTodo(description: String, isCompleted: Boolean): Result<Todo> {
        val localId = UUID.randomUUID().toString().addLocalBanner()
        val currentTime = System.currentTimeMillis()

        val initialLocalEntity = TodoEntity(
            id = localId,
            description = description,
            isCompleted = isCompleted,
            createdAt = currentTime,
            updatedAt = currentTime,
            isSynced = false,
            isDeleted = false
        )
        todoLocalDataSource.insertTodo(initialLocalEntity)

        return try {
            val response = todoRemoteDataSource.createTodo(description, isCompleted)
            val todoDto = response.data.todo

            todoLocalDataSource.deleteTodoById(localId)
            todoLocalDataSource.insertTodo(todoDto.toEntityTodo(isSynced = true).copy(createdAt = currentTime))

            Result.success(todoDto.toDomainTodo())
        } catch (e: Exception) {
            Result.success(initialLocalEntity.toDomainTodo())
        }
    }

    override suspend fun updateTodo(id: String, description: String, isCompleted: Boolean): Result<Todo> {
        val currentTime = System.currentTimeMillis()

        val existingTodo = todoLocalDataSource.getTodoById(id)
        val originalCreatedAt = existingTodo?.createdAt ?: currentTime

        val updatedLocalEntity = TodoEntity(
            id = id,
            description = description,
            isCompleted = isCompleted,
            createdAt = originalCreatedAt,
            updatedAt = currentTime,
            isSynced = false,
            isDeleted = false
        )
        todoLocalDataSource.insertTodo(updatedLocalEntity)

        return try {
            val response = todoRemoteDataSource.updateTodo(id, description, isCompleted)
            val todoDto = response.data.todo

            todoLocalDataSource.insertTodo(todoDto.toEntityTodo(isSynced = true).copy(createdAt = originalCreatedAt))
            Result.success(todoDto.toDomainTodo())
        } catch (e: Exception) {
            Result.success(updatedLocalEntity.toDomainTodo())
        }
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        return try {
            todoRemoteDataSource.deleteTodoById(id)
            todoLocalDataSource.deleteTodoById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            val localTodo = todoLocalDataSource.getTodoById(id)
            if (localTodo != null) {
                todoLocalDataSource.insertTodo(localTodo.copy(isDeleted = true, isSynced = false))
            }
            Result.success(Unit)
        }
    }

    override suspend fun deleteAllTodos(): Result<Unit> {
        return try {
            todoRemoteDataSource.deleteAllTodos()
            todoLocalDataSource.deleteAllTodos()
            Result.success(Unit)
        } catch (e: Exception) {
            todoLocalDataSource.deleteAllTodos()
            Result.success(Unit)
        }
    }
}