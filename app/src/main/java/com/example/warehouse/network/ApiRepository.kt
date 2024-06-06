package com.example.warehouse.network

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.warehouse.R
import io.ktor.client.request.get
import io.ktor.client.request.post
import com.example.warehouse.model.Goal
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class ApiRepository(context: Context) {
    private val client = ApiClient.client
    private val sharedPreferences: SharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)

    private fun getBaseUrl(): String {
        // TODO: convert to use `R.string.server_url_key` and `R.string.server_url_key`
        return sharedPreferences.getString("server_url", "http://10.10.10.145:5000") ?: "http://10.10.10.145:5000"
    }

    suspend fun getCurrentGoal(): Goal? {
        return try {
            val response: HttpResponse = client.get("${getBaseUrl()}/goal/current")
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                Json.decodeFromString<Goal>(json)
            } else {
                null
            }
        } catch (e: Exception) {
            // TODO: error handling
            null
        }
    }

    suspend fun startGoal() {
        try {
            client.post("${getBaseUrl()}/goal/started")
        } catch (e: Exception) {
            // TODO: error handling
        }
    }

    suspend fun completeGoal() {
        try {
            client.post("${getBaseUrl()}/goal/completed")
        } catch (e: Exception) {
            // TODO: error handling
        }
    }

    suspend fun beginExperiment() {
        try {
            client.post("${getBaseUrl()}/experiment/begin")
        } catch (e: Exception) {
            // TODO: error handling
        }
    }
}