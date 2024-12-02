package com.dicoding.cekladang.data.local.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Plants(
    val id: Int,
    val name: String,
    val labelPath: String,
    val modelPath: String,
) : Parcelable