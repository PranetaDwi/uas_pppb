package com.neta.uas_pppb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CobaMovies::class], version = 1, exportSchema = false)
abstract class CobaMoviesDatabase: RoomDatabase(){
    abstract fun cobaMoviesDao(): CobaMoviesDao?

    companion object{
        @Volatile
        private var INSTANCE:CobaMoviesDatabase? = null
        fun getDatabase(context: Context): CobaMoviesDatabase?{
            if (INSTANCE == null){
                synchronized(CobaMoviesDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CobaMoviesDatabase::class.java,
                        "coba_movie3"
                    ).build()
                }
            }
            return INSTANCE
        }
    }

}