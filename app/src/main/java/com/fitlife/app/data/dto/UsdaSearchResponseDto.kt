package com.fitlife.app.data.dto

data class UsdaSearchResponseDto(
    val totalHits: Int?,
    val currentPage: Int?,
    val totalPages: Int?,
    val foods: List<UsdaFoodDto>?   // nullable
)