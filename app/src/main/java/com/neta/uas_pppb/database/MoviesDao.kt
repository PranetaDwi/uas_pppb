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
    fun getMoviesById(moviesId: String): LiveData<Moviesdb>

    @Query("SELECT * FROM movies_table WHERE id = :moviesId")
    fun getImageMovieById(moviesId: String): Moviesdb

    @Query("DELETE FROM movies_table WHERE id = :movieId")
    fun deleteById(movieId: String)

    @Query("UPDATE movies_table SET title = :title, detail = :detail, director = :director, rate = :rate, image = :image WHERE id = :movieid")
    fun updateById(title: String, detail: String, director: String, rate: String, image: String, movieid: String)

    @Query("UPDATE movies_table SET title = :title, detail = :detail, director = :director, rate = :rate WHERE id = :movieid")
    fun updateByIdNoImage(title: String, detail: String, director: String, rate: String, movieid: String)
}