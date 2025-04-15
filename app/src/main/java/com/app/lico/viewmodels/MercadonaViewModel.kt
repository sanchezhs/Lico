package com.app.lico.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.lico.data.mercadona.ProductDao
import com.app.lico.data.mercadona.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val productDao: ProductDao
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<ProductEntity>>(emptyList())
    val suggestions: StateFlow<List<ProductEntity>> = _suggestions

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _suggestions.value = productDao.searchByName(query)
        }
    }
}