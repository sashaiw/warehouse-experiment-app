package com.example.warehouse.model

import kotlinx.serialization.Serializable

@Serializable
data class Experiment(
    val id: String,
    val condition: String,
)