package com.example.warehouse.ui
import android.util.Log
import androidx.lifecycle.*
import com.example.warehouse.network.ApiRepository
import com.example.warehouse.model.Goal
import kotlinx.coroutines.launch

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    private val _currentGoal = MutableLiveData<Goal?>()
    val currentGoal: LiveData<Goal?> get() = _currentGoal

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _success = MutableLiveData<Boolean?>()
    val success: LiveData<Boolean?> get() = _success

    fun fetchCurrentGoal() {
        viewModelScope.launch {
            _currentGoal.value = repository.getCurrentGoal()
        }
    }

    fun startGoal() {
        viewModelScope.launch() {
            repository.startGoal()
        }
    }

    fun completeGoal() {
        viewModelScope.launch {
            repository.completeGoal()
        }
    }

    fun beginExperiment() {
        viewModelScope.launch {
            try {
                Log.d("ApiViewModel", "Begin experiment")
                _success.value = repository.beginExperiment()
            } catch (e: Exception) {
                handleError(e)
                _success.value = false
            }
        }
    }

    private fun handleError(e: Exception) {
        Log.e("ApiViewModel", "API error: ${e.message}")
        _errorMessage.value = "API error: ${e.message}"
    }
}