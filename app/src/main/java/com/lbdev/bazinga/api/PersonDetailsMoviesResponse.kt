package com.lbdev.bazinga.api

data class PersonDetailsMoviesResponse(
    val biography: String,
    val birthday: String,
    val credits: Credits,
    val deathday: Any,
    val id: Int,
    val imdb_id: String,
    val name: String,
    val place_of_birth: String,
    val popularity: Double,
    val profile_path: String,
)