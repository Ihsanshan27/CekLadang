package com.dicoding.cekladang.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.cekladang.repository.HistoryRepository
import com.dicoding.cekladang.ui.history.HistoryViewModel

class ViewModelFactory(private val historyRepository: HistoryRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                return HistoryViewModel(historyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(historyRepository: HistoryRepository): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(historyRepository).also { INSTANCE = it }
            }
        }
    }
}