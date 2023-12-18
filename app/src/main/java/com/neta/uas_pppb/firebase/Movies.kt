package com.neta.uas_pppb.firebase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movies(
    @PrimaryKey
    var id: String = "",
    var title: String = "",
    var detail: String = "",
    val director: String = "",
    val rate: String = "",
    val duration: String = "",
    val genre: String = "",
    val image: String = ""


)
