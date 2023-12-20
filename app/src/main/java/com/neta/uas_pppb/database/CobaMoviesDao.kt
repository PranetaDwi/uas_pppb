package com.neta.uas_pppb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CobaMoviesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movies: List<CobaMovies>)

    @get:Query("SELECT * FROM coba_movies3")
    val allMovies: LiveData<List<CobaMovies>>

    @Query("DELETE FROM coba_movies3")
    fun deleteAllMovies()

}