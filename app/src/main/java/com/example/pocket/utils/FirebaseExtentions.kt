package com.example.pocket.utils

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * This extension method is used for creating a [FirebaseUser] with [name],[email],[password] and profile picture ([profilePhotoUri]).
 * This method uses [suspendCancellableCoroutine] and resumes with an instance of [FirebaseUser] if the user was created successfully.
 * Else, it resumes with an [Exception].
 *
 * Firebase doesn't provide a default method to create a user along with a display name and profile photo.In order to perform such a
 * task we need to chain two methods - [FirebaseAuth.createUserWithEmailAndPassword] and [FirebaseAuth.updateCurrentUser].
 */
suspend fun FirebaseAuth.createUser(
    name: String,
    email: String,
    password: String,
    profilePhotoUri: Uri?
): FirebaseUser = suspendCancellableCoroutine { cancellableContinuation ->
    createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { createUserTask ->
            if (createUserTask.isSuccessful) {

                //if user is created successfully, set the display name and profile picture
                val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(profilePhotoUri)
                    .build()

                currentUser?.updateProfile(userProfileChangeRequest)
                    ?.addOnCompleteListener { updateProfileTask ->
                        if (updateProfileTask.isSuccessful) {
                            //if the update task is successful, return the firebase user
                            cancellableContinuation.resume(currentUser!!)
                        } else {
                            cancellableContinuation.resumeWithException(
                                updateProfileTask.exception
                                    ?: Exception("An error occurred while updating the firebase user object")
                            )
                        }
                    }

            } else {
                //if there is an error creating the user, resume with exception
                cancellableContinuation.resumeWithException(
                    createUserTask.exception ?: Exception("An error occurred")
                )
            }
        }
}
