package com.fitlife.app.data.dto

data class ProductResponseDto(
    val status: Int?,       // 1 = found, 0 = not found
    val product: ProductDto?
)