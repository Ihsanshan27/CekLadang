package com.dicoding.cekladang.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "History")
@Parcelize
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "prediction")
    var prediction: String? = null,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis(),

    ) : Parcelable