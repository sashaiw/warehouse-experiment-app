package com.example.warehouse.ui
import android.util.Log
import androidx.lifecycle.*
import com.example.warehouse.network.ApiRepository
import com.example.warehouse.model.Goal
import com.example.warehouse.model.Result
import kotlinx.coroutines.launch

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

    fun startGoal() {
        viewModelScope.launch() {
            try {
                repository.startGoal()
                _result.value = Result.Success(Unit)
            } catch (e: Exception) {
                _result.value = Result.Error(e)
            }
        }
    }

    fun completeGoal() {
        viewModelScope.launch {
            try {
                repository.completeGoal()
                _result.value = Result.Success(Unit)
            } catch (e: Exception) {
                _result.value = Result.Error(e)
            }
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