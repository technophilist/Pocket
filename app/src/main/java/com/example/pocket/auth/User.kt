package com.example.pocket.auth

import android.net.Uri

//represents a logged-in user
data class User(
    val name: String?,
    val email: String?,
    val photoUrl: Uri?
)