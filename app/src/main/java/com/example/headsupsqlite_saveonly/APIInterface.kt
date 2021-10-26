package com.example.headsupsqlite_saveonly

import retrofit2.Call
import retrofit2.http.*

interface APIInterface {
    @GET("/celebrities/")
    fun getInformation(): Call<List<Information>>

    @POST("/celebrities/")
    fun addCelebrity(@Body personData: Information): Call<Information>

    @PUT("/celebrities/{id}")
    fun updateCelebrity(@Path("id") id: Int, @Body personData: Information): Call<Information>

    @DELETE("/celebrities/{id}")
    fun deleteCelebrity(@Path("id") id:Int): Call<Void>

}