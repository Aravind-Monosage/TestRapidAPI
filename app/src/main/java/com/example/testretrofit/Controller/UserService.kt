package com.example.testretrofit.Controller

import com.example.testretrofit.Model.User
import com.example.testretrofit.Model.UserWithId
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @PATCH("users/{id}")
    @Headers(
        "X-RapidAPI-Key:68555ab959msh84f43c3c5070fd5p1f847fjsn09329aa4ff94",
        "X-RapidAPI-Host:node-express-api-tutorial.p.rapidapi.com"
    )
    fun updateUser(
        @Path("id") id: String,
        @Body userData: UserWithId

    ): Call<UserWithId>

    @DELETE("users/{id}")
    @Headers(
        "X-RapidAPI-Key:68555ab959msh84f43c3c5070fd5p1f847fjsn09329aa4ff94",
        "X-RapidAPI-Host:node-express-api-tutorial.p.rapidapi.com"
    )
    fun deleteUser(
        @Path("id") id: String
    ): Call<Void>

    @GET("users/{id}")
    @Headers(
        "X-RapidAPI-Key:68555ab959msh84f43c3c5070fd5p1f847fjsn09329aa4ff94",
        "X-RapidAPI-Host:node-express-api-tutorial.p.rapidapi.com"
    )
    fun getUserById(
        @Path("id") id: String
    ): Call<UserWithId>

    @POST("users")
    @Headers(
        "X-RapidAPI-Key:68555ab959msh84f43c3c5070fd5p1f847fjsn09329aa4ff94",
        "X-RapidAPI-Host:node-express-api-tutorial.p.rapidapi.com",
        "'content-type': 'application/json'"
    )
    fun createUser(@Body userData: User): Call<String>


    @GET("users")
    @Headers(
        "X-RapidAPI-Key:68555ab959msh84f43c3c5070fd5p1f847fjsn09329aa4ff94",
        "X-RapidAPI-Host:node-express-api-tutorial.p.rapidapi.com"
    )
    fun getUsers(
    ): Call<List<UserWithId>>


}
