package com.fitlife.app.data.dto

data class SearchResponseDto(
    val count: Int?,
    val products: List<SearchProductDto>?
)