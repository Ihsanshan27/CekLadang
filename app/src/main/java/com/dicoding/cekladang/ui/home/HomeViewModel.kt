package com.dicoding.cekladang.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _home = MutableLiveData<List<String>>().apply {
        value = listOf("Tanaman 1", "Tanaman 2", "Tanaman 3", "Tanaman 4", "Tanaman 5")
    }
    val home: LiveData<List<String>> = _home

}