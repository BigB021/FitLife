package com.fitlife.app.viewModel

import androidx.lifecycle.*
import com.fitlife.app.data.repository.FoodRepository
import com.fitlife.app.domain.model.FoodEntry
import kotlinx.coroutines.launch

class FoodViewModel(private val foodRepository: FoodRepository) : ViewModel() {
    // Local State
    private val _foodList = MutableLiveData<List<FoodEntry>>(emptyList())
    val foodList: LiveData<List<FoodEntry>> = _foodList
    // Search State
    private val _searchResults = MutableLiveData<List<FoodEntry>>(emptyList())
    val searchResults: LiveData<List<FoodEntry>> = _searchResults
    private val _searchError = MutableLiveData<String?>(null)
    val searchError: LiveData<String?> = _searchError

    private val _barcodeResult = MutableLiveData<FoodEntry?>()
    val barcodeResult: LiveData<FoodEntry?> = _barcodeResult
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading


    fun clearError() { _searchError.value = null }
    // Local database functions
    fun loadFood() {
        viewModelScope.launch {
            _foodList.value = foodRepository.getFood()
        }
    }

    fun addFood(food: FoodEntry) {
        viewModelScope.launch {
            foodRepository.addFood(food)
            _foodList.value = foodRepository.getFood()
        }
    }

    fun updateFood(food: FoodEntry) {
        viewModelScope.launch {
            foodRepository.updateFood(food)
            _foodList.value = foodRepository.getFood()
        }
    }

    fun deleteFood(food: FoodEntry) {
        viewModelScope.launch {
            foodRepository.deleteFood(food)
            _foodList.value = foodRepository.getFood()
        }
    }

    // API Functions
    fun searchByName(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _searchResults.value = foodRepository.searchByName(query)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _searchError.value = e.message   // expose error to UI
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchByBarcode(barcode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = foodRepository.searchByBarcode(barcode)
                _barcodeResult.value = result
            } catch (e: Exception) {
                println(e)
                _barcodeResult.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _barcodeResult.value = null
    }
}