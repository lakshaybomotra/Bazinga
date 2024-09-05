package com.lbdev.bazinga.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://sg.opencv.fr/" // Update the base URL
    private const val FIND_MOVIES_URL = "https://api.themoviedb.org/3/person/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val api2: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FIND_MOVIES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
