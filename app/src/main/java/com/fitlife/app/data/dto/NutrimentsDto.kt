package com.fitlife.app.data.dto

import com.google.gson.annotations.SerializedName

data class NutrimentsDto(

    @SerializedName("energy-kcal_100g")
    val energy_kcal_100g: Float?,
    val proteins_100g: Float?,
    val carbohydrates_100g: Float?,
    val fat_100g: Float?
)