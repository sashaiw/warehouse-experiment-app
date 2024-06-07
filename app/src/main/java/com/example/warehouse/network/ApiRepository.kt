package com.example.warehouse.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
        Log.d("ApiRepository", "Getting current goal from /goal/current...")
        return try {
            val response: HttpResponse = client.get("${getBaseUrl()}/goal/current")
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                Log.d("ApiRepository", "Request succeeded")
                Json.decodeFromString<Goal>(json)
            } else {
                Log.d("ApiRepository", "Response code: ${response.status}")
                null
            }
        } catch (e: Exception) {
            // TODO: error handling
            Log.e("ApiRepository", "API error: ${e.message}")
            null
        }
    }

    suspend fun startGoal() {
        try {
            Log.d("ApiRepository", "Reporting started goal with /goal/started...")
            client.post("${getBaseUrl()}/goal/started")
        } catch (e: Exception) {
            Log.e("ApiRepository", "API error: ${e.message}")
            // TODO: error handling
        }
    }

    suspend fun completeGoal() {
        try {
            Log.d("ApiRepository", "Reporting completed goal with /goal/completed...")
            client.post("${getBaseUrl()}/goal/completed")
        } catch (e: Exception) {
            Log.e("ApiRepository", "API error: ${e.message}")
            // TODO: error handling
        }
    }

    suspend fun beginExperiment(): Boolean {
        try {
            Log.d("ApiRepository", "Initializing experiment with /experiment/begin...")
            client.post("${getBaseUrl()}/experiment/begin")
            return true
        } catch (e: Exception) {
            Log.e("ApiRepository", "API error: ${e.message}")
            return false
        }
    }
}