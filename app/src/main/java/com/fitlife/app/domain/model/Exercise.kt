package com.fitlife.app.domain.model

import androidx.room.Entity

@Entity()
data class Exercise (
    val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String
)