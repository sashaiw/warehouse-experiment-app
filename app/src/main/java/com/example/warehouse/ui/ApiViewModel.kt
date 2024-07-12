package com.example.warehouse.ui
import android.util.Log
import androidx.lifecycle.*
import com.example.warehouse.network.ApiRepository
import com.example.warehouse.model.Goal
import com.example.warehouse.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    private val _currentGoal = MutableLiveData<Result<Goal?>>()
    val currentGoal: LiveData<Result<Goal?>> get() = _currentGoal

    private val _result = MutableLiveData<Result<Unit>>()
    val result: LiveData<Result<Unit>> get() = _result

    fun fetchCurrentGoal() {
        viewModelScope.launch {
            try {
                val goal = repository.getCurrentGoal()
                _currentGoal.value = Result.Success(goal)
            } catch (e: Exception) {
                _currentGoal.value = Result.Error(e)
            }
        }
    }

    suspend fun startGoal(): Result<Unit> {
        return try {
            repository.startGoal()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun completeGoal(): Result<Unit> {
        return try {
            repository.completeGoal()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun beginExperiment() {
        viewModelScope.launch {
            try {
                Log.d("ApiViewModel", "Begin experiment")
                repository.beginExperiment()
                _result.value = Result.Success(Unit)
            } catch (e: Exception) {
                _result.value = Result.Error(e)
            }
        }
    }
}