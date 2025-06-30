package com.example.studyshare
import okhttp3.internal.concurrent.Task
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SupaBaseAPI{

    @GET("tasks")
    suspend fun getTasks(): List<Task>

    @POST("tasks")
    suspend fun insertTask(@Body task: Task): Response<Unit>

    @DELETE("tasks")
    suspend fun deleteTask(@Query("id") id: String): Response<Unit>
}