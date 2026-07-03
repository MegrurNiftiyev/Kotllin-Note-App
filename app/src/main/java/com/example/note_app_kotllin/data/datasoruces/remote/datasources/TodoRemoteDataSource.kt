package com.example.note_app_kotllin.data.datasoruces.remote.datasources
import com.example.note_app_kotllin.core.exceptions.NetworkException
import com.example.note_app_kotllin.core.exceptions.TodoException
import com.example.note_app_kotllin.data.datasoruces.remote.services.TodoApiService
import com.example.note_app_kotllin.data.models.request.TodoRequest
import com.example.note_app_kotllin.data.models.response.TodoListResponse
import com.example.note_app_kotllin.data.models.response.TodoResponse
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TodoRemoteDataSource @Inject constructor(
    private val todoApiService: TodoApiService
) {

    suspend fun getAllTodos(): TodoListResponse {
        return try {
            todoApiService.getAllTodos()
        } catch (e: HttpException) {
            throw when (e.code()) {
                401 -> TodoException.Unauthorized()
                500 -> TodoException.ServerError()
                else -> TodoException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw TodoException.Unknown()
        }
    }

    suspend fun getTodoById(id: String): TodoResponse {
        return try {
            todoApiService.getTodoById(id)
        } catch (e: HttpException) {
            throw when (e.code()) {
                400 -> TodoException.ValidationError()
                401 -> TodoException.Unauthorized()
                404 -> TodoException.TodoNotFound()
                500 -> TodoException.ServerError()
                else -> TodoException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw TodoException.Unknown()
        }
    }

    suspend fun createTodo(description: String, isCompleted: Boolean): TodoResponse {
        return try {
            todoApiService.createTodo(TodoRequest(description, isCompleted))
        } catch (e: HttpException) {
            throw when (e.code()) {
                400 -> TodoException.ValidationError()
                401 -> TodoException.Unauthorized()
                500 -> TodoException.ServerError()
                else -> TodoException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw TodoException.Unknown()
        }
    }

    suspend fun updateTodo(id: String, description: String, isCompleted: Boolean): TodoResponse {
        return try {
            todoApiService.updateTodo(id, TodoRequest(description, isCompleted))
        } catch (e: HttpException) {
            throw when (e.code()) {
                400 -> TodoException.ValidationError()
                401 -> TodoException.Unauthorized()
                404 -> TodoException.TodoNotFound()
                500 -> TodoException.ServerError()
                else -> TodoException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw TodoException.Unknown()
        }
    }

    suspend fun deleteAllTodos() {
        try {
            todoApiService.deleteAllTodos()
        } catch (e: HttpException) {
            throw when (e.code()) {
                401 -> TodoException.Unauthorized()
                500 -> TodoException.ServerError()
                else -> TodoException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw TodoException.Unknown()
        }
    }

    suspend fun deleteTodoById(id: String) {
        try {
            todoApiService.deleteTodoById(id)
        } catch (e: HttpException) {
            throw when (e.code()) {
                400 -> TodoException.ValidationError()
                401 -> TodoException.Unauthorized()
                404 -> TodoException.TodoNotFound()
                500 -> TodoException.ServerError()
                else -> TodoException.Unknown()
            }
        } catch (e: IOException) {
            throw NetworkException.NoInternet()
        } catch (e: Exception) {
            throw TodoException.Unknown()
        }
    }
}