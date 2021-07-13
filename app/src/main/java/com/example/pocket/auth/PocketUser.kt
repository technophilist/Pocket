package com.example.pocket.auth

import android.net.Uri

//represents a logged-in user
data class PocketUser(
    val name: String?,
    val email: String?,
    val photoUrl: Uri?
)