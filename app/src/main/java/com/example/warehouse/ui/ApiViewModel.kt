package com.example.warehouse.ui
import androidx.lifecycle.*
import com.example.warehouse.network.ApiRepository
import com.example.warehouse.model.Goal
import kotlinx.coroutines.launch

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    private val _currentGoal = MutableLiveData<Goal?>()
    val currentGoal: LiveData<Goal?> get() = _currentGoal

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
            repository.beginExperiment()
        }
    }
}