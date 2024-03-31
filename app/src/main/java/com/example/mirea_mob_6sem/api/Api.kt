package com.example.mirea_mob_6sem.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Dictionary


interface Api {

    @GET("register")
    fun register(
        @Query("log") login: String,
        @Query("password") password: String,
        ): Call<Map<String,Int>>

    @GET("login")
    fun login(
        @Query("log") login: String,
        @Query("password") password: String,
    ): Call<Map<String,Int>>

    @GET("find")
    fun find(
        @Query("line") line: String
    ): Call<List<Film>>

    @GET("get_film")
    fun get_film(
        @Query("prod_id") id: Int
    ): Call<Film>

    @POST("rate")
    fun rate(
        @Query("prod_id") id: Int,
        @Query("user_rate") rate: Boolean,
    ): Call<String>


    @GET("get_recommendations")
    fun get_recommendations(
    ): Call<List<Film>>

}