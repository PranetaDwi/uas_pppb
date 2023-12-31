package com.neta.uas_pppb.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coba_movies3")
data class CobaMovies(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "detail")
    val detail: String,
    @ColumnInfo(name = "director")
    val director: String,
    @ColumnInfo(name = "rate")
    val rate: String,
    @ColumnInfo(name = "duration")
    val duration: String,
    @ColumnInfo(name = "genre")
    val genre: String,
    @ColumnInfo(name = "image")
    val image: String
)
