package com.lbdev.bazinga.api

data class Credits(
    val cast: List<CastP>
)

data class CastP(
    val backdrop_path: String,
    val character: String,
    val id: Int,
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val vote_average: Double
)