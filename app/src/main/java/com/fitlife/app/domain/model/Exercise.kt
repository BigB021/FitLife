package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Exercise (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val muscle: String,
    val equipment: String,
    val difficulty: String,
    val instructions: String,
    val safetyInfo: String
) {
    // Getter to get back the list anywhere in the UI
    fun equipmentList(): List<String> =
        equipment.split(",").map { it.trim() }.filter { it.isNotBlank() }
}