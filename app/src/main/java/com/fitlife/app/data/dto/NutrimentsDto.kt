package com.fitlife.app.data.dto

import com.google.gson.annotations.SerializedName

// Using Double to absorb both int and float returns without throwing a type mismatch.
data class NutrimentsDto(
    @SerializedName("energy-kcal_100g")   val energyKcal100g: Double?,
    @SerializedName("energy_100g")        val energyKj100g: Double?,
    @SerializedName("proteins_100g")      val proteins100g: Double?,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Double?,
    @SerializedName("fat_100g")           val fat100g: Double?
) {
    fun resolvedKcal(): Float =
        (energyKcal100g ?: energyKj100g?.div(4.184))?.toFloat() ?: 0f

    fun resolvedProtein(): Float  = proteins100g?.toFloat() ?: 0f
    fun resolvedCarbs(): Float    = carbohydrates100g?.toFloat() ?: 0f
    fun resolvedFat(): Float      = fat100g?.toFloat() ?: 0f
}