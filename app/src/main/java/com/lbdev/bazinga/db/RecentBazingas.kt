package com.lbdev.bazinga.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recent_bazingas_table")
data class RecentBazingas(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "photoUrl") val photoUrl: String,
    @ColumnInfo(name = "dateAdded") val dateAdded: Long
)
