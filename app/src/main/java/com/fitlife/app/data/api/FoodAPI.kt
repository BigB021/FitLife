package com.fitlife.app.data.api

import com.fitlife.app.data.dto.ProductResponseDto
import com.fitlife.app.data.dto.UsdaSearchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Open Food Facts - barcode search
interface OpenFoodAPI {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String = "product_name,nutriments"
    ): Response<ProductResponseDto>

}

// USDA FoodData Central - name search
interface UsdaFoodAPI {
    @GET("fdc/v1/foods/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 25,
        // No dataType filter
    ): Response<UsdaSearchResponseDto>
}