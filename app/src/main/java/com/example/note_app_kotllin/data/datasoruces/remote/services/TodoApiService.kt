package com.example.note_app_kotllin.data.datasoruces.remote.services

import com.example.note_app_kotllin.data.models.request.TodoRequest
import com.example.note_app_kotllin.data.models.response.TodoListResponse
import com.example.note_app_kotllin.data.models.response.TodoResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TodoApiService {

    @GET("/api/todos")
    suspend fun getAllTodos(): TodoListResponse

    @GET("/api/todos/{id}")
    suspend fun getTodoById(@Path("id") id: String): TodoResponse

    @POST("/api/todos")
    suspend fun createTodo(@Body todoRequest: TodoRequest): TodoResponse

    @PATCH("/api/todos/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Body todoRequest: TodoRequest
    ): TodoResponse

    @DELETE("/api/todos")
    suspend fun deleteAllTodos(): Unit

    @DELETE("/api/todos/{id}")
    suspend fun deleteTodoById(@Path("id") id: String): Unit
}