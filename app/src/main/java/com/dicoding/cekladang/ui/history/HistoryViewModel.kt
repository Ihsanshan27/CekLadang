package com.dicoding.cekladang.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.repository.HistoryRepository

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    fun insert(history: History) {
        historyRepository.insert(history)
    }

    fun delete(history: History) {
        historyRepository.delete(history)
    }

    fun getAllHistoryUser(): LiveData<List<History>> = historyRepository.getAllHistoryUser()
}