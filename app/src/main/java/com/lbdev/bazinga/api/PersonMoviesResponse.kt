package com.lbdev.bazinga.api

import com.google.gson.annotations.SerializedName

data class PersonMoviesResponse(
    @SerializedName("cast")
    val cast: List<MovieCast>
)

data class MovieCast(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?, // Use String? if the value can be null
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?, // Use String? if the value can be null
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("character")
    val character: String,
    @SerializedName("credit_id")
    val creditId: String,
    @SerializedName("order")
    val order: Int
)
