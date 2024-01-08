package com.lbdev.bazinga.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/search") // Update the endpoint if necessary
    fun postData(
        @Header("X-API-Key") apiKey: String,
        @Body requestBody: ApiRequestBody
    ): Call<List<PersonResponse>>

    @GET("search/person")
    suspend fun searchPerson(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Header("accept") acceptHeader: String = "application/json",
        @Header("Authorization") authorizationHeader: String
    ): Response<SearchPersonResponse>

    @GET("person/{id}?append_to_response=credits&language=en-US")
    suspend fun searchPersonWithId(
        @Path("id") id: Int,
        @Header("accept") acceptHeader: String = "application/json",
        @Header("Authorization") authorizationHeader: String
    ): Response<PersonDetailsMoviesResponse>

    @GET("movie/{id}?append_to_response=credits&language=en-US")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Header("accept") acceptHeader: String = "application/json",
        @Header("Authorization") authorizationHeader: String
    ): Response<MovieDetailsResponse>

}