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

    override suspend fun createTodo(description: String, isCompleted: Boolean): Todo {
        val localId = UUID.randomUUID().toString().addLocalBanner()
        val currentTime = System.currentTimeMillis()

        val entity = TodoEntity(
            id = localId,
            description = description,
            isCompleted = isCompleted,
            createdAt = currentTime,
            updatedAt = currentTime,
            isSynced = false,
            isDeleted = false
        )
        todoLocalDataSource.insertTodo(entity)
        return entity.toDomainTodo()
    }

    override suspend fun updateTodo(id: String, description: String, isCompleted: Boolean) {
        val existing = todoLocalDataSource.getTodoById(id) ?: return
        todoLocalDataSource.insertTodo(
            existing.copy(
                description = description,
                isCompleted = isCompleted,
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
    }

    override suspend fun deleteTodo(id: String) {
        val existing = todoLocalDataSource.getTodoById(id) ?: return
        todoLocalDataSource.insertTodo(
            existing.copy(
                isDeleted = true,
                isSynced = false
            )
        )
    }

    override suspend fun syncTodos(): Result<Unit> {
        return try {
            val locallyDeleted = todoLocalDataSource.getLocallyDeletedTodos()
            locallyDeleted.forEach { localTodo ->
                try {
                    if (!localTodo.id.isLocal()) {
                        todoRemoteDataSource.deleteTodoById(localTodo.id)
                    }
                    todoLocalDataSource.deleteTodoById(localTodo.id)
                } catch (_: Exception) {}
            }

            val unsyncedTodos = todoLocalDataSource.getAllTodos().first().filter { !it.isSynced }
            unsyncedTodos.forEach { localTodo ->
                try {
                    if (localTodo.id.isLocal()) {
                        val response = todoRemoteDataSource.createTodo(localTodo.description, localTodo.isCompleted)
                        val serverTodo = response.data.todo
                        todoLocalDataSource.deleteTodoById(localTodo.id)
                        todoLocalDataSource.insertTodo(
                            serverTodo.toEntityTodo(isSynced = true).copy(createdAt = localTodo.createdAt)
                        )
                    } else {
                        val response = todoRemoteDataSource.updateTodo(localTodo.id, localTodo.description, localTodo.isCompleted)
                        val serverTodo = response.data.todo
                        todoLocalDataSource.insertTodo(
                            serverTodo.toEntityTodo(isSynced = true).copy(createdAt = localTodo.createdAt)
                        )
                    }
                } catch (_: Exception) {}
            }

            val remoteResponse = todoRemoteDataSource.getAllTodos()

            val entitiesToInsert = remoteResponse.data.todos.mapNotNull { remoteTodo ->
                val localExisting = todoLocalDataSource.getTodoById(remoteTodo.id)

                if (localExisting != null && !localExisting.isSynced) {
                    return@mapNotNull null
                }

                remoteTodo.toEntityTodo(isSynced = true).copy(
                    createdAt = localExisting?.createdAt ?: System.currentTimeMillis()
                )
            }

            todoLocalDataSource.insertTodos(entitiesToInsert)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}