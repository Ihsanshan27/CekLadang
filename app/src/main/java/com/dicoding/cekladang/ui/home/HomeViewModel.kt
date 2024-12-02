package com.dicoding.cekladang.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.cekladang.data.local.entity.Plants

class HomeViewModel : ViewModel() {

    private val _plantList = MutableLiveData<List<Plants>>()
    val plantList: LiveData<List<Plants>> get() = _plantList

    init {
        loadPlants()
    }

    private fun loadPlants() {
        // Data statis tanaman
        _plantList.value = listOf(
            Plants(1, "Jagung", "corn_labels.txt", "corn_model_with_metadata.tflite"),
            Plants(2, "Kedelai", "soybean_label.txt", "soybean_model.tflite"),
        )
    }

}