package com.lbdev.bazinga.api

data class ApiRequestBody(
    val collection_id: String,
    val images: List<String>,
    val max_results: Int,
    val min_score: Double,
    val search_mode: String
)