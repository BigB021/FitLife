package com.fitlife.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Exercise (
    @PrimaryKey val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String
)