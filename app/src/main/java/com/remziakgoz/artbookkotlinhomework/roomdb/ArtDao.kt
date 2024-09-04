package com.remziakgoz.artbookkotlinhomework.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.remziakgoz.artbookkotlinhomework.model.Art
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {

    @Query("SELECT * FROM Art")
    fun getAll(): Flowable<List<Art>>

    @Query("SELECT * FROM Art WHERE id = :id")
    suspend fun getArtById(id: Int): Art

    @Insert
    fun insert(art: Art) : Completable

    @Delete
    fun delete(art: Art) : Completable
}