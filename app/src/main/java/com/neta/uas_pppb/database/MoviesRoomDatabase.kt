package com.neta.uas_pppb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Moviesdb::class], version = 1, exportSchema = false)
abstract class MoviesRoomDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao?

    companion object{
        @Volatile
        private var INSTANCE:MoviesRoomDatabase? = null
        fun getDatabase(context: Context): MoviesRoomDatabase?{
            if (INSTANCE == null){
                synchronized(MoviesRoomDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MoviesRoomDatabase::class.java,
                        "movies_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }

}