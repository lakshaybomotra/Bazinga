package com.lbdev.bazinga.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecentBazingas :: class], version = 1)
abstract class RecentBazingasDatabase : RoomDatabase() {

    abstract fun recentDao() : RecentBazingasDao

    companion object{

        @Volatile
        private var INSTANCE : RecentBazingasDatabase? = null

        fun getDatabase(context: Context): RecentBazingasDatabase{

            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecentBazingasDatabase::class.java,
                    "recent_bazingas_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }

    }

}