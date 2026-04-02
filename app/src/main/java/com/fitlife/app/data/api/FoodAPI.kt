package com.fitlife.app.data.api

import com.fitlife.app.data.dto.ProductResponseDto
import com.fitlife.app.data.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodAPI {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,

        @Query("fields")
        fields: String = "energy-kcal_100g,product_name,proteins_100g,carbohydrates_100g,fat_100g"
    ): ProductResponseDto

    @GET("cgi/search.pl")
    suspend fun searchFood(
        @Query("search_terms") query: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): SearchResponseDto

}