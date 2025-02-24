package com.example.videoappvk.network

import com.example.videoappvk.dataclasses.Video
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers("Authorization: $TOKEN")
    @GET("videos/popular")
    suspend fun getVideos(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 15,
    ): Response<VideoResponse>

    @Headers("Authorization: $TOKEN")
    @GET("videos/search")
    suspend fun searchVideos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 15,
    ): Response<VideoResponse>

    @Headers("Authorization: $TOKEN")
    @GET("videos/videos/{id}")
    suspend fun getVideo(
        @Path("id") id: Int,
    ): Response<Video>

    companion object {
        private const val TOKEN = "H0iiShhwVIU0dd8rox3RQpHwOCjkHjJ7MJowFYvrM2v13txB1TfYbXKS"
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.pexels.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}