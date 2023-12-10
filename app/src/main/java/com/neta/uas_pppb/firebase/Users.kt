package com.neta.uas_pppb.firebase

data class Users(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    val password: String = "",
    val phone: String = ""
)
