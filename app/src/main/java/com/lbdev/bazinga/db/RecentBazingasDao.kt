package com.lbdev.bazinga.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentBazingasDao {

    @Upsert
    fun upsertRecentBazingas(recentBazingas: RecentBazingas)

    @Delete
    fun deleteRecentBazingas(recentBazingas: RecentBazingas)

    @Query("SELECT * FROM recent_bazingas_table ORDER BY dateAdded DESC")
    fun getAllRecentBazingas(): List<RecentBazingas>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(recentBazingas: RecentBazingas)
}