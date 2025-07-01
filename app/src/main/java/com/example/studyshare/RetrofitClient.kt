package com.example.studyshare
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://zktwurzgnafkwxqfwmjj.supabase.co/rest/v1/"

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}