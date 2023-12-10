package com.neta.uas_pppb.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movies: Moviesdb)

    @Update
    fun update(movies: Moviesdb)

    @Delete
    fun delete(movies: Moviesdb)

    @get:Query("SELECT* from movies_table ORDER BY id ASC")
    val allMovies: LiveData<List<Moviesdb>>

    @Query("SELECT * FROM movies_table WHERE id = :moviesId")
    fun getMoviesById(moviesId: String): Moviesdb

    @Query("SELECT image FROM movies_table WHERE id = :moviesId")
    fun getImageMovieById(moviesId: String): String?
}