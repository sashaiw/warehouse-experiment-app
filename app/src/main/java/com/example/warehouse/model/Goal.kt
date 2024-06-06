package com.example.warehouse.model

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: String,
    val label: String
)