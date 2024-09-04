package com.remziakgoz.artbookkotlinhomework.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Art(

    @ColumnInfo(name = "name")
    var artName: String,

    @ColumnInfo(name = "artname")
    var artistName: String,

    @ColumnInfo(name = "year")
    var year: String,

    @ColumnInfo(name = "imageUri")
    var imageUri: ByteArray

) {

    @PrimaryKey(autoGenerate = true)
    var id = 0
}
