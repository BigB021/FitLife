package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val date: String

)
