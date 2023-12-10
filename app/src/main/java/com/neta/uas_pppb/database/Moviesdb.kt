package com.neta.uas_pppb.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_table")
data class Moviesdb(
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
    @ColumnInfo(name = "image")
    val image: String
)
