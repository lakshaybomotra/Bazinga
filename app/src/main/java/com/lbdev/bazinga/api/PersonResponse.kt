package com.lbdev.bazinga.api

data class PersonResponse(
    val id: String,
    val name: String,
    val thumbnails: List<Thumbnail>,
    val gender: String?, // Assuming gender can be nullable
    val date_of_birth: String,
    val nationality: String,
    val notes: String?, // Assuming notes can be nullable
    val create_date: String,
    val modified_date: String,
    val score: Double,
    val collections: List<Any> // Assuming collections can be of any type
)

data class Thumbnail(
    val id: String,
    val thumbnail: String
)