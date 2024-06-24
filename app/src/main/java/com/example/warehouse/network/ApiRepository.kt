package com.example.warehouse.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.ktor.client.request.get
import io.ktor.client.request.post
import com.example.warehouse.model.Goal
import com.example.warehouse.model.Participant
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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

    private fun getParticipantId(): String {
        val participantId: String = sharedPreferences.getString("participant_id", "test")
            ?: throw Exception("Could not get participant ID from settings")
        return participantId
    }

    suspend fun getCurrentGoal(): Goal? {
        Log.d("ApiRepository", "Getting current goal from /goal/current...")
        val response: HttpResponse = client.get("${getBaseUrl()}/goal/current")
        return if (response.status.isSuccess()) {
            val json = response.bodyAsText()
            Log.d("ApiRepository", "Request succeeded")
            Json.decodeFromString<Goal>(json)
        } else {
            Log.d("ApiRepository", "Response code: ${response.status}")
            null
        }
    }

    suspend fun startGoal() {
        Log.d("ApiRepository", "Reporting started goal with /goal/started...")
        client.post("${getBaseUrl()}/goal/started")
    }

    suspend fun completeGoal() {
        Log.d("ApiRepository", "Reporting completed goal with /goal/completed...")
        client.post("${getBaseUrl()}/goal/completed")
    }

    suspend fun beginExperiment(): Boolean {
        Log.d("ApiRepository", "Initializing experiment with /experiment/begin...")
        client.post("${getBaseUrl()}/experiment/begin") {
            contentType(ContentType.Application.Json)
            setBody(Participant(id=getParticipantId()))
        }
        return true
    }
}