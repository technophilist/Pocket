package com.example.pocket.auth

import android.net.Uri

/**
 * A domain model class that represents a user with a [name],
 * [email] and a [photoUrl] that will be used as the profile
 * picture of the user.
 */
data class PocketUser(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: Uri?
)