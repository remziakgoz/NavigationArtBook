package com.remziakgoz.artbookkotlinhomework.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.remziakgoz.artbookkotlinhomework.model.Art

@Database(entities = [Art::class], version = 1)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}