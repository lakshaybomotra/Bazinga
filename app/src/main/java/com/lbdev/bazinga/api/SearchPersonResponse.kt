package com.lbdev.bazinga.api

import com.google.gson.annotations.SerializedName

data class SearchPersonResponse(
    @SerializedName("results")
    val results: List<PersonResult>
) {
    fun getFirstPerson(): PersonResult? {
        return results.firstOrNull()
    }
}

data class PersonResult(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_path")
    val profilePath: String?
)

